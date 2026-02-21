package com.medical.smartmedicine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 * 遵循阿里巴巴开发手册：枚举类名带上Enum后缀，枚举成员名称需要全大写，单词间用下划线隔开
 * 
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS("200", "操作成功"),

    /**
     * 失败
     */
    FAIL("500", "操作失败"),

    /**
     * 参数校验失败
     */
    PARAM_INVALID("400", "参数校验失败"),

    /**
     * 未授权
     */
    UNAUTHORIZED("401", "未授权,请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN("403", "禁止访问"),

    /**
     * 资源未找到
     */
    NOT_FOUND("404", "资源未找到"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR("500", "系统内部错误"),

    /**
     * 业务异常
     */
    BUSINESS_ERROR("600", "业务异常");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 消息
     */
    private final String message;
}
