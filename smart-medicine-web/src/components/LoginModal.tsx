import { Button, Form, Input, message, Modal, Space, Typography } from 'antd'
import { useState } from 'react'
import { authService } from '@/services/authService'
import { setAuth } from '@/utils/auth'
import { UserOutlined, LockOutlined } from '@ant-design/icons'

interface LoginModalProps {
  open: boolean
  onCancel: () => void
  onSuccess: () => void
  onSwitchToRegister: () => void
}

export default function LoginModal({ open, onCancel, onSuccess, onSwitchToRegister }: LoginModalProps) {
  const [loading, setLoading] = useState(false)
  const [form] = Form.useForm()

  const onFinish = async (values: any) => {
    setLoading(true)
    try {
      const res = await authService.login(values)
      const data = res.data?.data
      setAuth(data.token, data.refreshToken)
      message.success('登录成功')
      form.resetFields()
      onSuccess()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal
      open={open}
      onCancel={() => {
        form.resetFields()
        onCancel()
      }}
      footer={null}
      width={440}
      centered
      destroyOnClose
    >
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        {/* 标题区域 */}
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <div style={{ fontSize: 48, marginBottom: 12 }}>🌿</div>
          <Typography.Title level={3} style={{ margin: 0, color: '#2d3436' }}>
            欢迎回来
          </Typography.Title>
          <Typography.Text type="secondary" style={{ fontSize: 14 }}>
            登录以继续您的健康之旅
          </Typography.Text>
        </div>

        <Form form={form} layout="vertical" onFinish={onFinish}>
          <Form.Item name="userAccount" label="账号" rules={[{ required: true, message: '请输入账号' }]}>
            <Input 
              prefix={<UserOutlined style={{ color: '#4a90e2' }} />}
              placeholder="请输入账号" 
              size="large"
            />
          </Form.Item>
          <Form.Item name="userPwd" label="密码" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password 
              prefix={<LockOutlined style={{ color: '#4a90e2' }} />}
              placeholder="请输入密码" 
              size="large"
            />
          </Form.Item>
          <Form.Item style={{ marginTop: 24 }}>
            <Button 
              type="primary" 
              htmlType="submit" 
              block 
              loading={loading}
              size="large"
              style={{ height: 44, fontSize: 15, fontWeight: 500 }}
            >
              登录
            </Button>
          </Form.Item>
          <Form.Item style={{ textAlign: 'center', marginBottom: 0 }}>
            <Typography.Text type="secondary">
              还没有账号？
            </Typography.Text>
            {' '}
            <Typography.Link onClick={onSwitchToRegister} style={{ fontWeight: 600 }}>
              立即注册
            </Typography.Link>
          </Form.Item>
        </Form>
      </Space>
    </Modal>
  )
}
