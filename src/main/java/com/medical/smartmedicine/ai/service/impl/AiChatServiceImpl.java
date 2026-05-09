package com.medical.smartmedicine.ai.service.impl;

import cn.hutool.core.util.IdUtil;
import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.service.AiChatService;
import com.medical.smartmedicine.ai.vo.ChatResponse;
import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.rag.dto.RagSearchResult;
import com.medical.smartmedicine.rag.service.RagSearchService;
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

    private static final String NO_RELATED_DATA_MESSAGE = "无相关数据信息,请联系相关医生";

    private final ChatClient smartDoctorChatClient;
    private final ChatMemory redisChatMemory;
    private final RagSearchService ragSearchService;

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            log.info("AI Chat请求 - conversationId: {}, message: {}", 
                    request.getConversationId(), request.getMessage());

            RagSearchResult ragResult = ragSearchService.search(request.getMessage());
            if (!ragResult.hasEvidence()) {
                return ChatResponse.builder()
                        .content(NO_RELATED_DATA_MESSAGE)
                        .conversationId(request.getConversationId())
                        .messageId(IdUtil.fastSimpleUUID())
                        .build();
            }

            // 构建带有会话ID的Memory Advisor
            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(redisChatMemory)
                    .conversationId(request.getConversationId())
                    .build();

            // 调用ChatClient获取响应
            String content = smartDoctorChatClient
                    .prompt()
                    .user(buildRagPrompt(request.getMessage(), ragResult))
                    .advisors(memoryAdvisor)
                    .call()
                    .content();
            content = appendReferences(content, ragResult);

            log.info("AI Chat响应成功 - conversationId: {}", request.getConversationId());

            return ChatResponse.builder()
                    .content(content)
                    .conversationId(request.getConversationId())
                    .messageId(IdUtil.fastSimpleUUID())
                    .build();

        } catch (Exception e) {
            log.error("AI Chat异常 - conversationId: {}, error: {}", 
                    request.getConversationId(), e.getMessage(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "AI服务暂时不可用，请稍后再试");
        }
    }

    @Override
    public Flux<String> streamChat(ChatRequest request) {
        try {
            log.info("AI Stream Chat请求 - conversationId: {}, message: {}",
                    request.getConversationId(), request.getMessage());

            RagSearchResult ragResult = ragSearchService.search(request.getMessage());
            if (!ragResult.hasEvidence()) {
                return Flux.just(NO_RELATED_DATA_MESSAGE);
            }

            // 构建带有会话ID的Memory Advisor
            MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(redisChatMemory)
                    .conversationId(request.getConversationId())
                    .build();
            // 调用ChatClient获取流式响应
            return smartDoctorChatClient
                    .prompt()
                    .user(buildRagPrompt(request.getMessage(), ragResult))
                    .advisors(memoryAdvisor)
                    .stream()
                    .content()
                    .doOnNext(content -> log.debug("流式响应片段: ###{}###", content))
                    .concatWith(Flux.just(buildReferenceSection(ragResult)));

        } catch (Exception e) {
            log.error("AI Stream Chat异常 - conversationId: {}, error: {}",
                    request.getConversationId(), e.getMessage(), e);
            return Flux.error(new BusinessException(ResultCode.SYSTEM_ERROR, "AI服务暂时不可用，请稍后再试"));
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
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "清除会话失败");
        }
    }

    private String buildRagPrompt(String question, RagSearchResult ragResult) {
        return """
                用户问题：
                %s

                以下是从MySQL数据库严格检索到的资料，请优先且主要基于这些资料回答：
                %s

                回答要求：
                1. 只能把数据库资料作为事实依据，不要编造不存在的疾病、药品、视频或资讯。
                2. 可以补充通用健康建议，但必须使用“补充建议：”单独标注。
                3. 涉及严重症状、用药风险或不确定情况时，提醒用户咨询具备资质的医生。
                4. 引用资料时必须使用资料里的Markdown详情链接，例如[【资料1】](/medicine/1)，不要只输出纯文本【资料1】。
                5. 不要在正文中虚构参考来源。
                """.formatted(question, ragResult.buildContext());
    }

    private String appendReferences(String content, RagSearchResult ragResult) {
        return content + buildReferenceSection(ragResult);
    }

    private String buildReferenceSection(RagSearchResult ragResult) {
        return "\n\n参考数据：" + ragResult.buildReferenceText();
    }
}
