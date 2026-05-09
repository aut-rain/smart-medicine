package com.medical.smartmedicine.ai.service;

import com.medical.smartmedicine.ai.dto.ChatRequest;
import com.medical.smartmedicine.ai.dto.HealthGateResult;

/**
 * 健康问题门禁服务
 */
public interface HealthIntentGateService {

    HealthGateResult classify(ChatRequest request);
}
