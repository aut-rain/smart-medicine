package com.medical.smartmedicine.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG检索依据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagEvidence {

    private String sourceType;

    private Integer sourceId;

    private String title;

    private String content;

    private Integer score;

    public String getDetailUrl() {
        if (sourceId == null) {
            return "";
        }
        return switch (sourceType) {
            case "疾病" -> "/illness/" + sourceId;
            case "药品" -> "/medicine/" + sourceId;
            case "科普视频" -> "/science-video/" + sourceId;
            case "健康资讯" -> "/news/" + sourceId;
            default -> "";
        };
    }
}
