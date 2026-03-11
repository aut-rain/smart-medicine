package com.medical.smartmedicine.medicine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.enums.MedicineTypeEnum;
import com.medical.smartmedicine.common.enums.OperateTypeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.util.UserContextHolder;
import com.medical.smartmedicine.history.service.HistoryService;
import com.medical.smartmedicine.mapper.IllnessMedicineMapper;
import com.medical.smartmedicine.mapper.MedicineMapper;
import com.medical.smartmedicine.medicine.dto.MedicineCreateDTO;
import com.medical.smartmedicine.medicine.dto.MedicineQueryDTO;
import com.medical.smartmedicine.medicine.dto.MedicineUpdateDTO;
import com.medical.smartmedicine.medicine.service.MedicineService;
import com.medical.smartmedicine.medicine.vo.MedicineVO;
import com.medical.smartmedicine.model.entity.IllnessMedicine;
import com.medical.smartmedicine.model.entity.Medicine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineMapper medicineMapper;
    private final IllnessMedicineMapper illnessMedicineMapper;
    private final OssClient ossClient;
    private final HistoryService historyService;

    @Override
    public PageResult<MedicineVO> listMedicines(MedicineQueryDTO queryDTO) {
        // 构建分页对象
        Page<Medicine> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        
        // 按药品类型过滤
        if (queryDTO.getMedicineType() != null) {
            wrapper.eq(Medicine::getMedicineType, queryDTO.getMedicineType());
        }
        
        // 价格区间过滤
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(Medicine::getMedicinePrice, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(Medicine::getMedicinePrice, queryDTO.getMaxPrice());
        }
        
        // 关键词搜索(药品名称、功效、关键字)
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Medicine::getMedicineName, queryDTO.getKeyword())
                             .or()
                             .like(Medicine::getMedicineEffect, queryDTO.getKeyword())
                             .or()
                             .like(Medicine::getKeyword, queryDTO.getKeyword()));
        }
        
        wrapper.orderByDesc(Medicine::getId);

        // 执行查询
        Page<Medicine> medicinePage = medicineMapper.selectPage(page, wrapper);

        // 转换为VO
        return PageResult.of(medicinePage, this::convertToVO);
    }

    @Override
    public MedicineVO getMedicineById(Integer id) {
        Medicine medicine = medicineMapper.selectById(id);
        if (medicine == null) {
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }
        
        // 添加历史记录（异步处理，不影响主流程）
        try {
            Integer userId = UserContextHolder.getUserIdOrNull();
            if (userId != null) {
                historyService.addHistory(
                    userId,
                    OperateTypeEnum.VIEW_MEDICINE.getType(),
                    id,
                    medicine.getMedicineName()
                );
            }
        } catch (Exception e) {
            log.warn("添加药品浏览历史失败: medicineId={}", id, e);
        }
        
        return convertToVO(medicine);
    }

    @Override
    public List<MedicineVO> getMedicinesByIllnessId(Integer illnessId) {
        // 查询疾病关联的药品ID列表
        LambdaQueryWrapper<IllnessMedicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IllnessMedicine::getIllnessId, illnessId);
        List<IllnessMedicine> illnessMedicines = illnessMedicineMapper.selectList(wrapper);

        if (illnessMedicines.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询药品详情
        List<Integer> medicineIds = illnessMedicines.stream()
                .map(IllnessMedicine::getMedicineId)
                .collect(Collectors.toList());

        List<Medicine> medicines = medicineMapper.selectBatchIds(medicineIds);
        return medicines.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineVO> searchMedicines(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Medicine::getMedicineName, keyword)
                         .or()
                         .like(Medicine::getMedicineEffect, keyword)
                         .or()
                         .like(Medicine::getKeyword, keyword));
        wrapper.orderByDesc(Medicine::getId)
               .last("LIMIT 20");

        List<Medicine> medicines = medicineMapper.selectList(wrapper);
        return medicines.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换Medicine实体为MedicineVO
     */
    private MedicineVO convertToVO(Medicine medicine) {
        MedicineVO vo = new MedicineVO();
        BeanUtil.copyProperties(medicine, vo);
        
        // 设置药品类型描述
        vo.setMedicineTypeDesc(MedicineTypeEnum.getDescByType(medicine.getMedicineType()));
        
        // BigDecimal转Double
        if (medicine.getMedicinePrice() != null) {
            vo.setMedicinePrice(medicine.getMedicinePrice().doubleValue());
        }
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineVO createMedicine(MedicineCreateDTO createDTO) {
        log.info("创建药品: medicineName={}", createDTO.getMedicineName());
        
        // 构建实体
        Medicine medicine = Medicine.builder()
                .medicineName(createDTO.getMedicineName())
                .keyword(createDTO.getKeyword())
                .medicineEffect(createDTO.getMedicineEffect())
                .medicineBrand(createDTO.getMedicineBrand())
                .interaction(createDTO.getInteraction())
                .taboo(createDTO.getTaboo())
                .usAge(createDTO.getUsAge())
                .medicineType(createDTO.getMedicineType())
                .imgPath(createDTO.getImgPath())
                .medicinePrice(createDTO.getMedicinePrice())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 保存到数据库
        int rows = medicineMapper.insert(medicine);
        if (rows <= 0) {
            log.error("药品创建失败: {}", createDTO);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "药品创建失败");
        }

        log.info("药品创建成功: id={}", medicine.getId());
        return convertToVO(medicine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicineVO updateMedicine(Integer id, MedicineUpdateDTO updateDTO) {
        log.info("更新药品: id={}", id);
        
        // 查询药品是否存在
        Medicine medicine = medicineMapper.selectById(id);
        if (medicine == null) {
            log.warn("药品不存在: id={}", id);
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }

        // 更新字段(只更新非空字段)
        if (StrUtil.isNotBlank(updateDTO.getMedicineName())) {
            medicine.setMedicineName(updateDTO.getMedicineName());
        }
        if (updateDTO.getKeyword() != null) {
            medicine.setKeyword(updateDTO.getKeyword());
        }
        if (updateDTO.getMedicineEffect() != null) {
            medicine.setMedicineEffect(updateDTO.getMedicineEffect());
        }
        if (updateDTO.getMedicineBrand() != null) {
            medicine.setMedicineBrand(updateDTO.getMedicineBrand());
        }
        if (updateDTO.getInteraction() != null) {
            medicine.setInteraction(updateDTO.getInteraction());
        }
        if (updateDTO.getTaboo() != null) {
            medicine.setTaboo(updateDTO.getTaboo());
        }
        if (updateDTO.getUsAge() != null) {
            medicine.setUsAge(updateDTO.getUsAge());
        }
        if (updateDTO.getMedicineType() != null) {
            medicine.setMedicineType(updateDTO.getMedicineType());
        }
        if (updateDTO.getImgPath() != null) {
            medicine.setImgPath(updateDTO.getImgPath());
        }
        if (updateDTO.getMedicinePrice() != null) {
            medicine.setMedicinePrice(updateDTO.getMedicinePrice());
        }
        medicine.setUpdateTime(new Date());

        // 更新到数据库
        int rows = medicineMapper.updateById(medicine);
        if (rows <= 0) {
            log.error("药品更新失败: id={}", id);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "药品更新失败");
        }

        log.info("药品更新成功: id={}", id);
        return convertToVO(medicine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMedicine(Integer id) {
        log.info("删除药品: id={}", id);
        
        // 查询药品是否存在
        Medicine medicine = medicineMapper.selectById(id);
        if (medicine == null) {
            log.warn("药品不存在: id={}", id);
            throw new BusinessException(ResultCode.MEDICINE_NOT_FOUND);
        }

        String imgPath = medicine.getImgPath();

        // 删除药品
        int rows = medicineMapper.deleteById(id);
        if (rows <= 0) {
            log.error("药品删除失败: id={}", id);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "药品删除失败");
        }

        // 数据库删除成功后，删除OSS中的图片
        if (StrUtil.isNotBlank(imgPath)) {
            log.info("删除药品图片: {}", imgPath);
            boolean deleted = ossClient.delete(imgPath);
            if (!deleted) {
                log.warn("药品图片删除失败，但不影响主流程: {}", imgPath);
            }
        }

        log.info("药品删除成功: id={}, imgPath={}", id, imgPath);
    }
}
