package com.medical.smartmedicine.common.config.ai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.medical.smartmedicine.ai.memory.MessageDeserializer;
import org.springframework.ai.chat.messages.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson配置
 * 配置用于AI聊天消息序列化的ObjectMapper
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(Message.class)
public class JacksonConfig {

    /**
     * 聊天模块专用的ObjectMapper
     * 配置自定义的Message反序列化器
     */
    @Bean("chatObjectMapper")
    @Primary
    public ObjectMapper chatObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 忽略未知属性，避免反序列化失败
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 注册自定义的Message反序列化器
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Message.class, new MessageDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
