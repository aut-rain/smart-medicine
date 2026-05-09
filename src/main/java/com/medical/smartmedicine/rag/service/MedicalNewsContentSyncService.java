package com.medical.smartmedicine.rag.service;

/**
 * 医疗资讯正文同步服务
 */
public interface MedicalNewsContentSyncService {

    void syncAllPublishedNews();

    void syncNews(Integer newsId);
}
