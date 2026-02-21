package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.*
import retrofit2.http.*

/**
 * 认证 API 接口
 */
interface AuthApi {

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
}
