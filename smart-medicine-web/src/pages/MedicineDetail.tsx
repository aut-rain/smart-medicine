import { Button, Card, Col, Descriptions, Divider, Image, Row, Space, Typography, message } from 'antd'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { medicineService } from '@/services/medicineService'
import PageHeader from '@/components/PageHeader'

export default function MedicineDetail() {
  const { id } = useParams()
  const [detail, setDetail] = useState<any>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!id) return
    setLoading(true)
    medicineService.getDetail(Number(id)).then((res) => {
      setDetail(res.data?.data)
    }).catch((error) => {
      console.error('加载药品详情失败', error)
      message.error('加载药品详情失败')
    }).finally(() => setLoading(false))
  }, [id])

  if (!detail && !loading) {
    return (
      <div className="page">
        <Card>
          <Typography.Text>药品不存在或已被删除</Typography.Text>
        </Card>
      </div>
    )
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="药品详情" />
        <Card loading={loading}>
          {detail && (
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              {/* 药品图片 */}
              {detail.imgPath && (
                <Row justify="center" style={{ marginBottom: 24 }}>
                  <Col>
                    <Image
                      src={detail.imgPath}
                      alt={detail.medicineName}
                      style={{ maxWidth: '100%', maxHeight: 400, objectFit: 'contain' }}
                      placeholder={
                        <Card loading style={{ width: 300, height: 300 }} />
                      }
                      fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMIAAADDCAYAAADQvc6UAAABRWlDQ1BJQ0MgUHJvZmlsZQAAKJFjYGASSSwoyGFhYGDIzSspCnJ3UoiIjFJgf8LAwSDCIMogwMCcmFxc4BgQ4ANUwgCjUcG3awyMIPqyLsis7PPOq3QdDFcvjV3jOD1boQVTPQrgSkktTgbSf4A4LbmgqISBgTEFyFYuLykAsTuAbJEioKOA7DkgdjqEvQHEToKwj4DVhAQ5A9k3gGyB5IxEoBmML4BsnSQk8XQkNtReEOBxcfXxUQg1Mjc0dyHgXNJBSWpFCYh2zi+oLMpMzyhRcASGUqqCZ16yno6CkYGRAQMDKMwhqj/fAIcloxgHQqxAjIHBEugw5sUIsSQpBobtQPdLciLEVJYzMPBHMDBsayhILEqEO4DxG0txmrERhM29nYGBddr//5/DGRjYNRkY/l7////39v///y4Dmn+LgeHANwDrkl1AuO+pmgAAADhlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAAqACAAQAAAABAAAAwqADAAQAAAABAAAAwwAAAAD9b/HnAAAHlklEQVR4Ae3dP3PTWBSGcbGzM6GCKqlIBRV0dHRJFarQ0eUT8LH4BnRU0NHR0UEFVdIlFRV7TzRksomPY8uykTk/zewQfKw/9znv4yvJynLv4uLiV2dBoDiBf4qP3/ARuCRABEFAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghggQAQZQKAnYEaQBAQaASKIAQJEkAEEegJmBElAoBEgghgg"
                    />
                  </Col>
                </Row>
              )}
              
              {/* 基本信息 */}
              <Descriptions title="基本信息" bordered column={2}>
                <Descriptions.Item label="药品名称">{detail.medicineName}</Descriptions.Item>
                <Descriptions.Item label="品牌">{detail.medicineBrand || '未知'}</Descriptions.Item>
                <Descriptions.Item label="类型">{detail.medicineTypeDesc}</Descriptions.Item>
                <Descriptions.Item label="价格">
                  <Typography.Text type="danger" strong>
                    ￥{detail.medicinePrice}
                  </Typography.Text>
                </Descriptions.Item>
                <Descriptions.Item label="关键词" span={2}>
                  {detail.keyword || '无'}
                </Descriptions.Item>
              </Descriptions>

              <Divider />

              {/* 功能主治 */}
              <Card size="small" title="功能主治">
                <Typography.Paragraph>
                  {detail.medicineEffect || '暂无描述'}
                </Typography.Paragraph>
              </Card>

              {/* 用法用量 */}
              <Card size="small" title="用法用量">
                <Typography.Paragraph>
                  {detail.usAge || '请遵医嘱'}
                </Typography.Paragraph>
              </Card>

              {/* 禁忌 */}
              <Card size="small" title="禁忌">
                <Typography.Paragraph type="danger">
                  {detail.taboo || '无特殊禁忌'}
                </Typography.Paragraph>
              </Card>

              {/* 药物相互作用 */}
              <Card size="small" title="药物相互作用">
                <Typography.Paragraph>
                  {detail.interaction || '暂无相关信息'}
                </Typography.Paragraph>
              </Card>

              <Divider />

              {/* 操作按钮 */}
              <Space>
                <Button type="primary">收藏药品</Button>
                <Button>分享</Button>
              </Space>
            </Space>
          )}
        </Card>
      </Space>
    </div>
  )
}
