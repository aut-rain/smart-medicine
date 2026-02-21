package com.example.smart_medicine_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smart_medicine_android.data.network.model.UserInfo

/**
 * 用户表实体
 * 匹配后端 UserVO 的字段
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val userAccount: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userTel: String? = null,
    val userAge: Int? = null,
    val userSex: String? = null,
    val roleStatus: Int? = null,
    val imgPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 将网络模型（后端 UserVO）转换为实体
 */
fun UserInfo.toEntity(): UserEntity = UserEntity(
    id = id ?: 0,
    userAccount = userAccount,
    userName = userName,
    userEmail = userEmail,
    userTel = userTel,
    userAge = userAge,
    userSex = userSex,
    roleStatus = roleStatus,
    imgPath = imgPath
)

/**
 * 将实体转换为网络模型
 */
fun UserEntity.toUserInfo(): UserInfo = UserInfo(
    id = id,
    userAccount = userAccount,
    userName = userName,
    userEmail = userEmail,
    userTel = userTel,
    userAge = userAge,
    userSex = userSex,
    roleStatus = roleStatus,
    imgPath = imgPath
)
