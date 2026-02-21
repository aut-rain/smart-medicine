import { Button, Card, Form, Input, message, Modal, Select, Space, Table } from 'antd'
import { useEffect, useState } from 'react'
import { adminIllnessService } from '@/services/adminService'
import PageHeader from '@/components/PageHeader'

export default function AdminIllnesses() {
  const [illnesses, setIllnesses] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [editModal, setEditModal] = useState({ visible: false, data: null as any })
  const [editForm] = Form.useForm()

  useEffect(() => {
    loadIllnesses()
  }, [])

  const loadIllnesses = async (page = 1, keyword = '') => {
    setLoading(true)
    try {
      const res = await adminIllnessService.list({ page, size: pagination.size, keyword })
      const data = res.data?.data
      setIllnesses(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载疾病列表失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (values: any) => {
    loadIllnesses(1, values.keyword)
  }

  const handleCreate = () => {
    setEditModal({ visible: true, data: null })
    editForm.resetFields()
  }

  const handleEdit = (record: any) => {
    setEditModal({ visible: true, data: record })
    editForm.setFieldsValue(record)
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除疾病？',
      content: '此操作不可恢复，将同时删除相关的药品关联关系',
      onOk: async () => {
        try {
          await adminIllnessService.delete(id)
          message.success('删除成功')
          loadIllnesses(pagination.page)
        } catch (error: any) {
          message.error(error?.response?.data?.message || '删除失败')
        }
      }
    })
  }

  const handleSubmit = async (values: any) => {
    try {
      if (editModal.data) {
        await adminIllnessService.update(editModal.data.id, values)
        message.success('更新成功')
      } else {
        await adminIllnessService.create(values)
        message.success('创建成功')
      }
      setEditModal({ visible: false, data: null })
      loadIllnesses(pagination.page)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '操作失败')
    }
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '疾病名称',
      dataIndex: 'illnessName',
      key: 'illnessName',
    },
    {
      title: '分类',
      dataIndex: 'kindName',
      key: 'kindName',
    },
    {
      title: '浏览量',
      dataIndex: 'pageviews',
      key: 'pageviews',
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: any, record: any) => (
        <Space>
          <Button type="link" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" danger onClick={() => handleDelete(record.id)}>
            删除
          </Button>
        </Space>
      )
    }
  ]

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="疾病管理" />
        
        {/* 搜索栏 */}
        <Card>
          <Form form={searchForm} layout="inline" onFinish={handleSearch}>
            <Form.Item name="keyword" label="搜索">
              <Input placeholder="输入疾病名称" />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">搜索</Button>
                <Button type="primary" onClick={handleCreate}>新建疾病</Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 疾病表格 */}
        <Card>
          <Table
            dataSource={illnesses}
            columns={columns}
            loading={loading}
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              onChange: (page) => loadIllnesses(page)
            }}
            rowKey="id"
          />
        </Card>
        
        {/* 编辑/新建模态框 */}
        <Modal
          title={editModal.data ? '编辑疾病' : '新建疾病'}
          open={editModal.visible}
          onCancel={() => setEditModal({ visible: false, data: null })}
          footer={null}
          width={600}
        >
          <Form
            form={editForm}
            layout="vertical"
            onFinish={handleSubmit}
          >
            <Form.Item
              name="illnessName"
              label="疾病名称"
              rules={[{ required: true, message: '请输入疾病名称' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="kindId"
              label="疾病分类"
              rules={[{ required: true, message: '请选择疾病分类' }]}
            >
              <Select placeholder="请选择疾病分类">
                <Select.Option value={1}>呼吸系统疾病</Select.Option>
                <Select.Option value={2}>消化系统疾病</Select.Option>
                <Select.Option value={3}>心血管系统疾病</Select.Option>
                <Select.Option value={4}>神经系统疾病</Select.Option>
                <Select.Option value={5}>内分泌系统疾病</Select.Option>
                <Select.Option value={6}>泌尿系统疾病</Select.Option>
                <Select.Option value={7}>运动系统疾病</Select.Option>
                <Select.Option value={8}>皮肤疾病</Select.Option>
                <Select.Option value={9}>其他</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="illnessSymptom" label="主要症状">
              <Input.TextArea rows={4} placeholder="请输入主要症状" />
            </Form.Item>
            <Form.Item name="specialSymptom" label="特殊症状">
              <Input.TextArea rows={3} placeholder="请输入特殊症状" />
            </Form.Item>
            <Form.Item name="includeReason" label="病因">
              <Input.TextArea rows={3} placeholder="请输入病因" />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">保存</Button>
                <Button onClick={() => setEditModal({ visible: false, data: null })}>
                  取消
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Modal>
      </Space>
    </div>
  )
}
