/**
 * 从JWT token中解析用户ID
 */
export function getUserId(): number | null {
  const token = localStorage.getItem('token')
  if (!token) return null

  try {
    // JWT格式: header.payload.signature
    const parts = token.split('.')
    if (parts.length !== 3) return null

    // 解码payload (base64url)
    const payload = parts[1]
    // 添加padding以符合base64标准
    const padded = payload + '='.repeat((4 - payload.length % 4) % 4)
    const decoded = atob(padded.replace(/-/g, '+').replace(/_/g, '/'))
    const data = JSON.parse(decoded)

    return data.userId || data.sub || null
  } catch {
    return null
  }
}

export function getToken(): string | null {
  return localStorage.getItem('token')
}

export function getRefreshToken(): string | null {
  return localStorage.getItem('refreshToken')
}

export function setAuth(token: string, refreshToken?: string) {
  localStorage.setItem('token', token)
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
}

export function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
}
