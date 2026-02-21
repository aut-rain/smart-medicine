package com.medical.smartmedicine.feedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 反馈详情VO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "反馈详情")
public class FeedbackVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "反馈ID")
    private Integer id;

    @Schema(description = "反馈标题")
    private String feedbackTitle;

    @Schema(description = "反馈内容")
    private String feedbackContent;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "创建时间")
    private Date createTime;
}
