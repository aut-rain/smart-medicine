package com.medical.smartmedicine.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG检索结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchResult {

    @Builder.Default
    private List<RagEvidence> evidences = new ArrayList<>();

    public boolean hasEvidence() {
        return evidences != null && !evidences.isEmpty();
    }

    public String buildContext() {
        if (!hasEvidence()) {
            return "";
        }
        StringBuilder context = new StringBuilder();
        for (int i = 0; i < evidences.size(); i++) {
            RagEvidence evidence = evidences.get(i);
            String marker = "【资料" + (i + 1) + "】";
            context.append(marker)
                    .append(evidence.getSourceType())
                    .append(" - ")
                    .append(evidence.getTitle())
                    .append("\n详情链接：")
                    .append(buildMarkdownLink(marker, evidence))
                    .append("\n")
                    .append(evidence.getContent())
                    .append("\n\n");
        }
        return context.toString();
    }

    public String buildReferenceText() {
        if (!hasEvidence()) {
            return "";
        }
        List<String> references = new ArrayList<>();
        for (int i = 0; i < evidences.size(); i++) {
            RagEvidence evidence = evidences.get(i);
            String marker = "【资料" + (i + 1) + "】";
            references.add(buildMarkdownLink(marker, evidence) + " "
                    + evidence.getSourceType() + "：" + evidence.getTitle());
        }
        return references.stream().distinct().collect(Collectors.joining("；"));
    }

    private String buildMarkdownLink(String marker, RagEvidence evidence) {
        String detailUrl = evidence.getDetailUrl();
        if (detailUrl == null || detailUrl.isBlank()) {
            return marker;
        }
        return "[" + marker + "](" + detailUrl + ")";
    }
}
