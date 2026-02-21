package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.network.api.FeedbackApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.FeedbackDto
import com.example.smart_medicine_android.data.network.model.FeedbackSubmitRequest
import com.example.smart_medicine_android.data.network.model.PageResponse

/**
 * 用户反馈仓库
 */
class FeedbackRepository(
    private val feedbackApi: FeedbackApi
) {

    /**
     * 获取我的反馈列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页数据
     */
    suspend fun getMyFeedbacks(
        page: Int = 1,
        size: Int = 10
    ): Result<PageResponse<FeedbackDto>> {
        return try {
            val response = feedbackApi.getMyFeedbacks(page, size)
            if (response.isSuccess) {
                Result.success(response.getDataOrThrow())
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 提交反馈
     * @param request 反馈请求
     * @return 成功或失败
     */
    suspend fun submitFeedback(request: FeedbackSubmitRequest): Result<Unit> {
        return try {
            val response = feedbackApi.submitFeedback(request)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除我的反馈
     * @param feedbackId 反馈ID
     * @return 成功或失败
     */
    suspend fun deleteMyFeedback(feedbackId: Int): Result<Unit> {
        return try {
            val response = feedbackApi.deleteMyFeedback(feedbackId)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
