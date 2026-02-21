package com.example.smart_medicine_android.ui.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smart_medicine_android.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 启动页
 * 医疗主题设计，带有动画效果
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // 动画状态
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    // Logo 弹入动画
    LaunchedEffect(Unit) {
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.6f,
                stiffness = 100f
            )
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.5f,
                stiffness = 80f
            )
        )
    }

    // 文字淡入动画
    LaunchedEffect(logoScale.value) {
        if (logoScale.value > 0.8f) {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 80f
                )
            )
        }
    }

    // 脉冲动画
    LaunchedEffect(textAlpha.value) {
        if (textAlpha.value >= 1f) {
            while (true) {
                pulseScale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 50f
                    )
                )
                pulseScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.8f,
                        stiffness = 50f
                    )
                )
            }
        }
    }

    // 延迟后跳转
    LaunchedEffect(pulseScale.value) {
        if (pulseScale.value >= 1f) {
            delay(2000) // 显示2秒后跳转
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBlue,
                        PrimaryBlueLight,
                        AccentCyan
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 背景装饰圆圈
        BackgroundDecorations()

        // 主要内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo 区域
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale.value * pulseScale.value)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // 外圈光晕
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.15f)
                        )
                )

                // 医疗图标
                Icon(
                    imageVector = Icons.Rounded.LocalHospital,
                    contentDescription = "智能医疗",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White
                )

                // 心形小图标装饰
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 应用名称
            Text(
                text = "智能医疗",
                style = MaterialTheme.typography.displaySmall,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 副标题
            Text(
                text = "Smart Medicine",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 加载指示器
            CircularProgressIndicator(
                modifier = Modifier
                    .size(28.dp)
                    .alpha(textAlpha.value),
                strokeWidth = 3.dp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Slogan
            Text(
                text = "您的智能健康助手",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }

        // 底部版本信息
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "© 2026 Smart Medicine",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

/**
 * 背景装饰元素
 */
@Composable
private fun BackgroundDecorations() {
    // 左上角装饰
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.05f)
                )
        )

        // 右下角装饰
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.08f)
                )
        )

        // 左下角装饰
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .clip(CircleShape)
                .background(
                    Color.White.copy(alpha = 0.06f)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        onNavigateToHome = {}
    )
}
