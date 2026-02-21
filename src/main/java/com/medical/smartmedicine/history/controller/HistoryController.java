package com.medical.smartmedicine.history.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.history.service.HistoryService;
import com.medical.smartmedicine.history.vo.HistoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 浏览历史Controller
 * 处理用户浏览历史记录
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.HISTORY_PATH)
@Tag(name = "浏览历史", description = "用户浏览历史记录接口")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * 记录浏览历史
     *
     * @param userId 用户ID
     * @param operateType 操作类型: 2-查看疾病详情, 4-查看药品详情, 5-观看视频
     * @param operateId 操作对象ID
     * @param operateName 操作对象名称
     * @return 操作结果
     */
    @PostMapping("/record")
    @Operation(summary = "记录浏览历史", description = "记录用户的浏览历史")
    public Result<Void> recordHistory(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Integer userId,
            @Parameter(description = "操作类型: 2-查看疾病, 4-查看药品, 5-观看视频", required = true)
            @RequestParam Integer operateType,
            @Parameter(description = "操作对象ID", required = true)
            @RequestParam Integer operateId,
            @Parameter(description = "操作对象名称", required = true)
            @RequestParam String operateName) {

        log.info("记录浏览历史: userId={}, operateType={}, operateId={}, operateName={}",
                userId, operateType, operateId, operateName);

        historyService.addHistory(userId, operateType, operateId, operateName);

        return Result.success("记录成功");
    }

    /**
     * 查询我的浏览历史
     *
     * @param page 页码
     * @param size 每页大小
     * @return 历史记录列表
     */
    @GetMapping
    @Operation(summary = "查询我的浏览历史", description = "查询当前用户的浏览历史记录")
    public Result<PageResult<HistoryVO>> getMyHistories(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(value = "userId", required = false) Integer userId) {

        if (userId == null) {
            userId = 1;
        }

        log.info("查询浏览历史: userId={}, page={}, size={}", userId, page, size);
        PageResult<HistoryVO> result = historyService.getMyHistories(userId, page, size);

        return Result.success("查询成功", result);
    }

    /**
     * 清空我的浏览历史
     *
     * @return 操作结果
     */
    @DeleteMapping
    @Operation(summary = "清空浏览历史", description = "清空当前用户的所有浏览历史")
    public Result<Void> clearMyHistories(
            @Parameter(description = "用户ID", hidden = true)
            @RequestAttribute(value = "userId", required = false) Integer userId) {

        if (userId == null) {
            userId = 1;
        }

        log.info("清空浏览历史: userId={}", userId);
        historyService.clearMyHistories(userId);

        return Result.success("清空成功");
    }
}
