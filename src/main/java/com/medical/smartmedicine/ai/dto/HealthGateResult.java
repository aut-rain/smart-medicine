package com.medical.smartmedicine.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 健康门禁分类结果
 */
public record HealthGateResult(
        @JsonProperty(required = true, value = "healthRelated")
        Boolean healthRelated
) {
    public boolean isHealthRelated() {
        return Boolean.TRUE.equals(healthRelated);
    }
}
