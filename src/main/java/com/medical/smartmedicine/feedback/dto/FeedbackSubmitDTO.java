package com.medical.smartmedicine.feedback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 反馈提交DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "反馈提交请求")
public class FeedbackSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100字符")
    @Schema(description = "反馈标题", example = "建议增加药品搜索功能")
    private String feedbackTitle;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 1000, message = "内容长度不能超过1000字符")
    @Schema(description = "反馈内容", example = "希望可以按药品名称快速搜索...")
    private String feedbackContent;

    /**
     * 联系方式(可选)
     */
    @Size(max = 50, message = "联系方式长度不能超过50字符")
    @Schema(description = "联系方式", example = "user@example.com")
    private String contact;
}
