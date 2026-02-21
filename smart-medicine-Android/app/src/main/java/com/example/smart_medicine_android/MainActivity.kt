package com.example.smart_medicine_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.smart_medicine_android.data.auth.AuthStateManager
import com.example.smart_medicine_android.data.auth.AuthEvent
import com.example.smart_medicine_android.di.AppModule
import com.example.smart_medicine_android.navigation.AppNavHost
import com.example.smart_medicine_android.navigation.Screen
import com.example.smart_medicine_android.ui.theme.SmartMedicineTheme

/**
 * 主 Activity
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化依赖注入模块
        AppModule.init(this)

        enableEdgeToEdge()
        setContent {
            SmartMedicineTheme {
                SmartMedicineApp()
            }
        }
    }
}

/**
 * 应用入口
 */
@Composable
fun SmartMedicineApp() {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 监听认证状态变化（如 token 过期）
    LaunchedEffect(Unit) {
        AuthStateManager.authEvents.collect { event ->
            when (event) {
                is AuthEvent.LogoutRequested -> {
                    android.util.Log.w("MainActivity", "Logout requested: ${event.reason}")
                    // 清除导航栈并跳转到登录页
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is AuthEvent.TokenRefreshed -> {
                    android.util.Log.d("MainActivity", "Token refreshed successfully")
                }
            }
        }
    }

    // 监听生命周期，在 onResume 时再次检查登录状态
    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                android.util.Log.d("MainActivity", "onResume: checking login status")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppNavHost(
            navController = navController,
            startDestination = Screen.Splash.route  // 从启动页开始
        )
    }
}
