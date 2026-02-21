package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户反馈数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

}
