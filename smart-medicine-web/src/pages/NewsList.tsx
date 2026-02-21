import { Card, Col, Row, Empty, Input, Pagination, Typography, Tag, message } from 'antd'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { FireOutlined, SearchOutlined } from '@ant-design/icons'
import { newsService } from '@/services/newsService'
import PageHeader from '@/components/PageHeader'

const { Title, Paragraph } = Typography

export default function NewsList() {
  const navigate = useNavigate()
  const [news, setNews] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [keyword, setKeyword] = useState('')

  useEffect(() => {
    loadNews()
  }, [pagination.page, pagination.size])

  const loadNews = async () => {
    setLoading(true)
    try {
      const res = await newsService.list({
        page: pagination.page,
        size: pagination.size,
        keyword: keyword || undefined
      })
      const data = res.data?.data
      setNews(data?.records || [])
      setPagination({
        ...pagination,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载资讯列表失败', error)
      message.error('加载资讯列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (value: string) => {
    setKeyword(value)
    setPagination({ ...pagination, page: 1 })
  }

  const handlePageChange = (page: number, pageSize: number) => {
    setPagination({ ...pagination, page, size: pageSize })
  }

  return (
    <div className="page" style={{ paddingTop: 40 }}>
      <PageHeader title="健康资讯" />

      <Card style={{ marginBottom: 24 }}>
        <Input.Search
          placeholder="搜索资讯标题或内容..."
          size="large"
          prefix={<SearchOutlined />}
          allowClear
          enterButton="搜索"
          onSearch={handleSearch}
          onChange={(e) => {
            if (e.target.value === '') {
              handleSearch('')
            }
          }}
        />
      </Card>

      {news.length === 0 && !loading ? (
        <Card>
          <Empty description="暂无资讯" />
        </Card>
      ) : (
        <>
          <Row gutter={[16, 16]}>
            {news.map((item) => (
              <Col key={item.id} xs={24} sm={12} md={8}>
                <Card
                  loading={loading}
                  hoverable
                  onClick={() => navigate(`/news/${item.id}`)}
                  cover={
                    item.coverOssPath && (
                      <div style={{
                        height: 180,
                        overflow: 'hidden'
                      }}>
                        <img
                          src={item.coverOssPath}
                          alt={item.newsName}
                          style={{
                            width: '100%',
                            height: '100%',
                            objectFit: 'cover'
                          }}
                        />
                      </div>
                    )
                  }
                >
                  <Card.Meta
                    title={
                      <Title level={5} ellipsis={{ rows: 1 }} style={{ color: '#2c3e50' }}>
                        {item.newsName}
                      </Title>
                    }
                    description={
                      <>
                        <Paragraph
                          ellipsis={{ rows: 2 }}
                          type="secondary"
                          style={{ marginBottom: 12 }}
                        >
                          {item.newsSummary}
                        </Paragraph>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          {item.category && (
                            <Tag color="blue">{item.category}</Tag>
                          )}
                          <span style={{ fontSize: 12, color: '#999' }}>
                            {new Date(item.createTime).toLocaleDateString()}
                          </span>
                          <span style={{ fontSize: 12, color: '#999' }}>
                            {item.viewCount} 次浏览
                          </span>
                        </div>
                      </>
                    }
                  />
                </Card>
              </Col>
            ))}
          </Row>

          {pagination.total > 0 && (
            <div style={{ marginTop: 24, textAlign: 'center' }}>
              <Pagination
                current={pagination.page}
                pageSize={pagination.size}
                total={pagination.total}
                showSizeChanger
                showTotal={(total) => `共 ${total} 条`}
                onChange={handlePageChange}
              />
            </div>
          )}
        </>
      )}
    </div>
  )
}
