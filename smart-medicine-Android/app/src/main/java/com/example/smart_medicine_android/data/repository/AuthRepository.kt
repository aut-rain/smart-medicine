package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.local.datastore.UserPreferences
import com.example.smart_medicine_android.data.network.api.AuthApi
import com.example.smart_medicine_android.data.network.model.*
import kotlinx.coroutines.flow.firstOrNull

/**
 * 认证仓库
 * 负责处理用户登录、注册、Token 管理等
 */
class AuthRepository(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences
) {

    /**
     * 用户登录
     * @param account 账号（邮箱或用户名）
     * @param password 密码
     * @return 登录结果
     */
    suspend fun login(account: String, password: String): Result<UserInfo> {
        return try {
            val response = authApi.login(LoginRequest(account, password))
            if (response.isSuccess) {
                val tokenResponse = response.getDataOrThrow()
                // 保存 Token
                tokenResponse.token?.let { accessToken ->
                    tokenResponse.refreshToken?.let { refreshToken ->
                        userPreferences.saveTokens(accessToken, refreshToken)
                    }
                }
                // 构造用户信息
                val userInfo = UserInfo(
                    id = tokenResponse.userId,
                    userAccount = tokenResponse.userAccount,
                    userName = tokenResponse.userName,
                    userEmail = null,
                    userTel = null,
                    userAge = null,
                    userSex = null,
                    roleStatus = tokenResponse.roleStatus,
                    imgPath = null
                )
                // 保存用户信息
                userPreferences.saveUserInfo(
                    userId = tokenResponse.userId?.toString() ?: "",
                    username = tokenResponse.userAccount ?: "",
                    email = tokenResponse.userName ?: ""
                )
                Result.success(userInfo)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 用户注册
     * @param account 账号
     * @param email 邮箱
     * @param password 密码
     * @param code 验证码
     * @return 注册结果
     */
    suspend fun register(
        account: String,
        email: String,
        password: String,
        code: String? = null
    ): Result<UserInfo> {
        return try {
            val response = authApi.register(
                RegisterRequest(
                    userAccount = account,
                    userPwd = password,
                    userName = account,
                    userEmail = email,
                    emailCode = code ?: ""
                )
            )
            if (response.isSuccess) {
                val tokenResponse = response.getDataOrThrow()
                // 保存 Token
                tokenResponse.token?.let { accessToken ->
                    tokenResponse.refreshToken?.let { refreshToken ->
                        userPreferences.saveTokens(accessToken, refreshToken)
                    }
                }
                // 构造用户信息
                val userInfo = UserInfo(
                    id = tokenResponse.userId,
                    userAccount = tokenResponse.userAccount,
                    userName = tokenResponse.userName,
                    userEmail = null,
                    userTel = null,
                    userAge = null,
                    userSex = null,
                    roleStatus = tokenResponse.roleStatus,
                    imgPath = null
                )
                // 保存用户信息
                userPreferences.saveUserInfo(
                    userId = tokenResponse.userId?.toString() ?: "",
                    username = tokenResponse.userAccount ?: "",
                    email = tokenResponse.userName ?: ""
                )
                Result.success(userInfo)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 发送验证码
     * @param email 邮箱地址
     * @return 发送结果
     */
    suspend fun sendVerificationCode(email: String): Result<Unit> {
        return try {
            android.util.Log.d("AuthRepository", "发送邮箱验证码: email=$email")
            val response = authApi.sendEmailCode(email)
            if (response.isSuccess) {
                android.util.Log.d("AuthRepository", "验证码发送成功")
                Result.success(Unit)
            } else {
                val error = ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error")
                android.util.Log.e("AuthRepository", "验证码发送失败: $error")
                Result.failure(error)
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "发送验证码异常", e)
            Result.failure(e)
        }
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            // 无论 API 调用成功与否，都清除本地数据
            userPreferences.clear()
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            // 即使网络请求失败，也清除本地数据
            userPreferences.clear()
            Result.failure(e)
        }
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    suspend fun getCurrentUser(): Result<UserInfo> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.isSuccess) {
                val userInfo = response.getDataOrThrow()
                // 更新本地用户信息
                userPreferences.saveUserInfo(
                    userId = userInfo.id?.toString() ?: "",
                    username = userInfo.userAccount ?: "",
                    email = userInfo.userEmail ?: ""
                )
                Result.success(userInfo)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新用户资料
     * @param userInfo 用户信息
     * @return 更新结果
     */
    suspend fun updateProfile(userInfo: UserInfo): Result<UserInfo> {
        return try {
            val response = authApi.updateProfile(userInfo)
            if (response.isSuccess) {
                val updatedUserInfo = response.getDataOrThrow()
                // 更新本地用户信息
                userPreferences.saveUserInfo(
                    userId = updatedUserInfo.id?.toString() ?: "",
                    username = updatedUserInfo.userAccount ?: "",
                    email = updatedUserInfo.userEmail ?: ""
                )
                Result.success(updatedUserInfo)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return userPreferences.accessToken.firstOrNull() != null
    }

    /**
     * 获取当前 Token
     * @return Token
     */
    suspend fun getToken(): String? {
        return userPreferences.accessToken.firstOrNull()
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    suspend fun getUserId(): String? {
        return userPreferences.userId.firstOrNull()
    }

    /**
     * 刷新 Token
     * @return 新的 Token
     */
    suspend fun refreshToken(): Result<String> {
        return try {
            val currentRefreshToken = userPreferences.refreshToken.firstOrNull()
            if (currentRefreshToken == null) {
                return Result.failure(Exception("未登录"))
            }
            val response = authApi.refreshToken(currentRefreshToken)
            if (response.isSuccess) {
                val tokenResponse = response.getDataOrThrow()
                tokenResponse.token?.let { accessToken ->
                    tokenResponse.refreshToken?.let { refreshToken ->
                        userPreferences.saveTokens(accessToken, refreshToken)
                    }
                }
                Result.success(tokenResponse.token ?: "Unknown")
            } else {
                // Token 刷新失败，清除本地数据
                userPreferences.clear()
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
