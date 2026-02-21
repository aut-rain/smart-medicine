package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import com.example.smart_medicine_android.data.network.model.PageResponse
import com.example.smart_medicine_android.data.network.model.VideoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 视频相关 API
 */
interface VideoApi {

    /**
     * 分页获取视频列表
     * GET /api/v1/videos
     */
    @GET("api/v1/videos")
    suspend fun getVideos(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<VideoDto>>

    /**
     * 获取视频详情
     * GET /api/v1/videos/{id}
     */
    @GET("api/v1/videos/{id}")
    suspend fun getVideoDetail(
        @Path("id") videoId: Int
    ): ApiResponse<VideoDto>
}
