package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.MedicalNews;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医疗咨询数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface MedicalNewsMapper extends BaseMapper<MedicalNews> {

}
