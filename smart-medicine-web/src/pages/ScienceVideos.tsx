import { Button, Card, Col, Form, Input, Row, Space, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { scienceVideoService } from '@/services/scienceVideoService'
import { useNavigate, useSearchParams } from 'react-router-dom'
import PageHeader from '@/components/PageHeader'
import { PlayCircleOutlined } from '@ant-design/icons'

export default function ScienceVideos() {
  const [form] = Form.useForm()
  const [searchParams] = useSearchParams()
  const [videos, setVideos] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 12, total: 0 })
  const navigate = useNavigate()

  useEffect(() => {
    // 初始化表单值并加载数据
    const keyword = searchParams.get('keyword') || ''
    form.setFieldsValue({ keyword })
    
    const params = {
      keyword,
      page: 1,
      size: 12
    }
    
    setLoading(true)
    scienceVideoService.list(params)
      .then((res) => {
        const data = res.data?.data
        setVideos(data?.records || [])
        setPagination({
          page: data?.current || 1,
          size: data?.size || 12,
          total: data?.total || 0
        })
      })
      .catch((error) => {
        console.error('加载视频列表失败', error)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [searchParams])

  const handleSearch = async (values: any) => {
    // 更新URL参数
    const urlParams = new URLSearchParams()
    if (values.keyword) {
      urlParams.append('keyword', values.keyword)
    }
    const urlString = urlParams.toString()
    navigate(`/science-videos${urlString ? '?' + urlString : ''}`, { replace: true })
  }

  const handlePageChange = async (page: number) => {
    const values = form.getFieldsValue()
    const params = {
      keyword: values.keyword || '',
      page,
      size: pagination.size
    }
    
    setLoading(true)
    try {
      const res = await scienceVideoService.list(params)
      const data = res.data?.data
      setVideos(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 12,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('分页加载失败', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="健康科普视频" />
        
        {/* 搜索表单 */}
        <Card>
          <Form form={form} layout="vertical" onFinish={handleSearch}>
            <Row gutter={24}>
              <Col xs={24} md={12} lg={8}>
                <Form.Item name="keyword" label="视频标题或描述">
                  <Input placeholder="输入关键词，例如：饮食、运动" />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading}>
                搜索视频
              </Button>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 搜索结果 */}
        <div>
          <Typography.Title level={4}>
            搜索结果 ({pagination.total} 条)
          </Typography.Title>
          
          {videos.length === 0 && !loading ? (
            <div style={{ 
              textAlign: 'center', 
              padding: '60px 20px',
              color: '#999',
              fontSize: 16
            }}>
              暂无符合条件的视频
            </div>
          ) : (
            <>
              <Row gutter={[16, 16]}>
                {videos.map((item) => (
                  <Col key={item.id} xs={24} sm={12} md={8} lg={6}>
                    <Card 
                      loading={loading} 
                      hoverable 
                      onClick={() => navigate(`/science-video/${item.id}`)}
                      cover={
                        <div style={{ 
                          height: 180, 
                          overflow: 'hidden',
                          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          position: 'relative'
                        }}>
                          {item.imgPath ? (
                            <img
                              alt={item.title}
                              src={item.imgPath}
                              style={{ 
                                width: '100%',
                                height: '100%',
                                objectFit: 'cover'
                              }}
                              onError={(e) => {
                                e.currentTarget.style.display = 'none'
                              }}
                            />
                          ) : (
                            <PlayCircleOutlined style={{ fontSize: 64, color: 'rgba(255,255,255,0.8)' }} />
                          )}
                          <div style={{
                            position: 'absolute',
                            bottom: 0,
                            left: 0,
                            right: 0,
                            background: 'linear-gradient(transparent, rgba(0,0,0,0.6))',
                            padding: '20px 12px 8px',
                            color: '#fff',
                            fontSize: 12
                          }}>
                            <PlayCircleOutlined style={{ marginRight: 4 }} />
                            点击播放
                          </div>
                        </div>
                      }
                      style={{ height: '100%' }}
                    >
                      <Card.Meta
                        title={
                          <Typography.Text ellipsis strong style={{ fontSize: 15, color: '#2c3e50' }}>
                            {item.title}
                          </Typography.Text>
                        }
                        description={
                          <Space direction="vertical" size="small" style={{ width: '100%' }}>
                            <Typography.Paragraph 
                              ellipsis={{ rows: 2 }} 
                              type="secondary" 
                              style={{ margin: 0, fontSize: 13 }}
                            >
                              {item.description || '暂无描述'}
                            </Typography.Paragraph>
                            <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                              {new Date(item.createTime).toLocaleDateString()}
                            </Typography.Text>
                          </Space>
                        }
                      />
                    </Card>
                  </Col>
                ))}
              </Row>
              
              {/* 分页 */}
              {pagination.total > 0 && (
                <div style={{ textAlign: 'right', marginTop: 24 }}>
                  <Button.Group>
                    <Button
                      disabled={pagination.page <= 1}
                      onClick={() => handlePageChange(pagination.page - 1)}
                    >
                      上一页
                    </Button>
                    <Button disabled>
                      {pagination.page} / {Math.ceil(pagination.total / pagination.size)}
                    </Button>
                    <Button
                      disabled={pagination.page >= Math.ceil(pagination.total / pagination.size)}
                      onClick={() => handlePageChange(pagination.page + 1)}
                    >
                      下一页
                    </Button>
                  </Button.Group>
                </div>
              )}
            </>
          )}
        </div>
      </Space>
    </div>
  )
}