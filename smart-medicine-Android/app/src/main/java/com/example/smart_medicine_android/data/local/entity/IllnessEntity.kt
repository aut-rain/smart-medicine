package com.example.smart_medicine_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smart_medicine_android.data.network.model.IllnessDto

/**
 * 疾病表实体
 * 匹配后端 IllnessVO 的字段
 */
@Entity(tableName = "illnesses")
data class IllnessEntity(
    @PrimaryKey
    val id: Int,  // 后端返回的是 Int 类型
    val kindId: Int? = null,
    val kindName: String? = null,
    val illnessName: String? = null,
    val illnessSymptom: String? = null,
    val specialSymptom: String? = null,
    val pageviews: Int? = null,
    // 保留本地字段用于扩展
    val cause: String? = null,
    val treatment: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 将网络模型（后端 IllnessVO）转换为实体
 */
fun IllnessDto.toEntity(): IllnessEntity = IllnessEntity(
    id = id ?: 0,
    kindId = kindId,
    kindName = kindName,
    illnessName = illnessName,
    illnessSymptom = illnessSymptom,
    specialSymptom = specialSymptom,
    pageviews = pageviews,
    cause = null,
    treatment = null,
    description = null,
    imageUrl = null
)

/**
 * 将实体转换为网络模型
 */
fun IllnessEntity.toDto(): IllnessDto = IllnessDto(
    id = id,
    kindId = kindId,
    kindName = kindName,
    illnessName = illnessName,
    illnessSymptom = illnessSymptom,
    specialSymptom = specialSymptom,
    pageviews = pageviews
)
