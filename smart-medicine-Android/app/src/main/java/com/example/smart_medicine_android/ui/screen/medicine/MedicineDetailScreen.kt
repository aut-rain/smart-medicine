package com.example.smart_medicine_android.ui.screen.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.smart_medicine_android.ui.theme.*

/**
 * 药品详情屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: Int,
    onBackClick: () -> Unit,
    viewModel: MedicineDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 加载药品详情
    LaunchedEffect(medicineId) {
        viewModel.loadMedicineDetail(medicineId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "药品详情",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = RoundedCornerShape(12.dp),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSecondary)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingState()
            } else if (uiState.medicine != null) {
                MedicineDetailContent(medicine = uiState.medicine!!)
            } else if (uiState.errorMessage != null) {
                ErrorState(message = uiState.errorMessage!!)
            }
        }
    }
}

@Composable
private fun MedicineDetailContent(
    medicine: com.example.smart_medicine_android.data.network.model.MedicineDto
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 药品图片卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = CardShadow),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 药品图片
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (!medicine.imgPath.isNullOrEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(medicine.imgPath)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = medicine.medicineName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Medication,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }

                // 药品名称
                Text(
                    text = medicine.medicineName ?: "未知药品",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // 品牌
                medicine.medicineBrand?.let { brand ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AccentCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = brand,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 价格
                medicine.medicinePrice?.let { price ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WarningOrange.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "¥$price",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = WarningOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 药品详细信息
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 功效
            medicine.medicineEffect?.let { effect ->
                InfoCard(
                    title = "药品功效",
                    icon = Icons.Outlined.Healing,
                    iconTint = SuccessGreen,
                    content = effect
                )
            }

            // 用法用量
            medicine.usAge?.let { usage ->
                InfoCard(
                    title = "用法用量",
                    icon = Icons.Outlined.Schedule,
                    iconTint = InfoBlue,
                    content = usage
                )
            }

            // 禁忌
            medicine.taboo?.let { taboo ->
                InfoCard(
                    title = "用药禁忌",
                    icon = Icons.Outlined.Warning,
                    iconTint = ErrorRed,
                    content = taboo
                )
            }

            // 相互作用
            medicine.interaction?.let { interaction ->
                InfoCard(
                    title = "药物相互作用",
                    icon = Icons.Outlined.CompareArrows,
                    iconTint = WarningOrange,
                    content = interaction
                )
            }

            // 分类
            medicine.medicineTypeDesc?.let { typeDesc ->
                InfoCard(
                    title = "药品分类",
                    icon = Icons.Outlined.Category,
                    iconTint = PrimaryBlue,
                    content = typeDesc
                )
            }

            // 关键词
            medicine.keyword?.let { keyword ->
                InfoCard(
                    title = "关键词",
                    icon = Icons.Outlined.LocalOffer,
                    iconTint = AccentViolet,
                    content = keyword
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = CardShadow),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
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

            HorizontalDivider(color = DividerLight)

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            Text(
                text = "加载中...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = ErrorRed,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
