package com.medical.smartmedicine.sciencevideo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 科普视频VO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "视频信息")
public class VideoVO {

    @Schema(description = "视频ID")
    private Integer id;

    @Schema(description = "视频标题")
    private String title;

    @Schema(description = "视频描述")
    private String description;

    @Schema(description = "视频展示图片URL")
    private String imgPath;

    @Schema(description = "视频链接")
    private String link;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private Date updateTime;
}
