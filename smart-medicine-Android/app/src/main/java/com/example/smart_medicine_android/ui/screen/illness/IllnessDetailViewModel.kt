package com.example.smart_medicine_android.ui.screen.illness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.HistoryRepository
import com.example.smart_medicine_android.data.repository.IllnessRepository
import com.example.smart_medicine_android.di.AppModule
import com.example.smart_medicine_android.data.network.model.IllnessDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 疾病详情页状态
 */
data class IllnessDetailUiState(
    val isLoading: Boolean = false,
    val illness: IllnessDto? = null,
    val errorMessage: String? = null
)

/**
 * 疾病详情页 ViewModel
 */
class IllnessDetailViewModel(
    private val illnessRepository: IllnessRepository = AppModule.illnessRepository,
    private val historyRepository: HistoryRepository = AppModule.historyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IllnessDetailUiState())
    val uiState: StateFlow<IllnessDetailUiState> = _uiState.asStateFlow()

    /**
     * 加载疾病详情
     */
    fun loadIllnessDetail(illnessId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = illnessRepository.getIllnessDetail(illnessId)

            result.onSuccess { illness ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    illness = illness
                )
                // 异步记录浏览历史
                illness.id?.let { id ->
                    illness.illnessName?.let { name ->
                        recordViewHistory(id, name)
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "加载失败"
                )
            }
        }
    }

    /**
     * 刷新
     */
    fun refresh(illnessId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = illnessRepository.getIllnessDetail(illnessId, forceRefresh = true)

            result.onSuccess { illness ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    illness = illness
                )
                // 异步记录浏览历史
                illness.id?.let { id ->
                    illness.illnessName?.let { name ->
                        recordViewHistory(id, name)
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "刷新失败"
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

    /**
     * 异步记录浏览历史
     * 不阻塞主流程，失败也不影响用户体验
     */
    private fun recordViewHistory(illnessId: Int, illnessName: String) {
        viewModelScope.launch {
            try {
                val userId = AppModule.getUserId().toIntOrNull() ?: return@launch
                historyRepository.recordHistory(
                    userId = userId,
                    operateType = 2, // 2-查看疾病详情
                    operateId = illnessId,
                    operateName = illnessName
                )
            } catch (e: Exception) {
                // 静默失败，不影响用户体验
                android.util.Log.e("IllnessDetailViewModel", "Failed to record history", e)
            }
        }
    }
}
