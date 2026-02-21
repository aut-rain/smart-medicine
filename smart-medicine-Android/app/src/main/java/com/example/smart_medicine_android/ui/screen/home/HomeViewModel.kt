package com.example.smart_medicine_android.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.IllnessRepository
import com.example.smart_medicine_android.data.repository.MedicineRepository
import com.example.smart_medicine_android.data.network.model.IllnessDto
import com.example.smart_medicine_android.data.network.model.MedicineDto
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
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

/**
 * 首页 ViewModel
 */
class HomeViewModel(
    private val illnessRepository: IllnessRepository = com.example.smart_medicine_android.di.AppModule.illnessRepository,
    private val medicineRepository: MedicineRepository = com.example.smart_medicine_android.di.AppModule.medicineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * 加载首页数据（热门疾病和常用药品）
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            // 并行加载疾病和药品数据
            // 疾病加载 10 条
            val illnessesResult = illnessRepository.getHotIllnesses(limit = 10)
            // 药品加载 20 条（使用分页接口）
            val medicinesResult = medicineRepository.getHotMedicines(limit = 20)

            val illnesses = illnessesResult.getOrNull() ?: emptyList()
            val medicines = medicinesResult.getOrNull() ?: emptyList()

            val errorMessage = when {
                illnessesResult.isFailure && medicinesResult.isFailure ->
                    "加载失败，请检查网络连接"
                illnessesResult.isFailure ->
                    "疾病数据加载失败"
                medicinesResult.isFailure ->
                    "药品数据加载失败"
                else -> null
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                illnesses = illnesses,
                medicines = medicines,
                errorMessage = errorMessage
            )
        }
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)

            val illnessesResult = illnessRepository.getHotIllnesses(limit = 10, forceRefresh = true)
            val medicinesResult = medicineRepository.getHotMedicines(limit = 20, forceRefresh = true)

            val illnesses = illnessesResult.getOrNull() ?: _uiState.value.illnesses
            val medicines = medicinesResult.getOrNull() ?: _uiState.value.medicines

            val errorMessage = when {
                illnessesResult.isFailure && medicinesResult.isFailure ->
                    "刷新失败，请检查网络连接"
                illnessesResult.isFailure ->
                    "疾病数据刷新失败"
                medicinesResult.isFailure ->
                    "药品数据刷新失败"
                else -> null
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isRefreshing = false,
                illnesses = illnesses,
                medicines = medicines,
                errorMessage = errorMessage
            )
        }
    }

    /**
     * 搜索疾病或药品
     */
    fun search(query: String, tabIndex: Int = 0) {
        // 先更新 searchQuery，确保 UI 立即响应
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            loadHomeData()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (tabIndex) {
                0, 2 -> {
                    // 搜索疾病
                    val illnessesResult = illnessRepository.searchIllnesses(query)

                    illnessesResult.onSuccess { illnesses ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            illnesses = illnesses,
                            medicines = emptyList(),
                            errorMessage = if (illnesses.isEmpty()) "未找到相关疾病" else null
                        )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "搜索失败"
                        )
                    }
                }
                1 -> {
                    // 搜索药品
                    val medicinesResult = medicineRepository.searchMedicines(query)

                    medicinesResult.onSuccess { medicines ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            illnesses = emptyList(),
                            medicines = medicines,
                            errorMessage = if (medicines.isEmpty()) "未找到相关药品" else null
                        )
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "搜索失败"
                        )
                    }
                }
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
