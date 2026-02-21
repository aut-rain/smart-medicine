package com.medical.smartmedicine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.smartmedicine.model.entity.Medicine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 药品数据访问层
 *
 * @author ZZY
 */
@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {

    /**
     * 根据疾病ID查询关联药品
     *
     * @param illnessId 疾病ID
     * @return 药品列表
     */
    List<Medicine> findMedicineList(@Param("illnessId") Integer illnessId);

    /**
     * 根据疾病名称模糊查询关联的药品(去重)
     *
     * @param illnessName 疾病名称
     * @return 药品列表
     */
    @Select("SELECT DISTINCT m.id AS medicine_id, " +
            "m.medicine_name, m.keyword, m.medicine_effect, " +
            "m.medicine_brand, m.interaction, m.taboo, " +
            "m.us_age, m.medicine_type, m.img_path, m.medicine_price, " +
            "m.create_time, m.update_time " +
            "FROM illness i " +
            "INNER JOIN illness_medicine im ON i.id = im.illness_id " +
            "INNER JOIN medicine m ON im.medicine_id = m.id " +
            "WHERE i.illness_name LIKE CONCAT('%', #{illnessName}, '%')")
    List<Medicine> selectByIllnessName(@Param("illnessName") String illnessName);
}
