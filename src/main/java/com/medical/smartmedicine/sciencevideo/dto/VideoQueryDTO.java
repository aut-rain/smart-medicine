package com.medical.smartmedicine.sciencevideo.dto;

import com.medical.smartmedicine.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 科普视频查询DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "视频查询请求")
public class VideoQueryDTO extends PageQuery {

    @Schema(description = "关键词搜索(标题/描述)", example = "洗手")
    private String keyword;
}
