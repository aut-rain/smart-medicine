package com.example.smart_medicine_android.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.HistoryRepository
import com.example.smart_medicine_android.data.network.model.HistoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 浏览历史 UI 状态
 */
data class HistoryUiState(
    val isLoading: Boolean = false,
    val histories: List<HistoryDto> = emptyList(),
    val errorMessage: String? = null,
    val hasMore: Boolean = true,
    val currentPage: Int = 1,
    val isClearing: Boolean = false
)

/**
 * 浏览历史 ViewModel
 */
class HistoryViewModel(
    private val historyRepository: HistoryRepository = com.example.smart_medicine_android.di.AppModule.historyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistories()
    }

    /**
     * 加载浏览历史
     */
    fun loadHistories(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, currentPage = 1)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            val page = if (refresh) 1 else _uiState.value.currentPage
            val result = historyRepository.getHistories(page = page, size = 20)

            result.onSuccess { pageResponse ->
                val newHistories = pageResponse.list
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    histories = if (refresh) newHistories else _uiState.value.histories + newHistories,
                    hasMore = newHistories.size == 20,
                    currentPage = page + 1,
                    errorMessage = if (newHistories.isEmpty() && _uiState.value.histories.isEmpty()) {
                        "暂无浏览历史"
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
            loadHistories()
        }
    }

    /**
     * 清空浏览历史
     */
    fun clearHistories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearing = true)

            val result = historyRepository.clearHistories()

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    histories = emptyList(),
                    currentPage = 1
                )
                // 重新加载
                loadHistories(refresh = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isClearing = false,
                    errorMessage = error.message ?: "清空失败"
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
