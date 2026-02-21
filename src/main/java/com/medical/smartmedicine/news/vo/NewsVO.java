package com.medical.smartmedicine.news.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 资讯信息VO（列表展示）
 *
 * @author Smart Medicine Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资讯信息")
public class NewsVO implements Serializable {

    @Schema(description = "资讯ID")
    private Integer id;

    @Schema(description = "资讯标题")
    private String newsName;

    @Schema(description = "资讯摘要")
    private String newsSummary;

    @Schema(description = "封面图URL")
    private String coverOssPath;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "创建时间")
    private Date createTime;
}
