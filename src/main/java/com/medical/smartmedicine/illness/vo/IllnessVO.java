package com.medical.smartmedicine.illness.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 疾病信息VO (列表展示)
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "疾病信息")
public class IllnessVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病ID
     */
    @Schema(description = "疾病ID")
    private Integer id;

    /**
     * 疾病分类ID
     */
    @Schema(description = "疾病分类ID")
    private Integer kindId;

    /**
     * 疾病分类名称
     */
    @Schema(description = "疾病分类名称")
    private String kindName;

    /**
     * 疾病名称
     */
    @Schema(description = "疾病名称")
    private String illnessName;

    /**
     * 疾病症状(简要)
     */
    @Schema(description = "疾病症状")
    private String illnessSymptom;

    /**
     * 特殊症状(简要)
     */
    @Schema(description = "特殊症状")
    private String specialSymptom;

    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Integer pageviews;
}
