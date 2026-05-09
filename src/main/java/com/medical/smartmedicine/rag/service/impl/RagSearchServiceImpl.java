package com.medical.smartmedicine.rag.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.medical.smartmedicine.mapper.IllnessKindMapper;
import com.medical.smartmedicine.mapper.IllnessMapper;
import com.medical.smartmedicine.mapper.MedicalNewsContentMapper;
import com.medical.smartmedicine.mapper.MedicalNewsMapper;
import com.medical.smartmedicine.mapper.MedicineMapper;
import com.medical.smartmedicine.mapper.ScienceVideoMapper;
import com.medical.smartmedicine.model.entity.Illness;
import com.medical.smartmedicine.model.entity.IllnessKind;
import com.medical.smartmedicine.model.entity.MedicalNews;
import com.medical.smartmedicine.model.entity.MedicalNewsContent;
import com.medical.smartmedicine.model.entity.Medicine;
import com.medical.smartmedicine.model.entity.ScienceVideo;
import com.medical.smartmedicine.rag.dto.RagEvidence;
import com.medical.smartmedicine.rag.dto.RagSearchResult;
import com.medical.smartmedicine.rag.service.RagSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 基于MySQL字段匹配的严格RAG检索服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagSearchServiceImpl implements RagSearchService {

    private static final int MAX_EVIDENCE_COUNT = 8;
    private static final int MAX_CONTENT_LENGTH = 900;

    private final IllnessMapper illnessMapper;
    private final IllnessKindMapper illnessKindMapper;
    private final MedicineMapper medicineMapper;
    private final ScienceVideoMapper scienceVideoMapper;
    private final MedicalNewsMapper medicalNewsMapper;
    private final MedicalNewsContentMapper medicalNewsContentMapper;

    @Override
    @Transactional(readOnly = true)
    public RagSearchResult search(String question) {
        String keyword = normalizeKeyword(question);
        Set<String> tokens = buildTokens(question);
        if (StrUtil.isBlank(keyword) && tokens.isEmpty()) {
            return RagSearchResult.builder().evidences(new ArrayList<>()).build();
        }

        List<RagEvidence> evidences = new ArrayList<>();
        evidences.addAll(searchIllness(keyword, tokens));
        evidences.addAll(searchMedicine(keyword, tokens));
        evidences.addAll(searchVideo(keyword, tokens));
        evidences.addAll(searchNews(keyword, tokens));

        List<RagEvidence> topEvidences = evidences.stream()
                .sorted(Comparator.comparing(RagEvidence::getScore).reversed())
                .limit(MAX_EVIDENCE_COUNT)
                .toList();

        log.info("RAG检索完成: keyword={}, evidenceCount={}", keyword, topEvidences.size());
        return RagSearchResult.builder()
                .evidences(topEvidences)
                .build();
    }

    private List<RagEvidence> searchIllness(String keyword, Set<String> tokens) {
        LambdaQueryWrapper<Illness> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> matchAny(w, keyword, tokens,
                Illness::getIllnessName,
                Illness::getIncludeReason,
                Illness::getIllnessSymptom,
                Illness::getSpecialSymptom));
        wrapper.last("LIMIT 5");

        return illnessMapper.selectList(wrapper).stream()
                .map(illness -> {
                    IllnessKind kind = illness.getKindId() == null ? null : illnessKindMapper.selectById(illness.getKindId());
                    String content = String.format("疾病名称：%s\n分类：%s\n诱发因素：%s\n疾病症状：%s\n特殊症状：%s",
                            safe(illness.getIllnessName()),
                            kind == null ? "未知" : safe(kind.getName()),
                            safe(illness.getIncludeReason()),
                            safe(illness.getIllnessSymptom()),
                            safe(illness.getSpecialSymptom()));
                    return RagEvidence.builder()
                            .sourceType("疾病")
                            .sourceId(illness.getId())
                            .title(illness.getIllnessName())
                            .content(limit(content))
                            .score(score(keyword, tokens, illness.getIllnessName(), illness.getIllnessSymptom(), illness.getSpecialSymptom(), illness.getIncludeReason()))
                            .build();
                })
                .toList();
    }

    private List<RagEvidence> searchMedicine(String keyword, Set<String> tokens) {
        LambdaQueryWrapper<Medicine> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> matchAny(w, keyword, tokens,
                Medicine::getMedicineName,
                Medicine::getKeyword,
                Medicine::getMedicineEffect,
                Medicine::getMedicineBrand,
                Medicine::getInteraction,
                Medicine::getTaboo,
                Medicine::getUsAge));
        wrapper.last("LIMIT 5");

        return medicineMapper.selectList(wrapper).stream()
                .map(medicine -> {
                    String content = String.format("药品名称：%s\n关键词：%s\n品牌：%s\n功效：%s\n相互作用：%s\n禁忌：%s\n用法用量：%s",
                            safe(medicine.getMedicineName()),
                            safe(medicine.getKeyword()),
                            safe(medicine.getMedicineBrand()),
                            safe(medicine.getMedicineEffect()),
                            safe(medicine.getInteraction()),
                            safe(medicine.getTaboo()),
                            safe(medicine.getUsAge()));
                    return RagEvidence.builder()
                            .sourceType("药品")
                            .sourceId(medicine.getId())
                            .title(medicine.getMedicineName())
                            .content(limit(content))
                            .score(score(keyword, tokens, medicine.getMedicineName(), medicine.getKeyword(), medicine.getMedicineEffect(), medicine.getMedicineBrand()))
                            .build();
                })
                .toList();
    }

    private List<RagEvidence> searchVideo(String keyword, Set<String> tokens) {
        LambdaQueryWrapper<ScienceVideo> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> matchAny(w, keyword, tokens,
                ScienceVideo::getTitle,
                ScienceVideo::getDescription));
        wrapper.last("LIMIT 3");

        return scienceVideoMapper.selectList(wrapper).stream()
                .map(video -> RagEvidence.builder()
                        .sourceType("科普视频")
                        .sourceId(video.getId())
                        .title(video.getTitle())
                        .content(limit(String.format("视频标题：%s\n视频描述：%s", safe(video.getTitle()), safe(video.getDescription()))))
                        .score(score(keyword, tokens, video.getTitle(), video.getDescription()))
                        .build())
                .toList();
    }

    private List<RagEvidence> searchNews(String keyword, Set<String> tokens) {
        List<RagEvidence> evidences = new ArrayList<>();

        LambdaQueryWrapper<MedicalNews> newsWrapper = new LambdaQueryWrapper<>();
        newsWrapper.eq(MedicalNews::getStatus, 1)
                .and(w -> matchAny(w, keyword, tokens,
                        MedicalNews::getNewsName,
                        MedicalNews::getNewsSummary,
                        MedicalNews::getCategory,
                        MedicalNews::getAuthor));
        newsWrapper.last("LIMIT 5");

        for (MedicalNews news : medicalNewsMapper.selectList(newsWrapper)) {
            MedicalNewsContent content = selectNewsContent(news.getId());
            evidences.add(buildNewsEvidence(keyword, tokens, news, content));
        }

        LambdaQueryWrapper<MedicalNewsContent> contentWrapper = new LambdaQueryWrapper<>();
        contentWrapper.and(w -> matchAny(w, keyword, tokens, MedicalNewsContent::getPlainContent))
                .last("LIMIT 5");
        for (MedicalNewsContent content : medicalNewsContentMapper.selectList(contentWrapper)) {
            MedicalNews news = medicalNewsMapper.selectById(content.getNewsId());
            if (news != null && Integer.valueOf(1).equals(news.getStatus())) {
                evidences.add(buildNewsEvidence(keyword, tokens, news, content));
            }
        }

        return evidences.stream()
                .collect(java.util.stream.Collectors.toMap(
                        evidence -> evidence.getSourceType() + "#" + evidence.getSourceId(),
                        evidence -> evidence,
                        (left, right) -> left.getScore() >= right.getScore() ? left : right
                ))
                .values()
                .stream()
                .toList();
    }

    private MedicalNewsContent selectNewsContent(Integer newsId) {
        if (newsId == null) {
            return null;
        }
        LambdaQueryWrapper<MedicalNewsContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalNewsContent::getNewsId, newsId);
        return medicalNewsContentMapper.selectOne(wrapper);
    }

    private RagEvidence buildNewsEvidence(String keyword, Set<String> tokens, MedicalNews news, MedicalNewsContent newsContent) {
        String plainContent = newsContent == null ? "" : newsContent.getPlainContent();
        String content = String.format("资讯标题：%s\n分类：%s\n作者：%s\n摘要：%s\n正文：%s",
                safe(news.getNewsName()),
                safe(news.getCategory()),
                safe(news.getAuthor()),
                safe(news.getNewsSummary()),
                safe(plainContent));
        return RagEvidence.builder()
                .sourceType("健康资讯")
                .sourceId(news.getId())
                .title(news.getNewsName())
                .content(limit(content))
                .score(score(keyword, tokens, news.getNewsName(), news.getNewsSummary(), news.getCategory(), plainContent))
                .build();
    }

    private int score(String keyword, Set<String> tokens, String... fields) {
        int score = 0;
        for (int i = 0; i < fields.length; i++) {
            String value = fields[i];
            if (StrUtil.isBlank(value)) {
                continue;
            }
            if (value.equalsIgnoreCase(keyword)) {
                score += i == 0 ? 100 : 60;
                continue;
            }
            if (StrUtil.isNotBlank(keyword) && value.contains(keyword)) {
                score += i == 0 ? 80 : 30;
                continue;
            }
            for (String token : tokens) {
                if (StrUtil.isNotBlank(token) && value.contains(token)) {
                    score += i == 0 ? 45 : 15;
                    break;
                }
            }
        }
        return Math.max(score, 1);
    }

    private String normalizeKeyword(String question) {
        if (question == null) {
            return "";
        }
        return question.trim()
                .replaceAll("[?？!！。,.，、；;：:\\s]+", " ")
                .trim();
    }

    private Set<String> buildTokens(String question) {
        Set<String> tokens = new LinkedHashSet<>();
        String raw = normalizeKeyword(question);
        String compact = raw.replace(" ", "");
        if (StrUtil.isBlank(compact)) {
            return tokens;
        }

        for (String part : raw.split("\\s+")) {
            if (StrUtil.isNotBlank(part)) {
                tokens.add(part);
            }
        }

        if (compact.length() >= 2) {
            for (int i = 0; i <= compact.length() - 2; i++) {
                tokens.add(compact.substring(i, i + 2));
            }
        }
        if (compact.length() >= 3) {
            for (int i = 0; i <= compact.length() - 3; i++) {
                tokens.add(compact.substring(i, i + 3));
            }
        }

        return tokens.stream()
                .filter(token -> token.length() >= 2)
                .limit(20)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    @SafeVarargs
    private final <T> void matchAny(LambdaQueryWrapper<T> wrapper,
                                    String keyword,
                                    Set<String> tokens,
                                    SFunction<T, ?>... columns) {
        wrapper.and(inner -> {
            boolean firstCondition = true;
            for (SFunction<T, ?> column : columns) {
                if (StrUtil.isNotBlank(keyword)) {
                    if (firstCondition) {
                        inner.like(column, keyword);
                        firstCondition = false;
                    } else {
                        inner.or().like(column, keyword);
                    }
                }
                for (String token : tokens) {
                    if (StrUtil.isNotBlank(token)) {
                        if (firstCondition) {
                            inner.like(column, token);
                            firstCondition = false;
                        } else {
                            inner.or().like(column, token);
                        }
                    }
                }
            }
        });
    }

    private String safe(String value) {
        return StrUtil.blankToDefault(value, "无");
    }

    private String limit(String value) {
        if (value == null || value.length() <= MAX_CONTENT_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_CONTENT_LENGTH) + "...";
    }
}
