package com.example.smart_medicine_android.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * 认证状态管理器
 * 用于全局通知认证状态变化（如 token 过期、登出等）
 */
object AuthStateManager {

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    /**
     * 触发登出事件（当 token 过期或刷新失败时）
     */
    suspend fun notifyLogout(reason: LogoutReason) {
        _authEvents.emit(AuthEvent.LogoutRequested(reason))
    }

    /**
     * 触发 token 刷新成功事件
     */
    suspend fun notifyTokenRefreshed(newToken: String) {
        _authEvents.emit(AuthEvent.TokenRefreshed(newToken))
    }
}

/**
 * 认证事件
 */
sealed class AuthEvent {
    /**
     * 登出请求
     */
    data class LogoutRequested(val reason: LogoutReason) : AuthEvent()

    /**
     * Token 刷新成功
     */
    data class TokenRefreshed(val newToken: String) : AuthEvent()
}

/**
 * 登出原因
 */
enum class LogoutReason {
    /** Token 过期 */
    TOKEN_EXPIRED,

    /** Token 刷新失败 */
    REFRESH_FAILED,

    /** 用户主动登出 */
    USER_INITIATED,

    /** 网络错误 */
    NETWORK_ERROR
}
