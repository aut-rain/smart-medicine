package com.example.smart_medicine_android.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.VideoRepository
import com.example.smart_medicine_android.data.network.model.VideoDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 视频页面 UI 状态
 */
data class VideoUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val videos: List<VideoDto> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val hasMore: Boolean = true,
    val currentPage: Int = 1
)

/**
 * 视频页面 ViewModel
 */
class VideoViewModel(
    private val videoRepository: VideoRepository = com.example.smart_medicine_android.di.AppModule.videoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    /**
     * 加载视频列表
     */
    fun loadVideos(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true, currentPage = 1)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            val page = if (refresh) 1 else _uiState.value.currentPage
            val result = videoRepository.getVideos(page = page, size = 10)

            result.onSuccess { pageResponse ->
                val newVideos = pageResponse.list
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    videos = if (refresh) newVideos else _uiState.value.videos + newVideos,
                    hasMore = newVideos.size == 10,
                    currentPage = page + 1,
                    errorMessage = if (newVideos.isEmpty() && _uiState.value.videos.isEmpty()) {
                        "暂无视频数据"
                    } else null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = error.message ?: "加载失败"
                )
            }
        }
    }

    /**
     * 搜索视频
     */
    fun searchVideos(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchQuery = "")
            loadVideos(refresh = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                searchQuery = query
            )

            val result = videoRepository.getVideos(page = 1, size = 20, keyword = query)

            result.onSuccess { pageResponse ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    videos = pageResponse.list,
                    hasMore = false,
                    errorMessage = if (pageResponse.list.isEmpty()) "未找到相关视频" else null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "搜索失败"
                )
            }
        }
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (!_uiState.value.isLoading && !_uiState.value.isRefreshing && _uiState.value.hasMore) {
            loadVideos()
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
