import { Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { getToken } from '@/utils/auth'
import { usersService } from '@/services/usersService'
import { Spin } from 'antd'

export default function AdminRoute({ children }: { children: React.ReactElement }) {
  const token = getToken()
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)
  
  useEffect(() => {
    checkAdminPermission()
  }, [])

  const checkAdminPermission = async () => {
    if (!token) {
      setLoading(false)
      return
    }
    
    try {
      const res = await usersService.getCurrent()
      const userInfo = res.data?.data
      setIsAdmin(userInfo?.roleStatus === 1)
    } catch (error) {
      console.error('获取用户信息失败', error)
      setIsAdmin(false)
    } finally {
      setLoading(false)
    }
  }
  
  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '60vh' 
      }}>
        <Spin size="large" tip="验证权限中..." />
      </div>
    )
  }
  
  if (!token) {
    return <Navigate to="/login" replace />
  }
  
  if (!isAdmin) {
    return <Navigate to="/" replace />
  }
  
  return children
}