package com.example.smart_medicine_android.util

/**
 * 统一结果封装类
 *
 * 用于封装操作结果，包含成功、失败、加载中三种状态
 */
sealed class Result<out T> {
    /**
     * 成功状态，包含数据
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * 错误状态，包含异常和错误消息
     */
    data class Error(
        val exception: Throwable? = null,
        val message: String? = null
    ) : Result<Nothing>()

    /**
     * 加载中状态
     */
    data object Loading : Result<Nothing>()

    /**
     * 判断是否成功
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * 判断是否失败
     */
    val isError: Boolean
        get() = this is Error

    /**
     * 判断是否加载中
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * 获取数据，如果失败则返回 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * 获取数据，如果失败则抛出异常
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception ?: Exception(message)
        Loading -> throw IllegalStateException("Result is still loading")
    }

    /**
     * 映射数据
     */
    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        Loading -> Loading
    }

    /**
     * 异常捕获映射
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (exception: Throwable?, message: String?) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception, message)
        Loading -> onLoading()
    }
}

/**
 * 将异常转换为 Result
 */
inline fun <T> resultOf(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e, e.message)
}
