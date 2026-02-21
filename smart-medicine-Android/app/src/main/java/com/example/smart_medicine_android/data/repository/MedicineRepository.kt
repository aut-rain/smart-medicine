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
 */
class MedicineRepository(
    private val medicineApi: MedicineApi,
    private val medicineDao: MedicineDao
) {

    /**
     * 获取热门药品列表
     */
    suspend fun getHotMedicines(
        limit: Int = 10,
        forceRefresh: Boolean = false
    ): Result<List<MedicineDto>> {
        return try {
            // 不强制刷新时，先返回缓存数据
            if (!forceRefresh) {
                val cached = medicineDao.getAllMedicines().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    return Result.success(cached.take(limit).map { it.toDto() })
                }
            }

            // 从网络获取数据
            val response = medicineApi.getHotMedicines(page = 1, size = limit)
            if (response.isSuccess) {
                val pageData = response.getDataOrThrow()
                val medicines = pageData.list
                // 更新缓存
                medicineDao.upsertMedicines(medicines.map { it.toEntity() })
                Result.success(medicines)
            } else {
                // 网络失败，返回缓存
                val cached = medicineDao.getAllMedicines().firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.take(limit).map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
            // 异常时返回缓存
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
     */
    suspend fun searchMedicines(keyword: String): Result<List<MedicineDto>> {
        return try {
            val response = medicineApi.searchMedicines(keyword)
            if (response.isSuccess) {
                val medicines = response.getDataOrThrow()
                medicineDao.upsertMedicines(medicines.map { it.toEntity() })
                Result.success(medicines)
            } else {
                val cached = medicineDao.searchMedicines(keyword).firstOrNull()
                if (!cached.isNullOrEmpty()) {
                    Result.success(cached.map { it.toDto() })
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
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
     */
    suspend fun getMedicineDetail(
        medicineId: Int,
        forceRefresh: Boolean = false
    ): Result<MedicineDto> {
        return try {
            val response = medicineApi.getMedicineDetail(medicineId)
            if (response.isSuccess) {
                val medicine = response.getDataOrThrow()
                medicineDao.upsertMedicine(medicine.toEntity())
                Result.success(medicine)
            } else {
                val cached = medicineDao.getMedicineById(medicineId).firstOrNull()
                if (cached != null) {
                    Result.success(cached.toDto())
                } else {
                    Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
                }
            }
        } catch (e: Exception) {
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
                pageData.list?.let {
                    medicineDao.upsertMedicines(it.map { it.toEntity() })
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
    fun getAllCachedMedicines(): Flow<List<MedicineDto>> {
        return medicineDao.getAllMedicines().map { entities ->
            entities.map { it.toDto() }
        }
    }
}
