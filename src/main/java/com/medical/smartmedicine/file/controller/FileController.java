package com.medical.smartmedicine.file.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.file.service.FileService;
import com.medical.smartmedicine.file.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件管理Controller
 * 提供图片、视频等文件上传至OSS的功能
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.FILE_PATH)
@Tag(name = "文件管理", description = "文件上传、删除等接口，文件上传至阿里云OSS")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // 允许的视频格式
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv", "video/mkv"
    );

    // 图片最大大小：5MB
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    // 视频最大大小：100MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024;

    /**
     * 上传通用文件
     *
     * @param file 文件
     * @return 文件上传结果（包含OSS URL）
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "上传文件（通用）",
            description = "上传任意类型文件到OSS，返回文件访问URL。文件大小限制：100MB"
    )
    public Result<FileUploadVO> uploadFile(
            @Parameter(
                    description = "文件（必填）",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        log.info("上传文件: fileName={}, size={}, contentType={}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // 验证文件
        validateFile(file, MAX_VIDEO_SIZE);

        // 上传到OSS
        FileUploadVO uploadVO = fileService.uploadFile(file);

        if (uploadVO.getUrl() == null || uploadVO.getUrl().isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        log.info("文件上传成功: url={}", uploadVO.getUrl());
        return Result.success("文件上传成功", uploadVO);
    }

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片上传结果（包含OSS URL）
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "上传图片",
            description = "上传图片文件到OSS，支持jpg/png/gif/webp格式，大小限制5MB。" +
                    "返回的URL可用于用户头像、药品图片、新闻配图等场景"
    )
    public Result<FileUploadVO> uploadImage(
            @Parameter(
                    description = "图片文件（必填，支持jpg/png/gif/webp）",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        log.info("上传图片: fileName={}, size={}, contentType={}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // 验证文件
        validateFile(file, MAX_IMAGE_SIZE);

        // 验证图片格式
        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED,
                    "不支持的图片格式，仅支持：jpg、png、gif、webp");
        }

        // 上传到OSS
        FileUploadVO uploadVO = fileService.uploadImage(file);

        if (uploadVO.getUrl() == null || uploadVO.getUrl().isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        log.info("图片上传成功: url={}", uploadVO.getUrl());
        return Result.success("图片上传成功", uploadVO);
    }

    /**
     * 上传视频
     *
     * @param file 视频文件
     * @return 视频上传结果（包含OSS URL）
     */
    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "上传视频",
            description = "上传视频文件到OSS，支持mp4/avi/mov等格式，大小限制100MB。" +
                    "返回的URL可用于科普视频等场景"
    )
    public Result<FileUploadVO> uploadVideo(
            @Parameter(
                    description = "视频文件（必填，支持mp4/avi/mov/wmv/flv/mkv）",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file) {

        log.info("上传视频: fileName={}, size={}, contentType={}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // 验证文件
        validateFile(file, MAX_VIDEO_SIZE);

        // 验证视频格式
        if (!ALLOWED_VIDEO_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED,
                    "不支持的视频格式，仅支持：mp4、avi、mov、wmv、flv、mkv");
        }

        // 上传到OSS
        FileUploadVO uploadVO = fileService.upload(file);

        if (uploadVO.getUrl() == null || uploadVO.getUrl().isEmpty()) {
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED);
        }

        log.info("视频上传成功: url={}", uploadVO.getUrl());
        return Result.success("视频上传成功", uploadVO);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL（OSS完整URL）
     * @return 操作结果
     */
    @DeleteMapping
    @Operation(
            summary = "删除文件",
            description = "根据文件URL从OSS删除文件。URL格式：https://bucket-name.endpoint/path/file.ext"
    )
    public Result<Void> deleteFile(
            @Parameter(description = "文件URL", required = true, example = "https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/xxx.jpg")
            @RequestParam String fileUrl) {

        log.info("删除文件: fileUrl={}", fileUrl);

        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件URL不能为空");
        }

        boolean success = fileService.deleteFile(fileUrl);

        if (!success) {
            throw new BusinessException(ResultCode.FILE_DELETE_FAILED);
        }

        log.info("文件删除成功: fileUrl={}", fileUrl);
        return Result.success("文件删除成功");
    }

    /**
     * 验证文件
     *
     * @param file 文件
     * @param maxSize 最大大小（字节）
     */
    private void validateFile(MultipartFile file, long maxSize) {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_EMPTY);
        }

        // 验证文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ResultCode.FILE_NAME_INVALID);
        }

        // 验证文件大小
        if (file.getSize() > maxSize) {
            String maxSizeStr = maxSize / (1024 * 1024) + "MB";
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED,
                    "文件大小超过限制：" + maxSizeStr);
        }

        // 验证文件扩展名
        if (!originalFilename.contains(".")) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED,
                    "文件必须包含扩展名");
        }
    }
}
