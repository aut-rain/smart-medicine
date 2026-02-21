package com.example.smart_medicine_android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.smart_medicine_android.data.local.IllnessDao
import com.example.smart_medicine_android.data.local.MedicineDao
import com.example.smart_medicine_android.data.local.dao.ConsultationDao
import com.example.smart_medicine_android.data.local.datastore.UserPreferences
import com.example.smart_medicine_android.data.network.api.AuthApi
import com.example.smart_medicine_android.data.network.api.IllnessApi
import com.example.smart_medicine_android.data.network.api.ConsultationApi
import com.example.smart_medicine_android.data.network.api.MedicineApi
import com.example.smart_medicine_android.data.network.api.VideoApi
import com.example.smart_medicine_android.data.network.api.HistoryApi
import com.example.smart_medicine_android.data.network.api.FeedbackApi
import com.example.smart_medicine_android.data.network.api.UserApi
import com.example.smart_medicine_android.data.network.api.FileApi
import com.example.smart_medicine_android.data.repository.AuthRepository
import com.example.smart_medicine_android.data.repository.IllnessRepository
import com.example.smart_medicine_android.data.repository.ConsultationRepository
import com.example.smart_medicine_android.data.repository.MedicineRepository
import com.example.smart_medicine_android.data.repository.VideoRepository
import com.example.smart_medicine_android.data.repository.HistoryRepository
import com.example.smart_medicine_android.data.repository.FeedbackRepository
import com.example.smart_medicine_android.data.repository.UserRepository
import com.example.smart_medicine_android.data.repository.FileRepository
import com.example.smart_medicine_android.data.network.ApiConfig
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * DataStore 扩展 - 在 AppModule 中定义
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * 手动依赖注入 - 应用级模块
 * 提供所有核心依赖项的单例访问
 */
object AppModule {

    private lateinit var applicationContext: Context

    /**
     * 在 Application.onCreate() 中调用此方法进行初始化
     */
    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // ==================== 缓存的用户数据 ====================

    /**
     * 缓存的userId - 在Application启动时预加载
     * 避免ViewModel在主线程使用runBlocking等待DataStore
     */
    @Volatile
    private var cachedUserId: String? = null

    /**
     * 缓存的accessToken - 在Application启动时预加载
     */
    @Volatile
    private var cachedAccessToken: String? = null

    /**
     * 获取缓存的userId（同步，不会阻塞）
     * @return userId，如果未加载则返回null
     */
    fun getCachedUserId(): String? = cachedUserId

    /**
     * 获取缓存的accessToken（同步，不会阻塞）
     * @return accessToken，如果未加载则返回null
     */
    fun getCachedAccessToken(): String? = cachedAccessToken

    /**
     * 更新缓存的userId
     */
    fun updateCachedUserId(userId: String?) {
        cachedUserId = userId
        android.util.Log.d("AppModule", "Cached userId updated: $userId")
    }

    /**
     * 更新缓存的accessToken
     */
    fun updateCachedAccessToken(token: String?) {
        cachedAccessToken = token
        android.util.Log.d("AppModule", "Cached accessToken updated: ${token?.take(20)}...")
    }

    /**
     * 从DataStore重新加载userId和token到缓存
     * 用于登录后更新缓存
     */
    suspend fun refreshCache() {
        withContext(Dispatchers.IO) {
            cachedUserId = userPreferences.userId.firstOrNull()
            cachedAccessToken = userPreferences.accessToken.firstOrNull()
            android.util.Log.d("AppModule", "Cache refreshed: userId=$cachedUserId")
        }
    }

    // ==================== DataStore ====================

    val userPreferences: UserPreferences by lazy {
        UserPreferences(provideDataStore())
    }

    private fun provideDataStore() = applicationContext.dataStore

