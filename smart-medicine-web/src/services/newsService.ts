import http from './http'

export const newsService = {
  /**
   * 分页查询资讯列表
   */
  list(query: { page?: number; size?: number; status?: number; category?: string; keyword?: string }) {
    return http.get('/api/v1/medical-news', { params: query })
  },

  /**
   * 获取资讯详情
   */
  getDetail(id: number) {
    return http.get(`/api/v1/medical-news/${id}`)
  },

  /**
   * 获取推荐资讯（轮播用）
   */
  getFeatured(limit: number = 3) {
    return http.get('/api/v1/medical-news/featured', { params: { limit } })
  },

  /**
   * 搜索资讯
   */
  search(keyword: string, page?: number, size?: number) {
    return http.get('/api/v1/medical-news/search', { params: { keyword, page, size } })
  }
}
