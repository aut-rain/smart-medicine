package com.medical.smartmedicine.illness.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.enums.ErrorCodeEnum;
import com.medical.smartmedicine.common.enums.MedicineTypeEnum;
import com.medical.smartmedicine.common.enums.OperateTypeEnum;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.util.UserContextHolder;
import com.medical.smartmedicine.history.service.HistoryService;
import com.medical.smartmedicine.illness.dto.IllnessCreateDTO;
import com.medical.smartmedicine.illness.dto.IllnessQueryDTO;
import com.medical.smartmedicine.illness.dto.IllnessUpdateDTO;
import com.medical.smartmedicine.illness.service.IllnessService;
import com.medical.smartmedicine.illness.vo.IllnessDetailVO;
import com.medical.smartmedicine.illness.vo.IllnessVO;
import com.medical.smartmedicine.mapper.IllnessKindMapper;
import com.medical.smartmedicine.mapper.IllnessMapper;
import com.medical.smartmedicine.mapper.IllnessMedicineMapper;
import com.medical.smartmedicine.mapper.MedicineMapper;
import com.medical.smartmedicine.mapper.PageviewMapper;
import com.medical.smartmedicine.model.entity.Illness;
import com.medical.smartmedicine.model.entity.IllnessKind;
import com.medical.smartmedicine.model.entity.IllnessMedicine;
import com.medical.smartmedicine.model.entity.Medicine;
import com.medical.smartmedicine.model.entity.Pageview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 疾病服务实现类
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IllnessServiceImpl implements IllnessService {

    private final IllnessMapper illnessMapper;
    private final IllnessKindMapper illnessKindMapper;
    private final IllnessMedicineMapper illnessMedicineMapper;
    private final MedicineMapper medicineMapper;
    private final PageviewMapper pageviewMapper;
    private final HistoryService historyService;

    @Override
    public PageResult<IllnessVO> listIllnesses(IllnessQueryDTO queryDTO) {
        // 构建分页对象
        Page<Illness> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<Illness> wrapper = new LambdaQueryWrapper<>();
        
        // 按分类ID过滤
        if (queryDTO.getKindId() != null) {
            wrapper.eq(Illness::getKindId, queryDTO.getKindId());
        }
        
        // 关键词搜索(疾病名称或症状)
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Illness::getIllnessName, queryDTO.getKeyword())
                             .or()
                             .like(Illness::getIllnessSymptom, queryDTO.getKeyword()));
        }
        
        wrapper.orderByDesc(Illness::getId);

        // 执行查询
        Page<Illness> illnessPage = illnessMapper.selectPage(page, wrapper);

        // 转换为VO
        return PageResult.of(illnessPage, this::convertToVO);
    }

    @Override
    public IllnessDetailVO getIllnessById(Integer id) {
        // 查询疾病基本信息
        Illness illness = illnessMapper.selectById(id);
        if (illness == null) {
            throw new BusinessException(ErrorCodeEnum.ILLNESS_NOT_FOUND);
        }

        // 查询分类信息
        IllnessKind kind = illnessKindMapper.selectById(illness.getKindId());

        // 查询关联药品
        LambdaQueryWrapper<IllnessMedicine> imWrapper = new LambdaQueryWrapper<>();
        imWrapper.eq(IllnessMedicine::getIllnessId, id);
        List<IllnessMedicine> illnessMedicines = illnessMedicineMapper.selectList(imWrapper);

        List<IllnessDetailVO.MedicineSimpleVO> medicines = new ArrayList<>();
        for (IllnessMedicine im : illnessMedicines) {
            Medicine medicine = medicineMapper.selectById(im.getMedicineId());
            if (medicine != null) {
                medicines.add(IllnessDetailVO.MedicineSimpleVO.builder()
                        .id(medicine.getId())
                        .medicineName(medicine.getMedicineName())
                        .medicineEffect(medicine.getMedicineEffect())
                        .medicinePrice(medicine.getMedicinePrice() != null ? 
                                medicine.getMedicinePrice().doubleValue() : null)
                        .medicineType(medicine.getMedicineType())
                        .build());
            }
        }

        // 增加浏览量(异步处理,不影响主流程)
        try {
            increasePageview(id);
        } catch (Exception e) {
            log.warn("增加浏览量失败: illnessId={}", id, e);
        }
        
        // 添加历史记录（异步处理，不影响主流程）
        try {
            Integer userId = UserContextHolder.getUserIdOrNull();
            if (userId != null) {
                historyService.addHistory(
                    userId,
                    OperateTypeEnum.VIEW_ILLNESS.getType(),
                    id,
                    illness.getIllnessName()
                );
            }
        } catch (Exception e) {
            log.warn("添加疾病浏览历史失败: illnessId={}", id, e);
        }

        // 构建详情VO
        return IllnessDetailVO.builder()
                .id(illness.getId())
                .kindId(illness.getKindId())
                .category(kind != null ? IllnessDetailVO.IllnessCategoryVO.builder()
                        .id(kind.getId())
                        .name(kind.getName())
                        .info(kind.getInfo())
                        .build() : null)
                .illnessName(illness.getIllnessName())
                .includeReason(illness.getIncludeReason())
                .illnessSymptom(illness.getIllnessSymptom())
                .specialSymptom(illness.getSpecialSymptom())
                .medicines(medicines)
                .build();
    }

    @Override
    public List<IllnessVO> getHotIllnesses(Integer limit) {
        // 按浏览量排序查询热门疾病
        // 注意: 需要在illness表添加pageviews字段来存储浏览量
        // 这里暂时按ID倒序返回最新的疾病
        LambdaQueryWrapper<Illness> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Illness::getId)
               .last("LIMIT " + limit);

        List<Illness> illnesses = illnessMapper.selectList(wrapper);
        return illnesses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<IllnessVO> searchIllnesses(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Illness> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Illness::getIllnessName, keyword)
                         .or()
                         .like(Illness::getIllnessSymptom, keyword)
                         .or()
                         .like(Illness::getSpecialSymptom, keyword));
        wrapper.orderByDesc(Illness::getId)
               .last("LIMIT 20");

        List<Illness> illnesses = illnessMapper.selectList(wrapper);
        return illnesses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<IllnessVO> searchIllnessesPaged(String keyword, Integer page, Integer size) {
        log.info("分页搜索疾病: keyword={}, page={}, size={}", keyword, page, size);

        // 构建分页对象
        Page<Illness> illnessPage = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<Illness> wrapper = new LambdaQueryWrapper<>();
        
        // 如果关键词不为空，添加模糊查询条件
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Illness::getIllnessName, keyword)
                             .or()
                             .like(Illness::getIllnessSymptom, keyword)
                             .or()
                             .like(Illness::getSpecialSymptom, keyword));
        }
        // 关键词为空时，不添加任何条件，查询全部
        
        wrapper.orderByDesc(Illness::getId);

        // 执行分页查询
        illnessMapper.selectPage(illnessPage, wrapper);
        
        log.info("搜索结果: total={}, records={}", illnessPage.getTotal(), illnessPage.getRecords().size());

        // 转换为VO
        return PageResult.of(illnessPage, this::convertToVO);
    }

    @Override
    public void increasePageview(Integer id) {
        // 使用MyBatis-Plus更新浏览量
        LambdaQueryWrapper<Pageview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pageview::getIllnessId, id);
        Pageview pageview = pageviewMapper.selectOne(wrapper);
        
        if (pageview != null) {
            // 更新现有记录
            pageview.setPageviews(pageview.getPageviews() + 1);
            pageviewMapper.updateById(pageview);
            log.debug("增加疾病浏览量: illnessId={}, 新浏览量={}", id, pageview.getPageviews());
        } else {
            // 创建新记录
            pageview = Pageview.builder()
                    .illnessId(id)
                    .pageviews(1)
                    .build();
            pageviewMapper.insert(pageview);
            log.debug("创建疾病浏览量记录: illnessId={}", id);
        }
    }

    /**
     * 转换Illness实体为IllnessVO
     */
    private IllnessVO convertToVO(Illness illness) {
        IllnessVO vo = new IllnessVO();
        BeanUtil.copyProperties(illness, vo);
        
        // 查询分类名称
        if (illness.getKindId() != null) {
            IllnessKind kind = illnessKindMapper.selectById(illness.getKindId());
            if (kind != null) {
                vo.setKindName(kind.getName());
            }
        }
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IllnessVO createIllness(IllnessCreateDTO createDTO) {
        log.info("创建疾病: illnessName={}", createDTO.getIllnessName());
        
        // 构建实体
        Illness illness = Illness.builder()
                .kindId(createDTO.getKindId())
                .illnessName(createDTO.getIllnessName())
                .includeReason(createDTO.getIncludeReason())
                .illnessSymptom(createDTO.getIllnessSymptom())
                .specialSymptom(createDTO.getSpecialSymptom())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        // 保存到数据库
        int rows = illnessMapper.insert(illness);
        if (rows <= 0) {
            log.error("疾病创建失败: {}", createDTO);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "疾病创建失败");
        }

        log.info("疾病创建成功: id={}", illness.getId());
        return convertToVO(illness);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IllnessVO updateIllness(Integer id, IllnessUpdateDTO updateDTO) {
        log.info("更新疾病: id={}", id);
        
        // 查询疾病是否存在
        Illness illness = illnessMapper.selectById(id);
        if (illness == null) {
            log.warn("疾病不存在: id={}", id);
            throw new BusinessException(ErrorCodeEnum.ILLNESS_NOT_FOUND);
        }

        // 更新字段(只更新非空字段)
        if (updateDTO.getKindId() != null) {
            illness.setKindId(updateDTO.getKindId());
        }
        if (StrUtil.isNotBlank(updateDTO.getIllnessName())) {
            illness.setIllnessName(updateDTO.getIllnessName());
        }
        if (updateDTO.getIncludeReason() != null) {
            illness.setIncludeReason(updateDTO.getIncludeReason());
        }
        if (updateDTO.getIllnessSymptom() != null) {
            illness.setIllnessSymptom(updateDTO.getIllnessSymptom());
        }
        if (updateDTO.getSpecialSymptom() != null) {
            illness.setSpecialSymptom(updateDTO.getSpecialSymptom());
        }
        illness.setUpdateTime(new Date());

        // 更新到数据库
        int rows = illnessMapper.updateById(illness);
        if (rows <= 0) {
            log.error("疾病更新失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "疾病更新失败");
        }

        log.info("疾病更新成功: id={}", id);
        return convertToVO(illness);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIllness(Integer id) {
        log.info("删除疾病: id={}", id);
        
        // 查询疾病是否存在
        Illness illness = illnessMapper.selectById(id);
        if (illness == null) {
            log.warn("疾病不存在: id={}", id);
            throw new BusinessException(ErrorCodeEnum.ILLNESS_NOT_FOUND);
        }

        // 删除疾病
        int rows = illnessMapper.deleteById(id);
        if (rows <= 0) {
            log.error("疾病删除失败: id={}", id);
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "疾病删除失败");
        }

        log.info("疾病删除成功: id={}", id);
    }
}
