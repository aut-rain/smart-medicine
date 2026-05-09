package com.example.smart_medicine_android.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.AuthRepository
import com.example.smart_medicine_android.data.network.model.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 用户中心状态
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val hasCheckedLogin: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userInfo: UserInfo? = null,
    val errorMessage: String? = null
)

/**
 * 用户中心 ViewModel
 */
class ProfileViewModel(
    private val authRepository: AuthRepository = com.example.smart_medicine_android.di.AppModule.authRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
    }

    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val loggedIn = authRepository.isLoggedIn()

            if (loggedIn) {
                loadUserInfo()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasCheckedLogin = true,
                    isLoggedIn = false,
                    userInfo = null
                )
            }
        }
    }

    /**
     * 页面重新回到前台时刷新，避免编辑资料/服务端变更后继续展示旧状态。
     */
    fun refreshUserInfo() {
        checkLoginStatus()
    }

    /**
     * 加载用户信息
     */
    fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.getCurrentUser()

            result.onSuccess { userInfo ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasCheckedLogin = true,
                    isLoggedIn = true,
                    userInfo = userInfo
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasCheckedLogin = true,
                    errorMessage = error.message ?: "加载用户信息失败"
                )
            }
        }
    }

    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.logout()

            result.onSuccess {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    hasCheckedLogin = true,
                    isLoggedIn = false
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "登出失败"
                )
            }
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
