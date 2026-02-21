package com.example.smart_medicine_android.di

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smart_medicine_android.data.auth.AuthStateManager
import com.example.smart_medicine_android.data.auth.LogoutReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.smart_medicine_android.BuildConfig
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType

/**
 * 网络模块 - 提供 Retrofit 和 OkHttpClient
 */
object NetworkModule {

    /**
     * 提供 Retrofit 实例
     * @param baseUrl API 基础 URL
     * @param dataStore DataStore 用于读取 Token
     */
    fun provideRetrofit(
        baseUrl: String,
        dataStore: DataStore<Preferences>
    ): Retrofit {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient(dataStore))
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    /**
     * 提供 OkHttpClient 实例
     * 包含日志拦截器和认证拦截器
     */
    private fun provideOkHttpClient(
        dataStore: DataStore<Preferences>
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (isDebug()) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = runBlocking {
                dataStore.data.firstOrNull()?.get(ACCESS_TOKEN_KEY)
            }

            // 添加日志，查看token是否存在
            Log.d("NetworkModule", "Request: ${originalRequest.url}, hasToken: ${token != null}")

            val newRequest = if (token != null) {
                Log.d("NetworkModule", "Adding Authorization header: Bearer ${token.take(20)}...")
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.w("NetworkModule", "No token found, sending request without Authorization")
                originalRequest
            }

            chain.proceed(newRequest)
        }

        /**
         * 401 错误拦截器 - 处理 Token 过期
         * 逻辑：
         * 1. 检测 401 响应
         * 2. 清除本地 Token（异步）
         * 3. 通知登出（异步）
         * 4. 返回原始 401 响应
         */
        val authErrorInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val response = chain.proceed(originalRequest)

            // 检查是否为 401 未授权响应
            if (response.code == 401) {
                Log.w("NetworkModule", "Received 401 response, logging out")

                // 异步处理登出逻辑，避免阻塞网络线程
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        // 清除本地 token
                        dataStore.edit { preferences ->
                            preferences.remove(ACCESS_TOKEN_KEY)
                            preferences.remove(REFRESH_TOKEN_KEY)
                            preferences.remove(USER_ID_KEY)
                            preferences.remove(USERNAME_KEY)
                        }
                        Log.d("NetworkModule", "Local tokens cleared")

                        // 通知应用层登出
                        AuthStateManager.notifyLogout(LogoutReason.TOKEN_EXPIRED)
                        Log.d("NetworkModule", "Logout event sent")
                    } catch (e: Exception) {
                        Log.e("NetworkModule", "Error during logout", e)
                    }
                }

                // 不关闭 response，直接返回原始 401 响应
                // 这样日志拦截器可以正常读取它
                return@Interceptor response
            }

            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(authErrorInterceptor)  // 401 拦截器放在最前面
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)  // 增加读取超时到120秒，AI咨询可能需要更长时间
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * JSON 配置
     */
    val Json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    /**
     * 检查是否为 Debug 模式
     */
    private fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}

// ==================== DataStore Keys ====================

val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
val USER_ID_KEY = stringPreferencesKey("user_id")
val USERNAME_KEY = stringPreferencesKey("username")
