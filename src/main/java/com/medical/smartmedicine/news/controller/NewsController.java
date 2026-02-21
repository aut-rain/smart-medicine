package com.medical.smartmedicine.news.controller;

import com.medical.smartmedicine.common.constant.ApiConstant;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.common.result.Result;
import com.medical.smartmedicine.news.dto.NewsCreateDTO;
import com.medical.smartmedicine.news.dto.NewsQueryDTO;
import com.medical.smartmedicine.news.dto.NewsUpdateDTO;
import com.medical.smartmedicine.news.service.NewsService;
import com.medical.smartmedicine.news.vo.NewsDetailVO;
import com.medical.smartmedicine.news.vo.NewsVO;
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
 * 医疗资讯Controller
 * 处理资讯的查询、浏览等操作
 *
 * @author Smart Medicine Team
 */
@Slf4j
@RestController
@RequestMapping(ApiConstant.MEDICAL_NEWS_PATH)
@Tag(name = "医疗资讯", description = "医疗资讯相关接口")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // ==================== 公开API ====================

    /**
     * 分页查询资讯列表
     */
    @GetMapping
    @Operation(summary = "分页查询资讯列表", description = "根据条件分页查询资讯信息（仅已发布）")
    public Result<PageResult<NewsVO>> listNews(@Valid NewsQueryDTO queryDTO) {
        log.info("查询资讯列表: page={}, size={}, category={}, keyword={}",
                queryDTO.getPage(), queryDTO.getSize(),
                queryDTO.getCategory(), queryDTO.getKeyword());
        PageResult<NewsVO> result = newsService.listNews(queryDTO);
        return Result.success(result);
    }

    /**
     * 获取推荐资讯（用于轮播图）
     */
    @GetMapping("/featured")
    @Operation(summary = "获取推荐资讯", description = "获取推荐的资讯列表，用于首页轮播")
    public Result<List<NewsVO>> getFeaturedNews(
            @Parameter(description = "返回数量", example = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        log.info("获取推荐资讯: limit={}", limit);
        List<NewsVO> list = newsService.getFeaturedNews(limit);
        return Result.success(list);
    }

    /**
     * 搜索资讯
     */
    @GetMapping("/search")
    @Operation(summary = "搜索资讯", description = "根据关键词搜索资讯")
    public Result<List<NewsVO>> searchNews(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword) {
        log.info("搜索资讯: keyword={}", keyword);
        List<NewsVO> list = newsService.searchNews(keyword);
        return Result.success(list);
    }

    /**
     * 获取资讯的 Markdown 内容（管理员专用）
     * 必须放在 /{id} 路由之前，否则会被 /{id} 拦截
     */
    @GetMapping("/{id}/markdown")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取资讯 Markdown 内容", description = "管理员获取资讯的 Markdown 原文，用于编辑")
    public Result<String> getNewsMarkdown(
            @Parameter(description = "资讯ID", required = true)
            @PathVariable Integer id) {
        log.info("获取资讯 Markdown 内容: id={}", id);
        String markdown = newsService.getNewsMarkdown(id);
        // 使用 success(String message, T data) 明确指定 message 和 data
        return Result.success("获取成功", markdown);
    }

    /**
     * 获取资讯详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资讯详情", description = "根据ID获取资讯的详细信息")
    public Result<NewsDetailVO> getNewsById(
            @Parameter(description = "资讯ID", required = true)
            @PathVariable Integer id) {
        log.info("获取资讯详情: id={}", id);
        NewsDetailVO detailVO = newsService.getNewsById(id);
        return Result.success(detailVO);
    }

    // ==================== 管理员API ====================

    /**
     * 创建资讯
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建资讯", description = "管理员创建新资讯")
    public Result<NewsVO> createNews(
            @Valid @RequestBody NewsCreateDTO createDTO) {
        log.info("创建资讯: title={}", createDTO.getNewsName());
        NewsVO vo = newsService.createNews(createDTO);
        return Result.success(vo);
    }

    /**
     * 更新资讯
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新资讯", description = "管理员更新资讯信息")
    public Result<NewsVO> updateNews(
            @Parameter(description = "资讯ID", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody NewsUpdateDTO updateDTO) {
        log.info("更新资讯: id={}", id);
        NewsVO vo = newsService.updateNews(id, updateDTO);
        return Result.success(vo);
    }

    /**
     * 删除资讯
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除资讯", description = "管理员删除资讯")
    public Result<Void> deleteNews(
            @Parameter(description = "资讯ID", required = true)
            @PathVariable Integer id) {
        log.info("删除资讯: id={}", id);
        newsService.deleteNews(id);
        return Result.success();
    }
}
