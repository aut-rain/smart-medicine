package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.Pageview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 浏览量统计数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface PageviewMapper extends BaseMapper<Pageview> {

}
