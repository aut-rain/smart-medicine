package com.medical.smartmedicine.ai.service.impl;

import cn.hutool.core.util.IdUtil;
import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.service.AiChatService;
import com.medical.smartmedicine.ai.vo.ChatResponse;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI聊天服务实现
 * 基于Spring AI实现智能医生对话
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnClass(ChatClient.class)
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient smartDoctorChatClient;
    private final ChatMemory redisChatMemory;

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            log.info("AI Chat请求 - conversationId: {}, message: {}", 
                    request.getConversationId(), request.getMessage());

            // 构建带有会话ID的Memory Advisor
            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(redisChatMemory)
                    .conversationId(request.getConversationId())
                    .build();

            // 调用ChatClient获取响应
            String content = smartDoctorChatClient
                    .prompt()
                    .user(request.getMessage())
                    .advisors(memoryAdvisor)
                    .call()
                    .content();

            log.info("AI Chat响应成功 - conversationId: {}", request.getConversationId());

            return ChatResponse.builder()
                    .content(content)
                    .conversationId(request.getConversationId())
                    .messageId(IdUtil.fastSimpleUUID())
                    .build();

        } catch (Exception e) {
            log.error("AI Chat异常 - conversationId: {}, error: {}", 
                    request.getConversationId(), e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "AI服务暂时不可用，请稍后再试");
        }
    }

    @Override
    public Flux<String> streamChat(ChatRequest request) {
        try {
            log.info("AI Stream Chat请求 - conversationId: {}, message: {}",
                    request.getConversationId(), request.getMessage());

            // 构建带有会话ID的Memory Advisor
            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(redisChatMemory)
                    .conversationId(request.getConversationId())
                    .build();
            // 调用ChatClient获取流式响应
            return smartDoctorChatClient
                    .prompt()
                    .user(request.getMessage())
                    .advisors(memoryAdvisor)
                    .stream()
                    .content()
                    .doOnNext(content -> log.error("流式响应片段: ###{}###", content));

        } catch (Exception e) {
            log.error("AI Stream Chat异常 - conversationId: {}, error: {}",
                    request.getConversationId(), e.getMessage(), e);
            return Flux.error(new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "AI服务暂时不可用，请稍后再试"));
        }
    }

    @Override
    public void clearConversation(String conversationId) {
        try {
            log.info("清除会话历史 - conversationId: {}", conversationId);
            redisChatMemory.clear(conversationId);
            log.info("会话历史已清除 - conversationId: {}", conversationId);
        } catch (Exception e) {
            log.error("清除会话历史异常 - conversationId: {}, error: {}", 
                    conversationId, e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "清除会话失败");
        }
    }
}
