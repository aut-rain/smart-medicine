package com.medical.smartmedicine.sciencevideo.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.sciencevideo.dto.VideoCreateDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoQueryDTO;
import com.medical.smartmedicine.sciencevideo.dto.VideoUpdateDTO;
import com.medical.smartmedicine.sciencevideo.vo.VideoVO;

/**
 * 科普视频服务接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface VideoService {

    /**
     * 分页查询视频列表
     *
     * @param queryDTO 查询条件
     * @return 视频分页列表
     */
    PageResult<VideoVO> listVideos(VideoQueryDTO queryDTO);

    /**
     * 获取视频详情
     *
     * @param id 视频ID
     * @return 视频详情
     */
    VideoVO getVideoById(Integer id);

    /**
     * 创建视频(管理员)
     *
     * @param createDTO 创建信息
     * @return 创建的视频信息
     */
    VideoVO createVideo(VideoCreateDTO createDTO);

    /**
     * 更新视频(管理员)
     *
     * @param id 视频ID
     * @param updateDTO 更新信息
     * @return 更新后的视频信息
     */
    VideoVO updateVideo(Integer id, VideoUpdateDTO updateDTO);

    /**
     * 删除视频(管理员)
     *
     * @param id 视频ID
     */
    void deleteVideo(Integer id);
}
