package com.medical.smartmedicine.ai.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.service.AiChatService;
import com.medical.smartmedicine.ai.vo.ChatResponse;
import com.medical.smartmedicine.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI智能问答Controller
 * 提供智能医生对话接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai-chat")
@RequiredArgsConstructor
@Tag(name = "AI智能问答", description = "智能医生对话接口")
@ConditionalOnClass(name = "org.springframework.ai.chat.client.ChatClient")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/query")
    @Operation(summary = "发送消息", description = "向AI智能医生发送咨询消息")
    public Result<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("AI聊天请求: {}", request);

        // 如果没有提供conversationId，生成一个
        if (StrUtil.isBlank(request.getConversationId())) {
            request.setConversationId("conv_" + IdUtil.fastSimpleUUID());
        }

        ChatResponse response = aiChatService.chat(request);
        return Result.success(response);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式发送消息", description = "向AI智能医生发送咨询消息(流式响应)")
    public Flux<String> streamChat(@Valid @RequestBody ChatRequest request) {
        log.info("AI流式聊天请求: {}", request);

        // 如果没有提供conversationId，生成一个
        if (StrUtil.isBlank(request.getConversationId())) {
            request.setConversationId("conv_" + IdUtil.fastSimpleUUID());
        }

        return aiChatService.streamChat(request);
    }

    @DeleteMapping("/session/{conversationId}")
    @Operation(summary = "清空会话", description = "清空指定会话的历史记录")
    public Result<Void> clearConversation(
            @Parameter(description = "会话ID") @PathVariable String conversationId) {
        log.info("清空会话请求: conversationId={}", conversationId);
        
        aiChatService.clearConversation(conversationId);
        return Result.success();
    }
}
