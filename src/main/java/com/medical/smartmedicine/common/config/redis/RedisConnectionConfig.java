package com.medical.smartmedicine.common.config.redis;

import com.medical.smartmedicine.common.config.redis.properties.ChatRedisProperties;
import com.medical.smartmedicine.common.config.redis.properties.EmailRedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Redis连接工厂配置
 * 为不同业务模块创建独立的Redis连接
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Configuration
public class RedisConnectionConfig {

    /**
     * 聊天模块专用的Redis连接工厂
     * 使用app.redis.chat配置
     */
    @Bean("chatRedisConnectionFactory")
    public RedisConnectionFactory chatRedisConnectionFactory(ChatRedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setDatabase(properties.getDatabase());
        return new LettuceConnectionFactory(config);
    }

    /**
     * 邮件模块专用的Redis连接工厂
     * 使用app.redis.email配置
     */
    @Bean("emailRedisConnectionFactory")
    public RedisConnectionFactory emailRedisConnectionFactory(EmailRedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setDatabase(properties.getDatabase());
        return new LettuceConnectionFactory(config);
    }

    /**
     * 默认的Redis连接工厂
     * 供Spring Boot自动配置、Spring AI或其他未指定特定连接的组件使用
     * 从spring.data.redis前缀读取标准配置
     */
    @Bean
    @Primary
    public RedisConnectionFactory defaultRedisConnectionFactory() {
        // LettuceConnectionFactory的无参构造会自动读取spring.data.redis.*的配置
        return new LettuceConnectionFactory();
    }
}
