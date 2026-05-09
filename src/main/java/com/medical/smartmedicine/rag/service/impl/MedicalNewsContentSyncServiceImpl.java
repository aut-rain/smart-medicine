package com.medical.smartmedicine.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medical.smartmedicine.common.client.OssClient;
import com.medical.smartmedicine.mapper.MedicalNewsContentMapper;
import com.medical.smartmedicine.mapper.MedicalNewsMapper;
import com.medical.smartmedicine.model.entity.MedicalNews;
import com.medical.smartmedicine.model.entity.MedicalNewsContent;
import com.medical.smartmedicine.rag.service.MedicalNewsContentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;

/**
 * 将医疗资讯Markdown正文同步到MySQL纯文本缓存。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalNewsContentSyncServiceImpl implements MedicalNewsContentSyncService {

    private final MedicalNewsMapper medicalNewsMapper;
    private final MedicalNewsContentMapper medicalNewsContentMapper;
    private final OssClient ossClient;

    @Override
    public void syncAllPublishedNews() {
        LambdaQueryWrapper<MedicalNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNews::getStatus, 1);
        List<MedicalNews> newsList = medicalNewsMapper.selectList(wrapper);
        for (MedicalNews news : newsList) {
            try {
                syncNews(news.getId());
            } catch (Exception e) {
                log.warn("同步医疗资讯正文失败: newsId={}", news.getId(), e);
            }
        }
        log.info("医疗资讯正文同步完成: count={}", newsList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncNews(Integer newsId) {
        if (newsId == null) {
            return;
        }
        MedicalNews news = medicalNewsMapper.selectById(newsId);
        if (news == null || StrUtil.isBlank(news.getMarkdownOssPath())) {
            return;
        }

        String markdown = ossClient.readFileContent(news.getMarkdownOssPath());
        if (StrUtil.isBlank(markdown)) {
            log.warn("医疗资讯Markdown为空，跳过同步: newsId={}", newsId);
            return;
        }

        String plainContent = stripMarkdown(markdown);
        String hash = sha256(plainContent);
        MedicalNewsContent existing = selectByNewsId(newsId);
        Date now = new Date();

        if (existing == null) {
            MedicalNewsContent content = MedicalNewsContent.builder()
                    .newsId(newsId)
                    .plainContent(plainContent)
                    .contentHash(hash)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            medicalNewsContentMapper.insert(content);
            log.info("新增医疗资讯正文缓存: newsId={}", newsId);
            return;
        }

        if (!hash.equals(existing.getContentHash())) {
            existing.setPlainContent(plainContent);
            existing.setContentHash(hash);
            existing.setUpdateTime(now);
            medicalNewsContentMapper.updateById(existing);
            log.info("更新医疗资讯正文缓存: newsId={}", newsId);
        }
    }

    private MedicalNewsContent selectByNewsId(Integer newsId) {
        LambdaQueryWrapper<MedicalNewsContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNewsContent::getNewsId, newsId);
        return medicalNewsContentMapper.selectOne(wrapper);
    }

    private String stripMarkdown(String markdown) {
        return markdown
                .replaceAll("(?s)```.*?```", " ")
                .replaceAll("!\\[[^]]*]\\([^)]*\\)", " ")
                .replaceAll("\\[([^]]+)]\\([^)]*\\)", "$1")
                .replaceAll("[#>*_`~\\-]+", " ")
                .replaceAll("\\|", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256不可用", e);
        }
    }
}
