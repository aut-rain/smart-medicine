package com.medical.smartmedicine.history.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.history.vo.HistoryVO;

/**
 * 浏览历史服务接口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface HistoryService {

    /**
     * 添加浏览历史
     *
     * @param userId 用户ID
     * @param operateType 操作类型
     * @param operateId 操作对象ID
     * @param operateName 操作对象名称
     */
    void addHistory(Integer userId, Integer operateType, Integer operateId, String operateName);

    /**
     * 查询我的浏览历史
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 历史记录列表
     */
    PageResult<HistoryVO> getMyHistories(Integer userId, Integer page, Integer size);

    /**
     * 清空我的浏览历史
     *
     * @param userId 用户ID
     */
    void clearMyHistories(Integer userId);
}
