package com.medical.smartmedicine.medicine.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 药品信息VO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "药品信息")
public class MedicineVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 药品ID
     */
    @Schema(description = "药品ID")
    private Integer id;

    /**
     * 药品名称
     */
    @Schema(description = "药品名称")
    private String medicineName;

    /**
     * 药品关键词
     */
    @Schema(description = "药品关键词")
    private String keyword;

    /**
     * 药品功效
     */
    @Schema(description = "药品功效")
    private String medicineEffect;

    /**
     * 药品品牌
     */
    @Schema(description = "药品品牌")
    private String medicineBrand;

    /**
     * 药品类型 (0-西药, 1-中药, 2-中成药)
     */
    @Schema(description = "药品类型")
    private Integer medicineType;

    /**
     * 药品类型描述
     */
    @Schema(description = "药品类型描述")
    private String medicineTypeDesc;

    /**
     * 药品价格
     */
    @Schema(description = "药品价格")
    private Double medicinePrice;

    /**
     * 药品图片URL
     */
    @Schema(description = "药品图片URL")
    private String imgPath;

    /**
     * 药物相互作用
     */
    @Schema(description = "药物相互作用")
    private String interaction;

    /**
     * 禁忌
     */
    @Schema(description = "禁忌")
    private String taboo;

    /**
     * 用法用量
     */
    @Schema(description = "用法用量")
    private String usAge;
}
