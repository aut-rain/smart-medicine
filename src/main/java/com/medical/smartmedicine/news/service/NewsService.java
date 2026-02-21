package com.medical.smartmedicine.news.service;

import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.news.dto.NewsCreateDTO;
import com.medical.smartmedicine.news.dto.NewsQueryDTO;
import com.medical.smartmedicine.news.dto.NewsUpdateDTO;
import com.medical.smartmedicine.news.vo.NewsDetailVO;
import com.medical.smartmedicine.news.vo.NewsVO;

import java.util.List;

/**
 * 资讯服务接口
 *
 * @author Smart Medicine Team
 */
public interface NewsService {

    // ==================== 公开API ====================

    /**
     * 分页查询资讯列表
     */
    PageResult<NewsVO> listNews(NewsQueryDTO queryDTO);

    /**
     * 获取资讯详情
     */
    NewsDetailVO getNewsById(Integer id);

    /**
     * 获取推荐资讯（用于轮播图）
     */
    List<NewsVO> getFeaturedNews(Integer limit);

    /**
     * 搜索资讯
     */
    List<NewsVO> searchNews(String keyword);

    // ==================== 管理员API ====================

    /**
     * 创建资讯
     */
    NewsVO createNews(NewsCreateDTO createDTO);

    /**
     * 更新资讯
     */
    NewsVO updateNews(Integer id, NewsUpdateDTO updateDTO);

    /**
     * 删除资讯
     */
    void deleteNews(Integer id);

    /**
     * 获取资讯的 Markdown 原文内容（用于编辑）
     */
    String getNewsMarkdown(Integer id);
}
