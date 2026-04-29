import { Button, Card, Input, Modal, Space, Table, Tag, Typography, message } from 'antd'
import { useEffect, useState } from 'react'
import { adminFeedbackService } from '@/services/adminService'
import PageHeader from '@/components/PageHeader'

const { Paragraph } = Typography

export default function AdminFeedbacks() {
  const [feedbacks, setFeedbacks] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [detailModal, setDetailModal] = useState({ visible: false, data: null as any })

  useEffect(() => {
    loadFeedbacks()
  }, [])

  const loadFeedbacks = async (page = 1) => {
    setLoading(true)
    try {
      const res = await adminFeedbackService.list({ page, size: pagination.size })
      const data = res.data?.data
      setFeedbacks(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      message.error('加载反馈列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除反馈？',
      content: '此操作不可恢复，确认删除该条反馈吗？',
      okText: '确认删除',
      cancelText: '取消',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          await adminFeedbackService.delete(id)
          message.success('删除成功')
          loadFeedbacks(pagination.page)
        } catch (error: any) {
          message.error(error?.response?.data?.message || '删除失败')
        }
      }
    })
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 60,
    },
    {
      title: '反馈标题',
      dataIndex: 'feedbackTitle',
      key: 'feedbackTitle',
      width: 200,
      ellipsis: true,
    },
    {
      title: '反馈内容',
      dataIndex: 'feedbackContent',
      key: 'feedbackContent',
      width: 300,
      ellipsis: true,
      render: (text: string) => (
        <Paragraph ellipsis={{ rows: 2, tooltip: text }} style={{ margin: 0 }}>
          {text}
        </Paragraph>
      ),
    },
    {
      title: '联系方式',
      dataIndex: 'contact',
      key: 'contact',
      width: 180,
      ellipsis: true,
      render: (text: string) => text || <Tag>未提供</Tag>,
    },
    {
      title: '用户账号',
      dataIndex: 'userAccount',
      key: 'userAccount',
      width: 120,
    },
    {
      title: '提交时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (date: string) => date ? new Date(date).toLocaleString() : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 140,
      render: (_: any, record: any) => (
        <Space>
          <Button
            type="link"
            onClick={() => setDetailModal({ visible: true, data: record })}
          >
            查看
          </Button>
          <Button
            type="link"
            danger
            onClick={() => handleDelete(record.id)}
          >
            删除
          </Button>
        </Space>
      ),
    }
  ]

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="反馈管理" />

        {/* 反馈列表 */}
        <Card>
          <Table
            dataSource={feedbacks}
            columns={columns}
            loading={loading}
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              showTotal: (total) => `共 ${total} 条反馈`,
              onChange: (page) => loadFeedbacks(page),
            }}
            rowKey="id"
          />
        </Card>

        {/* 反馈详情弹窗 */}
        <Modal
          title="反馈详情"
          open={detailModal.visible}
          onCancel={() => setDetailModal({ visible: false, data: null })}
          footer={
            <Space>
              <Button onClick={() => setDetailModal({ visible: false, data: null })}>
                关闭
              </Button>
              <Button
                danger
                onClick={() => {
                  if (detailModal.data) {
                    handleDelete(detailModal.data.id)
                    setDetailModal({ visible: false, data: null })
                  }
                }}
              >
                删除该反馈
              </Button>
            </Space>
          }
          width={600}
        >
          {detailModal.data && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div>
                <span style={{ color: '#8c8c8c', fontSize: 13 }}>反馈标题</span>
                <div style={{ fontSize: 16, fontWeight: 500, marginTop: 4 }}>
                  {detailModal.data.feedbackTitle}
                </div>
              </div>
              <div>
                <span style={{ color: '#8c8c8c', fontSize: 13 }}>反馈内容</span>
                <div style={{
                  marginTop: 4,
                  padding: 12,
                  background: '#f5f5f5',
                  borderRadius: 6,
                  lineHeight: 1.8,
                  whiteSpace: 'pre-wrap',
                }}>
                  {detailModal.data.feedbackContent}
                </div>
              </div>
              <div style={{ display: 'flex', gap: 32 }}>
                <div>
                  <span style={{ color: '#8c8c8c', fontSize: 13 }}>联系方式</span>
                  <div style={{ marginTop: 4 }}>
                    {detailModal.data.contact || '未提供'}
                  </div>
                </div>
                <div>
                  <span style={{ color: '#8c8c8c', fontSize: 13 }}>用户账号</span>
                  <div style={{ marginTop: 4 }}>
                    {detailModal.data.userAccount || '-'}
                  </div>
                </div>
              </div>
              <div>
                <span style={{ color: '#8c8c8c', fontSize: 13 }}>提交时间</span>
                <div style={{ marginTop: 4 }}>
                  {detailModal.data.createTime
                    ? new Date(detailModal.data.createTime).toLocaleString()
                    : '-'}
                </div>
              </div>
            </div>
          )}
        </Modal>
      </Space>
    </div>
  )
}
