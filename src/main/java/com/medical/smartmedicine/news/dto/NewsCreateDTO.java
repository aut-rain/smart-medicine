package com.medical.smartmedicine.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建资讯请求DTO
 *
 * @author Smart Medicine Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建资讯请求")
public class NewsCreateDTO {

    @NotBlank(message = "资讯标题不能为空")
    @Schema(description = "资讯标题", example = "春季流感预防指南")
    private String newsName;

    @Schema(description = "资讯摘要", example = "春季如何有效预防流感，专家为您详细解读")
    private String newsSummary;

    @NotBlank(message = "封面图不能为空")
    @Schema(description = "封面图OSS路径", example = "smart-medicine/news/covers/flu.jpg")
    private String coverOssPath;

    @NotBlank(message = "Markdown文件不能为空")
    @Schema(description = "Markdown文件OSS路径", example = "smart-medicine/news/content/flu-prevention.md")
    private String markdownOssPath;

    @Schema(description = "分类", example = "健康科普")
    private String category;

    @Schema(description = "作者", example = "张医生")
    private String author;

    @Schema(description = "状态：0-草稿，1-已发布", example = "0")
    @Builder.Default
    private Integer status = 0;
}
