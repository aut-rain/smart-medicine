package com.medical.smartmedicine.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新资讯请求DTO
 *
 * @author Smart Medicine Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新资讯请求")
public class NewsUpdateDTO {

    @Schema(description = "资讯标题")
    private String newsName;

    @Schema(description = "资讯摘要")
    private String newsSummary;

    @Schema(description = "封面图OSS路径")
    private String coverOssPath;

    @Schema(description = "Markdown文件OSS路径")
    private String markdownOssPath;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "状态：0-草稿，1-已发布")
    private Integer status;
}
