package com.example.smart_medicine_android.ui.screen.illness

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_medicine_android.ui.theme.*

/**
 * 疾病详情页面 - 现代医疗风格
 * 参考：丁香医生详情页设计
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IllnessDetailScreen(
    illnessId: Int,
    onBackClick: () -> Unit,
    onMedicineClick: (Int) -> Unit = {},
    viewModel: IllnessDetailViewModel = viewModel()
) {
    LaunchedEffect(illnessId) {
        if (viewModel.uiState.value.illness == null) {
            viewModel.loadIllnessDetail(illnessId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    SmartMedicineTheme {
        Scaffold(
            topBar = {
                ModernDetailTopBar(
                    onBackClick = onBackClick,
                    onRefreshClick = { viewModel.refresh(illnessId) }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when {
                    uiState.isLoading && uiState.illness == null -> {
                        ModernLoadingContent()
                    }

                    uiState.illness != null -> {
                        ModernIllnessDetailContent(
                            illness = uiState.illness!!,
                            onMedicineClick = onMedicineClick
                        )
                    }

                    else -> {
                        ModernErrorContent(
                            errorMessage = uiState.errorMessage ?: "加载失败",
                            onRetry = { viewModel.loadIllnessDetail(illnessId) }
                        )
                    }
                }

                uiState.errorMessage?.let { error ->
                    if (uiState.illness != null) {
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
}

// ==================== 现代顶部栏 ====================

@Composable
private fun ModernDetailTopBar(
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "疾病详情",
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
            IconButton(onClick = onRefreshClick) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = BackgroundSecondary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新",
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

// ==================== 现代疾病详情内容 ====================

@Composable
private fun ModernIllnessDetailContent(
    illness: com.example.smart_medicine_android.data.network.model.IllnessDetailDto,
    onMedicineClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(BackgroundSecondary),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 顶部标题卡片
        IllnessHeaderCard(illness = illness)

        // 信息卡片组
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 病因
            illness.includeReason?.let { reason ->
                ModernInfoCard(
                    title = "病因",
                    icon = Icons.Default.Info,
                    iconTint = PrimaryBlue,
                    content = reason
                )
            }

            // 常见症状
            illness.illnessSymptom?.let { symptom ->
                ModernInfoCard(
                    title = "常见症状",
                    icon = Icons.Default.LocalHospital,
                    iconTint = WarningOrange,
                    content = symptom
                )
            }

            // 特殊症状
            illness.specialSymptom?.let { special ->
                ModernInfoCard(
                    title = "特殊症状",
                    icon = Icons.Default.Warning,
                    iconTint = WarningOrange,
                    content = special
                )
            }

            // 疾病分类
            illness.category?.name?.let { kind ->
                ModernInfoCard(
                    title = "疾病分类",
                    icon = Icons.Default.Category,
                    iconTint = AccentViolet,
                    content = kind
                )
            }
        }

        // 关联药品卡片
        if (!illness.medicines.isNullOrEmpty()) {
            RelatedMedicinesCard(
                medicines = illness.medicines!!,
                onMedicineClick = onMedicineClick
            )
        }

        // 底部留白
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==================== 关联药品卡片 ====================

@Composable
private fun RelatedMedicinesCard(
    medicines: List<com.example.smart_medicine_android.data.network.model.MedicineSimpleDto>,
    onMedicineClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = AccentCyan.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "关联药品",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = "共${medicines.size}种",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // 药品列表
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                medicines.forEach { medicine ->
                    MedicineListItem(
                        medicine = medicine,
                        onClick = { onMedicineClick(medicine.id ?: 0) }
                    )
                }
            }
        }
    }
}

// ==================== 药品列表项 ====================

@Composable
private fun MedicineListItem(
    medicine: com.example.smart_medicine_android.data.network.model.MedicineSimpleDto,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BackgroundSecondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 药品图标
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = AccentCyan.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 药品信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = medicine.medicineName ?: "未知药品",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                medicine.medicineEffect?.let { effect ->
                    Text(
                        text = effect,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 价格和箭头
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                medicine.medicinePrice?.let { price ->
                    Text(
                        text = "¥$price",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==================== 疾病头部卡片 ====================

@Composable
private fun IllnessHeaderCard(
    illness: com.example.smart_medicine_android.data.network.model.IllnessDetailDto
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 疾病名称
            Text(
                text = illness.illnessName ?: "未知疾病",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // 快速信息标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoTag(label = "常见疾病", color = PrimaryBlue.copy(alpha = 0.1f), textColor = PrimaryBlue)
                InfoTag(label = "需就医", color = WarningOrange.copy(alpha = 0.1f), textColor = WarningOrange)
                InfoTag(label = "可治愈", color = SuccessGreen.copy(alpha = 0.1f), textColor = SuccessGreen)
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

            // 描述预览
            illness.illnessSymptom?.let { symptom ->
                Text(
                    text = "症状: $symptom",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}

// ==================== 信息标签 ====================

@Composable
private fun InfoTag(
    label: String,
    color: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== 现代信息卡片 ====================

@Composable
private fun ModernInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // 内容
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

// ==================== 现代加载状态 ====================

@Composable
private fun ModernLoadingContent() {
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

// ==================== 现代错误状态 ====================

@Composable
private fun ModernErrorContent(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSecondary)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = ErrorRed.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = ErrorRed
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "加载失败",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "重试",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
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
