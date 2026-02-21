package com.medical.smartmedicine.common.config.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI聊天客户端配置
 * 配置带有记忆功能的ChatClient
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(ChatClient.class)
public class ChatClientConfig {

    /**
     * 智能医生ChatClient
     * 配置系统提示词，限制只回答医疗相关问题
     */
    @Bean
    public ChatClient smartDoctorChatClient(ChatModel chatModel, ChatMemory redisChatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一位专业的智能医生助手，具备丰富的医学知识。\n" +
                        "你的职责是：\n" +
                        "1. 解答用户的健康咨询和医疗问题\n" +
                        "2. 根据症状推荐可能的疾病和对应药品\n" +
                        "3. 提供健康建议和预防措施\n\n" +
                        "注意事项：\n" +
                        "- 仅回答与医疗健康相关的问题\n" +
                        "- 对严重症状建议及时就医\n" +
                        "- 不提供明确诊断，仅供参考\n" +
                        "- 使用简洁易懂的语言")
                .build();
    }
}
