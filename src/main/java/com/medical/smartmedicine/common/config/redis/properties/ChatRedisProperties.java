package com.medical.smartmedicine.common.config.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 聊天模块专用Redis配置属性
 * 自动绑定application-redis.yml中app.redis.chat前缀下的配置
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.redis.chat")
public class ChatRedisProperties {

    /**
     * Redis服务器地址
     */
    private String host = "localhost";

    /**
     * Redis服务器端口
     */
    private int port = 6379;

    /**
     * 使用的数据库索引
     */
    private int database = 0;

    /**
     * 连接超时时间
     */
    private String timeout = "2000ms";

    /**
     * Jedis连接池配置
     */
    private Pool jedis;

    /**
     * 内部类，用于映射连接池配置
     */
    @Data
    public static class Pool {
        /**
         * 最大活跃连接数
         */
        private int maxActive = 8;

        /**
         * 最大空闲连接数
         */
        private int maxIdle = 8;

        /**
         * 最小空闲连接数
         */
        private int minIdle = 0;
    }
}
