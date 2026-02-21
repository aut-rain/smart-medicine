package com.example.smart_medicine_android.ui.screen.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.FeedbackRepository
import com.example.smart_medicine_android.data.network.model.FeedbackDto
import com.example.smart_medicine_android.data.network.model.FeedbackSubmitRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 用户反馈列表 UI 状态
 */
data class FeedbackListUiState(
    val isLoading: Boolean = false,
    val feedbacks: List<FeedbackDto> = emptyList(),
    val errorMessage: String? = null,
    val hasMore: Boolean = true,
    val currentPage: Int = 1,
    val isDeleting: Set<Int> = emptySet()
)

/**
 * 用户反馈列表 ViewModel
 */
class FeedbackListViewModel(
    private val feedbackRepository: FeedbackRepository = com.example.smart_medicine_android.di.AppModule.feedbackRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackListUiState())
    val uiState: StateFlow<FeedbackListUiState> = _uiState.asStateFlow()

    init {
        loadFeedbacks()
    }

    /**
     * 加载反馈列表
     */
    fun loadFeedbacks(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, currentPage = 1)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            val page = if (refresh) 1 else _uiState.value.currentPage
            val result = feedbackRepository.getMyFeedbacks(page = page, size = 20)

            result.onSuccess { pageResponse ->
                val newFeedbacks = pageResponse.list
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    feedbacks = if (refresh) newFeedbacks else _uiState.value.feedbacks + newFeedbacks,
                    hasMore = newFeedbacks.size == 20,
                    currentPage = page + 1,
                    errorMessage = if (newFeedbacks.isEmpty() && _uiState.value.feedbacks.isEmpty()) {
                        "暂无反馈记录"
                    } else null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "加载失败"
                )
            }
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (!_uiState.value.isLoading && _uiState.value.hasMore) {
            loadFeedbacks()
        }
    }

    /**
     * 删除反馈
     */
    fun deleteFeedback(feedbackId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = _uiState.value.isDeleting + feedbackId
            )

            val result = feedbackRepository.deleteMyFeedback(feedbackId)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isDeleting = _uiState.value.isDeleting - feedbackId,
                    feedbacks = _uiState.value.feedbacks.filter { it.id != feedbackId }
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isDeleting = _uiState.value.isDeleting - feedbackId,
                    errorMessage = error.message ?: "删除失败"
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
