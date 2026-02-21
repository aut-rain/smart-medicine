package com.medical.smartmedicine.medicine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 药品创建DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "药品创建请求")
public class MedicineCreateDTO {

    @NotBlank(message = "药品名称不能为空")
    @Schema(description = "药品名称", example = "阿莫西林胶囊")
    private String medicineName;

    @Schema(description = "关键字", example = "消炎,抗生素")
    private String keyword;

    @Schema(description = "药品功效", example = "用于敏感菌所致的感染")
    private String medicineEffect;

    @Schema(description = "药品品牌", example = "同仁堂")
    private String medicineBrand;

    @Schema(description = "药物相互作用", example = "不能与头孢类药物同时使用")
    private String interaction;

    @Schema(description = "禁忌", example = "孕妇禁用")
    private String taboo;

    @Schema(description = "用法用量", example = "口服,一次2粒,一日3次")
    private String usAge;

    @Schema(description = "药物类型(0-西药,1-中药,2-中成药)", example = "0")
    private Integer medicineType;

    @Schema(description = "药物图片地址")
    private String imgPath;

    @DecimalMin(value = "0.0", message = "价格不能为负数")
    @Schema(description = "药品价格", example = "25.50")
    private BigDecimal medicinePrice;
}
