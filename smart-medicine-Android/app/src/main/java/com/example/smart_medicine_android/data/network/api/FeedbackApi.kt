package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import com.example.smart_medicine_android.data.network.model.FeedbackDto
import com.example.smart_medicine_android.data.network.model.FeedbackSubmitRequest
import com.example.smart_medicine_android.data.network.model.PageResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 用户反馈相关 API
 */
interface FeedbackApi {

    /**
     * 获取我的反馈列表
     * GET /api/v1/feedbacks/my
     */
    @GET("api/v1/feedbacks/my")
    suspend fun getMyFeedbacks(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<FeedbackDto>>

    /**
     * 提交反馈
     * POST /api/v1/feedbacks
     */
    @POST("api/v1/feedbacks")
    suspend fun submitFeedback(
        @Body request: FeedbackSubmitRequest
    ): ApiResponse<Void>

    /**
     * 修改我的反馈
     * PUT /api/v1/feedbacks/my/{id}
     */
    @PUT("api/v1/feedbacks/my/{id}")
    suspend fun updateMyFeedback(
        @Path("id") feedbackId: Int,
        @Body request: FeedbackSubmitRequest
    ): ApiResponse<FeedbackDto>

    /**
     * 删除我的反馈
     * DELETE /api/v1/feedbacks/my/{id}
     */
    @DELETE("api/v1/feedbacks/my/{id}")
    suspend fun deleteMyFeedback(
        @Path("id") feedbackId: Int
    ): ApiResponse<Void>
}
