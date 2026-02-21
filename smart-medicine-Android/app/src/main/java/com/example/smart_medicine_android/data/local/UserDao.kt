package com.example.smart_medicine_android.data.local

import androidx.room.*
import com.example.smart_medicine_android.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {
    /**
     * 获取用户信息（Flow）
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    /**
     * 获取当前登录用户
     */
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    /**
     * 插入或更新用户
     */
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    /**
     * 删除用户
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    /**
     * 删除所有用户
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
