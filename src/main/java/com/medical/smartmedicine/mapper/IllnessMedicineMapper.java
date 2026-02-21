package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.IllnessMedicine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 疾病药品关联数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface IllnessMedicineMapper extends BaseMapper<IllnessMedicine> {

}
