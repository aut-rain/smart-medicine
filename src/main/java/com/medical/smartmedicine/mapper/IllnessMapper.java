package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.Illness;
import org.apache.ibatis.annotations.Mapper;

/**
 * 疾病数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface IllnessMapper extends BaseMapper<Illness> {

}
