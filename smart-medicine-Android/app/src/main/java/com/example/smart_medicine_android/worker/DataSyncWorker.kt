package com.example.smart_medicine_android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smart_medicine_android.di.AppModule

/**
 * 疾病数据同步Worker
 * 定期同步疾病数据到本地缓存
 *
 * @author Smart Medicine Team
 */
class IllnessSyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("IllnessSyncWorker", "开始同步疾病数据")

            val illnessRepository = AppModule.illnessRepository

            // 同步热门疾病（限制数量，减少流量）
            val hotResult = illnessRepository.getHotIllnesses(limit = 20, forceRefresh = true)
            if (hotResult.isSuccess) {
                android.util.Log.d("IllnessSyncWorker", "同步热门疾病成功: ${hotResult.getOrNull()?.size ?: 0}条")
            }

            // 同步前100条疾病（用于本地搜索）
            val listResult = illnessRepository.getIllnesses(page = 1, size = 100)
            if (listResult.isSuccess) {
                android.util.Log.d("IllnessSyncWorker", "同步疾病列表成功: ${listResult.getOrNull()?.list?.size ?: 0}条")
            }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("IllnessSyncWorker", "同步疾病数据失败", e)
            Result.failure()
        }
    }
}

/**
 * 药品数据同步Worker
 * 定期同步药品数据到本地缓存
 *
 * @author Smart Medicine Team
 */
class MedicineSyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("MedicineSyncWorker", "开始同步药品数据")

            val medicineRepository = AppModule.medicineRepository

            // 同步热门药品
            val hotResult = medicineRepository.getHotMedicines(limit = 20, forceRefresh = true)
            if (hotResult.isSuccess) {
                android.util.Log.d("MedicineSyncWorker", "同步热门药品成功: ${hotResult.getOrNull()?.size ?: 0}条")
            }

            // 同步前100条药品（用于本地搜索）
            val listResult = medicineRepository.getMedicines(page = 1, size = 100)
            if (listResult.isSuccess) {
                android.util.Log.d("MedicineSyncWorker", "同步药品列表成功: ${listResult.getOrNull()?.list?.size ?: 0}条")
            }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("MedicineSyncWorker", "同步药品数据失败", e)
            Result.failure()
        }
    }
}

/**
 * 综合数据同步Worker
 * 顺序同步疾病和药品数据
 *
 * @author Smart Medicine Team
 */
class DataSyncWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("DataSyncWorker", "开始同步所有数据")

            val illnessRepository = AppModule.illnessRepository
            val medicineRepository = AppModule.medicineRepository

            // 顺序同步疾病数据
            try {
                illnessRepository.getHotIllnesses(limit = 50, forceRefresh = true)
                illnessRepository.getIllnesses(page = 1, size = 100)
                android.util.Log.d("DataSyncWorker", "疾病数据同步完成")
            } catch (e: Exception) {
                android.util.Log.e("DataSyncWorker", "疾病数据同步失败", e)
            }

            // 顺序同步药品数据
            try {
                medicineRepository.getHotMedicines(limit = 50, forceRefresh = true)
                medicineRepository.getMedicines(page = 1, size = 100)
                android.util.Log.d("DataSyncWorker", "药品数据同步完成")
            } catch (e: Exception) {
                android.util.Log.e("DataSyncWorker", "药品数据同步失败", e)
            }

            android.util.Log.d("DataSyncWorker", "数据同步完成")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DataSyncWorker", "数据同步异常", e)
            Result.failure()
        }
    }
}
