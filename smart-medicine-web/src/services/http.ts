import axios from 'axios'
import { clearAuth, getToken } from '@/utils/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
    const token = getToken()
    if (token) {
        // config.headers 的类型会被正确推断，无需手动赋值
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

http.interceptors.response.use(
  (resp: import('axios').AxiosResponse) => resp,
  (error: import('axios').AxiosError) => {
    const status = error?.response?.status
    if (status === 401) {
      clearAuth()
      // 跳转登录页
      if (typeof window !== 'undefined') {
        const current = window.location.pathname
        const to = current === '/login' ? '/login' : `/login?redirect=${encodeURIComponent(current)}`
        window.location.replace(to)
      }
    }
    return Promise.reject(error)
  }
)

export default http
