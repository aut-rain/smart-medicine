package com.medical.smartmedicine.rag.task;

import com.medical.smartmedicine.rag.service.MedicalNewsContentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 医疗资讯正文同步任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalNewsContentSyncTask implements ApplicationRunner {

    private final MedicalNewsContentSyncService syncService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            syncService.syncAllPublishedNews();
        } catch (Exception e) {
            log.warn("启动时同步医疗资讯正文失败", e);
        }
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncPeriodically() {
        syncService.syncAllPublishedNews();
    }
}
