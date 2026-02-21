import http from './http'

export const feedbackService = {
  submit(data: {
    feedbackTitle: string
    feedbackContent: string
    contact?: string
  }) {
    return http.post('/api/v1/feedbacks', data)
  },
  getMyList(page?: number, size?: number) {
    return http.get('/api/v1/feedbacks/my', { params: { page, size } })
  },
}
