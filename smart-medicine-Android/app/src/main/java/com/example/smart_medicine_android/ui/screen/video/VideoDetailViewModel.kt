package com.example.smart_medicine_android.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.HistoryRepository
import com.example.smart_medicine_android.data.repository.VideoRepository
import com.example.smart_medicine_android.data.network.model.VideoDto
import com.example.smart_medicine_android.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 视频详情 UI 状态
 */
data class VideoDetailUiState(
    val isLoading: Boolean = false,
    val video: VideoDto? = null,
    val errorMessage: String? = null
)

/**
 * 视频详情 ViewModel
 */
class VideoDetailViewModel(
    private val videoRepository: VideoRepository = AppModule.videoRepository,
    private val historyRepository: HistoryRepository = AppModule.historyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoDetailUiState())
    val uiState: StateFlow<VideoDetailUiState> = _uiState.asStateFlow()

    /**
     * 加载视频详情
     */
    fun loadVideoDetail(videoId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = videoRepository.getVideoDetail(videoId)

            result.onSuccess { video ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    video = video,
                    errorMessage = null
                )
                // 异步记录浏览历史
                video.id?.let { id ->
                    video.title?.let { title ->
                        recordViewHistory(id, title)
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
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * 异步记录浏览历史
     * 不阻塞主流程，失败也不影响用户体验
     */
    private fun recordViewHistory(videoId: Int, videoTitle: String) {
        viewModelScope.launch {
            try {
                val userId = AppModule.getUserId().toIntOrNull() ?: return@launch
                historyRepository.recordHistory(
                    userId = userId,
                    operateType = 5, // 5-观看视频
                    operateId = videoId,
                    operateName = videoTitle
                )
            } catch (e: Exception) {
                // 静默失败，不影响用户体验
                android.util.Log.e("VideoDetailViewModel", "Failed to record history", e)
            }
        }
    }
}
