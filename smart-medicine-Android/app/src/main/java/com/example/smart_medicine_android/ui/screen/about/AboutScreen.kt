package com.example.smart_medicine_android.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_medicine_android.BuildConfig
import com.example.smart_medicine_android.ui.theme.*

/**
 * 关于页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "关于我们",
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
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(BackgroundSecondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Logo 和名称
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = PrimaryBlue.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.LocalHospital,
                                contentDescription = "智能医疗",
                                modifier = Modifier.size(50.dp),
                                tint = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // App 名称
                    Text(
                        text = "智能医疗",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 英文名称
                    Text(
                        text = "Smart Medicine",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 版本号
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BackgroundSecondary
                    ) {
                        Text(
                            text = "版本 ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slogan
                    Text(
                        text = "您的智能健康助手",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = TextTertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 功能介绍
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "产品功能",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FeatureItem(
                        icon = Icons.Rounded.Search,
                        title = "疾病查询",
                        description = "快速查找疾病信息，了解症状和治疗方案"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerLight
                    )

                    FeatureItem(
                        icon = Icons.Rounded.Medication,
                        title = "药品信息",
                        description = "详细的药品说明，包括用法、用量和注意事项"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerLight
                    )

                    FeatureItem(
                        icon = Icons.Rounded.SmartToy,
                        title = "AI 智能咨询",
                        description = "基于 AI 的健康咨询服务，解答您的健康疑问"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerLight
                    )

                    FeatureItem(
                        icon = Icons.Rounded.VideoLibrary,
                        title = "科普视频",
                        description = "丰富的健康科普视频，传播健康知识"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 联系我们
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "联系我们",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContactItem(
                        icon = Icons.Rounded.Email,
                        title = "邮箱",
                        value = "毕设,别骂了,别骂了"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerLight
                    )

                    ContactItem(
                        icon = Icons.Rounded.Language,
                        title = "官网",
                        value = "毕设,别骂了,别骂了"
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerLight
                    )

                    ContactItem(
                        icon = Icons.Rounded.LocationOn,
                        title = "地址",
                        value = "中国·安徽"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 版权信息
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "© 2026 Smart Medicine",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = TextTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "All rights reserved",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = TextDisabled
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 功能介绍项
 */
@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = PrimaryBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = PrimaryBlue
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

/**
 * 联系方式项
 */
@Composable
private fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = AccentCyan.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = AccentCyan
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = TextTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}
