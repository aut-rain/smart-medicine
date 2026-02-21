package com.example.smart_medicine_android.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smart_medicine_android.data.local.IllnessDao
import com.example.smart_medicine_android.data.local.MedicineDao
import com.example.smart_medicine_android.data.local.dao.ConsultationDao
import com.example.smart_medicine_android.data.local.entity.IllnessEntity
import com.example.smart_medicine_android.data.local.entity.ConsultationEntity
import com.example.smart_medicine_android.data.local.entity.MedicineEntity
import com.example.smart_medicine_android.data.local.entity.UserEntity
import com.example.smart_medicine_android.data.local.entity.crossRef.IllnessMedicineCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Room 数据库
 */
@Database(
    entities = [
        IllnessEntity::class,
        ConsultationEntity::class,
        MedicineEntity::class,
        UserEntity::class,
        IllnessMedicineCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun illnessDao(): IllnessDao
    abstract fun medicineDao(): MedicineDao
    abstract fun consultationDao(): ConsultationDao

    companion object {
        private const val DATABASE_NAME = "smart_medicine.db"

        private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * 获取数据库单例实例
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback(applicationScope))
                    .fallbackToDestructiveMigration()  // 开发阶段使用简单迁移
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * 数据库创建回调
         */
        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 数据库创建时的初始化操作
                scope.launch {
                    // 可以在这里预填充数据
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // 数据库打开时的操作
            }
        }
    }
}
