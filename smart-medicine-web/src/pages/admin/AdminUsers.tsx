import { Button, Card, Form, Input, Modal, Space, Table, Typography, message } from 'antd'
import { useEffect, useState } from 'react'
import { adminUserService } from '@/services/adminService'
import PageHeader from '@/components/PageHeader'

export default function AdminUsers() {
  const [users, setUsers] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [editModal, setEditModal] = useState({ visible: false, data: null as any })

  useEffect(() => {
    loadUsers()
  }, [])

  const loadUsers = async (page = 1, keyword = '') => {
    setLoading(true)
    try {
      const res = await adminUserService.list({ page, size: pagination.size, keyword })
      const data = res.data?.data
      setUsers(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载用户列表失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (values: any) => {
    loadUsers(1, values.keyword)
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除用户？',
      content: '此操作不可恢复，将同时删除该用户的所有相关数据。',
      onOk: async () => {
        try {
          await adminUserService.delete(id)
          message.success('删除成功')
          loadUsers(pagination.page)
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
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
      key: 'userAccount',
    },
    {
      title: '姓名',
      dataIndex: 'userName',
      key: 'userName',
    },
    {
      title: '邮箱',
      dataIndex: 'userEmail',
      key: 'userEmail',
    },
    {
      title: '电话',
      dataIndex: 'userTel',
      key: 'userTel',
    },
    {
      title: '角色',
      dataIndex: 'roleStatus',
      key: 'roleStatus',
      render: (role: number) => (role === 1 ? '管理员' : '普通用户')
    },
    {
      title: '注册时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (date: string) => new Date(date).toLocaleString()
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: any) => (
        <Space>
          <Button 
            type="link" 
            onClick={() => setEditModal({ visible: true, data: record })}
          >
            编辑
          </Button>
          <Button 
            type="link" 
            danger 
            onClick={() => handleDelete(record.id)}
            disabled={record.roleStatus === 1} // 管理员不能删除自己
          >
            删除
          </Button>
        </Space>
      )
    }
  ]

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="用户管理" />
        
        {/* 搜索表单 */}
        <Card>
          <Form form={searchForm} layout="inline" onFinish={handleSearch}>
            <Form.Item name="keyword" label="搜索">
              <Input placeholder="输入账号、姓名或邮箱" />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">搜索</Button>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 用户表格 */}
        <Card>
          <Table
            dataSource={users}
            columns={columns}
            loading={loading}
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              onChange: (page) => loadUsers(page)
            }}
            rowKey="id"
          />
        </Card>
        
        {/* 编辑模态框 */}
        <Modal
          title="编辑用户"
          open={editModal.visible}
          onCancel={() => setEditModal({ visible: false, data: null })}
          footer={null}
        >
          {editModal.data && (
            <Form
              initialValues={editModal.data}
              layout="vertical"
              onFinish={(values) => {
                console.log('更新用户', values)
                setEditModal({ visible: false, data: null })
              }}
            >
              <Form.Item name="userName" label="姓名">
                <Input />
              </Form.Item>
              <Form.Item name="userEmail" label="邮箱">
                <Input />
              </Form.Item>
              <Form.Item name="userTel" label="电话">
                <Input />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">保存</Button>
              </Form.Item>
            </Form>
          )}
        </Modal>
      </Space>
    </div>
  )
}