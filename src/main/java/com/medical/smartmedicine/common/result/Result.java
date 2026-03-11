package com.medical.smartmedicine.common.result;

import com.medical.smartmedicine.common.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果封装
 * 遵循阿里巴巴开发手册：返回结果使用泛型，避免类型转换
 * 
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message) {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应（自定义消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return Result.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 失败响应（默认系统异常）
     */
    public static <T> Result<T> fail() {
        return Result.<T>builder()
                .code(ResultCode.SYSTEM_ERROR.getCode())
                .message(ResultCode.SYSTEM_ERROR.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 失败响应（自定义消息）
     * 
     * @deprecated 请使用 {@link #fail(ResultCode)} 或 {@link #fail(ResultCode, String)}
     */
    @Deprecated
    public static <T> Result<T> fail(String message) {
        return Result.<T>builder()
                .code(ResultCode.BUSINESS_ERROR.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 失败响应（使用错误码枚举）
     *
     * @param errorCode 错误码枚举
     * @return Result
     */
    public static <T> Result<T> fail(ResultCode errorCode) {
        return Result.<T>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 失败响应（使用错误码枚举和自定义消息）
     *
     * @param errorCode 错误码枚举
     * @param customMessage 自定义消息
     * @return Result
     */
    public static <T> Result<T> fail(ResultCode errorCode, String customMessage) {
        return Result.<T>builder()
                .code(errorCode.getCode())
                .message(customMessage)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 失败响应（自定义响应码和消息）
     */
    public static <T> Result<T> fail(String code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }
}
