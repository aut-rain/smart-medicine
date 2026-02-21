package com.medical.smartmedicine.medicine.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.medicine.dto.MedicineCreateDTO;
import com.medical.smartmedicine.medicine.dto.MedicineQueryDTO;
import com.medical.smartmedicine.medicine.dto.MedicineUpdateDTO;
import com.medical.smartmedicine.medicine.vo.MedicineVO;

import java.util.List;

/**
 * 药品服务接口
 * 负责药品信息查询等业务
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
public interface MedicineService {

    /**
     * 分页查询药品列表
     *
     * @param queryDTO 查询参数
     * @return 药品列表
     */
    PageResult<MedicineVO> listMedicines(MedicineQueryDTO queryDTO);

    /**
     * 根据ID获取药品详情
     *
     * @param id 药品ID
     * @return 药品详情
     */
    MedicineVO getMedicineById(Integer id);

    /**
     * 根据疾病ID获取关联药品
     *
     * @param illnessId 疾病ID
     * @return 药品列表
     */
    List<MedicineVO> getMedicinesByIllnessId(Integer illnessId);

    /**
     * 搜索药品
     * 根据关键词模糊搜索药品名称、功效等
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<MedicineVO> searchMedicines(String keyword);

    /**
     * 创建药品(管理员)
     *
     * @param createDTO 创建信息
     * @return 创建的药品信息
     */
    MedicineVO createMedicine(MedicineCreateDTO createDTO);

    /**
     * 更新药品(管理员)
     *
     * @param id 药品ID
     * @param updateDTO 更新信息
     * @return 更新后的药品信息
     */
    MedicineVO updateMedicine(Integer id, MedicineUpdateDTO updateDTO);

    /**
     * 删除药品(管理员)
     *
     * @param id 药品ID
     */
    void deleteMedicine(Integer id);
}
