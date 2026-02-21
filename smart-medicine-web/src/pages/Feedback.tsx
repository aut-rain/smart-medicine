import { Button, Card, Form, Input, List, message, Space, Tabs, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { feedbackService } from '@/services/feedbackService'
import PageHeader from '@/components/PageHeader'
import dayjs from 'dayjs'

export default function Feedback() {
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [myFeedbacks, setMyFeedbacks] = useState<any[]>([])
  const [activeTab, setActiveTab] = useState('submit')

  useEffect(() => {
    if (activeTab === 'my') {
      loadMyFeedbacks()
    }
  }, [activeTab])

  const loadMyFeedbacks = async () => {
    try {
      const res = await feedbackService.getMyList(1, 20)
      setMyFeedbacks(res.data?.data?.records || [])
    } catch (error) {
      console.error('加载反馈列表失败', error)
    }
  }

  const onSubmit = async (values: any) => {
    setLoading(true)
    try {
      await feedbackService.submit(values)
      message.success('提交成功，感谢您的反馈！')
      form.resetFields()
      if (activeTab === 'my') {
        loadMyFeedbacks()
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '提交失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page" style={{ maxWidth: 900 }}>
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="意见反馈" />
        
        <Card>
          <Tabs
            activeKey={activeTab}
            onChange={setActiveTab}
            items={[
              {
                key: 'submit',
                label: '提交反馈',
                children: (
                  <Form form={form} layout="vertical" onFinish={onSubmit}>
                    <Form.Item
                      name="feedbackTitle"
                      label="反馈标题"
                      rules={[{ required: true, message: '请输入反馈标题' }]}
                    >
                      <Input placeholder="简要描述您的问题或建议" />
                    </Form.Item>
                    
                    <Form.Item
                      name="feedbackContent"
                      label="反馈内容"
                      rules={[{ required: true, message: '请输入反馈内容' }]}
                    >
                      <Input.TextArea
                        rows={6}
                        placeholder="请详细描述您遇到的问题或改进建议..."
                      />
                    </Form.Item>
                    
                    <Form.Item name="contact" label="联系方式（可选）">
                      <Input placeholder="邮箱或电话，方便我们联系您" />
                    </Form.Item>
                    
                    <Form.Item>
                      <Button type="primary" htmlType="submit" loading={loading}>
                        提交反馈
                      </Button>
                    </Form.Item>
                  </Form>
                ),
              },
              {
                key: 'my',
                label: '我的反馈',
                children: (
                  <List
                    dataSource={myFeedbacks}
                    renderItem={(item) => (
                      <List.Item>
                        <List.Item.Meta
                          title={item.feedbackTitle}
                          description={
                            <Space direction="vertical" style={{ width: '100%' }}>
                              <div>{item.feedbackContent}</div>
                              <Typography.Text type="secondary">
                                提交时间：{dayjs(item.createTime).format('YYYY-MM-DD HH:mm:ss')}
                              </Typography.Text>
                            </Space>
                          }
                        />
                      </List.Item>
                    )}
                  />
                ),
              },
            ]}
          />
        </Card>
      </Space>
    </div>
  )
}
