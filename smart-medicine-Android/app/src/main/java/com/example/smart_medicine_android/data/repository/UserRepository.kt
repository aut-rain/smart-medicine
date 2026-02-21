package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.network.api.UserApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.PasswordUpdateRequest
import com.example.smart_medicine_android.data.network.model.UserInfo
import com.example.smart_medicine_android.data.network.model.UserProfileUpdateRequest

/**
 * 用户仓库
 */
class UserRepository(
    private val userApi: UserApi
) {

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    suspend fun getCurrentUser(): Result<UserInfo> {
        return try {
            val response = userApi.getCurrentUser()
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
     * 更新个人资料
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    suspend fun updateProfile(request: UserProfileUpdateRequest): Result<UserInfo> {
        return try {
            val response = userApi.updateProfile(request)
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
     * 修改密码
     * @param request 密码更新请求
     * @return 成功或失败
     */
    suspend fun updatePassword(request: PasswordUpdateRequest): Result<Unit> {
        return try {
            val response = userApi.updatePassword(request)
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
