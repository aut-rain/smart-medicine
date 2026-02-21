package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * 咨询 API 接口
 * 支持 AI 智能咨询
 */
interface ConsultationApi {

    /**
     * 发起 AI 咨询（SSE 流式响应）
     * POST /api/v1/ai/consult
     *
     * 注意：此接口返回 SSE 流，需要使用 OkHttpClient 直接调用
     */
    @POST("api/v1/ai/consult")
    @Streaming
    suspend fun consult(@Body request: RequestBody): ResponseBody

    /**
     * 发起 AI 咨询（普通响应）
     * POST /api/v1/ai-chat/query
     */
    @POST("api/v1/ai-chat/query")
    suspend fun consultSync(@Body request: ChatRequest): ApiResponse<ChatResponse>

    /**
     * 获取咨询历史
     * GET /api/v1/ai/histories
     */
    @GET("api/v1/ai/histories")
    suspend fun getConsultHistories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<com.example.smart_medicine_android.data.network.model.PageResponse<ConsultationHistoryItem>>
}

/**
 * AI 聊天请求
 */
@kotlinx.serialization.Serializable
data class ChatRequest(
    val message: String,
    val conversationId: String? = null,
    val userId: Int? = null
)

/**
 * AI 聊天响应（data中的内容）
 */
@kotlinx.serialization.Serializable
data class ChatResponse(
    val content: String? = null,
    val conversationId: String? = null,
    val messageId: String? = null
)

/**
 * 咨询请求（旧格式，保留兼容性）
 */
@kotlinx.serialization.Serializable
data class ConsultationRequest(
    val question: String,
    val illnessId: String? = null,
    val context: String? = null
)

/**
 * 咨询响应（旧格式，保留兼容性）
 */
@kotlinx.serialization.Serializable
data class ConsultationResponse(
    val answer: String,
    val consultationId: String
)

/**
 * 咨询历史项
 */
@kotlinx.serialization.Serializable
data class ConsultationHistoryItem(
    val id: String,
    val question: String,
    val answer: String,
    val createdAt: Long
)
