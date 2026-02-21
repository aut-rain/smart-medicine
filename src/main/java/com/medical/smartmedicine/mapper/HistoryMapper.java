package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.History;
import org.apache.ibatis.annotations.Mapper;

/**
 * 浏览历史数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface HistoryMapper extends BaseMapper<History> {

}
