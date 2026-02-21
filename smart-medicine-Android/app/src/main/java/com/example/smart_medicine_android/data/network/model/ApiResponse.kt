package com.example.smart_medicine_android.data.network.model

import kotlinx.serialization.Serializable

/**
 * 统一 API 响应格式
 *
 * 对应后端的 Result<T> 结构
 *
 * 后端返回格式：
 * {
 *   "code": "",
 *   "message": "",
 *   "data": {},
 *   "timestamp": 0,
 *   "success": true
 * }
 *
 * @param T 数据类型
 */
@Serializable
data class ApiResponse<T>(
    val code: String? = null,
    val message: String? = null,
    val data: T? = null,
    val timestamp: Long? = null,
    val success: Boolean? = null
) {
    /**
     * 判断是否成功
     * 优先使用 success 字段，如果没有则根据 code 判断
     */
    val isSuccess: Boolean
        get() = success == true || (success == null && code == "00000")

    /**
     * 获取数据，如果失败返回 null
     */
    fun getDataOrNull(): T? = if (isSuccess) data else null

    /**
     * 获取数据，如果失败抛出异常
     */
    fun getDataOrThrow(): T = data ?: throw ApiException(code ?: "UNKNOWN", message ?: "Unknown error")
}

/**
 * API 异常
 */
data class ApiException(
    val code: String,
    override val message: String
) : Exception("API Error [$code]: $message")

/**
 * 分页响应
 * 对应后端的 PageResult<T>
 */
@Serializable
data class PageResponse<T>(
    val records: List<T>? = null,
    val total: Long? = null,
    val pages: Long? = null,
    val current: Long? = null,
    val size: Long? = null
) {
    val list: List<T>
        get() = records ?: emptyList()
}

// ==================== 认证相关 ====================

/**
 * 登录请求
 * 对应后端的 LoginDTO
 */
@Serializable
data class LoginRequest(
    val userAccount: String,
    val userPwd: String
)

/**
 * 注册请求
 * 对应后端的 RegisterDTO
 */
@Serializable
data class RegisterRequest(
    val userAccount: String,
    val userPwd: String,
    val userName: String,
    val userEmail: String,
    val userAge: Int? = null,
    val userSex: String? = null,
    val userTel: String? = null,
    val emailCode: String
)

/**
 * Token 响应
 * 对应后端的 TokenVO
 */
@Serializable
data class TokenResponse(
    val userId: Int? = null,
    val userAccount: String? = null,
    val userName: String? = null,
    val roleStatus: Int? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val expiresIn: Long? = null,
    val issuedAt: String? = null
)

/**
 * 用户信息
 * 对应后端的 UserVO
 */
@Serializable
data class UserInfo(
    val id: Int? = null,
    val userAccount: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userTel: String? = null,
    val userAge: Int? = null,
    val userSex: String? = null,
    val roleStatus: Int? = null,
    val imgPath: String? = null
)

// ==================== 疾病相关 ====================

/**
 * 疾病信息
 * 对应后端的 IllnessVO
 */
@Serializable
data class IllnessDto(
    val id: Int? = null,
    val kindId: Int? = null,
    val kindName: String? = null,
    val illnessName: String? = null,
    val illnessSymptom: String? = null,
    val specialSymptom: String? = null,
    val pageviews: Int? = null
)

/**
 * 疾病详情
 * 对应后端的 IllnessDetailVO
 */
@Serializable
data class IllnessDetailDto(
    val id: Int? = null,
    val kindId: Int? = null,
    val category: IllnessCategoryVo? = null,
    val illnessName: String? = null,
    val includeReason: String? = null,
    val illnessSymptom: String? = null,
    val specialSymptom: String? = null,
    val pageviews: Int? = null,
    val medicines: List<MedicineSimpleDto>? = null
)

/**
 * 疾病分类
 */
@Serializable
data class IllnessCategoryVo(
    val id: Int? = null,
    val name: String? = null,
    val info: String? = null
)

// ==================== 药品相关 ====================

/**
 * 药品信息
 * 对应后端的 MedicineVO
 */
@Serializable
data class MedicineDto(
    val id: Int? = null,
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
    val usAge: String? = null
)

/**
 * 药品简要信息
 * 对应后端的 MedicineSimpleVO
 */
@Serializable
data class MedicineSimpleDto(
    val id: Int? = null,
    val medicineName: String? = null,
    val medicineEffect: String? = null,
    val medicinePrice: Double? = null,
    val medicineType: Int? = null
)

// ==================== 咨询相关 ====================

/**
 * AI 咨询请求
 * 对应后端的 ChatRequest
 */
@Serializable
data class ChatRequest(
    val message: String,
    val conversationId: String? = null,
    val userId: Int? = null
)

/**
 * AI 咨询响应
 * 对应后端的 ChatResponse
 */
@Serializable
data class ChatResponse(
    val content: String? = null,
    val conversationId: String? = null,
    val messageId: String? = null
)

// ==================== 视频相关 ====================

/**
 * 视频信息
 * 对应后端的 VideoVO
 */
@Serializable
data class VideoDto(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val imgPath: String? = null,
    val link: String? = null,
    val createTime: String? = null,
    val updateTime: String? = null
)

// ==================== 浏览历史相关 ====================

/**
 * 浏览历史信息
 * 对应后端的 HistoryVO
 */
@Serializable
data class HistoryDto(
    val id: Int? = null,
    val userId: Int? = null,
    val operateType: Int? = null,
    val operateTypeDesc: String? = null,
    val operateId: Int? = null,
    val operateName: String? = null,
    val createTime: String? = null
)

// ==================== 用户反馈相关 ====================

/**
 * 用户反馈信息
 * 对应后端的 FeedbackVO
 */
@Serializable
data class FeedbackDto(
    val id: Int? = null,
    val feedbackTitle: String? = null,
    val feedbackContent: String? = null,
    val contact: String? = null,
    val userId: Int? = null,
    val userAccount: String? = null,
    val createTime: String? = null
)

/**
 * 提交反馈请求
 * 对应后端的 FeedbackSubmitDTO
 */
@Serializable
data class FeedbackSubmitRequest(
    val feedbackTitle: String,
    val feedbackContent: String,
    val contact: String? = null
)

// ==================== 用户资料更新相关 ====================

/**
 * 更新用户资料请求
 * 对应后端的 UserUpdateDTO
 */
@Serializable
data class UserProfileUpdateRequest(
    val userName: String? = null,
    val userAge: Int? = null,
    val userSex: String? = null,
    val userTel: String? = null,
    val imgPath: String? = null
)

/**
 * 修改密码请求
 * 对应后端的 PasswordUpdateDTO
 */
@Serializable
data class PasswordUpdateRequest(
    val oldPassword: String,
    val newPassword: String
)

// ==================== 文件上传相关 ====================

/**
 * 文件上传响应
 * 对应后端的 FileUploadVO
 */
@Serializable
data class FileUploadResponse(
    val url: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val fileType: String? = null,
    val uploadTime: Long? = null
)
