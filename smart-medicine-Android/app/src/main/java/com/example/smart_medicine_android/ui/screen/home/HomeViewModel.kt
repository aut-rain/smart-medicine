package com.example.smart_medicine_android.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.IllnessRepository
import com.example.smart_medicine_android.data.repository.MedicineRepository
import com.example.smart_medicine_android.data.repository.NewsRepository
import com.example.smart_medicine_android.data.network.model.IllnessDto
import com.example.smart_medicine_android.data.network.model.MedicineDto
import com.example.smart_medicine_android.data.network.model.NewsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 首页状态
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val illnesses: List<IllnessDto> = emptyList(),
    val medicines: List<MedicineDto> = emptyList(),
    val news: List<NewsDto> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = ""
)

/**
 * 首页 ViewModel
 * 简单的刷新逻辑：从网络获取数据 -> 更新UI -> 更新缓存
 */
class HomeViewModel(
    private val illnessRepository: IllnessRepository = com.example.smart_medicine_android.di.AppModule.illnessRepository,
    private val medicineRepository: MedicineRepository = com.example.smart_medicine_android.di.AppModule.medicineRepository,
    private val newsRepository: NewsRepository = com.example.smart_medicine_android.di.AppModule.newsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * 加载首页数据
     * 优先显示缓存，同时后台刷新
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // 先显示缓存数据（如果有）
            val cachedIllnesses = illnessRepository.getHotIllnesses(limit = 50).getOrNull()
            val cachedMedicines = medicineRepository.getHotMedicines(limit = 50).getOrNull()
            val cachedNews = newsRepository.getFeaturedNews(limit = 50).getOrNull()

            if (cachedIllnesses != null || cachedMedicines != null || cachedNews != null) {
                _uiState.value = _uiState.value.copy(
                    illnesses = cachedIllnesses ?: emptyList(),
                    medicines = cachedMedicines ?: emptyList(),
                    news = cachedNews ?: emptyList(),
                    isLoading = false
                )
            }

            // 后台从网络获取最新数据
            refreshData()
        }
    }

    /**
     * 从网络刷新数据
     * 网络数据 -> 更新UI -> 更新缓存
     */
    private suspend fun refreshData(showSuccessMessage: Boolean = false) {
        try {
            // 从网络获取最新数据
            val illnessesResult = illnessRepository.getHotIllnesses(limit = 50, forceRefresh = true)
            val medicinesResult = medicineRepository.getHotMedicines(limit = 50, forceRefresh = true)
            val newsResult = newsRepository.getFeaturedNews(limit = 50)

            val illnesses = illnessesResult.getOrNull() ?: emptyList()
            val medicines = medicinesResult.getOrNull() ?: emptyList()
            val news = newsResult.getOrNull() ?: emptyList()

            Log.d("HomeViewModel", "refreshData: news size = ${news.size}")

            val shouldShowSuccess = showSuccessMessage && (illnessesResult.isSuccess || medicinesResult.isSuccess || newsResult.isSuccess)

            // 更新UI
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                illnesses = illnesses,
                medicines = medicines,
                news = news,
                errorMessage = if (illnessesResult.isFailure && medicinesResult.isFailure && newsResult.isFailure) "刷新失败" else null,
                successMessage = if (shouldShowSuccess) "刷新成功" else null
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = "网络错误"
            )
        }
    }

    /**
     * 用户主动刷新（下拉刷新）
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null, successMessage = null)
            refreshData(showSuccessMessage = true)
        }
    }

    /**
     * 搜索疾病或药品
     */
    fun search(query: String, tabIndex: Int = 0) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            loadHomeData()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (tabIndex) {
                0 -> {
                    // 搜索疾病
                    val result = illnessRepository.searchIllnesses(query)
                    result.onSuccess { illnesses ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            illnesses = illnesses,
                            medicines = emptyList(),
                            news = emptyList(),
                            errorMessage = if (illnesses.isEmpty()) "未找到相关疾病" else null
                        )
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "搜索失败"
                        )
                    }
                }
                1 -> {
                    // 搜索药品
                    val result = medicineRepository.searchMedicines(query)
                    result.onSuccess { medicines ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            illnesses = emptyList(),
                            medicines = medicines,
                            news = emptyList(),
                            errorMessage = if (medicines.isEmpty()) "未找到相关药品" else null
                        )
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "搜索失败"
                        )
                    }
                }
                2 -> {
                    // 搜索资讯
                    val result = newsRepository.searchNews(query)
                    result.onSuccess { news ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            illnesses = emptyList(),
                            medicines = emptyList(),
                            news = news,
                            errorMessage = if (news.isEmpty()) "未找到相关资讯" else null
                        )
                    }.onFailure {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "搜索失败"
                        )
                    }
                }
            }
        }
    }

    /**
     * 清除提示信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    /**
     * 清除成功消息
     */
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    /**
     * 手动刷新
     */
    fun syncData() {
        refresh()
    }
}
