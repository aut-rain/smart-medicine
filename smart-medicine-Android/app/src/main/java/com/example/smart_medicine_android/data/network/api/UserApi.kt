package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import com.example.smart_medicine_android.data.network.model.PasswordUpdateRequest
import com.example.smart_medicine_android.data.network.model.UserProfileUpdateRequest
import com.example.smart_medicine_android.data.network.model.UserInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * 用户管理相关 API
 */
interface UserApi {

    /**
     * 获取当前用户信息
     * GET /api/v1/users/current
     */
    @GET("api/v1/users/current")
    suspend fun getCurrentUser(): ApiResponse<UserInfo>

    /**
     * 更新个人资料
     * PUT /api/v1/users/profile
     */
    @PUT("api/v1/users/profile")
    suspend fun updateProfile(
        @Body request: UserProfileUpdateRequest
    ): ApiResponse<UserInfo>

    /**
     * 修改密码
     * PUT /api/v1/users/password
     */
    @PUT("api/v1/users/password")
    suspend fun updatePassword(
        @Body request: PasswordUpdateRequest
    ): ApiResponse<Void>
}
