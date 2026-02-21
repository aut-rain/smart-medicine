package com.medical.smartmedicine.sciencevideo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.mapper.ScienceVideoMapper;
import com.medical.smartmedicine.model.entity.ScienceVideo;
import com.medical.smartmedicine.sciencevideo.dto.VideoCreateDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoQueryDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoUpdateDTO;
import com.medical.smartmedicine.sciencevideo.service.VideoService;
import com.medical.smartmedicine.sciencevideo.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 科普视频服务实现
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final ScienceVideoMapper scienceVideoMapper;
    private final OssClient ossClient;

    @Override
    public PageResult<VideoVO> listVideos(VideoQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<ScienceVideo> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索(标题或描述)
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(ScienceVideo::getTitle, queryDTO.getKeyword())
                    .or()
                    .like(ScienceVideo::getDescription, queryDTO.getKeyword())
            );
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(ScienceVideo::getCreateTime);

        // 分页查询
        Page<ScienceVideo> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<ScienceVideo> videoPage = scienceVideoMapper.selectPage(page, wrapper);

        // 转换为VO
        List<VideoVO> voList = videoPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();

        return PageResult.<VideoVO>builder()
                .records(voList)
                .total(videoPage.getTotal())
                .pages(videoPage.getPages())
                .current(videoPage.getCurrent())
                .size(videoPage.getSize())
                .build();
    }

    @Override
    public VideoVO getVideoById(Integer id) {
        ScienceVideo video = scienceVideoMapper.selectById(id);
        if (video == null) {
            log.warn("视频不存在: id={}", id);
            throw new BusinessException(ErrorCodeEnum.RESOURCE_NOT_FOUND, "视频不存在");
        }
        return convertToVO(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoVO createVideo(VideoCreateDTO createDTO) {
        log.info("创建视频: title={}", createDTO.getTitle());
        
        // 构建实体
        ScienceVideo video = ScienceVideo.builder()
                .title(createDTO.getTitle())
                .description(createDTO.getDescription())
                .imgPath(createDTO.getImgPath())
                .link(createDTO.getLink())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 保存到数据库
        int rows = scienceVideoMapper.insert(video);
        if (rows <= 0) {
            log.error("视频创建失败: {}", createDTO);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "视频创建失败");
        }

        log.info("视频创建成功: id={}", video.getId());
        return convertToVO(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoVO updateVideo(Integer id, VideoUpdateDTO updateDTO) {
        log.info("更新视频: id={}", id);
        
        // 查询视频是否存在
        ScienceVideo video = scienceVideoMapper.selectById(id);
        if (video == null) {
            log.warn("视频不存在: id={}", id);
            throw new BusinessException(ErrorCodeEnum.RESOURCE_NOT_FOUND, "视频不存在");
        }

        // 更新字段(只更新非空字段)
        if (StrUtil.isNotBlank(updateDTO.getTitle())) {
            video.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            video.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getImgPath() != null) {
            video.setImgPath(updateDTO.getImgPath());
        }
        if (StrUtil.isNotBlank(updateDTO.getLink())) {
            video.setLink(updateDTO.getLink());
        }
        video.setUpdateTime(new Date());

        // 更新到数据库
        int rows = scienceVideoMapper.updateById(video);
        if (rows <= 0) {
            log.error("视频更新失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "视频更新失败");
        }

        log.info("视频更新成功: id={}", id);
        return convertToVO(video);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(Integer id) {
        log.info("删除视频: id={}", id);
        
        // 查询视频是否存在
        ScienceVideo video = scienceVideoMapper.selectById(id);
        if (video == null) {
            log.warn("视频不存在: id={}", id);
            throw new BusinessException(ErrorCodeEnum.RESOURCE_NOT_FOUND, "视频不存在");
        }

        String link = video.getLink();
        boolean isOssLink = link != null && link.contains("oss");

        // 删除视频
        int rows = scienceVideoMapper.deleteById(id);
        if (rows <= 0) {
            log.error("视频删除失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "视频删除失败");
        }

        // 数据库删除成功后，删除OSS中的视频文件（不删除外部链接）
        if (isOssLink) {
            log.info("删除视频文件: {}", link);
            boolean deleted = ossClient.delete(link);
            if (!deleted) {
                log.warn("视频文件删除失败，但不影响主流程: {}", link);
            }
        } else {
            log.info("视频为外部链接，跳过OSS删除: {}", link);
        }

        log.info("视频删除成功: id={}, link={}", id, link);
    }

    /**
     * 实体转换为VO
     */
    private VideoVO convertToVO(ScienceVideo video) {
        return BeanUtil.copyProperties(video, VideoVO.class);
    }
}
