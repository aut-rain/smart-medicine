package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import retrofit2.http.*

/**
 * 咨询 API 接口
 * 支持 AI 智能咨询
 */
interface ConsultationApi {

    /**
     * 发起 AI 咨询（普通响应）
     * POST /api/v1/ai-chat/query
     */
    @POST("api/v1/ai-chat/query")
    suspend fun consultSync(@Body request: ChatRequest): ApiResponse<ChatResponse>
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
