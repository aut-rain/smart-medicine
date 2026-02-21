package com.medical.smartmedicine.ai.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的AI聊天记忆实现
 * 使用Lua脚本保证原子性操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnClass(ChatMemory.class)
public class RedisChatMemory implements ChatMemory {

    private static final String KEY_PREFIX = "smart-doctor:memory:";
    private static final long SESSION_TTL_MINUTES = 120; // 会话超时时间：120分钟
    private static final int MEMORY_WINDOW_SIZE = 10; // 保留最近的10条消息

    // Lua脚本：原子性地添加消息、修剪列表、设置过期时间
    private static final String ADD_MESSAGE_SCRIPT =
            "local key = KEYS[1]\n" +
            "local memory_window = tonumber(ARGV[1])\n" +
            "local ttl_seconds = tonumber(ARGV[2])\n" +
            "\n" +
            "if memory_window == nil then memory_window = 10 end\n" +
            "if ttl_seconds == nil then ttl_seconds = 7200 end\n" +
            "\n" +
            "for i = 3, #ARGV do\n" +
            "    redis.call('RPUSH', key, ARGV[i])\n" +
            "end\n" +
            "\n" +
            "redis.call('LTRIM', key, -memory_window, -1)\n" +
            "redis.call('EXPIRE', key, ttl_seconds)\n" +
            "\n" +
            "return redis.call('LLEN', key)";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DefaultRedisScript<Long> addMessageScript;

    public RedisChatMemory(
            @Qualifier("chatRedisTemplate") RedisTemplate<String, String> redisTemplate,
            @Qualifier("chatObjectMapper") ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        // 初始化Lua脚本
        this.addMessageScript = new DefaultRedisScript<>();
        this.addMessageScript.setScriptText(ADD_MESSAGE_SCRIPT);
        this.addMessageScript.setResultType(Long.class);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            log.warn("消息列表为空: conversationId={}", conversationId);
            return;
        }

        String key = KEY_PREFIX + conversationId;

        try {
            // 准备Lua脚本参数
            List<Object> args = new ArrayList<>();
            args.add(MEMORY_WINDOW_SIZE);  // ARGV[1]
            args.add(TimeUnit.MINUTES.toSeconds(SESSION_TTL_MINUTES)); // ARGV[2]

            // 序列化消息（从ARGV[3]开始）
            for (Message msg : messages) {
                String jsonMessage = objectMapper.writeValueAsString(msg);
                args.add(jsonMessage);
            }

            // 原子性执行：添加消息 + 修剪列表 + 设置过期时间
            Long currentLength = redisTemplate.execute(
                    addMessageScript,
                    Collections.singletonList(key),
                    args.toArray()
            );

            log.info("添加{}条消息，当前长度: {}，会话ID: {}，TTL: {}分钟",
                    messages.size(), currentLength, conversationId, SESSION_TTL_MINUTES);

        } catch (JsonProcessingException e) {
            log.error("消息序列化失败: conversationId={}", conversationId, e);
            throw new RuntimeException("消息序列化失败", e);
        } catch (Exception e) {
            log.error("Redis操作失败: conversationId={}", conversationId, e);
            // 降级方案
            fallbackAdd(conversationId, messages);
        }
    }

    /**
     * 降级方案：当Lua脚本执行失败时使用
     */
    private void fallbackAdd(String conversationId, List<Message> messages) {
        log.warn("使用降级方案添加消息: conversationId={}", conversationId);
        String key = KEY_PREFIX + conversationId;

        try {
            List<String> messageJsons = messages.stream()
                    .map(msg -> {
                        try {
                            return objectMapper.writeValueAsString(msg);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("消息序列化失败", e);
                        }
                    }).toList();

            redisTemplate.opsForList().rightPushAll(key, messageJsons);
            redisTemplate.opsForList().trim(key, -MEMORY_WINDOW_SIZE, -1);
            redisTemplate.expire(key, SESSION_TTL_MINUTES, TimeUnit.MINUTES);

            log.info("降级方案：添加{}条消息成功，会话ID: {}", messages.size(), conversationId);
        } catch (Exception e) {
            log.error("降级方案也失败了: conversationId={}", conversationId, e);
            throw new RuntimeException("消息添加失败", e);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        String key = KEY_PREFIX + conversationId;

        try {
            List<String> messageJsons = redisTemplate.opsForList().range(key, -MEMORY_WINDOW_SIZE, -1);

            if (messageJsons == null || messageJsons.isEmpty()) {
                return new ArrayList<>();
            }

            List<Message> messages = new ArrayList<>();
            for (String messageJson : messageJsons) {
                try {
                    Message message = objectMapper.readValue(messageJson, Message.class);
                    messages.add(message);
                } catch (JsonProcessingException e) {
                    log.error("消息反序列化失败: {}", messageJson, e);
                }
            }

            // 刷新TTL（滑动过期）
            redisTemplate.expire(key, SESSION_TTL_MINUTES, TimeUnit.MINUTES);

            log.info("获取{}条消息，会话ID: {}", messages.size(), conversationId);
            return messages;

        } catch (Exception e) {
            log.error("获取消息失败: conversationId={}", conversationId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void clear(String conversationId) {
        String key = KEY_PREFIX + conversationId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.info("清除会话消息: conversationId={}, 成功: {}", conversationId, deleted);
        } catch (Exception e) {
            log.error("清除消息失败: conversationId={}", conversationId, e);
        }
    }
}
