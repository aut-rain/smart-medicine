package com.medical.smartmedicine.medicine.dto;

import com.medical.smartmedicine.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 药品查询DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "药品查询参数")
public class MedicineQueryDTO extends PageQuery {

    private static final long serialVersionUID = 1L;

    /**
     * 药品类型 (0-西药, 1-中药, 2-中成药)
     */
    @Schema(description = "药品类型", example = "0")
    private Integer medicineType;

    /**
     * 药品名称(用于精确搜索)
     */
    @Schema(description = "药品名称", example = "阿莫西林")
    private String medicineName;

    /**
     * 价格区间-最小值
     */
    @Schema(description = "最小价格", example = "0.0")
    private Double minPrice;

    /**
     * 价格区间-最大值
     */
    @Schema(description = "最大价格", example = "100.0")
    private Double maxPrice;
}
