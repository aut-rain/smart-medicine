package com.example.smart_medicine_android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smart_medicine_android.ui.theme.*

/**
 * 底部导航栏数据项
 * 4个Tab：首页、视频、AI问诊、我的
 */
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val iconFilled: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Outlined.Home, Icons.Filled.Home, "首页")
    object Video : BottomNavItem("video", Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "视频")
    object Consultation : BottomNavItem("consultation", Icons.Outlined.ChatBubble, Icons.Filled.ChatBubble, "AI问诊")
    object Profile : BottomNavItem("profile", Icons.Outlined.Person, Icons.Filled.Person, "我的")
}

/**
 * 底部导航栏组件
 * 现代极简风格：白色背景、更大更清晰的图标、优雅的间距
 *
 * @param onHomeDoubleTap 双击首页图标时的回调（触发数据同步）
 */
@Composable
fun ModernBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onHomeDoubleTap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Video,
        BottomNavItem.Consultation,
        BottomNavItem.Profile
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), spotColor = CardShadow),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val isHome = item is BottomNavItem.Home

                BottomNavItem(
                    item = item,
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    onHomeDoubleTap = if (isHome) onHomeDoubleTap else null
                )
            }
        }
    }
}

/**
 * 单个底部导航项
 */
@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    onHomeDoubleTap: (() -> Unit)? = null
) {
    // 双击检测（仅用于首页）
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val doubleClickTimeout = 300L // 双击间隔300毫秒

    val handleClick = {
        if (onHomeDoubleTap != null) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleClickTimeout && selected) {
                // 双击首页图标且当前已经在首页
                onHomeDoubleTap()
                lastClickTime = 0L
            } else {
                onClick()
                lastClickTime = currentTime
            }
        } else {
            onClick()
        }
    }

    Column(
        modifier = Modifier
            .clickable(onClick = { handleClick() })
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 图标
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) item.iconFilled else item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(28.dp),
                tint = if (selected) PrimaryBlue else TextSecondary.copy(alpha = 0.6f)
            )
        }

        // 文字标签
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) PrimaryBlue else TextSecondary.copy(alpha = 0.6f),
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
