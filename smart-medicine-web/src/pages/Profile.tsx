import { Avatar, Button, Card, Descriptions, Form, Input, List, message, Modal, Progress, Space, Tabs, Typography, Upload } from 'antd'
import { useEffect, useState } from 'react'
import { UserOutlined, PlusOutlined } from '@ant-design/icons'
import { historyService } from '@/services/historyService'
import { usersService } from '@/services/usersService'
import { fileService } from '@/services/fileService'
import { useNavigate } from 'react-router-dom'
import dayjs from 'dayjs'
import PageHeader from '@/components/PageHeader'

export default function Profile() {
  const [user, setUser] = useState<any>(null)
  const [histories, setHistories] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('info')
  const [profileForm] = Form.useForm()
  const [passwordForm] = Form.useForm()
  const [uploadProgress, setUploadProgress] = useState(0)
  const [uploading, setUploading] = useState(false)
  const [avatarUrl, setAvatarUrl] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    loadUserInfo()
    loadHistories()
  }, [])

  const loadUserInfo = async () => {
    try {
      const res = await usersService.getCurrent()
      const userData = res.data?.data
      setUser(userData)
      profileForm.setFieldsValue(userData)
      setAvatarUrl(userData?.imgPath || '')
    } catch (error: any) {
      message.error('加载用户信息失败')
    }
  }

  const loadHistories = async () => {
    try {
      const res = await historyService.list(1, 20)
      setHistories(res.data?.data?.records || [])
    } catch (error: any) {
      console.error('加载历史记录失败', error)
    }
  }

  const onUpdateProfile = async (values: any) => {
    setLoading(true)
    try {
      // 将上传的头像URL添加到表单数据中
      const submitData = {
        ...values,
        imgPath: avatarUrl || undefined
      }
      const res = await usersService.updateProfile(submitData)
      setUser(res.data?.data)
      message.success('修改成功')
      profileForm.setFieldsValue(res.data?.data)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '修改失败')
    } finally {
      setLoading(false)
    }
  }

  // 处理头像上传
  const handleAvatarUpload = async (file: File) => {
    setUploading(true)
    setUploadProgress(0)
    
    try {
      const res = await fileService.uploadImage(file, (percent) => {
        setUploadProgress(percent)
      })
      
      const url = res.data?.data?.url
      if (url) {
        setAvatarUrl(url)
        message.success('头像上传成功！')
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '头像上传失败')
    } finally {
      setUploading(false)
    }
    
    return false // 阻止默认上传行为
  }

  const onUpdatePassword = async (values: any) => {
    setLoading(true)
    try {
      await usersService.updatePassword(values)
      message.success('密码修改成功，请重新登录')
      passwordForm.resetFields()
      setTimeout(() => {
        navigate('/login')
      }, 1500)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '修改失败')
    } finally {
      setLoading(false)
    }
  }

  const clearHistories = async () => {
    Modal.confirm({
      title: '确认清空历史记录？',
      content: '此操作不可恢复',
      onOk: async () => {
        try {
          await historyService.clear()
          setHistories([])
          message.success('已清空')
        } catch (error: any) {
          message.error('操作失败')
        }
      },
    })
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="个人中心" />
        
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            {
              key: 'info',
              label: '基本信息',
              children: (
                <Card>
                  <Space direction="vertical" size="large" style={{ width: '100%' }}>
                    {/* 头像展示 */}
                    <div style={{ textAlign: 'center' }}>
                      <Avatar 
                        size={100} 
                        icon={<UserOutlined />} 
                        src={avatarUrl || user?.imgPath}
                      />
                    </div>
                    
                    <Descriptions column={2} bordered>
                      <Descriptions.Item label="账号">{user?.userAccount}</Descriptions.Item>
                      <Descriptions.Item label="姓名">{user?.userName}</Descriptions.Item>
                      <Descriptions.Item label="邮箱">{user?.userEmail}</Descriptions.Item>
                      <Descriptions.Item label="电话">{user?.userTel || '-'}</Descriptions.Item>
                      <Descriptions.Item label="年龄">{user?.userAge || '-'}</Descriptions.Item>
                      <Descriptions.Item label="性别">{user?.userSex || '-'}</Descriptions.Item>
                      <Descriptions.Item label="注册时间" span={2}>
                        {user?.createTime ? dayjs(user.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
                      </Descriptions.Item>
                    </Descriptions>
                  </Space>
                </Card>
              ),
            },
            {
              key: 'edit',
              label: '修改资料',
              children: (
                <Card>
                  <Form form={profileForm} layout="vertical" onFinish={onUpdateProfile}>
                    {/* 头像上传 */}
                    <Form.Item label="头像">
                      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                        <Avatar 
                          size={80} 
                          icon={<UserOutlined />} 
                          src={avatarUrl}
                        />
                        <div>
                          <Upload
                            showUploadList={false}
                            beforeUpload={handleAvatarUpload}
                            accept="image/*"
                          >
                            <Button icon={<PlusOutlined />} loading={uploading}>
                              {uploading ? '上传中...' : '更换头像'}
                            </Button>
                          </Upload>
                          {uploading && (
                            <Progress 
                              percent={uploadProgress} 
                              status="active"
                              strokeColor={{
                                '0%': '#108ee9',
                                '100%': '#87d068',
                              }}
                              style={{ marginTop: 8, width: 200 }}
                            />
                          )}
                        </div>
                      </div>
                    </Form.Item>
                    
                    <Form.Item name="userName" label="姓名">
                      <Input placeholder="请输入姓名" />
                    </Form.Item>
                    <Form.Item name="userAge" label="年龄">
                      <Input type="number" placeholder="请输入年龄" />
                    </Form.Item>
                    <Form.Item name="userSex" label="性别">
                      <Input placeholder="请输入性别" />
                    </Form.Item>
                    <Form.Item name="userTel" label="电话">
                      <Input placeholder="请输入电话" />
                    </Form.Item>
                    <Form.Item>
                      <Button type="primary" htmlType="submit" loading={loading}>
                        保存修改
                      </Button>
                    </Form.Item>
                  </Form>
                </Card>
              ),
            },
            {
              key: 'password',
              label: '修改密码',
              children: (
                <Card>
                  <Form form={passwordForm} layout="vertical" onFinish={onUpdatePassword}>
                    <Form.Item
                      name="oldPassword"
                      label="旧密码"
                      rules={[{ required: true, message: '请输入旧密码' }]}
                    >
                      <Input.Password placeholder="请输入旧密码" />
                    </Form.Item>
                    <Form.Item
                      name="newPassword"
                      label="新密码"
                      rules={[{ required: true, message: '请输入新密码' }]}
                    >
                      <Input.Password placeholder="请输入新密码" />
                    </Form.Item>
                    <Form.Item>
                      <Button type="primary" htmlType="submit" loading={loading}>
                        修改密码
                      </Button>
                    </Form.Item>
                  </Form>
                </Card>
              ),
            },
            {
              key: 'history',
              label: '浏览历史',
              children: (
                <Card
                  extra={
                    <Button danger onClick={clearHistories} disabled={histories.length === 0}>
                      清空历史
                    </Button>
                  }
                >
                  <List
                    dataSource={histories}
                    locale={{ emptyText: '暂无浏览记录' }}
                    renderItem={(h) => (
                      <List.Item>
                        <List.Item.Meta
                          title={`${h.operateTypeDesc}：${h.operateName}`}
                          description={dayjs(h.createTime).format('YYYY-MM-DD HH:mm:ss')}
                        />
                      </List.Item>
                    )}
                  />
                </Card>
              ),
            },
          ]}
        />
      </Space>
    </div>
  )
}
