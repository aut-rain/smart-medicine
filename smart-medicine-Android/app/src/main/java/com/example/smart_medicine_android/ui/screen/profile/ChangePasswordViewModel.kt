package com.example.smart_medicine_android.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.UserRepository
import com.example.smart_medicine_android.data.network.model.PasswordUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 修改密码 UI 状态
 */
data class ChangePasswordUiState(
    val isChanging: Boolean = false,
    val changeSuccess: Boolean = false,
    val errorMessage: String? = null,
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
)

/**
 * 修改密码 ViewModel
 */
class ChangePasswordViewModel(
    private val userRepository: UserRepository = com.example.smart_medicine_android.di.AppModule.userRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    /**
     * 修改密码
     */
    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        // 验证输入
        var hasError = false

        if (oldPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(oldPasswordError = "请输入原密码")
            hasError = true
        }

        if (newPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(newPasswordError = "请输入新密码")
            hasError = true
        } else if (newPassword.length < 6 || newPassword.length > 20) {
            _uiState.value = _uiState.value.copy(newPasswordError = "密码长度应为6-20位")
            hasError = true
        }

        if (confirmPassword.isBlank()) {
            _uiState.value = _uiState.value.copy(confirmPasswordError = "请再次输入新密码")
            hasError = true
        } else if (newPassword != confirmPassword) {
            _uiState.value = _uiState.value.copy(confirmPasswordError = "两次输入的密码不一致")
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isChanging = true)

            val request = PasswordUpdateRequest(
                oldPassword = oldPassword,
                newPassword = newPassword
            )

            val result = userRepository.updatePassword(request)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isChanging = false,
                    changeSuccess = true
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isChanging = false,
                    errorMessage = error.message ?: "修改失败，请检查原密码是否正确"
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
     * 清除字段错误
     */
    fun clearOldPasswordError() {
        _uiState.value = _uiState.value.copy(oldPasswordError = null)
    }

    fun clearNewPasswordError() {
        _uiState.value = _uiState.value.copy(newPasswordError = null)
    }

    fun clearConfirmPasswordError() {
        _uiState.value = _uiState.value.copy(confirmPasswordError = null)
    }
}
