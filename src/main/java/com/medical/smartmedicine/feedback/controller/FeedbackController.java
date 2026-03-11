package com.medical.smartmedicine.feedback.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.common.util.UserContextHolder;
import com.medical.smartmedicine.feedback.dto.FeedbackSubmitDTO;
import com.medical.smartmedicine.feedback.service.FeedbackService;
import com.medical.smartmedicine.feedback.vo.FeedbackVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈Controller
 * 处理用户反馈相关操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.FEEDBACK_PATH)
@Tag(name = "用户反馈", description = "用户反馈提交和查询接口")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交反馈
     *
     * @param submitDTO 反馈内容
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "提交反馈", description = "用户提交意见反馈")
    public Result<Void> submitFeedback(@Valid @RequestBody FeedbackSubmitDTO submitDTO) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();
        
        log.info("提交反馈请求: userId={}, title={}", userId, submitDTO.getFeedbackTitle());
        feedbackService.submitFeedback(submitDTO, userId);
        return Result.success("反馈提交成功,感谢您的建议");
    }

    /**
     * 查询我的反馈
     *
     * @param page 页码
     * @param size 每页大小
     * @return 反馈列表
     */
    @GetMapping("/my")
    @Operation(summary = "查询我的反馈", description = "查询当前用户的反馈列表")
    public Result<PageResult<FeedbackVO>> getMyFeedbacks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();
        
        log.info("查询我的反馈: userId={}, page={}, size={}", userId, page, size);
        PageResult<FeedbackVO> result = feedbackService.getMyFeedbacks(userId, page, size);
        return Result.success("查询成功", result);
    }

    /**
     * 修改我的反馈
     *
     * @param id 反馈ID
     * @param submitDTO 反馈内容
     * @return 操作结果
     */
    @PutMapping("/my/{id}")
    @Operation(summary = "修改我的反馈", description = "用户修改自己的反馈")
    public Result<FeedbackVO> updateMyFeedback(
            @Parameter(description = "反馈ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody FeedbackSubmitDTO submitDTO) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();
        
        log.info("修改反馈: userId={}, feedbackId={}", userId, id);
        FeedbackVO result = feedbackService.updateMyFeedback(id, userId, submitDTO);
        return Result.success("反馈修改成功", result);
    }

    /**
     * 删除我的反馈
     *
     * @param id 反馈ID
     * @return 操作结果
     */
    @DeleteMapping("/my/{id}")
    @Operation(summary = "删除我的反馈", description = "用户删除自己的反馈")
    public Result<Void> deleteMyFeedback(
            @Parameter(description = "反馈ID", required = true) @PathVariable Integer id) {
        // 从ThreadLocal获取当前用户ID
        Integer userId = UserContextHolder.getUserId();
        
        log.info("删除反馈: userId={}, feedbackId={}", userId, id);
        feedbackService.deleteMyFeedback(id, userId);
        return Result.success("反馈删除成功");
    }

    /**
     * 查询所有反馈(管理员)
     *
     * @param page 页码
     * @param size 每页大小
     * @return 反馈列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询所有反馈", description = "查询所有用户反馈(仅管理员)")
    public Result<PageResult<FeedbackVO>> getAllFeedbacks(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询所有反馈: page={}, size={}", page, size);
        PageResult<FeedbackVO> result = feedbackService.getAllFeedbacks(page, size);
        return Result.success("查询成功", result);
    }

    /**
     * 修改任意反馈(管理员)
     *
     * @param id 反馈ID
     * @param submitDTO 反馈内容
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "修改反馈(管理员)", description = "管理员修改任意用户的反馈")
    public Result<FeedbackVO> updateFeedback(
            @Parameter(description = "反馈ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody FeedbackSubmitDTO submitDTO) {
        
        log.info("管理员修改反馈: feedbackId={}", id);
        FeedbackVO result = feedbackService.updateFeedback(id, submitDTO);
        return Result.success("反馈修改成功", result);
    }

    /**
     * 删除任意反馈(管理员)
     *
     * @param id 反馈ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除反馈(管理员)", description = "管理员删除任意用户的反馈")
    public Result<Void> deleteFeedback(
            @Parameter(description = "反馈ID", required = true) @PathVariable Integer id) {
        
        log.info("管理员删除反馈: feedbackId={}", id);
        feedbackService.deleteFeedback(id);
        return Result.success("反馈删除成功");
    }
}
