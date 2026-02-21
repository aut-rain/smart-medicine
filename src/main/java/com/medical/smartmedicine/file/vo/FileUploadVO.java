package com.medical.smartmedicine.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传响应VO
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件URL
     */
    @Schema(description = "OSS文件访问URL", example = "https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/xxx.jpg")
    private String url;

    /**
     * 文件名称
     */
    @Schema(description = "原始文件名", example = "avatar.jpg")
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String fileType;

    /**
     * 上传时间戳
     */
    @Schema(description = "上传时间戳（毫秒）", example = "1701511234567")
    private Long uploadTime;
}
