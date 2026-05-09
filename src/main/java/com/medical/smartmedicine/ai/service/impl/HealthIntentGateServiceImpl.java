package com.medical.smartmedicine.ai.service.impl;

import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.dto.HealthGateResult;
import com.medical.smartmedicine.ai.service.HealthIntentGateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

/**
 * 基于LLM的健康门禁分类服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnClass(ChatClient.class)
public class HealthIntentGateServiceImpl implements HealthIntentGateService {

    private final ChatClient smartDoctorChatClient;

    @Override
    public HealthGateResult classify(ChatRequest request) {
        try {
            log.info("健康门禁分类请求 - conversationId: {}, message: {}",
                    request.getConversationId(), request.getMessage());

            return smartDoctorChatClient
                    .prompt()
                    .system("""
                            你是一个严格的健康问题分类器。
                            你的任务只有一个：判断用户原始输入是否属于医药、健康、疾病、症状、用药、检查、护理、预防、康复相关问题。
                            你不能回答用户问题，不能解释，不能输出多余内容。
                            你只能输出合法JSON，格式必须是：
                            {"healthRelated": true}
                            或
                            {"healthRelated": false}
                            判定规则：
                            - 只要涉及身体不适、症状、疾病、药品、治疗、检查、护理、预防、康复，就返回 true。
                            - 如果明显是天气、娱乐、闲聊、技术、财经、学习、新闻等非健康问题，就返回 false。
                            - 无法明确判断时，宁可返回 true。
                            """)
                    .user(request.getMessage())
                    .call()
                    .entity(HealthGateResult.class);
        } catch (Exception e) {
            log.error("健康门禁分类异常 - conversationId: {}, error: {}",
                    request.getConversationId(), e.getMessage(), e);
            return new HealthGateResult(true);
        }
    }
}
