package com.medical.smartmedicine.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI聊天消息DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天消息请求")
public class ChatMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "消息长度不能超过500字")
    @Schema(description = "消息内容", example = "我最近总是头痛,是什么原因?")
    private String content;

    /**
     * 会话ID(可选,用于保持对话上下文)
     */
    @Schema(description = "会话ID")
    private String sessionId;
}
