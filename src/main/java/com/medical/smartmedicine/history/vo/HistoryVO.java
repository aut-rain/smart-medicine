package com.medical.smartmedicine.history.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 浏览历史VO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "浏览历史")
public class HistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "历史ID")
    private Integer id;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "操作类型: 1-浏览疾病 2-浏览药品 3-AI问答 4-浏览视频 5-浏览资讯")
    private Integer operateType;

    @Schema(description = "操作类型描述")
    private String operateTypeDesc;

    @Schema(description = "对象ID(疾病ID/药品ID等)")
    private Integer operateId;

    @Schema(description = "对象名称")
    private String operateName;

    @Schema(description = "创建时间")
    private Date createTime;
}
