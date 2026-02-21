package com.example.smart_medicine_android.ui.widget

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smart_medicine_android.data.network.model.NewsDto
import com.example.smart_medicine_android.ui.theme.*

/**
 * 资讯轮播卡片 - 叠层效果
 *
 * @param newsList 资讯列表
 * @param currentIndex 当前激活的索引
 * @param onItemClick 点击卡片回调
 */
@Composable
fun NewsCarouselCard(
    newsList: List<NewsDto>,
    currentIndex: Int,
    onItemClick: (NewsDto) -> Unit
) {
    if (newsList.isEmpty()) return

    // 获取当前、上一张、下一张资讯
    val activeNews = newsList[currentIndex]
    val prevIndex = (currentIndex - 1 + newsList.size) % newsList.size
    val nextIndex = (currentIndex + 1) % newsList.size
    val prevNews = newsList[prevIndex]
    val nextNews = newsList[nextIndex]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // 上一张卡片（左侧，只露出边缘）
        StackedNewsCard(
            news = prevNews,
            offset = -40,
            scale = 0.85f,
            alpha = 0.5f,
            onClick = { }
        )

        // 下一张卡片（右侧，只露出边缘）
        StackedNewsCard(
            news = nextNews,
            offset = 40,
            scale = 0.85f,
            alpha = 0.5f,
            onClick = { }
        )

        // 激活卡片（居中，完整显示）
        StackedNewsCard(
            news = activeNews,
            offset = 0,
            scale = 1f,
            alpha = 1f,
            onClick = { onItemClick(activeNews) },
            isActive = true
        )
    }
}

/**
 * 单张叠层卡片
 */
@Composable
private fun StackedNewsCard(
    news: NewsDto,
    offset: Int,
    scale: Float,
    alpha: Float,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    // 使用更平滑的缓动函数
    val easing = FastOutSlowInEasing
    val animationDuration = 600

    val animatedOffset by animateFloatAsState(
        targetValue = offset.toFloat(),
        animationSpec = tween(durationMillis = animationDuration, easing = easing),
        label = "offset"
    )

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = animationDuration, easing = easing),
        label = "scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = animationDuration, easing = easing),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
            .offset(x = (animatedOffset).dp)
            .scale(animatedScale)
            .alpha(animatedAlpha)
            .shadow(
                elevation = if (isActive) 18.dp else 8.dp,
                spotColor = CardShadow.copy(alpha = 0.25f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = isActive, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 封面图片
            if (!news.coverOssPath.isNullOrEmpty()) {
                AsyncImage(
                    model = news.coverOssPath,
                    contentDescription = news.newsName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 渐变遮罩 - 更深的渐变效果
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.82f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            } else {
                // 无封面时的默认背景
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    PrimaryBlue.copy(alpha = 0.92f),
                                    AccentCyan.copy(alpha = 0.88f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "健康资讯",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 内容区域
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // 分类标签
                news.category?.let { category ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AccentCyan.copy(alpha = 0.94f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // 标题
                Text(
                    text = news.newsName ?: "资讯标题",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                // 摘要
                news.newsSummary?.let { summary ->
                    Text(
                        text = summary,
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
