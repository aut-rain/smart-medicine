package com.medical.smartmedicine.history.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.enums.OperateTypeEnum;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.history.service.HistoryService;
import com.medical.smartmedicine.history.vo.HistoryVO;
import com.medical.smartmedicine.mapper.HistoryMapper;
import com.medical.smartmedicine.model.entity.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 浏览历史服务实现
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryMapper historyMapper;

    @Async
    @Override
    public void addHistory(Integer userId, Integer operateType, Integer operateId, String operateName) {
        log.info("添加浏览历史: userId={}, operateType={}, operateId={}, operateName={}", 
                userId, operateType, operateId, operateName);

        // 检查是否已存在相同的历史记录（同一用户查看同一对象）
        LambdaQueryWrapper<History> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(History::getUserId, userId)
                .eq(History::getOperateType, operateType)
                .eq(History::getKeyword, operateName);
        
        History existingHistory = historyMapper.selectOne(queryWrapper);
        
        if (existingHistory != null) {
            // 如果已存在，更新时间（移到最新）
            existingHistory.setUpdateTime(new Date());
            historyMapper.updateById(existingHistory);
            log.debug("更新已存在的浏览历史: id={}", existingHistory.getId());
        } else {
            // 如果不存在，创建新记录
            History history = History.builder()
                    .userId(userId)
                    .operateType(operateType)
                    .keyword(operateName)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            
            historyMapper.insert(history);
            log.debug("新增浏览历史: id={}", history.getId());
        }
    }

    @Override
    public PageResult<HistoryVO> getMyHistories(Integer userId, Integer page, Integer size) {
        log.info("查询浏览历史: userId={}, page={}, size={}", userId, page, size);

        Page<History> historyPage = new Page<>(page, size);
        LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(History::getUserId, userId)
                .orderByDesc(History::getUpdateTime);  // 按更新时间倒序，最近查看的在前面

        historyMapper.selectPage(historyPage, wrapper);

        return PageResult.of(historyPage, this::convertToVO);
    }

    @Override
    public void clearMyHistories(Integer userId) {
        log.info("清空浏览历史: userId={}", userId);

        LambdaQueryWrapper<History> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(History::getUserId, userId);
        historyMapper.delete(wrapper);
    }

    /**
     * 转换为VO
     */
    private HistoryVO convertToVO(History history) {
        HistoryVO vo = new HistoryVO();
        BeanUtil.copyProperties(history, vo);
        
        // 设置操作类型描述
        OperateTypeEnum operateTypeEnum = OperateTypeEnum.fromType(history.getOperateType());
        vo.setOperateTypeDesc(operateTypeEnum.getDescription());
        vo.setOperateName(history.getKeyword());  // keyword映射为operateName
        
        return vo;
    }
}
