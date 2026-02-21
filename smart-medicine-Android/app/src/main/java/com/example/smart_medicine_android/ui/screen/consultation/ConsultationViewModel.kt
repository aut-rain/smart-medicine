package com.example.smart_medicine_android.ui.screen.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_medicine_android.data.repository.ConsultationRepository
import com.example.smart_medicine_android.data.repository.StreamResult
import com.example.smart_medicine_android.di.AppModule
import com.example.smart_medicine_android.data.network.ApiConfig
import com.example.smart_medicine_android.data.local.entity.ConsultationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AI 咨询页面状态
 */
data class ConsultationUiState(
    val isLoading: Boolean = false,
    val question: String = "",
    val history: List<ConsultationEntity> = emptyList(),
    val errorMessage: String? = null,
    val showNewConversationDialog: Boolean = false  // 显示新建会话确认对话框
)

/**
 * AI 咨询页面 ViewModel
 *
 * 设计说明：
 * - userId从AppModule缓存中读取，不需要等待DataStore
 * - 缓存在Application启动时预热，ViewModel创建时立即可用
 * - 避免在ViewModel初始化时使用runBlocking，防止死锁
 */
class ConsultationViewModel(
    private val consultationRepository: ConsultationRepository = AppModule.consultationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultationUiState())
    val uiState: StateFlow<ConsultationUiState> = _uiState.asStateFlow()

    // 从缓存直接读取userId，无需等待
    private val currentUserId: String = AppModule.getUserId()

    init {
        android.util.Log.d("ConsultationViewModel", "ViewModel initialized with userId: $currentUserId")

        // 启动历史记录监听
        startHistoryListener()
    }

    /**
     * 开始监听历史记录变化
     */
    private fun startHistoryListener() {
        viewModelScope.launch {
            consultationRepository.getConsultationsByUserId(currentUserId).collect { history ->
                android.util.Log.d("ConsultationViewModel", "History updated, size: ${history.size}")
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }

    /**
     * 更新问题输入
     */
    fun onQuestionChange(question: String) {
        _uiState.value = _uiState.value.copy(question = question)
    }

    /**
     * 发起咨询（流式 SSE 模式）
     *
     * 逻辑：
     * 1. Repository 会立即插入数据库记录（用户消息 + 空 AI 回复）
     * 2. 每个 chunk 到达时，Repository 更新数据库
     * 3. Room 的 Flow 自动触发 UI 更新
     * 4. ViewModel 只需处理加载状态和错误
     */
    fun consult() {
        val question = _uiState.value.question.trim()
        if (question.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "请输入问题")
            return
        }

        android.util.Log.d("ConsultationViewModel", "Starting stream consult with userId: '$currentUserId', question: '$question'")

        // 从缓存获取accessToken（同步，不会阻塞）
        val accessToken = AppModule.getCachedAccessToken()
        val baseUrl = ApiConfig.getBaseUrl()

        android.util.Log.d("ConsultationViewModel", "baseUrl: $baseUrl, hasToken: ${accessToken != null}")

        viewModelScope.launch {
            // 设置加载状态
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                question = "",
                errorMessage = null
            )

            // 使用流式 API
            // 注意：实际的用户消息和 AI 回复会通过 history Flow 自动更新
            consultationRepository.consultStream(
                question = question,
                userId = currentUserId,
                baseUrl = baseUrl,
                accessToken = accessToken
            ).collect { result ->
                when (result) {
                    is StreamResult.Start -> {
                        android.util.Log.d("ConsultationViewModel", "Stream started, record inserted to DB")
                        // 数据库已插入记录，history Flow 会自动更新 UI
                    }
                    is StreamResult.Fragment -> {
                        // 数据库正在被更新，history Flow 会自动更新 UI
                        android.util.Log.v("ConsultationViewModel", "Fragment received: ${result.fullText.length} chars")
                    }
                    is StreamResult.Complete -> {
                        android.util.Log.d("ConsultationViewModel", "Stream completed: ${result.consultation.id}")
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                    is StreamResult.Error -> {
                        android.util.Log.e("ConsultationViewModel", "Stream error: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * 显示新建会话确认对话框
     */
    fun showNewConversationDialog() {
        _uiState.value = _uiState.value.copy(showNewConversationDialog = true)
    }

    /**
     * 隐藏新建会话确认对话框
     */
    fun hideNewConversationDialog() {
        _uiState.value = _uiState.value.copy(showNewConversationDialog = false)
    }

    /**
     * 新建会话 - 清空所有聊天记录
     */
    fun newConversation() {
        viewModelScope.launch {
            consultationRepository.clearAllConsultations()
            _uiState.value = _uiState.value.copy(
                errorMessage = null,
                showNewConversationDialog = false
            )
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
