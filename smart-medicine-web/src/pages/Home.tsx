import { Button, Card, Col, Input, Row, Space, Typography, Badge } from 'antd'
import { useEffect, useState } from 'react'
import { illnessService } from '@/services/illnessService'
import { medicineService } from '@/services/medicineService'
import { newsService } from '@/services/newsService'
import { useNavigate, Link } from 'react-router-dom'
import { FireOutlined, LeftOutlined, RightOutlined, HeartOutlined } from '@ant-design/icons'
import { useRequireAuth } from '@/hooks/useRequireAuth'

const { Title, Text } = Typography

// 叠层轮播卡片 - 居中布局
const NewsCarouselCard = ({
  news,
  onClick,
  index,
  currentIndex,
  total
}: {
  news: any
  onClick: () => void
  index: number
  currentIndex: number
  total: number
}) => {
  const isActive = index === currentIndex
  // 计算相对位置，处理循环逻辑
  const getRelativePosition = () => {
    if (isActive) return 0
    // 计算距离当前位置的步数，考虑循环
    let steps = index - currentIndex
    // 处理循环：如果距离超过一半，用反方向
    if (Math.abs(steps) > total / 2) {
      steps = steps > 0 ? steps - total : steps + total
    }
    return steps
  }

  const position = getRelativePosition()
  const offset = position * 60  // 每张卡片偏移 60px
  const zIndex = isActive ? 10 : 10 - Math.abs(position)
  const scale = isActive ? 1 : 1 - Math.abs(position) * 0.1
  const opacity = isActive ? 1 : Math.max(1 - Math.abs(position) * 0.3, 0.2)

  return (
    <div
      onClick={isActive ? onClick : undefined}
      style={{
        position: 'absolute',
        left: '50%',
        top: '50%',
        transform: `translate(calc(-50% + ${offset}px), -50%) scale(${scale})`,
        width: '85%',
        height: '90%',
        cursor: isActive ? 'pointer' : 'default',
        borderRadius: 16,
        overflow: 'hidden',
        background: '#f8f9fa',
        zIndex,
        opacity,
        transition: 'all 0.6s cubic-bezier(0.4, 0, 0.2, 1)',
        boxShadow: isActive
          ? '0 20px 60px rgba(0,0,0,0.25)'
          : '0 8px 24px rgba(0,0,0,0.12)'
      }}
    >
      {/* 分类标签 */}
      {news.category && (
        <div style={{
          position: 'absolute',
          top: 16,
          left: 16,
          background: 'rgba(19, 194, 194, 0.95)',
          backdropFilter: 'blur(10px)',
          padding: '6px 16px',
          borderRadius: 20,
          fontSize: 13,
          fontWeight: 600,
          color: 'white',
          zIndex: 5
        }}>
          {news.category}
        </div>
      )}

      {/* 图片容器 - 4:3 比例 */}
      {news.coverOssPath ? (
        <div style={{ width: '100%', paddingTop: '75%', position: 'relative', overflow: 'hidden' }}>
          <img
            src={news.coverOssPath}
            alt={news.newsName}
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: '100%',
              objectFit: 'cover'
            }}
          />
          {/* 渐变遮罩 */}
          <div style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '70%',
            background: 'linear-gradient(to top, rgba(0,0,0,0.85) 0%, transparent 100%)',
            padding: '20px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'flex-end'
          }}>
            <Title level={4} ellipsis={{ rows: 2 }} style={{ color: 'white', margin: 0, fontSize: 20, fontWeight: 600 }}>
              {news.newsName}
            </Title>
            {news.newsSummary && (
              <div style={{ color: 'rgba(255,255,255,0.85)', fontSize: 14, marginTop: 8 }}>
                {news.newsSummary}
              </div>
            )}
          </div>
        </div>
      ) : (
        <div style={{
          height: '100%',
          padding: 24,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
        }}>
          <Title level={4} style={{ color: 'white', margin: 0, textAlign: 'center' }}>
            {news.newsName}
          </Title>
          {news.newsSummary && (
            <div style={{ color: 'rgba(255,255,255,0.85)', fontSize: 14, marginTop: 12 }}>
              {news.newsSummary}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

// 右侧资讯列表项
const NewsListItem = ({
  news,
  isActive,
  onClick,
  index
}: {
  news: any
  isActive: boolean
  onClick: () => void
  index: number
}) => {
  return (
    <div
      onClick={onClick}
      style={{
        padding: '14px 16px',
        marginBottom: 10,
        borderRadius: 10,
        cursor: 'pointer',
        background: isActive ? '#f0f9ff' : 'white',
        border: isActive ? '2px solid #13c2c2' : '1px solid #f0f0f0',
        transition: 'all 0.3s ease',
        display: 'flex',
        gap: 12,
        alignItems: 'center'
      }}
    >
      {/* 序号 */}
      <div style={{
        width: 28,
        height: 28,
        borderRadius: '50%',
        background: isActive ? '#13c2c2' : '#f0f0f0',
        color: isActive ? 'white' : '#999',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        fontWeight: 600,
        fontSize: 13,
        flexShrink: 0
      }}>
        {index + 1}
      </div>

      {/* 内容 */}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{
          fontSize: 15,
          fontWeight: isActive ? 600 : 500,
          color: isActive ? '#13c2c2' : '#2c3e50',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap'
        }}>
          {news.newsName}
        </div>
      </div>

      {/* 箭头 */}
      {isActive && (
        <div style={{ color: '#13c2c2', fontSize: 16 }}>
          ←
        </div>
      )}
    </div>
  )
}

export default function Home() {
  const [hotIllnesses, setHotIllnesses] = useState<any[]>([])
  const [medicines, setMedicines] = useState<any[]>([])
  const [featuredNews, setFeaturedNews] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [keyword, setKeyword] = useState('')
  const [searchType, setSearchType] = useState('illness')
  const [carouselIndex, setCarouselIndex] = useState(0)
  const navigate = useNavigate()
  const { requireAuth } = useRequireAuth()

  useEffect(() => {
    loadData()
  }, [])

  // 轮播自动播放
  useEffect(() => {
    if (featuredNews.length <= 1) return
    const timer = setInterval(() => {
      setCarouselIndex((prev) => (prev + 1) % featuredNews.length)
    }, 5000)
    return () => clearInterval(timer)
  }, [featuredNews.length])

  const loadData = () => {
    setLoading(true)
    Promise.all([
      illnessService.getHot(8),
      medicineService.list({ page: 1, size: 8 }),
      newsService.getFeatured(5)
    ]).then(([illnessRes, medicineRes, newsRes]) => {
      setHotIllnesses(illnessRes.data?.data || [])
      setMedicines(medicineRes.data?.data?.records || [])
      setFeaturedNews(newsRes.data?.data || [])
    }).finally(() => setLoading(false))
  }

  const onSearch = () => {
    const k = keyword.trim()
    if (!k) return
    if (searchType === 'illness') {
      navigate(`/illness-search?keyword=${encodeURIComponent(k)}`)
    } else if (searchType === 'medicine') {
      navigate(`/medicine-search?keyword=${encodeURIComponent(k)}`)
    } else {
      navigate(`/news-list?keyword=${encodeURIComponent(k)}`)
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
        {/* 资讯轮播 */}
        {featuredNews.length > 0 && (
          <div>
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 20 }}>
              <FireOutlined style={{ fontSize: 20, color: '#13c2c2', marginRight: 8 }} />
              <Title level={3} style={{ margin: 0, color: '#2c3e50' }}>
                健康资讯
              </Title>
              <Link to="/news-list" style={{ marginLeft: 'auto' }}>
                <Button type="link">更多 →</Button>
              </Link>
            </div>
            {/* 左右布局：左侧叠层轮播，右侧资讯列表 */}
            <Row gutter={24} align="stretch">
              {/* 左侧叠层轮播 */}
              <Col xs={24} md={14}>
                <div style={{ position: 'relative', height: 420 }}>
                  {featuredNews.map((news, index) => (
                    <NewsCarouselCard
                      key={news.id}
                      news={news}
                      index={index}
                      currentIndex={carouselIndex}
                      total={featuredNews.length}
                      onClick={() => navigate(`/news/${news.id}`)}
                    />
                  ))}
                </div>
              </Col>

              {/* 右侧资讯列表 */}
              <Col xs={24} md={10}>
                <div style={{ height: 420, overflowY: 'auto' }}>
                  {featuredNews.map((news, index) => (
                    <NewsListItem
                      key={news.id}
                      news={news}
                      index={index}
                      isActive={index === carouselIndex}
                      onClick={() => setCarouselIndex(index)}
                    />
                  ))}
                </div>
              </Col>
            </Row>
          </div>
        )}

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
