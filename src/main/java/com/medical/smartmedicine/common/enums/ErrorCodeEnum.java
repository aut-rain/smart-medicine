package com.medical.smartmedicine.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码枚举
 * 
 * 错误码规范:
 * - 1xxx: 通用错误
 * - 2xxx: 认证授权相关
 * - 3xxx: 用户相关
 * - 4xxx: 疾病相关
 * - 5xxx: 药品相关
 * - 6xxx: 反馈相关
 * - 7xxx: 历史记录相关
 * - 8xxx: 文件相关
 * - 9xxx: AI聊天相关
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    // ==================== 通用错误 1xxx ====================
    /**
     * 成功
     */
    SUCCESS("200", "操作成功"),
    
    /**
     * 系统异常
     */
    SYSTEM_ERROR("500", "系统异常,请联系管理员"),
    
    /**
     * 参数校验失败
     */
    PARAM_INVALID("1001", "参数校验失败"),
    
    /**
     * 请求方法不支持
     */
    METHOD_NOT_SUPPORTED("1002", "请求方法不支持"),
    
    /**
     * 资源不存在
     */
    RESOURCE_NOT_FOUND("1003", "请求的资源不存在"),
    
    /**
     * 业务异常
     */
    BUSINESS_ERROR("1004", "业务处理失败"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR("1005", "参数错误"),

    // ==================== 认证授权相关 2xxx ====================
    /**
     * 未登录或登录已过期
     */
    UNAUTHORIZED("2001", "未登录或登录已过期,请先登录"),
    
    /**
     * 无访问权限
     */
    FORBIDDEN("2002", "无访问权限"),
    
    /**
     * 用户不存在
     */
    USER_NOT_FOUND("2003", "用户不存在"),
    
    /**
     * 密码错误
     */
    PASSWORD_ERROR("2004", "密码错误"),
    
    /**
     * 账号已存在
     */
    ACCOUNT_EXISTS("2005", "账号已存在"),
    
    /**
     * 邮箱已被注册
     */
    EMAIL_EXISTS("2006", "邮箱已被注册"),
    
    /**
     * 邮箱验证码错误或已过期
     */
    EMAIL_CODE_INVALID("2007", "邮箱验证码错误或已过期"),
    
    /**
     * Token刷新失败
     */
    TOKEN_REFRESH_FAILED("2008", "刷新Token失败,请重新登录"),

    // ==================== 用户相关 3xxx ====================
    /**
     * 手机号已被使用
     */
    PHONE_EXISTS("3001", "手机号已被使用"),
    
    /**
     * 旧密码错误
     */
    OLD_PASSWORD_ERROR("3002", "旧密码错误"),
    
    /**
     * 不允许删除管理员账号
     */
    ADMIN_DELETE_FORBIDDEN("3003", "不允许删除管理员账号"),

    // ==================== 疾病相关 4xxx ====================
    /**
     * 疾病不存在
     */
    ILLNESS_NOT_FOUND("4001", "疾病不存在"),

    // ==================== 药品相关 5xxx ====================
    /**
     * 药品不存在
     */
    MEDICINE_NOT_FOUND("5001", "药品不存在"),

    // ==================== 反馈相关 6xxx ====================
    /**
     * 反馈不存在
     */
    FEEDBACK_NOT_FOUND("6001", "反馈不存在"),

    /**
     * 无权限修改此反馈
     */
    PERMISSION_DENIED("6002", "无权限修改此反馈"),

    // ==================== 历史记录相关 7xxx ====================
    /**
     * 历史记录不存在
     */
    HISTORY_NOT_FOUND("7001", "历史记录不存在"),

    // ==================== 文件相关 8xxx ====================
    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED("8001", "文件上传失败"),
    
    /**
     * 文件类型不支持
     */
    FILE_TYPE_NOT_SUPPORTED("8002", "文件类型不支持"),
    
    /**
     * 文件大小超出限制
     */
    FILE_SIZE_EXCEEDED("8003", "文件大小超出限制"),
    
    /**
     * 文件不存在
     */
    FILE_NOT_FOUND("8004", "文件不存在"),
    
    /**
     * 文件为空
     */
    FILE_EMPTY("8005", "文件为空"),
    
    /**
     * 文件名无效
     */
    FILE_NAME_INVALID("8006", "文件名无效"),
    
    /**
     * 文件删除失败
     */
    FILE_DELETE_FAILED("8007", "文件删除失败"),

    // ==================== AI聊天相关 9xxx ====================
    /**
     * 消息内容不能为空
     */
    MESSAGE_CONTENT_EMPTY("9001", "消息内容不能为空"),
    
    /**
     * AI服务不可用
     */
    AI_SERVICE_UNAVAILABLE("9002", "AI服务暂时不可用,请稍后重试"),
    
    /**
     * 会话ID不能为空
     */
    SESSION_ID_EMPTY("9003", "会话ID不能为空");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误描述
     */
    private final String message;

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCodeEnum fromCode(String code) {
        for (ErrorCodeEnum errorCode : ErrorCodeEnum.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }

    /**
     * 判断是否为成功状态
     *
     * @param code 错误码
     * @return 是否成功
     */
    public static boolean isSuccess(String code) {
        return SUCCESS.getCode().equals(code);
    }
}
