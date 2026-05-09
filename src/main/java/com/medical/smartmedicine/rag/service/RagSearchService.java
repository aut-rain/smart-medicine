package com.medical.smartmedicine.rag.service;

import com.medical.smartmedicine.rag.dto.RagSearchResult;

/**
 * 基于MySQL严格检索的RAG服务
 */
public interface RagSearchService {

    RagSearchResult search(String question);
}
