package com.medical.smartmedicine.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI聊天请求DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天请求")
public class ChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Schema(description = "用户消息", example = "感冒了应该吃什么药?")
    private String message;

    /**
     * 会话ID (可选,用于支持多轮对话)
     */
    @Schema(description = "会话ID", example = "conv_123456")
    private String conversationId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
}
