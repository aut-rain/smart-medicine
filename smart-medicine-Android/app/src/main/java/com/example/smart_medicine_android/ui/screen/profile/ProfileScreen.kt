package com.example.smart_medicine_android.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.smart_medicine_android.ui.theme.*

/**
 * 用户中心页面 - 现代医疗风格
 * 参考：丁香医生个人中心设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshUserInfo()
        }
    }

    // 监听登出成功
    LaunchedEffect(uiState.hasCheckedLogin, uiState.isLoggedIn, uiState.isLoading) {
        if (uiState.hasCheckedLogin && !uiState.isLoggedIn && !uiState.isLoading) {
            onLogout()
        }
    }

    SmartMedicineTheme {
        Scaffold(
            topBar = {
                ModernProfileTopBar(onBackClick = onBackClick, onSettingsClick = onSettingsClick)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundSecondary)
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading && uiState.userInfo == null) {
                    ModernLoadingContent()
                } else if (uiState.isLoggedIn) {
                    ModernProfileContent(
                        userInfo = uiState.userInfo,
                        onHistoryClick = onHistoryClick,
                        onLogoutClick = viewModel::logout,
                        onEditClick = onEditClick
                    )
                } else {
                    ModernNotLoggedInContent(
                        onLoginClick = onLogout
                    )
                }

                // 错误提示
                uiState.errorMessage?.let { error ->
                    ModernErrorSnackBar(
                        message = error,
                        onDismiss = viewModel::clearError,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

// ==================== 现代顶部栏 ====================

@Composable
private fun ModernProfileTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "个人中心",
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
        actions = {
            IconButton(onClick = onSettingsClick) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = BackgroundSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置",
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
        ),
        modifier = Modifier.shadow(4.dp)
    )
}

// ==================== 现代用户信息内容 ====================

@Composable
private fun ModernProfileContent(
    userInfo: com.example.smart_medicine_android.data.network.model.UserInfo?,
    onHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 用户头部卡片 - 点击进入编辑
        UserHeaderCard(
            userInfo = userInfo,
            onEditClick = onEditClick
        )

        // 功能菜单 - 只保留浏览历史
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 历史记录
            ModernActionCard(
                title = "浏览历史",
                subtitle = "查看浏览记录",
                icon = Icons.Default.History,
                iconTint = SuccessGreen,
                onClick = { onHistoryClick() }
            )
        }

        // 底部留白
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==================== 用户头部卡片 ====================

@Composable
private fun UserHeaderCard(
    userInfo: com.example.smart_medicine_android.data.network.model.UserInfo?,
    onEditClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onEditClick)
            .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 用户信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像 - 使用 AsyncImage 加载图片
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shadowElevation = 4.dp
                ) {
                    if (!userInfo?.imgPath.isNullOrEmpty()) {
                        coil.compose.SubcomposeAsyncImage(
                            model = coil.request.ImageRequest.Builder(context)
                                .data(userInfo?.imgPath)
                                .crossfade(true)
                                .build(),
                            contentDescription = "用户头像",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            loading = {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = PrimaryBlue
                                    )
                                }
                            },
                            error = {
                                Text(
                                    text = (userInfo?.userName ?: userInfo?.userAccount ?: "U")
                                        .firstOrNull()
                                        ?.toString()
                                        ?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (userInfo?.userName ?: userInfo?.userAccount ?: "U")
                                    .firstOrNull()
                                    ?.toString()
                                    ?.uppercase() ?: "U",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }

                // 用户名和简介
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = userInfo?.userName ?: userInfo?.userAccount ?: "未知用户",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    userInfo?.userEmail?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    // 会员标签
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentViolet.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "普通用户",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentViolet,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 渐变分隔线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.3f),
                                AccentViolet.copy(alpha = 0.3f),
                                AccentCyan.copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            // 简介
            Text(
                text = "用心守护您的健康",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

// ==================== 现代未登录内容 ====================

@Composable
private fun ModernNotLoggedInContent(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 装饰圆圈
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = BackgroundSecondary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape,
                    color = TextTertiary.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = TextTertiary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "未登录",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "登录后查看个人信息和使用更多功能",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = CardShadow),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "立即登录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ==================== 现代加载状态 ====================

@Composable
private fun ModernLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSecondary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = PrimaryBlue,
                strokeWidth = 3.dp
            )
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// ==================== 现代错误提示 ====================

@Composable
private fun ModernErrorSnackBar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        containerColor = ErrorRed,
        contentColor = Color.White,
        action = {
            TextButton(onClick = onDismiss) {
                Text("关闭", color = Color.White, fontWeight = FontWeight.Medium)
            }
        },
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(message, fontWeight = FontWeight.Medium)
            }
        }
    )
}

// ==================== 功能操作卡片 ====================

@Composable
private fun ModernActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "进入",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
