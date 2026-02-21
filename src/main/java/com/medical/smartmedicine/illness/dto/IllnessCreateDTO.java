package com.medical.smartmedicine.illness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 疾病创建DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "疾病创建请求")
public class IllnessCreateDTO {

    @NotNull(message = "疾病分类不能为空")
    @Schema(description = "疾病分类ID", example = "1")
    private Integer kindId;

    @NotBlank(message = "疾病名称不能为空")
    @Schema(description = "疾病名称", example = "感冒")
    private String illnessName;

    @Schema(description = "引起原因", example = "病毒感染")
    private String includeReason;

    @Schema(description = "主要症状", example = "发热、咳嗽、流鼻涕")
    private String illnessSymptom;

    @Schema(description = "特殊症状", example = "高热不退")
    private String specialSymptom;
}
