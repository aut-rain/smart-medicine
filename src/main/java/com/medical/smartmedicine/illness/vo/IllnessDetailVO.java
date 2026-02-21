package com.medical.smartmedicine.illness.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 疾病详情VO (详情页展示)
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "疾病详情")
public class IllnessDetailVO implements Serializable {

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
     * 疾病分类信息
     */
    @Schema(description = "疾病分类信息")
    private IllnessCategoryVO category;

    /**
     * 疾病名称
     */
    @Schema(description = "疾病名称")
    private String illnessName;

    /**
     * 病因
     */
    @Schema(description = "病因")
    private String includeReason;

    /**
     * 疾病症状
     */
    @Schema(description = "疾病症状")
    private String illnessSymptom;

    /**
     * 特殊症状
     */
    @Schema(description = "特殊症状")
    private String specialSymptom;

    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Integer pageviews;

    /**
     * 关联药品列表
     */
    @Schema(description = "关联药品列表")
    private List<MedicineSimpleVO> medicines;

    /**
     * 疾病分类VO (内嵌类)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "疾病分类信息")
    public static class IllnessCategoryVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "分类ID")
        private Integer id;

        @Schema(description = "分类名称")
        private String name;

        @Schema(description = "分类说明")
        private String info;
    }

    /**
     * 药品简要信息VO (内嵌类)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "药品简要信息")
    public static class MedicineSimpleVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "药品ID")
        private Integer id;

        @Schema(description = "药品名称")
        private String medicineName;

        @Schema(description = "药品功效")
        private String medicineEffect;

        @Schema(description = "药品价格")
        private Double medicinePrice;

        @Schema(description = "药品类型", example = "0-西药,1-中药,2-中成药")
        private Integer medicineType;
    }
}
