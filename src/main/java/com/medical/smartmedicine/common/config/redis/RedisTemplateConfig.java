package com.medical.smartmedicine.common.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis模板配置
 * 配置多个RedisTemplate用于不同业务场景
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * 聊天模块专用的RedisTemplate
     */
    @Bean("chatRedisTemplate")
    public RedisTemplate<String, String> chatRedisTemplate(
            @Qualifier("chatRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory);
    }

    /**
     * 邮件模块专用的RedisTemplate
     */
    @Bean("emailRedisTemplate")
    public RedisTemplate<String, String> emailRedisTemplate(
            @Qualifier("emailRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory);
    }

    /**
     * 默认的RedisTemplate
     * 供Spring AI等框架使用
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory);
    }

    /**
     * 默认的StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * 通用的RedisTemplate，用于存储Object类型的数据
     * 主要用于需要序列化复杂对象的场景
     */
    @Bean
    public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 私有辅助方法，统一RedisTemplate的序列化配置
     */
    private RedisTemplate<String, String> createRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
