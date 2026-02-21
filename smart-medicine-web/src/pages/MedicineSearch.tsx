import { Button, Card, Col, Empty, Form, Input, InputNumber, Row, Select, Space, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { medicineService } from '@/services/medicineService'
import { useNavigate, useSearchParams } from 'react-router-dom'
import PageHeader from '@/components/PageHeader'

const { Option } = Select

export default function MedicineSearch() {
  const [form] = Form.useForm()
  const [searchParams] = useSearchParams()
  const [medicines, setMedicines] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 12, total: 0 })
  const navigate = useNavigate()

  useEffect(() => {
    // 初始化表单值
    const keyword = searchParams.get('keyword') || ''
    const medicineType = searchParams.get('medicineType')
    const minPrice = searchParams.get('minPrice')
    const maxPrice = searchParams.get('maxPrice')
    
    form.setFieldsValue({
      keyword,
      medicineType: medicineType ? Number(medicineType) : undefined,
      minPrice: minPrice ? Number(minPrice) : undefined,
      maxPrice: maxPrice ? Number(maxPrice) : undefined
    })
    
    // 执行搜索（包括空搜索，展示全部）
    handleSearch({ 
      keyword, 
      medicineType: medicineType ? Number(medicineType) : undefined,
      minPrice: minPrice ? Number(minPrice) : undefined,
      maxPrice: maxPrice ? Number(maxPrice) : undefined
    })
  }, [searchParams])

  const handleSearch = async (values: any) => {
    const params = {
      ...values,
      page: 1,
      size: pagination.size
    }
    
    // 更新URL参数
    const urlParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        urlParams.append(key, String(value))
      }
    })
    const urlString = urlParams.toString()
    navigate(`/medicine-search${urlString ? '?' + urlString : ''}`, { replace: true })
    
    setLoading(true)
    try {
      const res = await medicineService.list(params)
      const data = res.data?.data
      setMedicines(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 12,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('搜索药品失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handlePageChange = async (page: number) => {
    const values = form.getFieldsValue()
    const params = {
      ...values,
      page,
      size: pagination.size
    }
    
    setLoading(true)
    try {
      const res = await medicineService.list(params)
      const data = res.data?.data
      setMedicines(data?.records || [])
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
        <PageHeader title="药品搜索" />
        
        {/* 搜索表单 */}
        <Card>
          <Form form={form} layout="vertical" onFinish={handleSearch}>
            <Row gutter={24}>
              <Col xs={24} md={12} lg={6}>
                <Form.Item name="keyword" label="药品名称或功效">
                  <Input placeholder="输入药品名称或功效关键词" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12} lg={6}>
                <Form.Item name="medicineType" label="药品类型">
                  <Select placeholder="请选择药品类型" allowClear>
                    <Option value={1}>处方药</Option>
                    <Option value={2}>非处方药</Option>
                    <Option value={3}>保健品</Option>
                    <Option value={4}>医疗器械</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} md={12} lg={6}>
                <Form.Item name="minPrice" label="最低价格">
                  <InputNumber placeholder="最低价格" style={{ width: '100%' }} />
                </Form.Item>
              </Col>
              <Col xs={24} md={12} lg={6}>
                <Form.Item name="maxPrice" label="最高价格">
                  <InputNumber placeholder="最高价格" style={{ width: '100%' }} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading}>
                搜索药品
              </Button>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 搜索结果 */}
        <div>
          <Typography.Title level={4}>
            搜索结果 ({pagination.total} 条)
          </Typography.Title>
          
          {medicines.length === 0 && !loading ? (
            <Empty 
              image="/assets/images/bg.png"
              imageStyle={{
                height: 300,
                marginBottom: 16
              }}
              description={
                <span style={{ fontSize: 16, color: '#999' }}>
                  暂无符合条件的药品
                </span>
              }
            >
              <Button type="primary" onClick={() => form.resetFields()}>
                重置搜索条件
              </Button>
            </Empty>
          ) : (
            <>
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
                        <Typography.Text strong style={{ fontSize: 18, color: '#f5222d' }}>
                          ￥{item.medicinePrice}
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
