package com.example.smart_medicine_android.data.network.api

import com.example.smart_medicine_android.data.network.model.ApiResponse
import com.example.smart_medicine_android.data.network.model.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * 文件管理相关 API
 */
interface FileApi {

    /**
     * 上传文件（通用）
     * POST /api/v1/files/upload
     */
    @Multipart
    @POST("api/v1/files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): ApiResponse<FileUploadResponse>

    /**
     * 上传图片
     * POST /api/v1/files/upload-image
     */
    @Multipart
    @POST("api/v1/files/upload-image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): ApiResponse<FileUploadResponse>

    /**
     * 上传视频
     * POST /api/v1/files/upload-video
     */
    @Multipart
    @POST("api/v1/files/upload-video")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part
    ): ApiResponse<FileUploadResponse>

    /**
     * 删除文件
     * DELETE /api/v1/files
     */
    @DELETE("api/v1/files")
    suspend fun deleteFile(
        @Query("url") fileUrl: String
    ): ApiResponse<Void>
}
