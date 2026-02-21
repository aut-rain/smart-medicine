import { Button, Card, Col, Form, Input, Row, Space, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { illnessService } from '@/services/illnessService'
import { useNavigate, useSearchParams } from 'react-router-dom'
import PageHeader from '@/components/PageHeader'

export default function IllnessSearch() {
  const [form] = Form.useForm()
  const [searchParams] = useSearchParams()
  const [illnesses, setIllnesses] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 12, total: 0 })
  const navigate = useNavigate()

  useEffect(() => {
    // 初始化表单值
    const keyword = searchParams.get('keyword') || ''
    
    form.setFieldsValue({
      keyword
    })
    
    // 执行搜索（包括空搜索，展示全部）
    loadData(keyword)
  }, [searchParams])

  const loadData = async (keyword: string) => {
    const params = {
      keyword: keyword || '',
      page: 1,
      size: pagination.size
    }
    
    setLoading(true)
    try {
      const res = await illnessService.searchPaged(params)
      const data = res.data?.data
      setIllnesses(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 12,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('搜索疾病失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async (values: any) => {
    const keyword = values.keyword || ''
    
    // 更新URL参数
    const urlParams = new URLSearchParams()
    if (keyword) {
      urlParams.append('keyword', keyword)
    }
    const urlString = urlParams.toString()
    navigate(`/illness-search${urlString ? '?' + urlString : ''}`, { replace: true })
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
      const res = await illnessService.searchPaged(params)
      const data = res.data?.data
      setIllnesses(data?.records || [])
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
        <PageHeader title="疾病搜索" />
        
        {/* 搜索表单 */}
        <Card>
          <Form form={form} layout="vertical" onFinish={handleSearch}>
            <Row gutter={24}>
              <Col xs={24} md={12} lg={8}>
                <Form.Item name="keyword" label="疾病名称或症状">
                  <Input placeholder="输入关键词，例如：感冒、头痛" />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading}>
                搜索疾病
              </Button>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 搜索结果 */}
        <div>
          <Typography.Title level={4}>
            搜索结果 ({pagination.total} 条)
          </Typography.Title>
          
          {illnesses.length === 0 && !loading ? (
            <div style={{ 
              textAlign: 'center', 
              padding: '60px 20px',
              color: '#999',
              fontSize: 16
            }}>
              暂无符合条件的疾病
            </div>
          ) : (
            <>
              <Row gutter={[16, 16]}>
                {illnesses.map((item) => (
                  <Col key={item.id} xs={24} sm={12} md={8} lg={6}>
                    <Card 
                      loading={loading} 
                      hoverable 
                      onClick={() => navigate(`/illness/${item.id}`)}
                      style={{ height: '100%' }}
                    >
                      <Card.Meta
                        title={
                          <Typography.Text ellipsis strong style={{ fontSize: 15, color: '#2c3e50' }}>
                            {item.illnessName}
                          </Typography.Text>
                        }
                        description={
                          <Space direction="vertical" size="small" style={{ width: '100%' }}>
                            <Typography.Paragraph 
                              ellipsis={{ rows: 3 }} 
                              type="secondary" 
                              style={{ margin: 0, fontSize: 13 }}
                            >
                              {item.illnessSymptom}
                            </Typography.Paragraph>
                            {item.kindName && (
                              <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                                分类：{item.kindName}
                              </Typography.Text>
                            )}
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
