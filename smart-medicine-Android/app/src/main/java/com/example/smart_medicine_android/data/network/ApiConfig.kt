package com.example.smart_medicine_android.data.network

import com.example.smart_medicine_android.BuildConfig

/**
 * API 配置
 * 集中管理所有 API 相关的配置项
 */
object ApiConfig {

    /**
     * 网络环境枚举
     */
    enum class Environment {
        DEV,        // 开发环境
        STAGING,    // 预发布环境
        PRODUCTION  // 生产环境
    }

    /**
     * 当前环境
     */
    private val currentEnvironment: Environment
        get() = when {
            BuildConfig.DEBUG -> Environment.DEV
            else -> Environment.PRODUCTION
        }

    /**
     * 获取 BaseUrl
     */
    fun getBaseUrl(): String {
        return when (currentEnvironment) {
            Environment.DEV -> DEV_BASE_URL
            Environment.STAGING -> STAGING_BASE_URL
            Environment.PRODUCTION -> PRODUCTION_BASE_URL
        }
    }

    /**
     * 获取环境名称
     */
    fun getEnvironmentName(): String {
        return currentEnvironment.name
    }

    // ==================== BaseUrl 配置 ====================

    /**
     * 开发环境 BaseUrl
     * 10.0.2.2 是 Android 模拟器访问宿主机的特殊地址
     * 如果使用真机调试，请替换为电脑的局域网 IP（如 192.168.1.100）
     */
    private const val DEV_BASE_URL = "http://10.0.2.2:8080/"

    /**
     * 预发布环境 BaseUrl
     */
    private const val STAGING_BASE_URL = "https://staging-api.example.com/"

    /**
     * 生产环境 BaseUrl
     * 部署时请替换为实际的生产服务器地址
     */
    private const val PRODUCTION_BASE_URL = "http://your-server-ip:8080/"

    // ==================== 超时配置 ====================

    /**
     * 连接超时时间（秒）
     */
    const val CONNECT_TIMEOUT = 30L

    /**
     * 读取超时时间（秒）
     */
    const val READ_TIMEOUT = 30L

    /**
     * 写入超时时间（秒）
     */
    const val WRITE_TIMEOUT = 30L

    // ==================== API 版本 ====================

    /**
     * API 版本
     */
    const val API_VERSION = "v1"

    // ==================== 调试配置 ====================

    /**
     * 是否启用请求日志
     */
    val ENABLE_LOGGING: Boolean
        get() = BuildConfig.DEBUG

    /**
     * 是否启用 Mock 数据（用于离线测试）
     */
    const val ENABLE_MOCK = false
}
