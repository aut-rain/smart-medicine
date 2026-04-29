import { Card, Space, Statistic, Row, Col, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminUserService } from '@/services/adminService'
import { adminMedicineService } from '@/services/adminService'
import { adminIllnessService } from '@/services/adminService'
import { adminFeedbackService } from '@/services/adminService'
import { adminNewsService } from '@/services/adminService'
import PageHeader from '@/components/PageHeader'

export default function AdminDashboard() {
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    userCount: 0,
    medicineCount: 0,
    illnessCount: 0,
    feedbackCount: 0,
    newsCount: 0
  })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    setLoading(true)
    try {
      // 并行加载统计数据
      const [userRes, medicineRes, illnessRes, feedbackRes, newsRes] = await Promise.all([
        adminUserService.list({ page: 1, size: 1 }),
        adminMedicineService.list({ page: 1, size: 1 }),
        adminIllnessService.list({ page: 1, size: 1 }),
        adminFeedbackService.list({ page: 1, size: 1 }),
        adminNewsService.list({ page: 1, size: 1 })
      ])

      setStats({
        userCount: userRes.data?.data?.total || 0,
        medicineCount: medicineRes.data?.data?.total || 0,
        illnessCount: illnessRes.data?.data?.total || 0,
        feedbackCount: feedbackRes.data?.data?.total || 0,
        newsCount: newsRes.data?.data?.total || 0
      })
    } catch (error) {
      console.error('加载统计信息失败', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="管理员后台" />
        
        {/* 统计卡片 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="用户总数"
                value={stats.userCount}
                loading={loading}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="药品总数"
                value={stats.medicineCount}
                loading={loading}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="疾病总数"
                value={stats.illnessCount}
                loading={loading}
                valueStyle={{ color: '#faad14' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="反馈总数"
                value={stats.feedbackCount}
                loading={loading}
                valueStyle={{ color: '#722ed1' }}
              />
            </Card>
          </Col>
        </Row>

        {/* 第二行统计数据 */}
        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          <Col xs={24} sm={12} md={6}>
            <Card>
              <Statistic
                title="资讯总数"
                value={stats.newsCount}
                loading={loading}
                valueStyle={{ color: '#13c2c2' }}
              />
            </Card>
          </Col>
        </Row>

        {/* 功能导航 */}
        <Card title="功能管理">
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={6}>
              <Card 
                hoverable 
                onClick={() => navigate('/admin/users')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>👥</Typography.Title>
                <div>用户管理</div>
              </Card>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Card 
                hoverable 
                onClick={() => navigate('/admin/medicines')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>💊</Typography.Title>
                <div>药品管理</div>
              </Card>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Card 
                hoverable 
                onClick={() => navigate('/admin/illnesses')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>🏥</Typography.Title>
                <div>疾病管理</div>
              </Card>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Card
                hoverable
                onClick={() => navigate('/admin/videos')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>📺</Typography.Title>
                <div>视频管理</div>
              </Card>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Card
                hoverable
                onClick={() => navigate('/admin/news')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>📰</Typography.Title>
                <div>资讯管理</div>
              </Card>
            </Col>
            <Col xs={24} sm={12} md={6}>
              <Card
                hoverable
                onClick={() => navigate('/admin/feedbacks')}
                style={{ textAlign: 'center', cursor: 'pointer' }}
              >
                <Typography.Title level={5}>💬</Typography.Title>
                <div>反馈管理</div>
              </Card>
            </Col>
          </Row>
        </Card>
      </Space>
    </div>
  )
}