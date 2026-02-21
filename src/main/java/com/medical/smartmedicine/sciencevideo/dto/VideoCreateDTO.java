package com.medical.smartmedicine.sciencevideo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 科普视频创建DTO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "视频创建请求")
public class VideoCreateDTO {

    @NotBlank(message = "视频标题不能为空")
    @Schema(description = "视频标题", example = "如何正确洗手预防疾病")
    private String title;

    @Schema(description = "视频描述", example = "科学洗手方法演示")
    private String description;

    @Schema(description = "视频展示图片URL")
    private String imgPath;

    @NotBlank(message = "视频链接不能为空")
    @Schema(description = "视频链接", example = "https://example.com/video.mp4")
    private String link;
}
