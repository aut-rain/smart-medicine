import http from './http'

export interface VideoVO {
  id: number
  title: string
  description: string
  link: string
  createTime: string
  updateTime: string
}

export interface VideoQueryDTO {
  page?: number
  size?: number
  keyword?: string
}

export const scienceVideoService = {
  list(query?: VideoQueryDTO) {
    return http.get('/api/v1/videos', { params: query })
  },
  getDetail(id: number) {
    return http.get(`/api/v1/videos/${id}`)
  },
}