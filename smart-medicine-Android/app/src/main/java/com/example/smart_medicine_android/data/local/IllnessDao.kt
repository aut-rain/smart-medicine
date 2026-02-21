package com.example.smart_medicine_android.data.local

import androidx.room.*
import com.example.smart_medicine_android.data.local.entity.IllnessEntity
import kotlinx.coroutines.flow.Flow

/**
 * 疾病数据访问对象
 */
@Dao
interface IllnessDao {
    /**
     * 获取所有疾病（Flow）
     */
    @Query("SELECT * FROM illnesses ORDER BY updatedAt DESC")
    fun getAllIllnesses(): Flow<List<IllnessEntity>>

    /**
     * 根据ID获取疾病
     */
    @Query("SELECT * FROM illnesses WHERE id = :illnessId")
    fun getIllnessById(illnessId: Int): Flow<IllnessEntity?>

    /**
     * 搜索疾病
     */
    @Query("SELECT * FROM illnesses WHERE illnessName LIKE '%' || :keyword || '%' OR illnessSymptom LIKE '%' || :keyword || '%'")
    fun searchIllnesses(keyword: String): Flow<List<IllnessEntity>>

    /**
     * 获取热门疾病（按浏览量排序）
     */
    @Query("SELECT * FROM illnesses ORDER BY pageviews DESC LIMIT :limit")
    fun getHotIllnesses(limit: Int = 10): Flow<List<IllnessEntity>>

    /**
     * 插入或更新疾病
     */
    @Upsert
    suspend fun upsertIllness(illness: IllnessEntity)

    /**
     * 批量插入或更新疾病
     */
    @Upsert
    suspend fun upsertIllnesses(illnesses: List<IllnessEntity>)

    /**
     * 删除疾病
     */
    @Query("DELETE FROM illnesses WHERE id = :illnessId")
    suspend fun deleteIllness(illnessId: Int)

    /**
     * 删除所有疾病
     */
    @Query("DELETE FROM illnesses")
    suspend fun deleteAllIllnesses()
}
