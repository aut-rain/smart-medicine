package com.medical.smartmedicine.news.dto;

import com.medical.smartmedicine.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 资讯查询请求DTO
 *
 * @author Smart Medicine Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资讯查询请求")
public class NewsQueryDTO extends PageQuery {

    @Schema(description = "状态：0-草稿，1-已发布")
    private Integer status;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "搜索关键词")
    private String keyword;
}
