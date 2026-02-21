package com.example.smart_medicine_android.ui.screen.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.FeedbackRepository
import com.example.smart_medicine_android.data.network.model.FeedbackSubmitRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 提交反馈 UI 状态
 */
data class FeedbackSubmitUiState(
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false,
    val errorMessage: String? = null,
    val titleError: String? = null,
    val contentError: String? = null
)

/**
 * 提交反馈 ViewModel
 */
class FeedbackSubmitViewModel(
    private val feedbackRepository: FeedbackRepository = com.example.smart_medicine_android.di.AppModule.feedbackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackSubmitUiState())
    val uiState: StateFlow<FeedbackSubmitUiState> = _uiState.asStateFlow()

    /**
     * 提交反馈
     */
    fun submitFeedback(title: String, content: String, contact: String?) {
        // 验证输入
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(titleError = "请输入反馈标题")
            return
        }
        if (content.isBlank()) {
            _uiState.value = _uiState.value.copy(contentError = "请输入反馈内容")
            return
        }

        // 清除错误
        _uiState.value = _uiState.value.copy(titleError = null, contentError = null)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)

            val request = FeedbackSubmitRequest(
                feedbackTitle = title.trim(),
                feedbackContent = content.trim(),
                contact = contact?.trim()
            )

            val result = feedbackRepository.submitFeedback(request)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitSuccess = true
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = error.message ?: "提交失败"
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
}
