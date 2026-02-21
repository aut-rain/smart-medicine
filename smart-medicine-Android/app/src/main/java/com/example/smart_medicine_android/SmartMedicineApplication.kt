package com.example.smart_medicine_android

import android.app.Application
import com.example.smart_medicine_android.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull

/**
 * 智慧医疗 Application 类
 *
 * 使用手动依赖注入
 */
class SmartMedicineApplication : Application() {

    /**
     * Application级别的协程作用域
     * 用于初始化等不需要绑定生命周期的操作
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // 初始化手动依赖注入模块
        AppModule.init(this)

        // 预热所有lazy初始化的依赖，并加载用户数据到缓存
        preloadDependencies()
    }

    /**
     * 预热依赖 - 在后台线程中初始化所有lazy单例
     * 这样当ViewModel访问这些依赖时，它们已经初始化完成
     *
     * 同时加载userId和token到内存缓存，避免ViewModel在主线程使用runBlocking
     */
    private fun preloadDependencies() {
        applicationScope.launch {
            try {
                android.util.Log.d("SmartMedicineApp", "Starting to preload dependencies...")

                // 步骤1：触发DataStore和Repository初始化
                val initResult = runCatching {
                    // 触发userPreferences lazy初始化
                    AppModule.userPreferences

                    // 触发repository初始化（会触发API初始化）
                    AppModule.authRepository
                    AppModule.consultationRepository
                }
                android.util.Log.d("SmartMedicineApp", "Dependencies init result: ${initResult.isSuccess}")

                // 步骤2：加载userId和token到缓存（异步，不阻塞）
                withContext(Dispatchers.IO) {
                    val userId = AppModule.userPreferences.userId.firstOrNull()
                    val accessToken = AppModule.userPreferences.accessToken.firstOrNull()

                    // 更新缓存
                    AppModule.updateCachedUserId(userId)
                    AppModule.updateCachedAccessToken(accessToken)

                    android.util.Log.d("SmartMedicineApp", "User data loaded to cache: userId=$userId, hasToken=${accessToken != null}")
                }

                android.util.Log.d("SmartMedicineApp", "All dependencies preloaded and cached successfully")
            } catch (e: Exception) {
                android.util.Log.e("SmartMedicineApp", "Error preloading dependencies", e)
                // 预加载失败不影响应用运行，lazy初始化会在首次访问时重试
            }
        }
    }
}
