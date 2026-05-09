import { Card, Descriptions, Divider, List, Space, Typography } from 'antd'
import { useEffect, useRef, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { illnessService } from '@/services/illnessService'
import { medicineService } from '@/services/medicineService'
import { historyService } from '@/services/historyService'
import { getUserId } from '@/utils/auth'
import PageHeader from '@/components/PageHeader'

export default function IllnessDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [detail, setDetail] = useState<any>(null)
  const [medicines, setMedicines] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const requestedDetailIdRef = useRef<string | null>(null)

  useEffect(() => {
    if (!id) return
    if (requestedDetailIdRef.current === id) return
    requestedDetailIdRef.current = id
    setLoading(true)
    Promise.all([
      illnessService.getDetail(Number(id)),
      medicineService.listByIllness(Number(id))
    ]).then(([illnessRes, medicineRes]) => {
      const illness = illnessRes.data?.data
      setDetail(illness)
      setMedicines(medicineRes.data?.data || [])

      // 异步记录浏览历史
      const userId = getUserId()
      if (userId && illness) {
        historyService.record(userId, 2, illness.id, illness.illnessName).catch((err) => {
          console.warn('记录浏览历史失败:', err)
        })
      }
    }).catch((error) => {
      console.error('加载疾病详情失败', error)
    }).finally(() => setLoading(false))
  }, [id])

  if (!detail && !loading) {
    return (
      <div className="page">
        <Card>
          <Typography.Text>疾病不存在或已被删除</Typography.Text>
        </Card>
      </div>
    )
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="疾病详情" />
        <Card loading={loading}>
          {detail && (
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              {/* 基本信息 */}
              <Descriptions title="基本信息" bordered>
                <Descriptions.Item label="疾病名称">{detail.illnessName}</Descriptions.Item>
                <Descriptions.Item label="所属分类">{detail.category?.name || '未知'}</Descriptions.Item>
                <Descriptions.Item label="浏览量">{detail.pageviews ?? 0}</Descriptions.Item>
              </Descriptions>

              <Divider />

              {/* 症状 */}
              <Card size="small" title="症状">
                <Typography.Paragraph>
                  {detail.illnessSymptom || '暂无描述'}
                </Typography.Paragraph>
              </Card>

              {/* 特殊症状 */}
              {detail.specialSymptom && (
                <Card size="small" title="特殊症状">
                  <Typography.Paragraph>
                    {detail.specialSymptom}
                  </Typography.Paragraph>
                </Card>
              )}

              {/* 病因 */}
              <Card size="small" title="病因">
                <Typography.Paragraph>
                  {detail.includeReason || '暂无相关信息'}
                </Typography.Paragraph>
              </Card>

              <Divider />

              {/* 关联药品 */}
              <Typography.Title level={4}>关联药品</Typography.Title>
              {medicines.length > 0 ? (
                <List
                  grid={{ gutter: 16, column: 4 }}
                  dataSource={medicines}
                  renderItem={(m) => (
                    <List.Item>
                      <Card
                        hoverable
                        size="small"
                        title={m.medicineName}
                        onClick={() => navigate(`/medicine/${m.id}`)}
                      >
                        <Typography.Paragraph ellipsis={{ rows: 2 }}>
                          {m.medicineEffect}
                        </Typography.Paragraph>
                        <Typography.Text type="danger" strong>
                          ￥{m.medicinePrice}
                        </Typography.Text>
                      </Card>
                    </List.Item>
                  )}
                />
              ) : (
                <Typography.Text type="secondary">暂无关联药品</Typography.Text>
              )}
            </Space>
          )}
        </Card>
      </Space>
    </div>
  )
}
