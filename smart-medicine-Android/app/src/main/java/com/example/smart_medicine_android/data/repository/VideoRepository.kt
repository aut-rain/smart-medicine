package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.network.api.VideoApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.PageResponse
import com.example.smart_medicine_android.data.network.model.VideoDto

/**
 * 视频仓库
 * 负责视频数据的获取
 */
class VideoRepository(
    private val videoApi: VideoApi
) {

    /**
     * 分页获取视频列表
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 分页数据
     */
    suspend fun getVideos(
        page: Int = 1,
        size: Int = 10,
        keyword: String? = null
    ): Result<PageResponse<VideoDto>> {
        return try {
            val response = videoApi.getVideos(page, size, keyword)
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
     * 获取视频详情
     * @param videoId 视频ID
     * @return 视频详情
     */
    suspend fun getVideoDetail(videoId: Int): Result<VideoDto> {
        return try {
            val response = videoApi.getVideoDetail(videoId)
            if (response.isSuccess) {
                Result.success(response.getDataOrThrow())
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
