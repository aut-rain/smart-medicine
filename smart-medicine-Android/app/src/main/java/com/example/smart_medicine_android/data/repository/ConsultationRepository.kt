package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.local.dao.ConsultationDao
import com.example.smart_medicine_android.data.local.entity.ConsultationEntity
import com.example.smart_medicine_android.data.network.api.ConsultationApi
import com.example.smart_medicine_android.data.network.api.ChatRequest
import com.example.smart_medicine_android.data.network.api.ChatResponse
import com.example.smart_medicine_android.data.network.model.ApiException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * 咨询仓库
 * 负责 AI 智能咨询相关操作
 */
class ConsultationRepository(
    private val consultationApi: ConsultationApi,
    private val consultationDao: ConsultationDao
) {

    private val jsonMediaType = "application/json".toMediaType()

    /**
     * 发起 AI 咨询（流式 SSE 模式）
     * 使用 /api/v1/ai-chat/stream 端点
     *
     * 流式逻辑：仿照 Web 前端实现
     * 1. 立即插入数据库记录（用户消息 + 空 AI 回复）
     * 2. 每个 chunk 到达时，更新数据库中的 answer 字段
     * 3. Room 的 Flow 会自动触发 UI 更新
     */
    fun consultStream(
        question: String,
        userId: String,
        baseUrl: String,
        accessToken: String?
    ): Flow<StreamResult> = flow {
        val consultationId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        try {
            // 步骤1：立即插入数据库记录（用户消息 + 空 AI 回复）
            // 这样 UI 可以立即显示用户消息
            val initialConsultation = ConsultationEntity(
                id = consultationId,
                userId = userId,
                question = question,
                answer = "",
                status = "streaming",
                createdAt = currentTime,
                updatedAt = currentTime
            )
            consultationDao.insertConsultation(initialConsultation)
            android.util.Log.d("ConsultationRepo", "Initial consultation inserted: id=$consultationId, status=streaming")

            // 发射开始事件，通知 ViewModel 开始监听数据库变化
            emit(StreamResult.Start(consultationId))

            // 步骤2：发起 SSE 流式请求
            val userIdInt = try {
                userId?.toIntOrNull() ?: 0
            } catch (e: Exception) {
                android.util.Log.w("ConsultationRepo", "Failed to convert userId '$userId' to Int, using 0", e)
                0
            }

            val jsonBody = JSONObject().apply {
                put("message", question)
                put("conversationId", consultationId)
                put("userId", userIdInt)
            }

            android.util.Log.d("ConsultationRepo", "Request payload: message=$question, conversationId=$consultationId, userId=$userIdInt")

            val cleanBaseUrl = baseUrl.trimEnd('/')
            val request = Request.Builder()
                .url("$cleanBaseUrl/api/v1/ai-chat/stream")
                .post(RequestBody.create(jsonMediaType, jsonBody.toString()))
                .apply {
                    accessToken?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                    addHeader("Content-Type", "application/json")
                }
                .build()

            val client = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            android.util.Log.d("ConsultationRepo", "Sending SSE request to: $cleanBaseUrl/api/v1/ai-chat/stream")

            val response = client.newCall(request).execute()

            android.util.Log.d("ConsultationRepo", "Response received: code=${response.code}, successful=${response.isSuccessful}, body null?${response.body == null}")

            if (!response.isSuccessful) {
                val errorMsg = "HTTP ${response.code}: ${response.message}"
                val errorBody = response.body?.string()
                android.util.Log.e("ConsultationRepo", "Stream error: $errorMsg - $errorBody")

                // 删除数据库中的记录，不显示错误信息在聊天中
                consultationDao.deleteConsultation(consultationId)
                emit(StreamResult.Error("$errorMsg - $errorBody"))
                return@flow
            }

            // 检查响应体是否为空
            if (response.body == null) {
                android.util.Log.e("ConsultationRepo", "Response body is null")
                // 删除数据库中的记录
                consultationDao.deleteConsultation(consultationId)
                emit(StreamResult.Error("服务器返回了空响应"))
                return@flow
            }

            // 步骤3：解析 SSE 流，实时更新数据库
            val responseBody = response.body ?: throw IOException("Response body is null")
            var fullAnswer = ""
            var parsedAnyData = false
            var lastUpdateTime = currentTime
            val updateInterval = 50L  // 更新间隔：50ms，更流畅的流式体验
            var totalLinesRead = 0
            var rawDataReceived = false

            android.util.Log.d("ConsultationRepo", "Starting to read SSE stream...")

            var buffer = ""
            responseBody.byteStream().bufferedReader().use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    totalLinesRead++
                    rawDataReceived = true

                    // 每100行记录一次进度
                    if (totalLinesRead % 100 == 0) {
                        android.util.Log.d("ConsultationRepo", "Read $totalLinesRead lines, buffer size: ${buffer.length}")
                    }

                    buffer += line + "\n"

                    while (buffer.contains("\n\n")) {
                        val splitIndex = buffer.indexOf("\n\n")
                        val message = buffer.substring(0, splitIndex)
                        buffer = buffer.substring(splitIndex + 2)

                        android.util.Log.v("ConsultationRepo", "SSE message: '$message'")

                        if (message.startsWith("data:")) {
                            // 完全仿照 Web 前端：msg.slice(5) 不带 trim
                            val payload = message.substring(5)

                            if (payload == "[DONE]") {
                                android.util.Log.d("ConsultationRepo", "Stream ended with [DONE]")
                                break
                            }

                            if (payload.isNotEmpty()) {
                                parsedAnyData = true
                                // 完全仿照 Web 前端：使用正则全局替换
                                // Web: payload.replace(/\ndata:/g, '\n')
                                val content = payload.replace(Regex("\\ndata:"), "\n")
                                fullAnswer += content

                                // 调试日志
                                android.util.Log.v("ConsultationRepo", "Chunk: '${content.take(20)}...', total: ${fullAnswer.length}")

                                // 步骤4：实时更新数据库中的 answer
                                // 限制更新频率：每 50ms，确保流畅的流式体验
                                val now = System.currentTimeMillis()
                                if (now - lastUpdateTime >= updateInterval) {
                                    val updatedConsultation = initialConsultation.copy(
                                        answer = fullAnswer,
                                        updatedAt = now
                                    )
                                    consultationDao.updateConsultation(updatedConsultation)
                                    lastUpdateTime = now

                                    // 调试日志
                                    android.util.Log.d("ConsultationRepo", "Updated DB: ${fullAnswer.length} chars, status=${updatedConsultation.status}")
                                }

                                // 始终发射片段事件（用于日志和调试）
                                emit(StreamResult.Fragment(content, fullAnswer))
                            }
                        }
                    }

                    if (buffer.contains("[DONE]")) {
                        break
                    }
                }
            }

            android.util.Log.d("ConsultationRepo", "SSE stream ended. Total lines: $totalLinesRead, rawDataReceived: $rawDataReceived, parsedAnyData: $parsedAnyData, fullAnswer length: ${fullAnswer.length}")

            // 检查解析结果
            if (!parsedAnyData) {
                val errorMsg = when {
                    !rawDataReceived -> "网络连接中断，未收到任何数据"
                    totalLinesRead == 0 -> "服务器未返回任何数据"
                    buffer.isNotEmpty() -> "服务器返回了数据但格式不正确"
                    else -> "AI未返回有效回复"
                }

                android.util.Log.e("ConsultationRepo", "No data parsed from SSE stream: $errorMsg, buffer preview: ${buffer.take(100)}")
                // 删除数据库中的记录，不显示错误信息在聊天中
                consultationDao.deleteConsultation(consultationId)
                emit(StreamResult.Error(errorMsg))
                return@flow
            }

            // 步骤5：流式完成，最终更新数据库为 completed 状态
            val completedConsultation = initialConsultation.copy(
                answer = fullAnswer,
                status = "completed",
                updatedAt = System.currentTimeMillis()
            )
            consultationDao.updateConsultation(completedConsultation)
            android.util.Log.d("ConsultationRepo", "Stream completed: ${fullAnswer.length} chars")
            emit(StreamResult.Complete(completedConsultation))

        } catch (e: Exception) {
            android.util.Log.e("ConsultationRepo", "Exception during stream", e)
            emit(StreamResult.Error(e.message ?: "请求失败: ${e.javaClass.simpleName}"))
        }
    }.flowOn(kotlinx.coroutines.Dispatchers.IO)  // 确保网络操作在 IO 线程执行

    /**
     * 发起 AI 咨询（同步模式 - 保留兼容性）
     */
    suspend fun consult(
        question: String,
        illnessId: String? = null,
        userId: String
    ): Result<ConsultationEntity> {
        val consultationId = UUID.randomUUID().toString()
        val currentTime = System.currentTimeMillis()

        android.util.Log.d("ConsultationRepo", "consult() called with userId: $userId, question: $question")

        return try {
            val userIdInt = userId.toIntOrNull() ?: 0
            android.util.Log.d("ConsultationRepo", "userIdInt: $userIdInt, consultationId: $consultationId")

            val request = ChatRequest(
                message = question,
                conversationId = consultationId,
                userId = userIdInt
            )
            android.util.Log.d("ConsultationRepo", "Sending request: $request")

            val response = consultationApi.consultSync(request)
            android.util.Log.d("ConsultationRepo", "Response received. isSuccess: ${response.isSuccess}, code: ${response.code}, message: ${response.message}")

            if (response.isSuccess) {
                val chatResponse = response.getDataOrThrow()
                val answer = chatResponse.content ?: ""

                android.util.Log.d("ConsultationRepo", "Chat response content length: ${answer.length}")

                if (answer.isNotEmpty()) {
                    val completedConsultation = ConsultationEntity(
                        id = consultationId,
                        userId = userId,
                        illnessId = illnessId,
                        question = question,
                        answer = answer,
                        status = "completed",
                        createdAt = currentTime,
                        updatedAt = currentTime
                    )
                    consultationDao.insertConsultation(completedConsultation)
                    Result.success(completedConsultation)
                } else {
                    android.util.Log.e("ConsultationRepo", "AI returned empty response")
                    Result.failure(Exception("AI未返回有效回复"))
                }
            } else {
                android.util.Log.e("ConsultationRepo", "API request failed. code: ${response.code}, message: ${response.message}")
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "请求失败"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ConsultationRepo", "Exception during consult", e)
            Result.failure(e)
        }
    }

    /**
     * 获取用户的咨询历史
     */
    fun getConsultationsByUserId(userId: String): Flow<List<ConsultationEntity>> {
        return consultationDao.getConsultationsByUserId(userId)
    }

    /**
     * 根据疾病ID获取咨询记录
     */
    fun getConsultationsByIllnessId(illnessId: String): Flow<List<ConsultationEntity>> {
        return consultationDao.getConsultationsByIllnessId(illnessId)
    }

    /**
     * 根据ID获取咨询记录
     */
    fun getConsultationById(consultationId: String): Flow<ConsultationEntity?> {
        return consultationDao.getConsultationById(consultationId)
    }

    /**
     * 获取待处理的咨询
     */
    fun getPendingConsultations(): Flow<List<ConsultationEntity>> {
        return consultationDao.getPendingConsultations()
    }

    /**
     * 删除咨询记录
     */
    suspend fun deleteConsultation(consultationId: String) {
        consultationDao.deleteConsultation(consultationId)
    }

    /**
     * 清空所有咨询记录
     */
    suspend fun clearAllConsultations() {
        consultationDao.deleteAllConsultations()
    }
}

/**
 * 流式响应结果
 */
sealed class StreamResult {
    /**
     * 开始
     */
    data class Start(val consultationId: String) : StreamResult()

    /**
     * 文本片段
     */
    data class Fragment(val text: String, val fullText: String) : StreamResult()

    /**
     * 完成
     */
    data class Complete(val consultation: ConsultationEntity) : StreamResult()

    /**
     * 错误
     */
    data class Error(val message: String) : StreamResult()
}
