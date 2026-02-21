package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.IllnessKind;
import org.apache.ibatis.annotations.Mapper;

/**
 * 疾病分类数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface IllnessKindMapper extends BaseMapper<IllnessKind> {

}
