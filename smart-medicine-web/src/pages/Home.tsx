import { Button, Card, Col, Input, Row, Space, Tabs, Typography, Badge } from 'antd'
import { useEffect, useState } from 'react'
import { illnessService } from '@/services/illnessService'
import { medicineService } from '@/services/medicineService'
import { useNavigate } from 'react-router-dom'
import { HeartOutlined, FireOutlined, ThunderboltOutlined } from '@ant-design/icons'
import { useRequireAuth } from '@/hooks/useRequireAuth'

export default function Home() {
  const [hotIllnesses, setHotIllnesses] = useState<any[]>([])
  const [medicines, setMedicines] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [keyword, setKeyword] = useState('')
  const [searchType, setSearchType] = useState('illness')
  const navigate = useNavigate()
  const { requireAuth } = useRequireAuth()

  useEffect(() => {
    loadData()
  }, [])

  const loadData = () => {
    setLoading(true)
    Promise.all([
      illnessService.getHot(8),
      medicineService.list({ page: 1, size: 8 })
    ]).then(([illnessRes, medicineRes]) => {
      setHotIllnesses(illnessRes.data?.data || [])
      setMedicines(medicineRes.data?.data?.records || [])
    }).finally(() => setLoading(false))
  }

  const onSearch = () => {
    const k = keyword.trim()
    if (!k) return
    if (searchType === 'illness') {
      navigate(`/illness-search?keyword=${encodeURIComponent(k)}`)
    } else {
      // 药品搜索可以后续实现
      navigate(`/illness-search?keyword=${encodeURIComponent(k)}`)
    }
  }

  return (
    <div className="page" style={{ paddingTop: 40 }}>
      {/* 装饰元素 */}
      <div style={{
        position: 'absolute',
        top: 100,
        right: -100,
        width: 300,
        height: 300,
        background: 'radial-gradient(circle, rgba(74, 144, 226, 0.08) 0%, transparent 70%)',
        borderRadius: '50%',
        pointerEvents: 'none',
        zIndex: 0
      }} />
      <div style={{
        position: 'absolute',
        bottom: 200,
        left: -150,
        width: 400,
        height: 400,
        background: 'radial-gradient(circle, rgba(93, 173, 226, 0.06) 0%, transparent 70%)',
        borderRadius: '50%',
        pointerEvents: 'none',
        zIndex: 0
      }} />

      {/* Hero Banner */}
      <div style={{
        background: 'linear-gradient(135deg, #4a90e2 0%, #357abd 100%)',
        borderRadius: 12,
        padding: '50px 40px',
        marginBottom: 40,
        color: 'white',
        textAlign: 'center',
        boxShadow: '0 4px 12px rgba(74, 144, 226, 0.15)',
        position: 'relative',
        overflow: 'hidden',
        zIndex: 1
      }}>
        {/* Banner 装饰图案 */}
        <div style={{
          position: 'absolute',
          top: -30,
          right: -30,
          fontSize: 120,
          opacity: 0.1,
          transform: 'rotate(-15deg)'
        }}>
          ⚕️
        </div>
        <div style={{
          position: 'absolute',
          bottom: -20,
          left: -20,
          fontSize: 100,
          opacity: 0.08,
          transform: 'rotate(20deg)'
        }}>
          💊
        </div>
        <div style={{
          position: 'absolute',
          top: 50,
          left: 100,
          fontSize: 60,
          opacity: 0.06
        }}>
          ❤️
        </div>
        
        <Typography.Title level={1} style={{ color: 'white', marginBottom: 12, fontSize: 36, position: 'relative', zIndex: 1 }}>
          🌿 智慧医疗系统
        </Typography.Title>
        <Typography.Title level={4} style={{ color: 'rgba(255, 255, 255, 0.95)', fontWeight: 400, marginBottom: 0, position: 'relative', zIndex: 1 }}>
          专业、可靠、随时随地的健康服务
        </Typography.Title>
      </div>

      <Space direction="vertical" style={{ width: '100%', position: 'relative', zIndex: 1 }} size="large">
        {/* 快捷导航 */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
            <Typography.Title level={3} style={{ margin: 0, color: '#2c3e50' }}>
              快捷导航
            </Typography.Title>
          </div>
          <Row gutter={[16, 16]}>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => navigate('/illness-search')} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#4a90e2' }}>🔍</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>疾病查询</div>
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => navigate('/medicine-search')} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#5dade2' }}>💊</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>药品查询</div>
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => {
                  if (requireAuth()) {
                    navigate('/science-videos')
                  }
                }} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#52c41a' }}>📺</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>健康科普</div>
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => {
                  if (requireAuth(true)) { // 显示提示
                    navigate('/ai-chat')
                  }
                }} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#722ed1' }}>🤖</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>AI问诊</div>
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => {
                  if (requireAuth(true)) { // 显示提示
                    navigate('/feedback')
                  }
                }} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#faad14' }}>💬</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>意见反馈</div>
              </Card>
            </Col>
            <Col xs={12} sm={8} md={6} lg={4}>
              <Card 
                hoverable 
                onClick={() => navigate('/profile')} 
                style={{ 
                  textAlign: 'center',
                  background: 'white',
                  border: '1px solid #e1e8ed'
                }}
              >
                <Typography.Title level={2} style={{ margin: '8px 0', color: '#13c2c2' }}>👤</Typography.Title>
                <div style={{ fontWeight: 500, fontSize: 15, color: '#2c3e50' }}>个人中心</div>
              </Card>
            </Col>
          </Row>
        </div>

        {/* 热门疾病 */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
            <FireOutlined style={{ fontSize: 20, color: '#f5222d', marginRight: 8 }} />
            <Typography.Title level={3} style={{ margin: 0, color: '#2c3e50' }}>
              热门疾病
            </Typography.Title>
          </div>
          <Row gutter={[16, 16]}>
            {hotIllnesses.map((item) => (
              <Col key={item.id} xs={24} sm={12} md={8} lg={6}>
                <Card
                  loading={loading}
                  hoverable
                  onClick={() => navigate(`/illness/${item.id}`)}
                  style={{ height: '100%' }}
                >
                  <Typography.Title level={5} ellipsis style={{ color: '#2c3e50', marginBottom: 12 }}>
                    {item.illnessName}
                  </Typography.Title>
                  <Typography.Paragraph ellipsis={{ rows: 2 }} type="secondary" style={{ marginBottom: 12 }}>
                    {item.illnessSymptom}
                  </Typography.Paragraph>
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                      <HeartOutlined style={{ color: '#f5222d', marginRight: 4 }} />
                      {item.pageviews} 次浏览
                    </Typography.Text>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>
        </div>

        {/* 常用药品 */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 24 }}>
            <Typography.Title level={3} style={{ margin: 0, color: '#2c3e50' }}>
              💊 常用药品
            </Typography.Title>
          </div>
          <Row gutter={[16, 16]}>
            {medicines.map((item) => (
              <Col key={item.id} xs={24} sm={12} md={8} lg={6}>
                <Card
                  loading={loading}
                  hoverable
                  onClick={() => navigate(`/medicine/${item.id}`)}
                  cover={
                    <div style={{ 
                      height: 180, 
                      overflow: 'hidden',
                      background: '#f5f5f5',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}>
                      <img
                        alt={item.medicineName}
                        src={item.imgPath || '/assets/images/bg.png'}
                        style={{ 
                          width: '100%',
                          height: '100%',
                          objectFit: item.imgPath ? 'cover' : 'contain',
                          padding: item.imgPath ? 0 : 20
                        }}
                        onError={(e) => {
                          e.currentTarget.src = '/assets/images/bg.png'
                          e.currentTarget.style.objectFit = 'contain'
                          e.currentTarget.style.padding = '20px'
                        }}
                      />
                    </div>
                  }
                >
                  <Card.Meta
                    title={
                      <Typography.Text ellipsis strong style={{ fontSize: 15, color: '#2c3e50' }}>
                        {item.medicineName}
                      </Typography.Text>
                    }
                    description={
                      <Space direction="vertical" size="small" style={{ width: '100%' }}>
                        <Typography.Paragraph ellipsis={{ rows: 2 }} type="secondary" style={{ margin: 0, fontSize: 13 }}>
                          {item.medicineEffect}
                        </Typography.Paragraph>
                        <Typography.Text strong style={{ 
                          fontSize: 18,
                          color: '#f5222d'
                        }}>
                          ￥{item.medicinePrice}
                        </Typography.Text>
                      </Space>
                    }
                  />
                </Card>
              </Col>
            ))}
          </Row>
        </div>
      </Space>
    </div>
  )
}
