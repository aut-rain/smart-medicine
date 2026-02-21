import { Button, Form, Input, message, Modal, Space, Typography } from 'antd'
import { useState } from 'react'
import { authService, RegisterDTO } from '@/services/authService'
import { setAuth } from '@/utils/auth'
import { UserOutlined, LockOutlined, MailOutlined, IdcardOutlined, SafetyOutlined } from '@ant-design/icons'

interface RegisterModalProps {
  open: boolean
  onCancel: () => void
  onSuccess: () => void
  onSwitchToLogin: () => void
}

export default function RegisterModal({ open, onCancel, onSuccess, onSwitchToLogin }: RegisterModalProps) {
  const [loading, setLoading] = useState(false)
  const [countdown, setCountdown] = useState(0)
  const [form] = Form.useForm()

  const onFinish = async (values: RegisterDTO) => {
    setLoading(true)
    try {
      const res = await authService.register(values)
      const data = res.data?.data
      setAuth(data.token, data.refreshToken)
      message.success('注册成功')
      form.resetFields()
      onSuccess()
    } catch (e: any) {
      message.error(e?.response?.data?.message || '注册失败')
    } finally {
      setLoading(false)
    }
  }

  const sendCode = async () => {
    const email = form.getFieldValue('userEmail')
    if (!email) {
      message.warning('请先填写邮箱')
      return
    }
    if (countdown > 0) {
      return
    }
    try {
      await authService.sendEmailCode(email)
      message.success('验证码已发送到您的邮箱')
      // 开始60秒倒计时
      setCountdown(60)
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer)
            return 0
          }
          return prev - 1
        })
      }, 1000)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '发送失败')
    }
  }

  return (
    <Modal
      open={open}
      onCancel={() => {
        form.resetFields()
        setCountdown(0)
        onCancel()
      }}
      footer={null}
      width={500}
      centered
      destroyOnClose
    >
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        {/* 标题区域 */}
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <div style={{ fontSize: 48, marginBottom: 12 }}>🌱</div>
          <Typography.Title level={3} style={{ margin: 0, color: '#2d3436' }}>
            开启健康新生活
          </Typography.Title>
          <Typography.Text type="secondary" style={{ fontSize: 14 }}>
            创建账号，开始你的健康管理之旅
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
          <Form.Item name="userName" label="姓名" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input 
              prefix={<IdcardOutlined style={{ color: '#4a90e2' }} />}
              placeholder="请输入姓名" 
              size="large"
            />
          </Form.Item>
          <Form.Item 
            name="userEmail" 
            label="邮箱" 
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入正确的邮箱格式' }
            ]}
          >
            <Input 
              prefix={<MailOutlined style={{ color: '#4a90e2' }} />}
              placeholder="请输入邮箱" 
              size="large"
            />
          </Form.Item>
          <Form.Item name="emailCode" label="邮箱验证码" rules={[{ required: true, message: '请输入验证码' }]}>
            <Input 
              prefix={<SafetyOutlined style={{ color: '#4a90e2' }} />}
              placeholder="请输入验证码" 
              size="large"
              addonAfter={
                <Button 
                  type="link" 
                  onClick={sendCode}
                  disabled={countdown > 0}
                  style={{ fontWeight: 600 }}
                >
                  {countdown > 0 ? `${countdown}秒后重试` : '发送验证码'}
                </Button>
              } 
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
              注册
            </Button>
          </Form.Item>
          <Form.Item style={{ textAlign: 'center', marginBottom: 0 }}>
            <Typography.Text type="secondary">
              已有账号？
            </Typography.Text>
            {' '}
            <Typography.Link onClick={onSwitchToLogin} style={{ fontWeight: 600 }}>
              立即登录
            </Typography.Link>
          </Form.Item>
        </Form>
      </Space>
    </Modal>
  )
}
