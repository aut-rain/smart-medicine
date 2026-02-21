package com.example.smart_medicine_android.data.repository

import com.example.smart_medicine_android.data.network.api.FileApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.FileUploadResponse
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.webkit.MimeTypeMap

/**
 * 文件仓库
 */
class FileRepository(
    private val fileApi: FileApi
) {

    /**
     * 根据文件扩展名获取 MIME 类型
     */
    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "mp4" -> "video/mp4"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "mkv" -> "video/x-matroska"
            else -> {
                // 尝试使用系统默认的 MIME 类型映射
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                mimeType ?: "application/octet-stream"
            }
        }
    }

    /**
     * 上传文件（通用）
     * @param file 文件
     * @return 上传结果
     */
    suspend fun uploadFile(file: File): Result<FileUploadResponse> {
        return try {
            val mimeType = getMimeType(file)
            val requestBody: RequestBody = file.asRequestBody(mimeType.toMediaType())
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val response = fileApi.uploadFile(part)
            if (response.isSuccess) {
                Result.success(response.getDataOrThrow())
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 上传图片
     * @param file 图片文件
     * @return 上传结果
     */
    suspend fun uploadImage(file: File): Result<FileUploadResponse> {
        return try {
            val mimeType = getMimeType(file)
            val requestBody: RequestBody = file.asRequestBody(mimeType.toMediaType())
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val response = fileApi.uploadImage(part)
            if (response.isSuccess) {
                Result.success(response.getDataOrThrow())
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 上传视频
     * @param file 视频文件
     * @return 上传结果
     */
    suspend fun uploadVideo(file: File): Result<FileUploadResponse> {
        return try {
            val mimeType = getMimeType(file)
            val requestBody: RequestBody = file.asRequestBody(mimeType.toMediaType())
            val part: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val response = fileApi.uploadVideo(part)
            if (response.isSuccess) {
                Result.success(response.getDataOrThrow())
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 成功或失败
     */
    suspend fun deleteFile(fileUrl: String): Result<Unit> {
        return try {
            val response = fileApi.deleteFile(fileUrl)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
