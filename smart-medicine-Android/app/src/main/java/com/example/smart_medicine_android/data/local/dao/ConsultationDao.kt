package com.example.smart_medicine_android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smart_medicine_android.data.local.entity.ConsultationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 咨询记录数据访问对象
 */
@Dao
interface ConsultationDao {

    /**
     * 获取所有咨询记录
     * 按时间升序排列（最早的在前，最新的在后），符合聊天界面习惯
     */
    @Query("SELECT * FROM consultations ORDER BY createdAt ASC")
    fun getAllConsultations(): Flow<List<ConsultationEntity>>

    /**
     * 根据用户ID获取咨询记录
     * 按时间升序排列（最早的在前，最新的在后），符合聊天界面习惯
     */
    @Query("SELECT * FROM consultations WHERE userId = :userId ORDER BY createdAt ASC")
    fun getConsultationsByUserId(userId: String): Flow<List<ConsultationEntity>>

    /**
     * 根据疾病ID获取咨询记录
     * 按时间升序排列（最早的在前，最新的在后），符合聊天界面习惯
     */
    @Query("SELECT * FROM consultations WHERE illnessId = :illnessId ORDER BY createdAt ASC")
    fun getConsultationsByIllnessId(illnessId: String): Flow<List<ConsultationEntity>>

    /**
     * 根据ID获取咨询记录
     */
    @Query("SELECT * FROM consultations WHERE id = :consultationId")
    fun getConsultationById(consultationId: String): Flow<ConsultationEntity?>

    /**
     * 获取待处理的咨询
     */
    @Query("SELECT * FROM consultations WHERE status = 'pending' ORDER BY createdAt ASC")
    fun getPendingConsultations(): Flow<List<ConsultationEntity>>

    /**
     * 插入咨询记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: ConsultationEntity)

    /**
     * 批量插入咨询记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultations(consultations: List<ConsultationEntity>)

    /**
     * 更新咨询记录
     */
    @Update
    suspend fun updateConsultation(consultation: ConsultationEntity)

    /**
     * 更新咨询状态和答案
     */
    @Query("UPDATE consultations SET status = :status, answer = :answer, updatedAt = :updatedAt WHERE id = :consultationId")
    suspend fun updateConsultationStatus(
        consultationId: String,
        status: String,
        answer: String?,
        updatedAt: Long = System.currentTimeMillis()
    )

    /**
     * 删除咨询记录
     */
    @Query("DELETE FROM consultations WHERE id = :consultationId")
    suspend fun deleteConsultation(consultationId: String)

    /**
     * 删除所有咨询记录
     */
    @Query("DELETE FROM consultations")
    suspend fun deleteAllConsultations()
}
