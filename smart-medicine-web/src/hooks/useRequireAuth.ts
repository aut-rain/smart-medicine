import { useAuth } from '@/contexts/AuthContext'
import { getToken } from '@/utils/auth'
import { message } from 'antd'

/**
 * 自定义 Hook：检查登录状态并在需要时打开登录弹窗
 * @returns 返回一个函数，调用时会检查登录状态，未登录则打开弹窗并返回 false
 */
export function useRequireAuth() {
  const { openLoginModal } = useAuth()
  const isLoggedIn = !!getToken()

  /**
   * 检查是否已登录
   * @param showMessage 是否显示提示消息，默认为 false
   * @returns 已登录返回 true，未登录返回 false 并打开登录弹窗
   */
  const requireAuth = (showMessage: boolean = false): boolean => {
    if (!isLoggedIn) {
      if (showMessage) {
        message.warning('请先登录')
      }
      openLoginModal()
      return false
    }
    return true
  }

  return { requireAuth, isLoggedIn }
}
