package com.medical.smartmedicine.ai.service;

import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.vo.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * AI聊天服务接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface AiChatService {

    /**
     * 发送聊天消息(同步)
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 发送聊天消息(流式响应)
     *
     * @param request 聊天请求
     * @return 流式响应
     */
    Flux<String> streamChat(ChatRequest request);

    /**
     * 清除会话历史
     *
     * @param conversationId 会话ID
     */
    void clearConversation(String conversationId);
}
