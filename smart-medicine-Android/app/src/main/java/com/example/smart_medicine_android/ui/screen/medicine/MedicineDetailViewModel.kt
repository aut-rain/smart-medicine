package com.example.smart_medicine_android.ui.screen.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.HistoryRepository
import com.example.smart_medicine_android.data.repository.MedicineRepository
import com.example.smart_medicine_android.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 药品详情 UI 状态
 */
data class MedicineDetailUiState(
    val isLoading: Boolean = false,
    val medicine: com.example.smart_medicine_android.data.network.model.MedicineDto? = null,
    val errorMessage: String? = null
)

/**
 * 药品详情 ViewModel
 */
class MedicineDetailViewModel(
    private val medicineRepository: MedicineRepository = AppModule.medicineRepository,
    private val historyRepository: HistoryRepository = AppModule.historyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineDetailUiState())
    val uiState: StateFlow<MedicineDetailUiState> = _uiState.asStateFlow()

    /**
     * 加载药品详情
     */
    fun loadMedicineDetail(medicineId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = medicineRepository.getMedicineDetail(medicineId)

            result.onSuccess { medicine ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    medicine = medicine
                )
                // 异步记录浏览历史
                medicine.id?.let { id ->
                    medicine.medicineName?.let { name ->
                        recordViewHistory(id, name)
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "加载药品详情失败"
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
    private fun recordViewHistory(medicineId: Int, medicineName: String) {
        viewModelScope.launch {
            try {
                val userId = AppModule.getUserId().toIntOrNull() ?: return@launch
                historyRepository.recordHistory(
                    userId = userId,
                    operateType = 4, // 4-查看药品详情
                    operateId = medicineId,
                    operateName = medicineName
                )
            } catch (e: Exception) {
                // 静默失败，不影响用户体验
                android.util.Log.e("MedicineDetailViewModel", "Failed to record history", e)
            }
        }
    }
}
