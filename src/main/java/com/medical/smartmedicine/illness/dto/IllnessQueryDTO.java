package com.medical.smartmedicine.illness.dto;

import com.medical.smartmedicine.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 疾病查询DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "疾病查询参数")
public class IllnessQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 疾病分类ID
     */
    @Schema(description = "疾病分类ID", example = "1")
    private Integer kindId;

    /**
     * 疾病名称(用于精确搜索)
     */
    @Schema(description = "疾病名称", example = "感冒")
    private String illnessName;
}