    // ==================== Database ====================

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(applicationContext)
    }

    val illnessDao by lazy { database.illnessDao() }
    val medicineDao by lazy { database.medicineDao() }
    val consultationDao by lazy { database.consultationDao() }

    // ==================== Network ====================

    private val retrofitClient by lazy { provideRetrofit() }

    private fun provideRetrofit() = NetworkModule.provideRetrofit(
        baseUrl = getBaseUrl(),
        dataStore = applicationContext.dataStore
    )

    private fun getBaseUrl(): String {
        return ApiConfig.getBaseUrl()
    }

    /**
     * 设置自定义 baseUrl（用于测试或动态配置）
     * @param url 新的 baseUrl（必须以 / 结尾）
     */
    fun setCustomBaseUrl(url: String) {
        // TODO: 实现动态更新 baseUrl 的逻辑
        // 这需要重新创建 Retrofit 实例，或者使用动态 baseUrl 拦截器
    }

    /**
     * 获取当前环境信息
     */
    fun getEnvironmentInfo(): String {
        return "Environment: ${ApiConfig.getEnvironmentName()}, BaseUrl: ${getBaseUrl()}"
    }

    val authApi: AuthApi by lazy {
        retrofitClient.create(AuthApi::class.java)
    }

    val illnessApi: IllnessApi by lazy {
        retrofitClient.create(IllnessApi::class.java)
    }

    val consultationApi: ConsultationApi by lazy {
        retrofitClient.create(ConsultationApi::class.java)
    }

    val medicineApi: MedicineApi by lazy {
        retrofitClient.create(MedicineApi::class.java)
    }

    val videoApi: VideoApi by lazy {
        retrofitClient.create(VideoApi::class.java)
    }

    val historyApi: HistoryApi by lazy {
        retrofitClient.create(HistoryApi::class.java)
    }

    val feedbackApi: FeedbackApi by lazy {
        retrofitClient.create(FeedbackApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofitClient.create(UserApi::class.java)
    }

    val fileApi: FileApi by lazy {
        retrofitClient.create(FileApi::class.java)
    }

    // ==================== Repositories ====================

    val authRepository: AuthRepository by lazy {
        AuthRepository(
            authApi = authApi,
            userPreferences = userPreferences
        )
    }

    val illnessRepository: IllnessRepository by lazy {
        IllnessRepository(
            illnessApi = illnessApi,
            illnessDao = illnessDao
        )
    }

    val consultationRepository: ConsultationRepository by lazy {
        ConsultationRepository(
            consultationApi = consultationApi,
            consultationDao = consultationDao
        )
    }

    val medicineRepository: MedicineRepository by lazy {
        MedicineRepository(
            medicineApi = medicineApi,
            medicineDao = medicineDao
        )
    }

    val videoRepository: VideoRepository by lazy {
        VideoRepository(
            videoApi = videoApi
        )
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepository(
            historyApi = historyApi
        )
    }

    val feedbackRepository: FeedbackRepository by lazy {
        FeedbackRepository(
            feedbackApi = feedbackApi
        )
    }

    val userRepository: UserRepository by lazy {
        UserRepository(
            userApi = userApi
        )
    }

    val fileRepository: FileRepository by lazy {
        FileRepository(
            fileApi = fileApi
        )
    }

    // ==================== 工具方法 ====================

    /**
     * 获取当前访问 Token
     * 首先尝试从缓存获取，如果没有则同步读取
     */
    fun getAccessToken(): String? {
        // 优先使用缓存
        if (cachedAccessToken != null) {
            return cachedAccessToken
        }
        // 缓存未命中，同步读取（这种情况不应该发生，因为Application会预热）
        return runBlocking {
            userPreferences.accessToken.firstOrNull().also {
                if (it != null) cachedAccessToken = it
            }
        }
    }

    /**
     * 获取userId
     * 首先尝试从缓存获取，如果没有则返回默认值"0"
     */
    fun getUserId(): String {
        return cachedUserId.takeIf { !it.isNullOrEmpty() } ?: "0"
    }

    /**
     * 检查用户是否已登录
     */
    fun isLoggedIn(): Boolean = getAccessToken() != null

    /**
     * 清除所有缓存和用户数据（用于登出）
     */
    suspend fun clearAll() {
        userPreferences.clear()
        database.clearAllTables()
        // 清除缓存
        cachedUserId = null
        cachedAccessToken = null
    }
}
