package com.medical.smartmedicine.feedback.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.feedback.dto.FeedbackSubmitDTO;
import com.medical.smartmedicine.feedback.vo.FeedbackVO;

/**
 * 反馈服务接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface FeedbackService {

    /**
     * 提交反馈
     *
     * @param submitDTO 反馈内容
     * @param userId 用户ID
     */
    void submitFeedback(FeedbackSubmitDTO submitDTO, Integer userId);

    /**
     * 查询我的反馈列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 反馈列表
     */
    PageResult<FeedbackVO> getMyFeedbacks(Integer userId, Integer page, Integer size);

    /**
     * 查询所有反馈列表(管理员)
     *
     * @param page 页码
     * @param size 每页大小
     * @return 反馈列表
     */
    PageResult<FeedbackVO> getAllFeedbacks(Integer page, Integer size);

    /**
     * 用户修改自己的反馈
     *
     * @param id 反馈ID
     * @param userId 用户ID
     * @param submitDTO 反馈内容
     * @return 反馈信息
     */
    FeedbackVO updateMyFeedback(Integer id, Integer userId, FeedbackSubmitDTO submitDTO);

    /**
     * 用户删除自己的反馈
     *
     * @param id 反馈ID
     * @param userId 用户ID
     */
    void deleteMyFeedback(Integer id, Integer userId);

    /**
     * 管理员修改任意反馈
     *
     * @param id 反馈ID
     * @param submitDTO 反馈内容
     * @return 反馈信息
     */
    FeedbackVO updateFeedback(Integer id, FeedbackSubmitDTO submitDTO);

    /**
     * 管理员删除任意反馈
     *
     * @param id 反馈ID
     */
    void deleteFeedback(Integer id);
}
