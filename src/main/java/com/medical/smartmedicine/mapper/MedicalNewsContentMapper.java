package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.MedicalNewsContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医疗资讯正文缓存Mapper
 */
@Mapper
public interface MedicalNewsContentMapper extends BaseMapper<MedicalNewsContent> {
}
