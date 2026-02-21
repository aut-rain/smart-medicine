package com.medical.smartmedicine.common.exception;

import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 业务异常
 * 用于业务逻辑层抛出的异常
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 使用错误码枚举构造
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 使用错误码枚举和自定义消息构造
     *
     * @param errorCode 错误码枚举
     * @param customMessage 自定义消息
     */
    public BusinessException(ErrorCodeEnum errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

    /**
     * 使用错误码枚举和异常原因构造
     *
     * @param errorCode 错误码枚举
     * @param cause 异常原因
     */
    public BusinessException(ErrorCodeEnum errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 兼容旧版本: 使用错误信息构造(不推荐)
     *
     * @param message 错误信息
     * @deprecated 请使用 {@link #BusinessException(ErrorCodeEnum)} 或 {@link #BusinessException(ErrorCodeEnum, String)}
     */
    @Deprecated
    public BusinessException(String message) {
        super(message);
        this.code = "500";
        this.message = message;
    }

    /**
     * 兼容旧版本: 使用错误码和错误信息构造(不推荐)
     *
     * @param code 错误码
     * @param message 错误信息
     * @deprecated 请使用 {@link #BusinessException(ErrorCodeEnum)} 或 {@link #BusinessException(ErrorCodeEnum, String)}
     */
    @Deprecated
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = String.valueOf(code);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
