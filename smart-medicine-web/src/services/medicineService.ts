import http from './http'

export const medicineService = {
  list(query: {
    page?: number
    size?: number
    keyword?: string
    medicineType?: number
    medicineName?: string
    minPrice?: number
    maxPrice?: number
  }) {
    return http.get('/api/v1/medicines', { params: query })
  },
  getDetail(id: number) {
    return http.get(`/api/v1/medicines/${id}`)
  },
  listByIllness(illnessId: number) {
    return http.get(`/api/v1/medicines/illness/${illnessId}`)
  },
}
