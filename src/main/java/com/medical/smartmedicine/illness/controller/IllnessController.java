package com.medical.smartmedicine.illness.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.illness.dto.IllnessCreateDTO;
import com.medical.smartmedicine.illness.dto.IllnessQueryDTO;
import com.medical.smartmedicine.illness.dto.IllnessUpdateDTO;
import com.medical.smartmedicine.illness.service.IllnessService;
import com.medical.smartmedicine.illness.vo.IllnessDetailVO;
import com.medical.smartmedicine.illness.vo.IllnessVO;
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
 * 疾病管理Controller
 * 处理疾病信息的查询、浏览等操作
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.ILLNESS_PATH)
@Tag(name = "疾病管理", description = "疾病信息查询相关接口")
@RequiredArgsConstructor
public class IllnessController {

    private final IllnessService illnessService;

    /**
     * 分页查询疾病列表
     *
     * @param queryDTO 查询参数
     * @return 疾病列表
     */
    @GetMapping
    @Operation(summary = "分页查询疾病列表", description = "根据条件分页查询疾病信息")
    public Result<PageResult<IllnessVO>> listIllnesses(@Valid IllnessQueryDTO queryDTO) {
        log.info("查询疾病列表: page={}, size={}, kindId={}, keyword={}", 
                queryDTO.getPage(), queryDTO.getSize(), 
                queryDTO.getKindId(), queryDTO.getKeyword());
        PageResult<IllnessVO> result = illnessService.listIllnesses(queryDTO);
        
        return Result.success(result);
    }

    /**
     * 获取疾病详情
     *
     * @param id 疾病ID
     * @return 疾病详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取疾病详情", description = "根据ID获取疾病的详细信息,包括关联药品")
    public Result<IllnessDetailVO> getIllnessById(
            @Parameter(description = "疾病ID", required = true)
            @PathVariable Integer id) {
        log.info("获取疾病详情: id={}", id);
        IllnessDetailVO detailVO = illnessService.getIllnessById(id);
        
        return Result.success(detailVO);
    }

    /**
     * 获取热门疾病列表
     *
     * @param limit 返回数量
     * @return 热门疾病列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门疾病", description = "按浏览量获取热门疾病列表")
    public Result<List<IllnessVO>> getHotIllnesses(
            @Parameter(description = "返回数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门疾病: limit={}", limit);
        List<IllnessVO> list = illnessService.getHotIllnesses(limit);
        
        return Result.success(list);
    }

    /**
     * 搜索疾病
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索疾病", description = "根据关键词搜索疾病")
    public Result<List<IllnessVO>> searchIllnesses(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        log.info("搜索疾病: keyword={}", keyword);
        List<IllnessVO> list = illnessService.searchIllnesses(keyword);
        
        return Result.success(list);
    }

    /**
     * 分页搜索疾病
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 分页搜索结果
     */
    @GetMapping("/search/paged")
    @Operation(summary = "分页搜索疾病", description = "根据关键词分页搜索疾病名称和症状，关键词为空时返回全部")
    public Result<PageResult<IllnessVO>> searchIllnessesPaged(
            @Parameter(description = "搜索关键词（可选）")
            @RequestParam(required = false, defaultValue = "") String keyword,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("分页搜索疾病: keyword={}, page={}, size={}", keyword, page, size);
        PageResult<IllnessVO> result = illnessService.searchIllnessesPaged(keyword, page, size);
        
        return Result.success(result);
    }

    /**
     * 创建疾病(管理员)
     *
     * @param createDTO 疾病创建信息
     * @return 创建的疾病信息
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建疾病(管理员)", description = "添加新的疾病信息,仅管理员可操作")
    public Result<IllnessVO> createIllness(@Valid @RequestBody IllnessCreateDTO createDTO) {
        log.info("创建疾病: illnessName={}", createDTO.getIllnessName());
        IllnessVO illnessVO = illnessService.createIllness(createDTO);
        
        return Result.success("疾病创建成功", illnessVO);
    }

    /**
     * 更新疾病(管理员)
     *
     * @param id 疾病ID
     * @param updateDTO 更新信息
     * @return 更新后的疾病信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新疾病(管理员)", description = "更新疾病信息,仅管理员可操作")
    public Result<IllnessVO> updateIllness(
            @Parameter(description = "疾病ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody IllnessUpdateDTO updateDTO) {
        log.info("更新疾病: id={}", id);
        IllnessVO illnessVO = illnessService.updateIllness(id, updateDTO);
        
        return Result.success("疾病更新成功", illnessVO);
    }

    /**
     * 删除疾病(管理员)
     *
     * @param id 疾病ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除疾病(管理员)", description = "删除指定疾病,仅管理员可操作")
    public Result<Void> deleteIllness(
            @Parameter(description = "疾病ID", required = true)
            @PathVariable Integer id) {
        log.info("删除疾病: id={}", id);
        illnessService.deleteIllness(id);
        
        return Result.success("疾病删除成功");
    }
}
