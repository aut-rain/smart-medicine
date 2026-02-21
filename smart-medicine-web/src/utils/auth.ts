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
