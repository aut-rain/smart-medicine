package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.*
import retrofit2.http.*

/**
 * 疾病 API 接口
 * 对应后端API文档
 */
interface IllnessApi {

    /**
     * 获取热门疾病列表
     * GET /api/v1/illnesses/hot
     */
    @GET("api/v1/illnesses/hot")
    suspend fun getHotIllnesses(
        @Query("limit") limit: Int? = 10
    ): ApiResponse<List<IllnessDto>>

    /**
     * 搜索疾病
     * GET /api/v1/illnesses/search
     */
    @GET("api/v1/illnesses/search")
    suspend fun searchIllnesses(
        @Query("keyword") keyword: String
    ): ApiResponse<List<IllnessDto>>

    /**
     * 获取疾病详情
     * GET /api/v1/illnesses/{id}
     */
    @GET("api/v1/illnesses/{id}")
    suspend fun getIllnessDetail(
        @Path("id") illnessId: Int
    ): ApiResponse<IllnessDto>

    /**
     * 分页获取疾病列表
     * GET /api/v1/illnesses
     */
    @GET("api/v1/illnesses")
    suspend fun getIllnesses(
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10,
        @Query("kindId") kindId: Int? = null,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<IllnessDto>>
}
