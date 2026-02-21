package com.medical.smartmedicine.common.constant;

/**
 * 业务常量类
 * 定义业务相关的常量
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public class BusinessConstant {

    /**
     * 默认头像
     */
    public static final String DEFAULT_AVATAR = "https://smart-medicine.oss-cn-hangzhou.aliyuncs.com/avatar/default.jpg";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 密码最小长度
     */
    public static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 用户账号最小长度
     */
    public static final int ACCOUNT_MIN_LENGTH = 4;

    /**
     * 用户账号最大长度
     */
    public static final int ACCOUNT_MAX_LENGTH = 20;

    /**
     * 验证码长度
     */
    public static final int VERIFY_CODE_LENGTH = 6;

    /**
     * 文件上传最大大小 - 图片(5MB)
     */
    public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    /**
     * 文件上传最大大小 - 视频(200MB)
     */
    public static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024;

    /**
     * 允许的图片格式
     */
    public static final String[] ALLOWED_IMAGE_FORMATS = {"jpg", "jpeg", "png", "gif"};

    /**
     * 允许的视频格式
     */
    public static final String[] ALLOWED_VIDEO_FORMATS = {"mp4", "avi", "mov"};

    /**
     * 热门疾病展示数量
     */
    public static final int HOT_ILLNESS_LIMIT = 10;

    private BusinessConstant() {
        // 私有构造函数,防止实例化
    }
}
