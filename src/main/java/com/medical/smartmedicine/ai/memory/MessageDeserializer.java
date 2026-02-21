package com.medical.smartmedicine.ai.memory;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.ai.chat.messages.*;

import java.io.IOException;

/**
 * 自定义消息反序列化器
 * 用于将JSON字符串转换为正确的Message实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public class MessageDeserializer extends StdDeserializer<Message> {

    public MessageDeserializer() {
        this(null);
    }

    public MessageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Message deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        String messageType = node.get("messageType").asText();

        return switch (messageType) {
            case "USER" -> new UserMessage(node.get("text").asText());
            case "ASSISTANT" -> new AssistantMessage(node.get("text").asText());
            case "SYSTEM" -> new SystemMessage(node.get("text").asText());
            default -> throw new IllegalArgumentException("未知的消息类型: " + messageType);
        };
    }
}
