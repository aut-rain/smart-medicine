import http from './http'

export const usersService = {
  getCurrent() {
    return http.get('/api/v1/users/current')
  },
  updateProfile(payload: {
    userName?: string
    userAge?: number
    userSex?: string
    userTel?: string
    imgPath?: string
  }) {
    return http.put('/api/v1/users/profile', payload)
  },
  updatePassword(payload: { oldPassword: string; newPassword: string }) {
    return http.put('/api/v1/users/password', payload)
  },
}
