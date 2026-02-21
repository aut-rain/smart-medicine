package com.example.smart_medicine_android.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.AuthRepository
import com.example.smart_medicine_android.data.network.model.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * 登录页面状态
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val isLoginMode: Boolean = true,  // true=登录, false=注册
    val account: String = "",
    val email: String = "",
    val password: String = "",
    val verificationCode: String = "",
    val countdown: Int = 0  // 验证码倒计时（秒）
)

/**
 * 登录页面 ViewModel
 */
class LoginViewModel(
    private val authRepository: AuthRepository = com.example.smart_medicine_android.di.AppModule.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * 更新账号输入
     */
    fun onAccountChange(value: String) {
        _uiState.value = _uiState.value.copy(account = value, errorMessage = null)
    }

    /**
     * 更新邮箱输入
     */
    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    /**
     * 更新密码输入
     */
    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    /**
     * 更新验证码输入
     */
    fun onVerificationCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(verificationCode = value, errorMessage = null)
    }

    /**
     * 切换登录/注册模式
     */
    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            isLoginMode = !_uiState.value.isLoginMode,
            errorMessage = null
        )
    }

    /**
     * 发送邮箱验证码
     */
    fun sendVerificationCode() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入邮箱地址")
            return
        }

        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "邮箱格式不正确")
            return
        }

        if (_uiState.value.countdown > 0) {
            // 倒计时中，不允许重复发送
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.sendVerificationCode(email)

            result.onSuccess {
                android.util.Log.d("LoginViewModel", "验证码发送成功")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    countdown = 60  // 开始60秒倒计时
                )
                // 启动倒计时
                startCountdown()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "发送验证码失败"
                )
            }
        }
    }

    /**
     * 启动倒计时
     */
    private fun startCountdown() {
        viewModelScope.launch {
            var remaining = _uiState.value.countdown
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.value = _uiState.value.copy(countdown = remaining)
            }
        }
    }

    /**
     * 登录
     */
    fun login() {
        val state = _uiState.value
        if (!validateLoginForm(state)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.login(state.account, state.password)

            result.onSuccess { userInfo ->
                // 登录成功后，更新AppModule的缓存
                // 这样NetworkModule的authInterceptor才能获取到最新的token
                com.example.smart_medicine_android.di.AppModule.refreshCache()

                android.util.Log.d("LoginViewModel", "Login successful, cache refreshed. userId=${userInfo.id}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "登录失败"
                )
            }
        }
    }

    /**
     * 注册
     */
    fun register() {
        val state = _uiState.value
        if (!validateRegisterForm(state)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.register(
                state.account,
                state.email,
                state.password,
                state.verificationCode
            )

            result.onSuccess { userInfo ->
                // 注册成功后，更新AppModule的缓存
                com.example.smart_medicine_android.di.AppModule.refreshCache()

                android.util.Log.d("LoginViewModel", "Register successful, cache refreshed. userId=${userInfo.id}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "注册失败"
                )
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * 验证登录表单
     */
    private fun validateLoginForm(state: LoginUiState): Boolean {
        if (state.account.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入账号")
            return false
        }
        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入密码")
            return false
        }
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "密码至少6位")
            return false
        }
        return true
    }

    /**
     * 验证注册表单
     */
    private fun validateRegisterForm(state: LoginUiState): Boolean {
        if (state.account.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入账号")
            return false
        }
        if (state.email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入邮箱")
            return false
        }
        if (!isValidEmail(state.email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "邮箱格式不正确")
            return false
        }
        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入密码")
            return false
        }
        if (state.password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "密码至少6位")
            return false
        }
        if (state.verificationCode.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入验证码")
            return false
        }
        return true
    }

    /**
     * 验证邮箱格式
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
