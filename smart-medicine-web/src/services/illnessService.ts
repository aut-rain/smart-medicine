import http from './http'

export const illnessService = {
  getHot(limit = 10) {
    return http.get('/api/v1/illnesses/hot', { params: { limit } })
  },
  search(keyword: string) {
    return http.get('/api/v1/illnesses/search', { params: { keyword } })
  },
  searchPaged(query: {
    keyword: string
    page?: number
    size?: number
  }) {
    return http.get('/api/v1/illnesses/search/paged', { params: query })
  },
  list(query: {
    page?: number
    size?: number
    keyword?: string
    kindId?: number
    illnessName?: string
  }) {
    return http.get('/api/v1/illnesses', { params: query })
  },
  getDetail(id: number) {
    return http.get(`/api/v1/illnesses/${id}`)
  },
}
