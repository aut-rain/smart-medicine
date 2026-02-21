package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.*
import retrofit2.http.*

/**
 * 资讯 API 接口
 * 对应后端API文档
 */
interface NewsApi {

    /**
     * 获取推荐资讯（用于轮播图）
     * GET /api/v1/medical-news/featured
     */
    @GET("api/v1/medical-news/featured")
    suspend fun getFeaturedNews(
        @Query("limit") limit: Int? = 5
    ): ApiResponse<List<NewsDto>>

    /**
     * 搜索资讯
     * GET /api/v1/medical-news/search
     */
    @GET("api/v1/medical-news/search")
    suspend fun searchNews(
        @Query("keyword") keyword: String
    ): ApiResponse<List<NewsDto>>

    /**
     * 获取资讯详情
     * GET /api/v1/medical-news/{id}
     */
    @GET("api/v1/medical-news/{id}")
    suspend fun getNewsDetail(
        @Path("id") newsId: Int
    ): ApiResponse<NewsDetailDto>

    /**
     * 分页获取资讯列表
     * GET /api/v1/medical-news
     */
    @GET("api/v1/medical-news")
    suspend fun getNewsList(
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10,
        @Query("category") category: String? = null,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<NewsDto>>
}
