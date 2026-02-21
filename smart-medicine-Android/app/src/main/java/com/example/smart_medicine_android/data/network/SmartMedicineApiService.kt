package com.example.smart_medicine_android.data.network

import com.example.smart_medicine_android.data.network.model.*
import retrofit2.http.*

/**
 * 智慧医疗 API 服务接口
 *
 * 定义所有 API 端点
 */
interface SmartMedicineApiService {

    // ============================================
    // 认证相关接口
    // ============================================

    /**
     * 用户登录
     * POST /api/v1/auth/login
     */
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<TokenResponse>

    /**
     * 用户注册
     * POST /api/v1/auth/register
     */
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<TokenResponse>

    /**
     * 发送邮箱验证码
     * POST /api/v1/auth/email-code
     */
    @POST("api/v1/auth/email-code")
    suspend fun sendEmailCode(@Query("email") email: String): ApiResponse<Void>

    /**
     * 刷新 Token
     * POST /api/v1/auth/refresh-token
     */
    @POST("api/v1/auth/refresh-token")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): ApiResponse<TokenResponse>

    /**
     * 登出
     * POST /api/v1/auth/logout
     */
    @POST("api/v1/auth/logout")
    suspend fun logout(): ApiResponse<Void>

    // ============================================
    // 用户相关接口
    // ============================================

    /**
     * 获取当前用户信息
     * GET /api/v1/users/current
     */
    @GET("api/v1/users/current")
    suspend fun getCurrentUser(): ApiResponse<UserInfo>

    /**
     * 更新用户资料
     * PUT /api/v1/users/profile
     */
    @PUT("api/v1/users/profile")
    suspend fun updateProfile(@Body userInfo: UserInfo): ApiResponse<UserInfo>

    // ============================================
    // 疾病相关接口
    // ============================================

    /**
     * 获取热门疾病列表
     * GET /api/v1/illnesses/hot
     */
    @GET("api/v1/illnesses/hot")
    suspend fun getHotIllnesses(
        @Query("limit") limit: Int = 10
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
        @Path("id") illnessId: String
    ): ApiResponse<IllnessDto>

    /**
     * 分页获取疾病列表
     * GET /api/v1/illnesses
     */
    @GET("api/v1/illnesses")
    suspend fun getIllnesses(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("kindId") kindId: String? = null,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<IllnessDto>>

    // ============================================
    // 药品相关接口
    // ============================================

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
        @Path("id") medicineId: String
    ): ApiResponse<MedicineDto>

    /**
     * 获取疾病关联药品
     * GET /api/v1/medicines/illness/{illnessId}
     */
    @GET("api/v1/medicines/illness/{illnessId}")
    suspend fun getMedicinesByIllness(
        @Path("illnessId") illnessId: String
    ): ApiResponse<List<MedicineDto>>

    /**
     * 分页获取药品列表
     * GET /api/v1/medicines
     */
    @GET("api/v1/medicines")
    suspend fun getMedicines(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("medicineType") medicineType: String? = null,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<PageResponse<MedicineDto>>

    // ============================================
    // 浏览历史接口
    // ============================================

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
    ): ApiResponse<PageResponse<Any>>

    /**
     * 清空浏览历史
     * DELETE /api/v1/histories
     */
    @DELETE("api/v1/histories")
    suspend fun clearHistories(): ApiResponse<Void>
}
