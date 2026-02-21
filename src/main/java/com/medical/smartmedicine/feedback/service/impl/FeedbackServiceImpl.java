package com.medical.smartmedicine.feedback.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.feedback.dto.FeedbackSubmitDTO;
import com.medical.smartmedicine.feedback.service.FeedbackService;
import com.medical.smartmedicine.feedback.vo.FeedbackVO;
import com.medical.smartmedicine.mapper.FeedbackMapper;
import com.medical.smartmedicine.mapper.UserMapper;
import com.medical.smartmedicine.model.entity.Feedback;
import com.medical.smartmedicine.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 反馈服务实现
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackMapper feedbackMapper;
    private final UserMapper userMapper;

    @Override
    public void submitFeedback(FeedbackSubmitDTO submitDTO, Integer userId) {
        log.info("提交反馈: userId={}, title={}", userId, submitDTO.getFeedbackTitle());

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 构建反馈实体(注意: feedback表没有user_id字段,只存储name)
        Feedback feedback = Feedback.builder()
                .name(user.getUserAccount())
                .email(submitDTO.getContact() != null ? submitDTO.getContact() : user.getUserEmail())
                .title(submitDTO.getFeedbackTitle())
                .content(submitDTO.getFeedbackContent())
                .build();

        feedbackMapper.insert(feedback);
        log.info("反馈提交成功: feedbackId={}", feedback.getId());
    }

    @Override
    public PageResult<FeedbackVO> getMyFeedbacks(Integer userId, Integer page, Integer size) {
        log.info("查询我的反馈: userId={}, page={}, size={}", userId, page, size);

        // 查询用户信息(获取用户账号名)
        User user = userMapper.selectById(userId);
        if (user == null) {
            // 用户不存在,直接返回空列表(而不是抛异常)
            log.warn("查询反馈时发现用户不存在: userId={}", userId);
            return PageResult.empty();
        }

        // 根据name字段查询(因为feedback表没有user_id字段)
        Page<Feedback> feedbackPage = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getName, user.getUserAccount())
                .orderByDesc(Feedback::getCreateTime);

        feedbackMapper.selectPage(feedbackPage, wrapper);

        return PageResult.of(feedbackPage, this::convertToVO);
    }

    @Override
    public PageResult<FeedbackVO> getAllFeedbacks(Integer page, Integer size) {
        log.info("查询所有反馈: page={}, size={}", page, size);

        Page<Feedback> feedbackPage = new Page<>(page, size);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Feedback::getCreateTime);

        feedbackMapper.selectPage(feedbackPage, wrapper);

        // feedback表没有user_id字段,直接返回name信息
        return PageResult.of(feedbackPage, this::convertToVO);
    }

    @Override
    public FeedbackVO updateMyFeedback(Integer id, Integer userId, FeedbackSubmitDTO submitDTO) {
        log.info("用户修改反馈: userId={}, feedbackId={}", userId, id);

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 查询反馈是否存在
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ErrorCodeEnum.FEEDBACK_NOT_FOUND, "反馈不存在");
        }

        // 验证反馈是否属于该用户
        if (!feedback.getName().equals(user.getUserAccount())) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED, "无权限修改此反馈");
        }

        // 更新反馈内容
        feedback.setTitle(submitDTO.getFeedbackTitle());
        feedback.setContent(submitDTO.getFeedbackContent());
        feedback.setEmail(submitDTO.getContact() != null ? submitDTO.getContact() : user.getUserEmail());
        
        int rows = feedbackMapper.updateById(feedback);
        if (rows <= 0) {
            log.error("反馈更新失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "反馈更新失败");
        }

        log.info("反馈更新成功: feedbackId={}", id);
        return convertToVO(feedback);
    }

    @Override
    public void deleteMyFeedback(Integer id, Integer userId) {
        log.info("用户删除反馈: userId={}, feedbackId={}", userId, id);

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }

        // 查询反馈是否存在
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ErrorCodeEnum.FEEDBACK_NOT_FOUND, "反馈不存在");
        }

        // 验证反馈是否属于该用户
        if (!feedback.getName().equals(user.getUserAccount())) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED, "无权限删除此反馈");
        }

        // 删除反馈
        int rows = feedbackMapper.deleteById(id);
        if (rows <= 0) {
            log.error("反馈删除失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "反馈删除失败");
        }

        log.info("反馈删除成功: feedbackId={}", id);
    }

    @Override
    public FeedbackVO updateFeedback(Integer id, FeedbackSubmitDTO submitDTO) {
        log.info("管理员修改反馈: feedbackId={}", id);

        // 查询反馈是否存在
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ErrorCodeEnum.FEEDBACK_NOT_FOUND, "反馈不存在");
        }

        // 更新反馈内容
        feedback.setTitle(submitDTO.getFeedbackTitle());
        feedback.setContent(submitDTO.getFeedbackContent());
        if (submitDTO.getContact() != null) {
            feedback.setEmail(submitDTO.getContact());
        }
        
        int rows = feedbackMapper.updateById(feedback);
        if (rows <= 0) {
            log.error("反馈更新失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "反馈更新失败");
        }

        log.info("反馈更新成功: feedbackId={}", id);
        return convertToVO(feedback);
    }

    @Override
    public void deleteFeedback(Integer id) {
        log.info("管理员删除反馈: feedbackId={}", id);

        // 查询反馈是否存在
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(ErrorCodeEnum.FEEDBACK_NOT_FOUND, "反馈不存在");
        }

        // 删除反馈
        int rows = feedbackMapper.deleteById(id);
        if (rows <= 0) {
            log.error("反馈删除失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "反馈删除失败");
        }

        log.info("反馈删除成功: feedbackId={}", id);
    }

    /**
     * 转换为VO
     */
    private FeedbackVO convertToVO(Feedback feedback) {
        FeedbackVO vo = new FeedbackVO();
        BeanUtil.copyProperties(feedback, vo);
        vo.setFeedbackTitle(feedback.getTitle());
        vo.setFeedbackContent(feedback.getContent());
        vo.setContact(feedback.getEmail());
        vo.setUserAccount(feedback.getName());
        return vo;
    }
}
