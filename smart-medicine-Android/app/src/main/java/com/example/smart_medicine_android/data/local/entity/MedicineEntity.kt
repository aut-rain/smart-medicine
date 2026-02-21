package com.example.smart_medicine_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smart_medicine_android.data.network.model.MedicineDto

/**
 * 药品表实体
 * 匹配后端 MedicineVO 的字段
 */
@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey
    val id: Int,
    val medicineName: String? = null,
    val keyword: String? = null,
    val medicineEffect: String? = null,
    val medicineBrand: String? = null,
    val medicineType: Int? = null,
    val medicineTypeDesc: String? = null,
    val medicinePrice: Double? = null,
    val imgPath: String? = null,
    val interaction: String? = null,
    val taboo: String? = null,
    val usAge: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 将网络模型（后端 MedicineVO）转换为实体
 */
fun MedicineDto.toEntity(): MedicineEntity = MedicineEntity(
    id = id ?: 0,
    medicineName = medicineName,
    keyword = keyword,
    medicineEffect = medicineEffect,
    medicineBrand = medicineBrand,
    medicineType = medicineType,
    medicineTypeDesc = medicineTypeDesc,
    medicinePrice = medicinePrice,
    imgPath = imgPath,
    interaction = interaction,
    taboo = taboo,
    usAge = usAge
)

/**
 * 将实体转换为网络模型
 */
fun MedicineEntity.toDto(): MedicineDto = MedicineDto(
    id = id,
    medicineName = medicineName,
    keyword = keyword,
    medicineEffect = medicineEffect,
    medicineBrand = medicineBrand,
    medicineType = medicineType,
    medicineTypeDesc = medicineTypeDesc,
    medicinePrice = medicinePrice,
    imgPath = imgPath,
    interaction = interaction,
    taboo = taboo,
    usAge = usAge
)
