package com.example.smart_medicine_android.ui.screen.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.FileRepository
import com.example.smart_medicine_android.data.repository.UserRepository
import com.example.smart_medicine_android.data.network.model.UserInfo
import com.example.smart_medicine_android.data.network.model.UserProfileUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * 编辑个人资料 UI 状态
 */
data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userInfo: UserInfo? = null,
    val nameError: String? = null,
    val ageError: String? = null,
    val phoneError: String? = null,
    val isUploadingAvatar: Boolean = false,
    val uploadedAvatarUrl: String? = null
)

/**
 * 编辑个人资料 ViewModel
 */
class EditProfileViewModel(
    private val userRepository: UserRepository = com.example.smart_medicine_android.di.AppModule.userRepository,
    private val fileRepository: FileRepository = com.example.smart_medicine_android.di.AppModule.fileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    /**
     * 加载用户信息
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = userRepository.getCurrentUser()

            result.onSuccess { userInfo ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userInfo = userInfo
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "加载用户信息失败"
                )
            }
        }
    }

    /**
     * 更新个人资料
     */
    fun updateProfile(
        userName: String?,
        userAge: Int?,
        userSex: String?,
        userTel: String?,
        imgPath: String? = null
    ) {
        // 验证输入
        var hasError = false

        if (userName.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(nameError = "请输入姓名")
            hasError = true
        }

        if (userAge != null && (userAge < 0 || userAge > 150)) {
            _uiState.value = _uiState.value.copy(ageError = "请输入有效年龄")
            hasError = true
        }

        if (!userTel.isNullOrBlank() && !userTel.matches(Regex("^1[3-9]\\d{9}$"))) {
            _uiState.value = _uiState.value.copy(phoneError = "请输入有效手机号")
            hasError = true
        }

        if (hasError) return

        // 清除错误
        _uiState.value = _uiState.value.copy(
            nameError = null,
            ageError = null,
            phoneError = null
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val request = UserProfileUpdateRequest(
                userName = userName,
                userAge = userAge,
                userSex = userSex,
                userTel = userTel,
                imgPath = imgPath
            )

            val result = userRepository.updateProfile(request)

            result.onSuccess { updatedUserInfo ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    userInfo = updatedUserInfo,
                    uploadedAvatarUrl = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = error.message ?: "保存失败"
                )
            }
        }
    }

    /**
     * 上传头像
     * @param file 头像文件
     */
    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingAvatar = true)

            val result = fileRepository.uploadImage(file)

            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isUploadingAvatar = false,
                    uploadedAvatarUrl = response.url
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isUploadingAvatar = false,
                    errorMessage = error.message ?: "上传头像失败"
                )
            }
        }
    }

    /**
     * 清除上传的头像URL
     */
    fun clearUploadedAvatar() {
        _uiState.value = _uiState.value.copy(uploadedAvatarUrl = null)
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
    fun clearFieldErrors() {
        _uiState.value = _uiState.value.copy(
            nameError = null,
            ageError = null,
            phoneError = null
        )
    }
}
