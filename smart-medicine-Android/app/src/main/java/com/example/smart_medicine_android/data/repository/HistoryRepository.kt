package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.network.api.HistoryApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.HistoryDto
import com.example.smart_medicine_android.data.network.model.PageResponse

/**
 * 浏览历史仓库
 * 负责浏览历史的记录和查询
 */
class HistoryRepository(
    private val historyApi: HistoryApi
) {

    /**
     * 记录浏览历史（异步）
     * @param userId 用户ID
     * @param operateType 操作类型: 2-查看疾病, 4-查看药品, 5-观看视频
     * @param operateId 操作对象ID
     * @param operateName 操作对象名称
     * @return 成功或失败
     */
    suspend fun recordHistory(
        userId: Int,
        operateType: Int,
        operateId: Int,
        operateName: String
    ): Result<Unit> {
        return try {
            val response = historyApi.recordHistory(userId, operateType, operateId, operateName)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            // 静默失败，不影响主流程
            Result.failure(e)
        }
    }

    /**
     * 获取浏览历史
     * @param page 页码
     * @param size 每页大小
     * @return 分页数据
     */
    suspend fun getHistories(
        page: Int = 1,
        size: Int = 10
    ): Result<PageResponse<HistoryDto>> {
        return try {
            val response = historyApi.getHistories(page, size)
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
     * 清空浏览历史
     * @return 成功或失败
     */
    suspend fun clearHistories(): Result<Unit> {
        return try {
            val response = historyApi.clearHistories()
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
