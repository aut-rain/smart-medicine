import { Button, Card, Form, Image, Input, InputNumber, message, Modal, Progress, Select, Space, Table, Typography, Upload } from 'antd'
import { useEffect, useState } from 'react'
import { PlusOutlined } from '@ant-design/icons'
import { adminMedicineService } from '@/services/adminService'
import { fileService } from '@/services/fileService'
import PageHeader from '@/components/PageHeader'

const { Option } = Select

export default function AdminMedicines() {
  const [medicines, setMedicines] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [editModal, setEditModal] = useState({ visible: false, data: null as any })
  const [editForm] = Form.useForm()
  const [uploadProgress, setUploadProgress] = useState(0)
  const [uploading, setUploading] = useState(false)
  const [imageUrl, setImageUrl] = useState('')

  useEffect(() => {
    loadMedicines()
  }, [])

  const loadMedicines = async (page = 1, keyword = '') => {
    setLoading(true)
    try {
      const res = await adminMedicineService.list({ page, size: pagination.size, keyword })
      const data = res.data?.data
      setMedicines(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载药品列表失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (values: any) => {
    loadMedicines(1, values.keyword)
  }

  const handleCreate = () => {
    setEditModal({ visible: true, data: null })
    editForm.resetFields()
    setImageUrl('')
    setUploadProgress(0)
  }

  const handleEdit = (record: any) => {
    setEditModal({ visible: true, data: record })
    editForm.setFieldsValue(record)
    setImageUrl(record.imgPath || '')
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除药品？',
      content: '此操作不可恢复',
      onOk: async () => {
        try {
          await adminMedicineService.delete(id)
          message.success('删除成功')
          loadMedicines(pagination.page)
        } catch (error: any) {
          message.error(error?.response?.data?.message || '删除失败')
        }
      }
    })
  }

  const handleSubmit = async (values: any) => {
    try {
      // 将上传的图片URL添加到表单数据中
      const submitData = {
        ...values,
        imgPath: imageUrl || undefined
      }
      
      if (editModal.data) {
        await adminMedicineService.update(editModal.data.id, submitData)
        message.success('更新成功')
      } else {
        await adminMedicineService.create(submitData)
        message.success('创建成功')
      }
      setEditModal({ visible: false, data: null })
      setImageUrl('')
      loadMedicines(pagination.page)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '操作失败')
    }
  }

  // 处理图片上传
  const handleImageUpload = async (file: File) => {
    setUploading(true)
    setUploadProgress(0)
    
    try {
      const res = await fileService.uploadImage(file, (percent) => {
        setUploadProgress(percent)
      })
      
      const url = res.data?.data?.url
      if (url) {
        setImageUrl(url)
        message.success('图片上传成功！')
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '图片上传失败')
    } finally {
      setUploading(false)
    }
    
    return false // 阻止默认上传行为
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '图片',
      dataIndex: 'imgPath',
      key: 'imgPath',
      width: 100,
      render: (imgPath: string) => (
        imgPath ? (
          <Image
            src={imgPath}
            alt="药品图片"
            width={60}
            height={60}
            style={{ objectFit: 'cover', borderRadius: 4 }}
            preview={{ mask: '预览' }}
          />
        ) : (
          <div style={{ width: 60, height: 60, background: '#f0f0f0', borderRadius: 4, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            无图
          </div>
        )
      ),
    },
    {
      title: '药品名称',
      dataIndex: 'medicineName',
      key: 'medicineName',
    },
    {
      title: '品牌',
      dataIndex: 'medicineBrand',
      key: 'medicineBrand',
    },
    {
      title: '类型',
      dataIndex: 'medicineTypeDesc',
      key: 'medicineTypeDesc',
    },
    {
      title: '价格',
      dataIndex: 'medicinePrice',
      key: 'medicinePrice',
      render: (price: number) => `￥${price}`,
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
        <PageHeader title="药品管理" />
        
        {/* 搜索栏 */}
        <Card>
          <Form form={searchForm} layout="inline" onFinish={handleSearch}>
            <Form.Item name="keyword" label="搜索">
              <Input placeholder="输入药品名称或品牌" />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">搜索</Button>
                <Button type="primary" onClick={handleCreate}>新建药品</Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 药品表格 */}
        <Card>
          <Table
            dataSource={medicines}
            columns={columns}
            loading={loading}
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              onChange: (page) => loadMedicines(page)
            }}
            rowKey="id"
          />
        </Card>
        
        {/* 编辑/新建模态框 */}
        <Modal
          title={editModal.data ? '编辑药品' : '新建药品'}
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
              name="medicineName"
              label="药品名称"
              rules={[{ required: true, message: '请输入药品名称' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item name="medicineBrand" label="品牌">
              <Input />
            </Form.Item>
            <Form.Item
              name="medicineType"
              label="药品类型"
              rules={[{ required: true, message: '请选择药品类型' }]}
            >
              <Select>
                <Option value={0}>西药</Option>
                <Option value={1}>中药</Option>
                <Option value={2}>中成药</Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="medicinePrice"
              label="价格"
              rules={[{ required: true, message: '请输入价格' }]}
            >
              <InputNumber min={0} precision={2} style={{ width: '100%' }} addonBefore="￥" />
            </Form.Item>
            <Form.Item name="keyword" label="关键词">
              <Input placeholder="用逗号分隔多个关键词" />
            </Form.Item>
            <Form.Item name="medicineEffect" label="功效">
              <Input.TextArea rows={3} placeholder="请输入药品功效" />
            </Form.Item>
            <Form.Item name="interaction" label="药物相互作用">
              <Input.TextArea rows={3} placeholder="请输入药物相互作用" />
            </Form.Item>
            <Form.Item name="taboo" label="禁忌">
              <Input.TextArea rows={3} placeholder="请输入禁忌" />
            </Form.Item>
            <Form.Item name="usAge" label="用法用量">
              <Input.TextArea rows={3} placeholder="请输入用法用量" />
            </Form.Item>
            <Form.Item name="imgPath" label="药品图片">
              <Upload
                listType="picture-card"
                showUploadList={false}
                beforeUpload={handleImageUpload}
                accept="image/*"
              >
                {imageUrl ? (
                  <img src={imageUrl} alt="药品图片" style={{ width: '100%' }} />
                ) : (
                  <div>
                    <PlusOutlined />
                    <div style={{ marginTop: 8 }}>上传图片</div>
                  </div>
                )}
              </Upload>
              {uploading && (
                <Progress 
                  percent={uploadProgress} 
                  status="active"
                  strokeColor={{
                    '0%': '#108ee9',
                    '100%': '#87d068',
                  }}
                />
              )}
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
