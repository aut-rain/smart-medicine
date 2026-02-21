package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import com.example.smart_medicine_android.data.network.model.HistoryDto
import com.example.smart_medicine_android.data.network.model.PageResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 浏览历史相关 API
 */
interface HistoryApi {

    /**
     * 记录浏览历史
     * POST /api/v1/histories/record
     */
    @FormUrlEncoded
    @POST("api/v1/histories/record")
    suspend fun recordHistory(
        @Field("userId") userId: Int,
        @Field("operateType") operateType: Int,
        @Field("operateId") operateId: Int,
        @Field("operateName") operateName: String
    ): ApiResponse<Void>

    /**
     * 获取浏览历史
     * GET /api/v1/histories
     */
    @GET("api/v1/histories")
    suspend fun getHistories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<PageResponse<HistoryDto>>

    /**
     * 清空浏览历史
     * DELETE /api/v1/histories
     */
    @DELETE("api/v1/histories")
    suspend fun clearHistories(): ApiResponse<Void>
}
