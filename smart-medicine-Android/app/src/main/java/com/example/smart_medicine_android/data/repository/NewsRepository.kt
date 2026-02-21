package com.example.smart_medicine_android.data.repository

import android.util.Log
import com.example.smart_medicine_android.data.network.api.NewsApi
import com.example.smart_medicine_android.data.network.model.ApiException
import com.example.smart_medicine_android.data.network.model.NewsDto
import com.example.smart_medicine_android.data.network.model.NewsDetailDto
import com.example.smart_medicine_android.data.network.model.PageResponse

/**
 * 资讯仓库
 */
class NewsRepository(
    private val newsApi: NewsApi
) {

    private val TAG = "NewsRepository"

    /**
     * 获取推荐资讯（用于轮播图）
     * @param limit 数量限制，默认5条
     */
    suspend fun getFeaturedNews(limit: Int = 5): Result<List<NewsDto>> {
        return try {
            val response = newsApi.getFeaturedNews(limit)
            if (response.isSuccess) {
                val news = response.getDataOrThrow()
                Log.d(TAG, "getFeaturedNews: limit=$limit, returned ${news.size} items")
                Result.success(news)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 搜索资讯
     */
    suspend fun searchNews(keyword: String): Result<List<NewsDto>> {
        return try {
            val response = newsApi.searchNews(keyword)
            if (response.isSuccess) {
                val news = response.getDataOrThrow()
                Result.success(news)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取资讯详情
     */
    suspend fun getNewsDetail(newsId: Int): Result<NewsDetailDto> {
        return try {
            val response = newsApi.getNewsDetail(newsId)
            if (response.isSuccess) {
                val detail = response.getDataOrThrow()
                Result.success(detail)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 分页获取资讯列表
     */
    suspend fun getNewsList(
        page: Int = 1,
        size: Int = 10,
        category: String? = null,
        keyword: String? = null
    ): Result<PageResponse<NewsDto>> {
        return try {
            val response = newsApi.getNewsList(page, size, category, keyword)
            if (response.isSuccess) {
                val pageData = response.getDataOrThrow()
                Result.success(pageData)
            } else {
                Result.failure(ApiException(response.code ?: "UNKNOWN", response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
