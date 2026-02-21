package com.medical.smartmedicine.common.constant;

/**
 * Redis常量类
 * 定义Redis键名前缀等常量
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public class RedisConstant {

    /**
     * 邮箱验证码键前缀
     */
    public static final String EMAIL_CODE_PREFIX = "email:code:";

    /**
     * Token黑名单键前缀
     */
    public static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 用户登录信息键前缀
     */
    public static final String USER_LOGIN_PREFIX = "user:login:";

    /**
     * AI聊天会话键前缀
     */
    public static final String AI_CHAT_SESSION_PREFIX = "ai:chat:session:";

    /**
     * 热门疾病缓存键
     */
    public static final String HOT_ILLNESS_KEY = "illness:hot";

    /**
     * 疾病分类列表缓存键
     */
    public static final String ILLNESS_CATEGORY_LIST_KEY = "illness:category:list";

    /**
     * 疾病详情缓存键前缀
     */
    public static final String ILLNESS_DETAIL_PREFIX = "illness:detail:";

    /**
     * 药品详情缓存键前缀
     */
    public static final String MEDICINE_DETAIL_PREFIX = "medicine:detail:";

    /**
     * 验证码过期时间(分钟)
     */
    public static final long EMAIL_CODE_EXPIRE_MINUTES = 5;

    /**
     * Token过期时间(秒) - 2小时
     */
    public static final long TOKEN_EXPIRE_SECONDS = 7200;

    /**
     * 热门数据缓存时间(秒) - 1小时
     */
    public static final long HOT_DATA_CACHE_SECONDS = 3600;

    private RedisConstant() {
        // 私有构造函数,防止实例化
    }
}
