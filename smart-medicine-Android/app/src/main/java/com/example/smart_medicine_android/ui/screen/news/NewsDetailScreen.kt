package com.example.smart_medicine_android.ui.screen.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_medicine_android.data.network.model.NewsDetailDto
import com.example.smart_medicine_android.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope

/**
 * 资讯详情状态
 */
data class NewsDetailUiState(
    val isLoading: Boolean = true,
    val newsDetail: NewsDetailDto? = null,
    val errorMessage: String? = null
)

/**
 * 资讯详情 ViewModel
 */
class NewsDetailViewModel(
    private val newsRepository: com.example.smart_medicine_android.data.repository.NewsRepository = com.example.smart_medicine_android.di.AppModule.newsRepository,
    private val historyRepository: com.example.smart_medicine_android.data.repository.HistoryRepository = com.example.smart_medicine_android.di.AppModule.historyRepository
) : androidx.lifecycle.ViewModel() {

    private val _uiState = MutableStateFlow(NewsDetailUiState())
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    fun loadNewsDetail(newsId: Int) {
        viewModelScope.launch {
            _uiState.value = NewsDetailUiState(isLoading = true, errorMessage = null)

            val result = newsRepository.getNewsDetail(newsId)
            result.onSuccess { detail ->
                // 记录浏览历史
                detail.newsName?.let { name ->
                    recordViewHistory(newsId, name)
                }
                _uiState.value = NewsDetailUiState(
                    isLoading = false,
                    newsDetail = detail
                )
            }.onFailure { exception ->
                _uiState.value = NewsDetailUiState(
                    isLoading = false,
                    errorMessage = exception.message ?: "加载失败"
                )
            }
        }
    }

    /**
     * 记录浏览历史
     * operateType: 6 = 资讯
     */
    private fun recordViewHistory(newsId: Int, newsName: String) {
        viewModelScope.launch {
            try {
                val userId = com.example.smart_medicine_android.di.AppModule.getUserId().toIntOrNull() ?: return@launch
                historyRepository.recordHistory(
                    userId = userId,
                    operateType = 6, // 6 = 资讯
                    operateId = newsId,
                    operateName = newsName
                )
                android.util.Log.d("NewsDetailViewModel", "History recorded for news: $newsName")
            } catch (e: Exception) {
                android.util.Log.e("NewsDetailViewModel", "Failed to record history", e)
            }
        }
    }
}

/**
 * 资讯详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: Int,
    onBackClick: () -> Unit,
    viewModel: NewsDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(newsId) {
        viewModel.loadNewsDetail(newsId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("资讯详情") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "未知错误",
                            color = TextSecondary
                        )
                        Button(onClick = { viewModel.loadNewsDetail(newsId) }) {
                            Text("重试")
                        }
                    }
                }
            }
            uiState.newsDetail != null -> {
                NewsDetailContent(
                    detail = uiState.newsDetail!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun NewsDetailContent(
    detail: NewsDetailDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        // 封面图
        if (!detail.coverOssPath.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = detail.coverOssPath,
                    contentDescription = detail.newsName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 渐变遮罩
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )
            }
        }

        // 内容区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 分类标签
            detail.category?.let { category ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentCyan.copy(alpha = 0.15f),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = category,
                        color = AccentCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // 标题
            Text(
                text = detail.newsName ?: "资讯标题",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 22.sp
            )

            // 摘要
            detail.newsSummary?.let { summary ->
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }

            Divider(color = BackgroundSecondary, thickness = 1.dp)

            // 作者、浏览量、时间
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                detail.author?.let { author ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = author,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                detail.viewCount?.let { count ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$count 次浏览",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Divider(color = BackgroundSecondary, thickness = 1.dp)

            // 正文内容
            detail.markdownContent?.let { content ->
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )
            } ?: Text(
                text = "暂无内容",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            // 相关资讯
            if (!detail.relatedNews.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "相关资讯",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 18.sp
                )

                detail.relatedNews.forEach { related ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundSecondary)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 相关资讯封面
                            if (!related.coverOssPath.isNullOrEmpty()) {
                                AsyncImage(
                                    model = related.coverOssPath,
                                    contentDescription = related.newsName,
                                    modifier = Modifier
                                        .size(70.dp, 50.dp)
                                        .shadow(4.dp, RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = related.newsName ?: "相关资讯",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                related.newsSummary?.let { summary ->
                                    Text(
                                        text = summary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
