package com.medical.smartmedicine.medicine.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.medicine.dto.MedicineCreateDTO;
import com.medical.smartmedicine.medicine.dto.MedicineQueryDTO;
import com.medical.smartmedicine.medicine.dto.MedicineUpdateDTO;
import com.medical.smartmedicine.medicine.service.MedicineService;
import com.medical.smartmedicine.medicine.vo.MedicineVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品管理Controller
 * 处理药品信息的查询、浏览等操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.MEDICINE_PATH)
@Tag(name = "药品管理", description = "药品信息查询相关接口")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * 分页查询药品列表
     *
     * @param queryDTO 查询参数
     * @return 药品列表
     */
    @GetMapping
    @Operation(summary = "分页查询药品列表", description = "根据条件分页查询药品信息")
    public Result<PageResult<MedicineVO>> listMedicines(@Valid MedicineQueryDTO queryDTO) {
        log.info("查询药品列表: page={}, size={}, type={}, keyword={}", 
                queryDTO.getPage(), queryDTO.getSize(), 
                queryDTO.getMedicineType(), queryDTO.getKeyword());
        PageResult<MedicineVO> result = medicineService.listMedicines(queryDTO);
        
        return Result.success(result);
    }

    /**
     * 获取药品详情
     *
     * @param id 药品ID
     * @return 药品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取药品详情", description = "根据ID获取药品的详细信息")
    public Result<MedicineVO> getMedicineById(
            @Parameter(description = "药品ID", required = true)
            @PathVariable Integer id) {
        log.info("获取药品详情: id={}", id);
        MedicineVO medicineVO = medicineService.getMedicineById(id);
        
        return Result.success(medicineVO);
    }

    /**
     * 根据疾病ID获取关联药品
     *
     * @param illnessId 疾病ID
     * @return 药品列表
     */
    @GetMapping("/illness/{illnessId}")
    @Operation(summary = "获取疾病关联药品", description = "根据疾病ID获取推荐的关联药品")
    public Result<List<MedicineVO>> getMedicinesByIllnessId(
            @Parameter(description = "疾病ID", required = true)
            @PathVariable Integer illnessId) {
        log.info("获取疾病关联药品: illnessId={}", illnessId);
        List<MedicineVO> list = medicineService.getMedicinesByIllnessId(illnessId);
        
        return Result.success(list);
    }

    /**
     * 搜索药品
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索药品", description = "根据关键词搜索药品")
    public Result<List<MedicineVO>> searchMedicines(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        log.info("搜索药品: keyword={}", keyword);
        List<MedicineVO> list = medicineService.searchMedicines(keyword);
        
        return Result.success(list);
    }

    /**
     * 创建药品(管理员)
     *
     * @param createDTO 药品创建信息
     * @return 创建的药品信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建药品(管理员)", description = "添加新的药品信息,仅管理员可操作")
    public Result<MedicineVO> createMedicine(@Valid @RequestBody MedicineCreateDTO createDTO) {
        log.info("创建药品: medicineName={}", createDTO.getMedicineName());
        MedicineVO medicineVO = medicineService.createMedicine(createDTO);
        
        return Result.success("药品创建成功", medicineVO);
    }

    /**
     * 更新药品(管理员)
     *
     * @param id 药品ID
     * @param updateDTO 更新信息
     * @return 更新后的药品信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新药品(管理员)", description = "更新药品信息,仅管理员可操作")
    public Result<MedicineVO> updateMedicine(
            @Parameter(description = "药品ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody MedicineUpdateDTO updateDTO) {
        log.info("更新药品: id={}", id);
        MedicineVO medicineVO = medicineService.updateMedicine(id, updateDTO);
        
        return Result.success("药品更新成功", medicineVO);
    }

    /**
     * 删除药品(管理员)
     *
     * @param id 药品ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除药品(管理员)", description = "删除指定药品,仅管理员可操作")
    public Result<Void> deleteMedicine(
            @Parameter(description = "药品ID", required = true)
            @PathVariable Integer id) {
        log.info("删除药品: id={}", id);
        medicineService.deleteMedicine(id);
        
        return Result.success("药品删除成功");
    }
}
