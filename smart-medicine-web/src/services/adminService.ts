import http from './http'

// 注意：管理员接口使用普通接口，后端通过JWT Token验证权限

// 用户管理
export const adminUserService = {
  list(query: {
    page?: number
    size?: number
    keyword?: string
  }) {
    return http.get('/api/v1/users', { params: query })
  },
  getDetail(id: number) {
    return http.get(`/api/v1/users/${id}`)
  },
  delete(id: number) {
    return http.delete(`/api/v1/users/${id}`)
  },
  update(id: number, data: any) {
    return http.put(`/api/v1/users/${id}`, data)
  },
}

// 药品管理
export const adminMedicineService = {
  list(query: {
    page?: number
    size?: number
    keyword?: string
  }) {
    return http.get('/api/v1/medicines', { params: query })
  },
  create(data: any) {
    return http.post('/api/v1/medicines', data)
  },
  update(id: number, data: any) {
    return http.put(`/api/v1/medicines/${id}`, data)
  },
  delete(id: number) {
    return http.delete(`/api/v1/medicines/${id}`)
  },
}

// 疾病管理
export const adminIllnessService = {
  list(query: {
    page?: number
    size?: number
    keyword?: string
  }) {
    return http.get('/api/v1/illnesses', { params: query })
  },
  create(data: any) {
    return http.post('/api/v1/illnesses', data)
  },
  update(id: number, data: any) {
    return http.put(`/api/v1/illnesses/${id}`, data)
  },
  delete(id: number) {
    return http.delete(`/api/v1/illnesses/${id}`)
  },
}

// 反馈管理
export const adminFeedbackService = {
  list(query: {
    page?: number
    size?: number
  }) {
    return http.get('/api/v1/feedbacks', { params: query })
  },
  delete(id: number) {
    return http.delete(`/api/v1/feedbacks/${id}`)
  },
}

// 视频管理
export const adminVideoService = {
  list(query: {
    page?: number
    size?: number
    keyword?: string
  }) {
    return http.get('/api/v1/videos', { params: query })
  },
  create(data: any) {
    return http.post('/api/v1/videos', data)
  },
  update(id: number, data: any) {
    return http.put(`/api/v1/videos/${id}`, data)
  },
  delete(id: number) {
    return http.delete(`/api/v1/videos/${id}`)
  },
}