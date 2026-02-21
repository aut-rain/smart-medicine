package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.local.MedicineDao
import com.example.smart_medicine_android.data.local.entity.toDto
import com.example.smart_medicine_android.data.local.entity.toEntity
import com.example.smart_medicine_android.data.network.api.MedicineApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.MedicineDto
import com.example.smart_medicine_android.data.network.model.PageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * 药品仓库
 * 负责药品数据的获取和缓存管理
 */
class MedicineRepository(
    private val medicineApi: MedicineApi,
    private val medicineDao: MedicineDao
) {

    /**
     * 获取热门药品列表
     * @param limit 数量限制
     * @param forceRefresh 是否强制从网络刷新
     * @return 药品列表
     */
    suspend fun getHotMedicines(
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): Result<List<MedicineDto>> {
        return try {
            // 如果不强制刷新，先尝试从缓存获取
            if (!forceRefresh) {
                val cached = medicineDao.getAllMedicines().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    return Result.success(cached.take(limit).map { it.toDto() })
                }
            }

            // 从网络获取（使用分页接口）
            val response = medicineApi.getHotMedicines(page = 1, size = limit)
            if (response.isSuccess) {
                val pageData = response.getDataOrThrow()
                val medicines = pageData.list
                // 更新缓存
                medicineDao.upsertMedicines(medicines.map { it.toEntity() })
                Result.success(medicines)
            } else {
                // 网络失败，尝试返回缓存
                val cached = medicineDao.getAllMedicines().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.take(limit).map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 网络异常，尝试返回缓存
            val cached = medicineDao.getAllMedicines().firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.take(limit).map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 搜索药品
     * @param keyword 搜索关键词
     * @return 药品列表
     */
    suspend fun searchMedicines(keyword: String): Result<List<MedicineDto>> {
        return try {
            val response = medicineApi.searchMedicines(keyword)
            if (response.isSuccess) {
                val medicines = response.getDataOrThrow()
                // 更新缓存
                medicineDao.upsertMedicines(medicines.map { it.toEntity() })
                Result.success(medicines)
            } else {
                // 网络失败，尝试本地搜索
                val cached = medicineDao.searchMedicines(keyword).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 网络异常，尝试本地搜索
            val cached = medicineDao.searchMedicines(keyword).firstOrNull()
            if (!cached.isNullOrEmpty()) {
                Result.success(cached.map { it.toDto() })
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取药品详情
     * 注意：即使有缓存，也必须发起网络请求以触发后端的浏览历史记录
     *
     * @param medicineId 药品ID
     * @param forceRefresh 是否强制从网络刷新
     * @return 药品详情
     */
    suspend fun getMedicineDetail(
        medicineId: Int,
        forceRefresh: Boolean = false
    ): Result<MedicineDto> {
        return try {
            android.util.Log.d("MedicineRepository", "Fetching medicine detail: id=$medicineId, forceRefresh=$forceRefresh")

            // 总是尝试从网络获取，以触发后端的浏览历史记录
            val response = medicineApi.getMedicineDetail(medicineId)
            if (response.isSuccess) {
                val medicine = response.getDataOrThrow()
                // 更新缓存
                medicineDao.upsertMedicine(medicine.toEntity())
                android.util.Log.d("MedicineRepository", "Medicine detail fetched from network: id=$medicineId")
                Result.success(medicine)
            } else {
                // 网络失败，尝试返回缓存
                android.util.Log.w("MedicineRepository", "Network request failed: ${response.code} - ${response.message}")
                val cached = medicineDao.getMedicineById(medicineId).firstOrNull()
                if (cached != null) {
                    android.util.Log.d("MedicineRepository", "Returning cached data: id=$medicineId")
                    Result.success(cached.toDto())
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MedicineRepository", "Exception fetching medicine detail", e)
            // 网络异常，尝试返回缓存
            val cached = medicineDao.getMedicineById(medicineId).firstOrNull()
            if (cached != null) {
                Result.success(cached.toDto())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * 分页获取药品列表
     * @param page 页码
     * @param size 每页大小
     * @param medicineType 药品类型
     * @param keyword 搜索关键词
     * @return 分页数据
     */
    suspend fun getMedicines(
        page: Int = 1,
        size: Int = 10,
        medicineType: Int? = null,
        keyword: String? = null
    ): Result<PageResponse<MedicineDto>> {
        return try {
            val response = medicineApi.getMedicines(page, size, medicineType, keyword)
            if (response.isSuccess) {
                val pageData = response.getDataOrThrow()
                // 更新缓存
                pageData.list?.let { list ->
                    medicineDao.upsertMedicines(list.map { it.toEntity() })
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
     * 获取所有缓存的药品（Flow）
     */
    fun getAllCachedMedicines(): Flow<List<MedicineDto>> {
        return medicineDao.getAllMedicines().map { entities ->
            entities.map { it.toDto() }
        }
    }
}
