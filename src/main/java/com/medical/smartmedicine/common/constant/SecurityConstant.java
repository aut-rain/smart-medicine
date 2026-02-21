package com.medical.smartmedicine.common.constant;

/**
 * 安全相关常量类
 * 定义JWT、认证等安全相关常量
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public class SecurityConstant {

    /**
     * Token请求头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Token类型
     */
    public static final String TOKEN_TYPE = "JWT";

    /**
     * Token签发者
     */
    public static final String TOKEN_ISSUER = "smart-medicine";

    /**
     * 用户ID声明
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * 用户账号声明
     */
    public static final String CLAIM_USER_ACCOUNT = "userAccount";

    /**
     * 用户角色声明
     */
    public static final String CLAIM_ROLE = "role";

    /**
     * Token类型声明 (access/refresh)
     */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    /**
     * Access Token类型
     */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /**
     * Refresh Token类型
     */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * 密码加密强度
     */
    public static final int PASSWORD_ENCODER_STRENGTH = 10;

    private SecurityConstant() {
        // 私有构造函数,防止实例化
    }
}
