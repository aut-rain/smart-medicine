package com.example.smart_medicine_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 咨询记录实体
 */
@Entity(
    tableName = "consultations",
    foreignKeys = [
        ForeignKey(
            entity = IllnessEntity::class,
            parentColumns = ["id"],
            childColumns = ["illnessId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["illnessId"]),
        Index(value = ["createdAt"])
    ]
)
data class ConsultationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val illnessId: String? = null,
    val question: String,
    val answer: String? = null,
    val status: String,  // "pending", "completed", "failed"
    val errorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
