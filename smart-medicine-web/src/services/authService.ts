import http from './http'

export interface LoginDTO {
  userAccount: string
  userPwd: string
}

export interface RegisterDTO {
  userAccount: string
  userPwd: string
  userName: string
  userEmail: string
  userAge?: number
  userSex?: string
  userTel?: string
  emailCode: string
}

export const authService = {
  sendEmailCode(email: string) {
    return http.post('/api/v1/auth/email-code', null, { params: { email } })
  },
  login(data: LoginDTO) {
    return http.post('/api/v1/auth/login', data)
  },
  register(data: RegisterDTO) {
    return http.post('/api/v1/auth/register', data)
  },
  logout() {
    return http.post('/api/v1/auth/logout')
  },
  refreshToken(refreshToken: string) {
    return http.post('/api/v1/auth/refresh-token', null, { params: { refreshToken } })
  },
}
