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
    // 使用URLSearchParams构建query参数
    const params = new URLSearchParams()
    params.append('email', email)
    return http.post(`/api/v1/auth/email-code?${params.toString()}`)
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
