package com.medical.smartmedicine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Medicine System - 应用启动类
 * 智能医疗平台主入口
 *
 * @author Smart Medicine Team
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.medical.smartmedicine.mapper")
@EnableAsync
@EnableScheduling
public class SmartMedicineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMedicineApplication.class, args);
    }
}
