package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.local.IllnessDao
import com.example.smart_medicine_android.data.local.entity.IllnessEntity
import com.example.smart_medicine_android.data.local.entity.toDto
import com.example.smart_medicine_android.data.local.entity.toEntity
import com.example.smart_medicine_android.data.network.api.IllnessApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.IllnessDto
import com.example.smart_medicine_android.data.network.model.PageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * 疾病仓库
 * 负责疾病数据的获取和缓存管理
 */
class IllnessRepository(
    private val illnessApi: IllnessApi,
    private val illnessDao: IllnessDao
) {

    /**
     * 获取热门疾病列表
     * @param limit 数量限制
     * @param forceRefresh 是否强制从网络刷新
     * @return 疾病列表
     */
    suspend fun getHotIllnesses(
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): Result<List<IllnessDto>> {
        return try {
            // 如果不强制刷新，先尝试从缓存获取
            if (!forceRefresh) {
                val cached = illnessDao.getHotIllnesses(limit).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    return Result.success(cached.map { it.toDto() })
                }
            }

            // 从网络获取
            val response = illnessApi.getHotIllnesses(limit)
            if (response.isSuccess) {
                val illnesses = response.getDataOrThrow()
                // 更新缓存
                illnessDao.upsertIllnesses(illnesses.map { it.toEntity() })
                Result.success(illnesses)
            } else {
                // 网络失败，尝试返回缓存
                val cached = illnessDao.getHotIllnesses(limit).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 网络异常，尝试返回缓存
            val cached = illnessDao.getHotIllnesses(limit).firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 搜索疾病
     * @param keyword 搜索关键词
     * @return 疾病列表
     */
    suspend fun searchIllnesses(keyword: String): Result<List<IllnessDto>> {
        return try {
            val response = illnessApi.searchIllnesses(keyword)
            if (response.isSuccess) {
                val illnesses = response.getDataOrThrow()
                // 更新缓存
                illnessDao.upsertIllnesses(illnesses.map { it.toEntity() })
                Result.success(illnesses)
            } else {
                // 网络失败，尝试本地搜索
                val cached = illnessDao.searchIllnesses(keyword).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 网络异常，尝试本地搜索
            val cached = illnessDao.searchIllnesses(keyword).firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取疾病详情
     * 注意：即使有缓存，也必须发起网络请求以触发后端的浏览历史记录
     *
     * @param illnessId 疾病ID
     * @param forceRefresh 是否强制从网络刷新
     * @return 疾病详情
     */
    suspend fun getIllnessDetail(
        illnessId: Int,
        forceRefresh: Boolean = false
    ): Result<IllnessDto> {
        return try {
            android.util.Log.d("IllnessRepository", "Fetching illness detail: id=$illnessId, forceRefresh=$forceRefresh")

            // 总是尝试从网络获取，以触发后端的浏览历史记录
            val response = illnessApi.getIllnessDetail(illnessId)
            if (response.isSuccess) {
                val illness = response.getDataOrThrow()
                // 更新缓存
                illnessDao.upsertIllness(illness.toEntity())
                android.util.Log.d("IllnessRepository", "Illness detail fetched from network: id=$illnessId")
                Result.success(illness)
            } else {
                // 网络失败，尝试返回缓存
                android.util.Log.w("IllnessRepository", "Network request failed: ${response.code} - ${response.message}")
                val cached = illnessDao.getIllnessById(illnessId).firstOrNull()
                if (cached != null) {
                    android.util.Log.d("IllnessRepository", "Returning cached data: id=$illnessId")
                    Result.success(cached.toDto())
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("IllnessRepository", "Exception fetching illness detail", e)
            // 网络异常，尝试返回缓存
            val cached = illnessDao.getIllnessById(illnessId).firstOrNull()
            if (cached != null) {
                Result.success(cached.toDto())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 分页获取疾病列表
     * @param page 页码
     * @param size 每页大小
     * @param kindId 分类ID
     * @param keyword 搜索关键词
     * @return 分页数据
     */
    suspend fun getIllnesses(
        page: Int = 1,
        size: Int = 10,
        kindId: Int? = null,
        keyword: String? = null
    ): Result<PageResponse<IllnessDto>> {
        return try {
            val response = illnessApi.getIllnesses(page, size, kindId, keyword)
            if (response.isSuccess) {
                val pageData = response.getDataOrThrow()
                // 更新缓存
                pageData.list?.let { list ->
                    illnessDao.upsertIllnesses(list.map { it.toEntity() })
                }
                Result.success(pageData)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取所有缓存的疾病（Flow）
     */
    fun getAllCachedIllnesses(): Flow<List<IllnessDto>> {
        return illnessDao.getAllIllnesses().map { entities ->
            entities.map { it.toDto() }
        }
    }

    /**
     * 根据ID获取缓存的疾病（Flow）
     */
    fun getCachedIllnessById(illnessId: Int): Flow<IllnessDto?> {
        return illnessDao.getIllnessById(illnessId).map { entity ->
            entity?.toDto()
        }
    }

    /**
     * 搜索缓存的疾病（Flow）
     */
    fun searchCachedIllnesses(keyword: String): Flow<List<IllnessDto>> {
        return illnessDao.searchIllnesses(keyword).map { entities ->
            entities.map { it.toDto() }
        }
    }
}
