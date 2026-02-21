package com.example.smart_medicine_android.data.local.entity.crossRef

import androidx.room.Entity

/**
 * 疾病-药品关联表实体
 *
 * 多对多关系的中间表
 */
@Entity(
    tableName = "illness_medicine_cross_ref",
    primaryKeys = ["illnessId", "medicineId"]
)
data class IllnessMedicineCrossRef(
    val illnessId: String,
    val medicineId: String
)
