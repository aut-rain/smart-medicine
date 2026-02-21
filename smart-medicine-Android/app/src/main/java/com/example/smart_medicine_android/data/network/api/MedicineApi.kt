package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 药品 API 接口
 */
interface MedicineApi {

    /**
     * 获取热门药品列表（使用分页接口）
     * GET /api/v1/medicines
     */
    @GET("api/v1/medicines")
    suspend fun getHotMedicines(
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): ApiResponse<PageResponse<MedicineDto>>

    /**
     * 搜索药品
     * GET /api/v1/medicines/search
     */
    @GET("api/v1/medicines/search")
    suspend fun searchMedicines(
        @Query("keyword") keyword: String
    ): ApiResponse<List<MedicineDto>>

    /**
     * 获取药品详情
     * GET /api/v1/medicines/{id}
     */
    @GET("api/v1/medicines/{id}")
    suspend fun getMedicineDetail(
        @Path("id") medicineId: Int
    ): ApiResponse<MedicineDto>

    /**
     * 分页获取药品列表
     * GET /api/v1/medicines
     */
    @GET("api/v1/medicines")
    suspend fun getMedicines(
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10,
        @Query("medicineType") medicineType: Int? = null,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<MedicineDto>>
}
