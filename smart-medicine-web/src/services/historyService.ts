import http from './http'

export const historyService = {
  list(page?: number, size?: number) {
    return http.get('/api/v1/histories', { params: { page, size } })
  },
  clear() {
    return http.delete('/api/v1/histories')
  },
}
