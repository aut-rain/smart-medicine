package com.medical.smartmedicine.common.constant;

/**
 * API常量类
 * 定义API版本、路径前缀等常量
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public class ApiConstant {

    /**
     * API版本 v1
     */
    public static final String API_VERSION_V1 = "/api/v1";

    /**
     * 认证模块路径
     */
    public static final String AUTH_PATH = API_VERSION_V1 + "/auth";

    /**
     * 用户模块路径
     */
    public static final String USER_PATH = API_VERSION_V1 + "/users";

    /**
     * 疾病模块路径
     */
    public static final String ILLNESS_PATH = API_VERSION_V1 + "/illnesses";

    /**
     * 疾病分类模块路径
     */
    public static final String ILLNESS_CATEGORY_PATH = API_VERSION_V1 + "/illness-categories";

    /**
     * 药品模块路径
     */
    public static final String MEDICINE_PATH = API_VERSION_V1 + "/medicines";

    /**
     * 疾病-药品关联模块路径
     */
    public static final String ILLNESS_MEDICINE_PATH = API_VERSION_V1 + "/illness-medicines";

    /**
     * 医疗资讯模块路径
     */
    public static final String MEDICAL_NEWS_PATH = API_VERSION_V1 + "/medical-news";

    /**
     * 科普视频模块路径
     */
    public static final String VIDEO_PATH = API_VERSION_V1 + "/videos";

    /**
     * 反馈模块路径
     */
    public static final String FEEDBACK_PATH = API_VERSION_V1 + "/feedbacks";

    /**
     * 浏览历史模块路径
     */
    public static final String HISTORY_PATH = API_VERSION_V1 + "/histories";

    /**
     * AI聊天模块路径
     */
    public static final String AI_PATH = API_VERSION_V1 + "/ai";

    /**
     * 文件模块路径
     */
    public static final String FILE_PATH = API_VERSION_V1 + "/files";

    private ApiConstant() {
        // 私有构造函数,防止实例化
    }
}
