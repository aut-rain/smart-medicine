package com.medical.smartmedicine.sciencevideo.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.sciencevideo.dto.VideoCreateDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoQueryDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoUpdateDTO;
import com.medical.smartmedicine.sciencevideo.service.VideoService;
import com.medical.smartmedicine.sciencevideo.vo.VideoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 科普视频Controller
 * 提供视频的查询、上架、修改、删除等功能
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.VIDEO_PATH)
@Tag(name = "科普视频管理", description = "科普视频的增删改查接口")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 分页查询视频列表
     *
     * @param queryDTO 查询参数
     * @return 视频列表
     */
    @GetMapping
    @Operation(summary = "分页查询视频列表", description = "根据条件分页查询科普视频")
    public Result<PageResult<VideoVO>> listVideos(@Valid VideoQueryDTO queryDTO) {
        log.info("查询视频列表: page={}, size={}, keyword={}", 
                queryDTO.getPage(), queryDTO.getSize(), queryDTO.getKeyword());
        PageResult<VideoVO> result = videoService.listVideos(queryDTO);
        
        return Result.success(result);
    }

    /**
     * 获取视频详情
     *
     * @param id 视频ID
     * @return 视频详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取视频详情", description = "根据ID获取视频的详细信息")
    public Result<VideoVO> getVideoById(
            @Parameter(description = "视频ID", required = true)
            @PathVariable Integer id) {
        log.info("获取视频详情: id={}", id);
        VideoVO videoVO = videoService.getVideoById(id);
        
        return Result.success(videoVO);
    }

    /**
     * 创建视频(管理员)
     *
     * @param createDTO 视频创建信息
     * @return 创建的视频信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建视频(管理员)", description = "上传新的科普视频,仅管理员可操作")
    public Result<VideoVO> createVideo(@Valid @RequestBody VideoCreateDTO createDTO) {
        log.info("创建视频: title={}", createDTO.getTitle());
        VideoVO videoVO = videoService.createVideo(createDTO);
        
        return Result.success("视频创建成功", videoVO);
    }

    /**
     * 更新视频(管理员)
     *
     * @param id 视频ID
     * @param updateDTO 更新信息
     * @return 更新后的视频信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新视频(管理员)", description = "更新视频信息,仅管理员可操作")
    public Result<VideoVO> updateVideo(
            @Parameter(description = "视频ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody VideoUpdateDTO updateDTO) {
        log.info("更新视频: id={}", id);
        VideoVO videoVO = videoService.updateVideo(id, updateDTO);
        
        return Result.success("视频更新成功", videoVO);
    }

    /**
     * 删除视频(管理员)
     *
     * @param id 视频ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除视频(管理员)", description = "删除指定视频,仅管理员可操作")
    public Result<Void> deleteVideo(
            @Parameter(description = "视频ID", required = true)
            @PathVariable Integer id) {
        log.info("删除视频: id={}", id);
        videoService.deleteVideo(id);
        
        return Result.success("视频删除成功");
    }
}
