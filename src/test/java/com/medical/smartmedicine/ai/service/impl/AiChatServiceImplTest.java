package com.medical.smartmedicine.ai.service.impl;

import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.dto.HealthGateResult;
import com.medical.smartmedicine.ai.vo.ChatResponse;
import com.medical.smartmedicine.ai.service.HealthIntentGateService;
import com.medical.smartmedicine.rag.dto.RagSearchResult;
import com.medical.smartmedicine.rag.service.RagSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiChatServiceImplTest {

    private ChatClient chatClient;
    private ChatMemory chatMemory;
    private RagSearchService ragSearchService;
    private HealthIntentGateService healthIntentGateService;
    private AiChatServiceImpl aiChatService;

    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
        chatMemory = mock(ChatMemory.class);
        ragSearchService = mock(RagSearchService.class);
        healthIntentGateService = mock(HealthIntentGateService.class);
        aiChatService = new AiChatServiceImpl(chatClient, chatMemory, ragSearchService, healthIntentGateService);
    }

    @Test
    void chat_should_return_health_only_message_for_non_health_question() {
        when(healthIntentGateService.classify(any(ChatRequest.class)))
                .thenReturn(new HealthGateResult(false));

        ChatRequest request = ChatRequest.builder()
                .conversationId("conv_1")
                .message("今天天气怎么样")
                .build();

        ChatResponse response = aiChatService.chat(request);

        assertEquals("只回答健康问题", response.getContent());
        verify(ragSearchService, never()).search(anyString());
    }

    @Test
    void chat_should_return_no_related_data_when_health_question_has_no_evidence() {
        when(healthIntentGateService.classify(any(ChatRequest.class)))
                .thenReturn(new HealthGateResult(true));
        when(ragSearchService.search("感冒吃什么药"))
                .thenReturn(RagSearchResult.builder().build());

        ChatRequest request = ChatRequest.builder()
                .conversationId("conv_2")
                .message("感冒吃什么药")
                .build();

        ChatResponse response = aiChatService.chat(request);

        assertEquals("无相关数据信息,请联系相关医生", response.getContent());
    }

    @Test
    void chat_should_treat_common_symptoms_as_health_question() {
        when(healthIntentGateService.classify(any(ChatRequest.class)))
                .thenReturn(new HealthGateResult(true));
        when(ragSearchService.search("我头疼头晕眼疼很难受"))
                .thenReturn(RagSearchResult.builder().build());

        ChatRequest request = ChatRequest.builder()
                .conversationId("conv_symptom")
                .message("我头疼头晕眼疼很难受")
                .build();

        ChatResponse response = aiChatService.chat(request);

        assertEquals("无相关数据信息,请联系相关医生", response.getContent());
        verify(ragSearchService).search("我头疼头晕眼疼很难受");
    }

    @Test
    void chat_should_still_query_rag_for_health_question() {
        when(healthIntentGateService.classify(any(ChatRequest.class)))
                .thenReturn(new HealthGateResult(true));
        when(ragSearchService.search("感冒吃什么药"))
                .thenReturn(RagSearchResult.builder()
                        .evidences(java.util.List.of(
                                com.medical.smartmedicine.rag.dto.RagEvidence.builder()
                                        .sourceType("药品")
                                        .sourceId(1)
                                        .title("感冒灵")
                                        .content("用于感冒")
                                        .score(100)
                                        .build()
                        ))
                        .build());

        ChatRequest request = ChatRequest.builder()
                .conversationId("conv_4")
                .message("感冒吃什么药")
                .build();

        when(chatClient.prompt()
                .user(anyString())
                .advisors(any(MessageChatMemoryAdvisor.class))
                .call()
                .content())
                .thenReturn("建议遵医嘱用药");

        ChatResponse response = aiChatService.chat(request);

        assertEquals("建议遵医嘱用药\n\n参考数据：[【资料1】](/medicine/1) 药品：感冒灵", response.getContent());
        verify(ragSearchService).search("感冒吃什么药");
    }

    @Test
    void streamChat_should_return_health_only_message_for_non_health_question() {
        when(healthIntentGateService.classify(any(ChatRequest.class)))
                .thenReturn(new HealthGateResult(false));

        ChatRequest request = ChatRequest.builder()
                .conversationId("conv_3")
                .message("给我讲个笑话")
                .build();

        StepVerifier.create(aiChatService.streamChat(request))
                .expectNext("只回答健康问题")
                .verifyComplete();

        verify(ragSearchService, never()).search(anyString());
    }
}
