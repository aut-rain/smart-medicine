package com.medical.smartmedicine.news.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.common.enums.ResultCode;
import com.medical.smartmedicine.common.exception.BusinessException;
import com.medical.smartmedicine.common.result.PageResult;
import com.medical.smartmedicine.mapper.MedicalNewsMapper;
import com.medical.smartmedicine.model.entity.MedicalNews;
import com.medical.smartmedicine.news.dto.NewsCreateDTO;
import com.medical.smartmedicine.news.dto.NewsQueryDTO;
import com.medical.smartmedicine.news.dto.NewsUpdateDTO;
import com.medical.smartmedicine.news.service.NewsService;
import com.medical.smartmedicine.news.vo.NewsDetailVO;
import com.medical.smartmedicine.news.vo.NewsVO;
import com.medical.smartmedicine.rag.service.MedicalNewsContentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资讯服务实现类
 *
 * @author Smart Medicine Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final MedicalNewsMapper newsMapper;
    private final OssClient ossClient;
    private final MedicalNewsContentSyncService medicalNewsContentSyncService;

    @Override
    public PageResult<NewsVO> listNews(NewsQueryDTO queryDTO) {
        // 构建分页对象
        Page<MedicalNews> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<MedicalNews> wrapper = new LambdaQueryWrapper<>();

        // 只显示已发布的资讯
        wrapper.eq(MedicalNews::getStatus, 1);

        // 按分类过滤
        if (StrUtil.isNotBlank(queryDTO.getCategory())) {
            wrapper.eq(MedicalNews::getCategory, queryDTO.getCategory());
        }

        // 关键词搜索
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(MedicalNews::getNewsName, queryDTO.getKeyword())
                    .or()
                    .like(MedicalNews::getNewsSummary, queryDTO.getKeyword()));
        }

        wrapper.orderByDesc(MedicalNews::getCreateTime);

        // 执行查询
        Page<MedicalNews> resultPage = newsMapper.selectPage(page, wrapper);

        // 转换为VO
        List<NewsVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(resultPage, entity -> convertToVO(entity));
    }

    @Override
    public NewsDetailVO getNewsById(Integer id) {
        // 查询资讯
        MedicalNews news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException(ResultCode.NEWS_NOT_FOUND);
        }

        // 只显示已发布的资讯
        if (news.getStatus() != 1) {
            throw new BusinessException(ResultCode.NEWS_NOT_FOUND);
        }

        // 从OSS读取Markdown内容
        String markdownContent = readMarkdownFromOss(news.getMarkdownOssPath());

        // 增加浏览量
        increaseViewCount(id);

        // 获取相关资讯（同分类的最新3条）
        List<NewsVO> relatedNews = getRelatedNews(news.getCategory(), id, 3);

        return NewsDetailVO.builder()
                .id(news.getId())
                .newsName(news.getNewsName())
                .newsSummary(news.getNewsSummary())
                .coverOssPath(buildOssUrl(news.getCoverOssPath()))
                .markdownContent(markdownContent)
                .category(news.getCategory())
                .author(news.getAuthor())
                .status(news.getStatus())
                .viewCount(news.getViewCount())
                .createTime(news.getCreateTime())
                .updateTime(news.getUpdateTime())
                .relatedNews(relatedNews)
                .build();
    }

    @Override
    public List<NewsVO> getFeaturedNews(Integer limit) {
        // 获取已发布的资讯，按创建时间倒序
        LambdaQueryWrapper<MedicalNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNews::getStatus, 1)
                .orderByDesc(MedicalNews::getCreateTime)
                .last("LIMIT " + (limit != null ? limit : 3));

        List<MedicalNews> newsList = newsMapper.selectList(wrapper);
        return newsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsVO> searchNews(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<MedicalNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNews::getStatus, 1)
                .and(w -> w.like(MedicalNews::getNewsName, keyword)
                        .or()
                        .like(MedicalNews::getNewsSummary, keyword))
                .orderByDesc(MedicalNews::getCreateTime)
                .last("LIMIT 50");

        List<MedicalNews> newsList = newsMapper.selectList(wrapper);
        return newsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NewsVO createNews(NewsCreateDTO createDTO) {
        MedicalNews news = BeanUtil.copyProperties(createDTO, MedicalNews.class);
        news.setViewCount(0);
        newsMapper.insert(news);
        trySyncNewsContent(news.getId());
        return convertToVO(news);
    }

    @Override
    @Transactional
    public NewsVO updateNews(Integer id, NewsUpdateDTO updateDTO) {
        MedicalNews existingNews = newsMapper.selectById(id);
        if (existingNews == null) {
            throw new BusinessException(ResultCode.NEWS_NOT_FOUND);
        }

        // 只更新非空字段
        MedicalNews news = new MedicalNews();
        news.setId(id);
        if (StrUtil.isNotBlank(updateDTO.getNewsName())) {
            news.setNewsName(updateDTO.getNewsName());
        }
        if (StrUtil.isNotBlank(updateDTO.getNewsSummary())) {
            news.setNewsSummary(updateDTO.getNewsSummary());
        }
        if (StrUtil.isNotBlank(updateDTO.getCoverOssPath())) {
            news.setCoverOssPath(updateDTO.getCoverOssPath());
        }
        if (StrUtil.isNotBlank(updateDTO.getMarkdownOssPath())) {
            news.setMarkdownOssPath(updateDTO.getMarkdownOssPath());
        }
        if (StrUtil.isNotBlank(updateDTO.getCategory())) {
            news.setCategory(updateDTO.getCategory());
        }
        if (StrUtil.isNotBlank(updateDTO.getAuthor())) {
            news.setAuthor(updateDTO.getAuthor());
        }
        if (updateDTO.getStatus() != null) {
            news.setStatus(updateDTO.getStatus());
        }

        newsMapper.updateById(news);
        trySyncNewsContent(id);
        return convertToVO(newsMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteNews(Integer id) {
        MedicalNews news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException(ResultCode.NEWS_NOT_FOUND);
        }

        // 删除OSS文件
        if (StrUtil.isNotBlank(news.getCoverOssPath())) {
            ossClient.delete(buildOssUrl(news.getCoverOssPath()));
        }
        if (StrUtil.isNotBlank(news.getMarkdownOssPath())) {
            ossClient.delete(buildOssUrl(news.getMarkdownOssPath()));
        }

        newsMapper.deleteById(id);
    }

    // ==================== 私有方法 ====================

    /**
     * 转换为VO
     */
    private NewsVO convertToVO(MedicalNews news) {
        return NewsVO.builder()
                .id(news.getId())
                .newsName(news.getNewsName())
                .newsSummary(news.getNewsSummary())
                .coverOssPath(buildOssUrl(news.getCoverOssPath()))
                .category(news.getCategory())
                .author(news.getAuthor())
                .status(news.getStatus())
                .viewCount(news.getViewCount())
                .createTime(news.getCreateTime())
                .build();
    }

    /**
     * 获取相关资讯
     */
    private List<NewsVO> getRelatedNews(String category, Integer excludeId, Integer limit) {
        LambdaQueryWrapper<MedicalNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNews::getStatus, 1)
                .ne(MedicalNews::getId, excludeId);

        if (StrUtil.isNotBlank(category)) {
            wrapper.eq(MedicalNews::getCategory, category);
        }

        wrapper.orderByDesc(MedicalNews::getCreateTime)
                .last("LIMIT " + limit);

        List<MedicalNews> newsList = newsMapper.selectList(wrapper);
        return newsList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 增加浏览量
     */
    private void increaseViewCount(Integer id) {
        try {
            MedicalNews news = newsMapper.selectById(id);
            if (news != null) {
                news.setViewCount((news.getViewCount() == null ? 0 : news.getViewCount()) + 1);
                newsMapper.updateById(news);
            }
        } catch (Exception e) {
            log.error("增加浏览量失败: newsId={}", id, e);
        }
    }

    /**
     * 从OSS读取Markdown内容
     */
    private String readMarkdownFromOss(String ossPath) {
        if (StrUtil.isBlank(ossPath)) {
            log.warn("Markdown OSS路径为空");
            return "# 内容暂无\n\n该资讯暂无内容";
        }

        try {
            String content = ossClient.readFileContent(ossPath);
            if (StrUtil.isNotBlank(content)) {
                return content;
            }
            log.warn("从OSS读取的Markdown内容为空: {}", ossPath);
            return "# 内容加载失败\n\n请稍后重试";
        } catch (Exception e) {
            log.error("读取Markdown文件失败: {}", ossPath, e);
            return "# 内容加载失败\n\n请稍后重试";
        }
    }

    /**
     * 构建完整的OSS URL
     */
    private String buildOssUrl(String ossPath) {
        return ossClient.buildUrl(ossPath);
    }

    @Override
    public String getNewsMarkdown(Integer id) {
        // 查询资讯
        MedicalNews news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException(ResultCode.NEWS_NOT_FOUND);
        }

        // 从OSS读取Markdown内容
        String markdownContent = readMarkdownFromOss(news.getMarkdownOssPath());
        return markdownContent;
    }

    private void trySyncNewsContent(Integer newsId) {
        try {
            medicalNewsContentSyncService.syncNews(newsId);
        } catch (Exception e) {
            log.warn("同步医疗资讯正文缓存失败: newsId={}", newsId, e);
        }
    }
}
