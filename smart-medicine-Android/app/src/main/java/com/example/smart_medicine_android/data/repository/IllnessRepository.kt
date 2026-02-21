package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.local.IllnessDao
import com.example.smart_medicine_android.data.local.entity.IllnessEntity
import com.example.smart_medicine_android.data.local.entity.toDto
import com.example.smart_medicine_android.data.local.entity.toEntity
import com.example.smart_medicine_android.data.network.api.IllnessApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.IllnessDto
import com.example.smart_medicine_android.data.network.model.IllnessDetailDto
import com.example.smart_medicine_android.data.network.model.PageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * 疾病仓库
 */
class IllnessRepository(
    private val illnessApi: IllnessApi,
    private val illnessDao: IllnessDao
) {

    /**
     * 获取热门疾病列表
     * @param limit 数量限制
     * @param forceRefresh 是否强制从网络刷新
     */
    suspend fun getHotIllnesses(
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): Result<List<IllnessDto>> {
        return try {
            // 不强制刷新时，先返回缓存数据
            if (!forceRefresh) {
                val cached = illnessDao.getAllIllnesses().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    return Result.success(cached.take(limit).map { it.toDto() })
                }
            }

            // 从网络获取数据
            val response = illnessApi.getHotIllnesses(limit)
            if (response.isSuccess) {
                val illnesses = response.getDataOrThrow()
                // 更新缓存
                illnessDao.upsertIllnesses(illnesses.map { it.toEntity() })
                Result.success(illnesses)
            } else {
                // 网络失败，返回缓存
                val cached = illnessDao.getAllIllnesses().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.take(limit).map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 异常时返回缓存
            val cached = illnessDao.getAllIllnesses().firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.take(limit).map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 搜索疾病
     */
    suspend fun searchIllnesses(keyword: String): Result<List<IllnessDto>> {
        return try {
            val response = illnessApi.searchIllnesses(keyword)
            if (response.isSuccess) {
                val illnesses = response.getDataOrThrow()
                illnessDao.upsertIllnesses(illnesses.map { it.toEntity() })
                Result.success(illnesses)
            } else {
                val cached = illnessDao.searchIllnesses(keyword).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            val cached = illnessDao.searchIllnesses(keyword).firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取疾病详情（包含关联药品）
     */
    suspend fun getIllnessDetail(
        illnessId: Int,
        forceRefresh: Boolean = false
    ): Result<IllnessDetailDto> {
        return try {
            // 总是从网络获取最新详情（包含关联药品）
            val response = illnessApi.getIllnessDetail(illnessId)
            if (response.isSuccess) {
                val detail = response.getDataOrThrow()
                // 更新基础疾病信息到缓存
                detail.id?.let { id ->
                    illnessDao.upsertIllness(
                        IllnessEntity(
                            id = id,
                            kindId = detail.kindId,
                            kindName = detail.category?.name,
                            illnessName = detail.illnessName,
                            illnessSymptom = detail.illnessSymptom,
                            specialSymptom = detail.specialSymptom,
                            pageviews = detail.pageviews,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                Result.success(detail)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 向后兼容：返回不包含medicines的IllnessDto
    suspend fun getIllnessDetailLegacy(
        illnessId: Int,
        forceRefresh: Boolean = false
    ): Result<IllnessDto> {
        return getIllnessDetail(illnessId, forceRefresh).map { detail ->
            IllnessDto(
                id = detail.id,
                kindId = detail.kindId,
                kindName = detail.category?.name,
                illnessName = detail.illnessName,
                illnessSymptom = detail.illnessSymptom,
                specialSymptom = detail.specialSymptom,
                pageviews = detail.pageviews
            )
        }
    }

    /**
     * 分页获取疾病列表
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
                pageData.list?.let {
                    illnessDao.upsertIllnesses(it.map { it.toEntity() })
                }
                Result.success(pageData)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Flow方法供UI监听使用
    fun getAllCachedIllnesses(): Flow<List<IllnessDto>> {
        return illnessDao.getAllIllnesses().map { entities ->
            entities.map { it.toDto() }
        }
    }

    fun getCachedIllnessById(illnessId: Int): Flow<IllnessDto?> {
        return illnessDao.getIllnessById(illnessId).map { entity ->
            entity?.toDto()
        }
    }

    fun searchCachedIllnesses(keyword: String): Flow<List<IllnessDto>> {
        return illnessDao.searchIllnesses(keyword).map { entities ->
            entities.map { it.toDto() }
        }
    }
}
