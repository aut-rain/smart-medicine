package com.example.smart_medicine_android.data.local

import androidx.room.*
import com.example.smart_medicine_android.data.local.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow

/**
 * 药品数据访问对象
 */
@Dao
interface MedicineDao {
    /**
     * 获取所有药品（Flow）
     */
    @Query("SELECT * FROM medicines ORDER BY updatedAt DESC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    /**
     * 获取热门药品（按浏览量排序）
     */
    @Query("SELECT * FROM medicines ORDER BY updatedAt DESC LIMIT :limit")
    fun getHotMedicines(limit: Int): Flow<List<MedicineEntity>>

    /**
     * 根据ID获取药品
     */
    @Query("SELECT * FROM medicines WHERE id = :medicineId")
    fun getMedicineById(medicineId: Int): Flow<MedicineEntity?>

    /**
     * 搜索药品（按药品名称或功效搜索）
     */
    @Query("SELECT * FROM medicines WHERE medicineName LIKE '%' || :keyword || '%' OR medicineEffect LIKE '%' || :keyword || '%'")
    fun searchMedicines(keyword: String): Flow<List<MedicineEntity>>

    /**
     * 插入或更新药品
     */
    @Upsert
    suspend fun upsertMedicine(medicine: MedicineEntity)

    /**
     * 批量插入或更新药品
     */
    @Upsert
    suspend fun upsertMedicines(medicines: List<MedicineEntity>)

    /**
     * 删除药品
     */
    @Query("DELETE FROM medicines WHERE id = :medicineId")
    suspend fun deleteMedicine(medicineId: Int)

    /**
     * 删除所有药品
     */
    @Query("DELETE FROM medicines")
    suspend fun deleteAllMedicines()
}
