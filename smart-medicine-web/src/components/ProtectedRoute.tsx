import { useNavigate } from 'react-router-dom'
import { getToken } from '@/utils/auth'
import { useEffect, ReactNode } from 'react'
import { useAuth } from '@/contexts/AuthContext'

export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const token = getToken()
  const navigate = useNavigate()
  const { openLoginModal } = useAuth()
  
  useEffect(() => {
    if (!token) {
      openLoginModal() // 打开登录弹窗
      navigate('/', { replace: true })
    }
  }, [token, navigate, openLoginModal])
  
  if (!token) {
    return null
  }
  return children
}
