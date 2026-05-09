package com.example.smart_medicine_android.ui.screen.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import android.widget.VideoView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.smart_medicine_android.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 视频详情屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: Int,
    viewModel: VideoDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(videoId) {
        viewModel.loadVideoDetail(videoId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "视频详情",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = BackgroundSecondary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "返回",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.video == null -> {
                    EmptyState(uiState.errorMessage ?: "加载失败")
                }
                else -> {
                    VideoDetailContent(
                        video = uiState.video!!,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            uiState.errorMessage?.let { error ->
                if (uiState.video != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        ErrorSnackBar(
                            message = error,
                            onDismiss = viewModel::clearError
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoDetailContent(
    video: com.example.smart_medicine_android.data.network.model.VideoDto,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }  // 毫秒
    var duration by remember { mutableStateOf(0) }  // 毫秒
    var isSeeking by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var videoView: VideoView? by remember { mutableStateOf(null) }
    var isVideoPrepared by remember { mutableStateOf(false) }

    // 自动隐藏控制条
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            showControls = true
            delay(3000)
            showControls = false
        }
    }

    // 更新播放位置（只在播放时）
    LaunchedEffect(isPlaying, isVideoPrepared) {
        if (isPlaying && isVideoPrepared && videoView != null) {
            while (isPlaying) {
                delay(250)  // 每250ms更新一次
                videoView?.let { view ->
                    if (!isSeeking) {
                        currentPosition = view.currentPosition
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            // 视频播放区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color.Black)
                    .clickable {
                        if (isVideoPrepared) {
                            showControls = !showControls
                        }
                    }
            ) {
                // VideoView 始终存在（只创建一次）
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse(video.link))
                            setOnPreparedListener { mp ->
                                duration = mp.duration
                                isVideoPrepared = true
                                mp.setVolume(1f, 1f)
                            }
                            setOnCompletionListener {
                                isPlaying = false
                                currentPosition = 0
                            }
                            setOnErrorListener { _, _, _ ->
                                isPlaying = false
                                false
                            }
                        }.also { videoView = it }
                    },
                    update = { view ->
                        // 不需要在这里做任何处理
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // 生命周期处理
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_PAUSE -> {
                                if (isPlaying) {
                                    videoView?.let { view ->
                                        currentPosition = view.currentPosition
                                        view.pause()
                                    }
                                    isPlaying = false
                                }
                            }
                            Lifecycle.Event.ON_DESTROY -> {
                                videoView?.stopPlayback()
                                isVideoPrepared = false
                            }
                            else -> {}
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // 初始封面/播放按钮（未开始播放时显示）
                if (!isVideoPrepared || (currentPosition == 0 && !isPlaying)) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // 封面图片
                        if (video.imgPath != null) {
                            AsyncImage(
                                model = video.imgPath,
                                contentDescription = video.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // 播放按钮
                        Surface(
                            modifier = Modifier
                                .size(72.dp)
                                .clickable {
                                    videoView?.start()
                                    isPlaying = true
                                },
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "播放",
                                    modifier = Modifier.size(36.dp),
                                    tint = PrimaryBlue
                                )
                            }
                        }
                    }
                }

                // 播放控制条（覆盖在视频上）
                if (showControls && isVideoPrepared) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Color.Black.copy(alpha = 0.6f)
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 播放/暂停按钮
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color.White
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    IconButton(
                                        onClick = {
                                            videoView?.let { view ->
                                                if (isPlaying) {
                                                    currentPosition = view.currentPosition
                                                    view.pause()
                                                    isPlaying = false
                                                } else {
                                                    view.start()
                                                    isPlaying = true
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (isPlaying) "暂停" else "播放",
                                            tint = Color.Black,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            // 进度条和时间
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // 进度条
                                Slider(
                                    value = if (duration > 0) {
                                        currentPosition.toFloat() / duration
                                    } else 0f,
                                    onValueChange = { newValue ->
                                        isSeeking = true
                                        val newPos = (newValue * duration).toInt()
                                        currentPosition = newPos
                                        videoView?.seekTo(newPos)
                                    },
                                    onValueChangeFinished = {
                                        isSeeking = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = PrimaryBlue,
                                        activeTrackColor = PrimaryBlue,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                )

                                // 时间显示
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formatTimeMs(currentPosition),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                    Text(
                                        text = formatTimeMs(duration),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // 视频信息卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 标题
                    Text(
                        text = video.title ?: "未命名视频",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Divider(color = BackgroundSecondary)

                    // 描述
                    if (video.description != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "视频简介",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = video.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    // 创建时间
                    video.createTime?.let { time ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "发布时间",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextTertiary
                            )
                            Text(
                                text = formatTimeIso(time),
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.VideoLibrary,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextTertiary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun ErrorSnackBar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        shape = RoundedCornerShape(12.dp),
        containerColor = ErrorRed,
        contentColor = Color.White,
        action = {
            TextButton(onClick = onDismiss) {
                Text("关闭", color = Color.White)
            }
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(message)
        }
    }
}

/**
 * 格式化时间字符串
 */
private fun formatTimeIso(isoString: String): String {
    return try {
        val inputFormatters = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        for (formatter in inputFormatters) {
            runCatching {
                LocalDateTime.parse(isoString.replace("T", " "), formatter)
            }.getOrNull()?.let { dateTime ->
                return dateTime.format(outputFormatter)
            }
        }

        isoString
    } catch (e: Exception) {
        isoString
    }
}

/**
 * 格式化时间（毫秒转 MM:SS）
 */
private fun formatTimeMs(milliseconds: Int): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
