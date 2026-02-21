import { Avatar, Button, Dropdown, Layout, Menu } from 'antd'
import { Link, Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom'
import { LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons'
import { lazy, Suspense, useEffect, useState } from 'react'
import { clearAuth, getToken } from './utils/auth'
import ProtectedRoute from './components/ProtectedRoute'
import AdminRoute from './components/AdminRoute'
import PageSkeleton from './components/PageSkeleton'
import { usersService } from './services/usersService'
import LoginModal from './components/LoginModal'
import RegisterModal from './components/RegisterModal'
import { AuthContext_Provider } from './contexts/AuthContext'

// 路由懒加载
const Home = lazy(() => import('./pages/Home'))
const IllnessSearch = lazy(() => import('./pages/IllnessSearch'))
const IllnessDetail = lazy(() => import('./pages/IllnessDetail'))
const MedicineDetail = lazy(() => import('./pages/MedicineDetail'))
const MedicineSearch = lazy(() => import('./pages/MedicineSearch'))
const ScienceVideos = lazy(() => import('./pages/ScienceVideos'))
const ScienceVideoDetail = lazy(() => import('./pages/ScienceVideoDetail'))
const NewsList = lazy(() => import('./pages/NewsList'))
const NewsDetail = lazy(() => import('./pages/NewsDetail'))
const AiChat = lazy(() => import('./pages/AiChat'))
const Profile = lazy(() => import('./pages/Profile'))
const Feedback = lazy(() => import('./pages/Feedback'))
const AdminDashboard = lazy(() => import('./pages/admin/AdminDashboard'))
const AdminUsers = lazy(() => import('./pages/admin/AdminUsers'))
const AdminMedicines = lazy(() => import('./pages/admin/AdminMedicines'))
const AdminIllnesses = lazy(() => import('./pages/admin/AdminIllnesses'))
const AdminVideos = lazy(() => import('./pages/admin/AdminVideos'))
const AdminNews = lazy(() => import('./pages/admin/AdminNews'))

const { Header, Content, Footer } = Layout

export default function App() {
  const location = useLocation()
  const navigate = useNavigate()
  const selectedKey = location.pathname.split('/')[1] || 'home'
  const isLoggedIn = !!getToken()
  const [userInfo, setUserInfo] = useState<any>(null)
  const [loginModalOpen, setLoginModalOpen] = useState(false)
  const [registerModalOpen, setRegisterModalOpen] = useState(false)

  useEffect(() => {
    if (isLoggedIn) {
      loadUserInfo()
    }
  }, [isLoggedIn])

  const loadUserInfo = async () => {
    try {
      const res = await usersService.getCurrent()
      setUserInfo(res.data?.data)
    } catch (error) {
      console.error('加载用户信息失败', error)
    }
  }

  // 判断是否为管理员（roleStatus = 1）
  const isAdmin = userInfo?.roleStatus === 1

  const handleLogout = () => {
    clearAuth()
    setUserInfo(null)
    navigate('/')
  }

  const handleLoginSuccess = () => {
    setLoginModalOpen(false)
    loadUserInfo()
    window.location.reload() // 刷新页面以更新登录状态
  }

  const handleRegisterSuccess = () => {
    setRegisterModalOpen(false)
    loadUserInfo()
    window.location.reload() // 刷新页面以更新登录状态
  }

  // 提供给全局使用的方法
  const authContextValue = {
    openLoginModal: () => setLoginModalOpen(true),
    openRegisterModal: () => setRegisterModalOpen(true)
  }

  return (
    <AuthContext_Provider value={authContextValue}>
      <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ 
        position: 'sticky', 
        top: 0, 
        zIndex: 10, 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'space-between',
        background: 'white',
        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)',
        padding: '0 24px',
        height: 64,
        borderBottom: '1px solid #e8e8e8'
      }}>
        {/* Logo */}
        <div 
          onClick={() => navigate('/')}
          style={{ 
            display: 'flex',
            alignItems: 'center',
            cursor: 'pointer',
            marginRight: 40
          }}
        >
          <img 
            src="/assets/images/logo.png" 
            alt="智慧医疗" 
            style={{ height: 50, width: 120, objectFit: 'contain' }}
            onError={(e) => {
              e.currentTarget.style.display = 'none'
            }}
          />
        </div>
        <Menu 
          mode="horizontal" 
          selectedKeys={[selectedKey]} 
          style={{ 
            flex: 1, 
            minWidth: 0,
            background: 'transparent',
            border: 'none',
            fontWeight: 500,
            fontSize: 14
          }} 
          items={[
            { key: '', label: <Link to="/" style={{ color: '#18191c' }}>首页</Link> },
            { key: 'illness-search', label: <Link to="/illness-search" style={{ color: '#18191c' }}>疾病查询</Link> },
            { key: 'medicine-search', label: <Link to="/medicine-search" style={{ color: '#18191c' }}>药品查询</Link> },
            { key: 'science-videos', label: <Link to="/science-videos" style={{ color: '#18191c' }}>健康科普</Link> },
            { key: 'news-list', label: <Link to="/news-list" style={{ color: '#18191c' }}>健康资讯</Link> },
            { key: 'ai-chat', label: <Link to="/ai-chat" style={{ color: '#18191c' }}>AI 问诊</Link> },
            { key: 'feedback', label: <Link to="/feedback" style={{ color: '#18191c' }}>意见反馈</Link> },
          ]} 
        />
        {isLoggedIn ? (
          <Dropdown 
            menu={{
              items: [
                { key: 'profile', icon: <UserOutlined />, label: <Link to="/profile">个人中心</Link> },
                ...(isAdmin ? [{ key: 'admin', icon: <SettingOutlined />, label: <Link to="/admin">管理后台</Link> }] : []),
                { type: 'divider' },
                { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout },
              ]
            }}
            placement="bottomRight"
          >
            <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              gap: 8,
              cursor: 'pointer',
              padding: '4px 12px',
              borderRadius: 20,
              transition: 'background 0.3s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = '#f0f0f0'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = 'transparent'
            }}
            >
              <Avatar 
                size={36}
                src={userInfo?.imgPath}
                icon={<UserOutlined />}
                style={{ 
                  background: userInfo?.imgPath ? 'transparent' : 'linear-gradient(135deg, #4a90e2 0%, #357abd 100%)',
                  cursor: 'pointer'
                }}
              />
              <span style={{ 
                fontSize: 14, 
                color: '#18191c',
                fontWeight: 500,
                maxWidth: 100,
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap'
              }}>
                {userInfo?.userName || '用户'}
              </span>
            </div>
          </Dropdown>
        ) : (
          <Button 
            type="primary" 
            onClick={() => setLoginModalOpen(true)}
            style={{
              borderRadius: 6,
              fontWeight: 500
            }}
          >
            登录
          </Button>
        )}
      </Header>
      <Content style={{ padding: 0 }}>
        <Suspense fallback={<PageSkeleton />}>
          <Routes>
            <Route path="/" element={<Home />} />
          <Route path="/illness/:id" element={<IllnessDetail />} />
          <Route path="/medicine/:id" element={<MedicineDetail />} />
          <Route path="/news/:id" element={<NewsDetail />} />
          <Route path="/news-list" element={<NewsList />} />
          <Route path="/illness-search" element={<IllnessSearch />} />
          <Route path="/medicine-search" element={<MedicineSearch />} />
          <Route
            path="/science-videos"
            element={
              <ProtectedRoute>
                <ScienceVideos />
              </ProtectedRoute>
            }
          />
          <Route path="/science-video/:id" element={<ScienceVideoDetail />} />
          <Route
            path="/ai-chat"
            element={
              <ProtectedRoute>
                <AiChat />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/feedback"
            element={
              <ProtectedRoute>
                <Feedback />
              </ProtectedRoute>
            }
          />
          {/* 管理员路由 */}
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminDashboard />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUsers />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/medicines"
            element={
              <AdminRoute>
                <AdminMedicines />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/illnesses"
            element={
              <AdminRoute>
                <AdminIllnesses />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/videos"
            element={
              <AdminRoute>
                <AdminVideos />
              </AdminRoute>
            }
          />
          <Route
            path="/admin/news"
            element={
              <AdminRoute>
                <AdminNews />
              </AdminRoute>
            }
          />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Suspense>
      </Content>
      <Footer style={{ 
        textAlign: 'center',
        background: '#f5f7fa',
        fontWeight: 500,
        color: '#636e72',
        borderTop: '1px solid #e8ecf0'
      }}>
        Smart Medicine © {new Date().getFullYear()} | 关爱健康，智慧生活 ❤️
      </Footer>

      {/* 登录弹窗 */}
      <LoginModal 
        open={loginModalOpen}
        onCancel={() => setLoginModalOpen(false)}
        onSuccess={handleLoginSuccess}
        onSwitchToRegister={() => {
          setLoginModalOpen(false)
          setRegisterModalOpen(true)
        }}
      />

      {/* 注册弹窗 */}
      <RegisterModal 
        open={registerModalOpen}
        onCancel={() => setRegisterModalOpen(false)}
        onSuccess={handleRegisterSuccess}
        onSwitchToLogin={() => {
          setRegisterModalOpen(false)
          setLoginModalOpen(true)
        }}
      />
      </Layout>
    </AuthContext_Provider>
  )
}
