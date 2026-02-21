import { Button, Card, Form, Input, message, Modal, Progress, Space, Table, Upload } from 'antd'
import { useEffect, useState } from 'react'
import { UploadOutlined, VideoCameraOutlined, PictureOutlined } from '@ant-design/icons'
import { adminVideoService } from '@/services/adminService'
import { fileService } from '@/services/fileService'
import PageHeader from '@/components/PageHeader'

export default function AdminVideos() {
  const [videos, setVideos] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [editModal, setEditModal] = useState({ visible: false, data: null as any })
  const [editForm] = Form.useForm()
  const [uploadProgress, setUploadProgress] = useState(0)
  const [uploading, setUploading] = useState(false)
  const [videoUrl, setVideoUrl] = useState('')
  const [imgUrl, setImgUrl] = useState('')
  const [imgUploading, setImgUploading] = useState(false)

  useEffect(() => {
    loadVideos()
  }, [])

  const loadVideos = async (page = 1, keyword = '') => {
    setLoading(true)
    try {
      const res = await adminVideoService.list({ page, size: pagination.size, keyword })
      const data = res.data?.data
      setVideos(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载视频列表失败', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (values: any) => {
    loadVideos(1, values.keyword)
  }

  const handleCreate = () => {
    setEditModal({ visible: true, data: null })
    editForm.resetFields()
    setVideoUrl('')
    setImgUrl('')
    setUploadProgress(0)
  }

  const handleEdit = (record: any) => {
    setEditModal({ visible: true, data: record })
    editForm.setFieldsValue(record)
    setVideoUrl(record.link || '')
    setImgUrl(record.imgPath || '')
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除视频？',
      content: '此操作不可恢复',
      onOk: async () => {
        try {
          await adminVideoService.delete(id)
          message.success('删除成功')
          loadVideos(pagination.page)
        } catch (error: any) {
          message.error(error?.response?.data?.message || '删除失败')
        }
      }
    })
  }

  const handleSubmit = async (values: any) => {
    try {
      const submitData = {
        ...values,
        link: videoUrl || values.link,
        imgPath: imgUrl || values.imgPath
      }
      
      if (editModal.data) {
        await adminVideoService.update(editModal.data.id, submitData)
        message.success('更新成功')
      } else {
        await adminVideoService.create(submitData)
        message.success('创建成功')
      }
      setEditModal({ visible: false, data: null })
      setVideoUrl('')
      setImgUrl('')
      loadVideos(pagination.page)
    } catch (error: any) {
      message.error(error?.response?.data?.message || '操作失败')
    }
  }

  // 处理视频上传
  const handleVideoUpload = async (file: File) => {
    setUploading(true)
    setUploadProgress(0)
    
    try {
      const res = await fileService.uploadVideo(file, (percent) => {
        setUploadProgress(percent)
      })
      
      const url = res.data?.data?.url
      if (url) {
        setVideoUrl(url)
        editForm.setFieldsValue({ link: url })
        message.success('视频上传成功！')
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '视频上传失败')
    } finally {
      setUploading(false)
    }
    
    return false
  }

  // 处理封面图片上传
  const handleImgUpload = async (file: File) => {
    setImgUploading(true)
    
    try {
      const res = await fileService.uploadImage(file)
      const url = res.data?.data?.url
      
      if (url) {
        setImgUrl(url)
        editForm.setFieldsValue({ imgPath: url })
        message.success('封面上传成功！')
      }
    } catch (error: any) {
      message.error(error?.response?.data?.message || '封面上传失败')
    } finally {
      setImgUploading(false)
    }
    
    return false
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '视频标题',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: string) => new Date(time).toLocaleDateString(),
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
        <PageHeader title="视频管理" />
        
        {/* 搜索栏 */}
        <Card>
          <Form form={searchForm} layout="inline" onFinish={handleSearch}>
            <Form.Item name="keyword" label="搜索">
              <Input placeholder="输入视频标题或描述" />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">搜索</Button>
                <Button type="primary" onClick={handleCreate}>新建视频</Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
        
        {/* 视频表格 */}
        <Card>
          <Table
            dataSource={videos}
            columns={columns}
            loading={loading}
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              onChange: (page) => loadVideos(page)
            }}
            rowKey="id"
          />
        </Card>
        
        {/* 编辑/新建模态框 */}
        <Modal
          title={editModal.data ? '编辑视频' : '新建视频'}
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
              name="title"
              label="视频标题"
              rules={[{ required: true, message: '请输入视频标题' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item name="description" label="视频描述">
              <Input.TextArea rows={4} placeholder="请输入视频描述" />
            </Form.Item>
            <Form.Item label="封面图片">
              <Upload
                beforeUpload={handleImgUpload}
                showUploadList={false}
                accept="image/*"
              >
                <Button icon={<UploadOutlined />} loading={imgUploading}>
                  {imgUploading ? '上传中...' : '选择封面图片'}
                </Button>
              </Upload>
              {imgUrl && (
                <div style={{ marginTop: 8 }}>
                  <PictureOutlined style={{ color: '#52c41a' }} /> 封面已上传
                  <img src={imgUrl} alt="封面" style={{ marginTop: 8, maxWidth: '100%', maxHeight: 200, display: 'block' }} />
                </div>
              )}
            </Form.Item>
            <Form.Item name="imgPath" hidden>
              <Input />
            </Form.Item>
            <Form.Item label="视频文件">
              <Upload
                beforeUpload={handleVideoUpload}
                showUploadList={false}
                accept="video/*"
              >
                <Button icon={<UploadOutlined />} loading={uploading}>
                  {uploading ? '上传中...' : '选择视频文件'}
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
                  style={{ marginTop: 8 }}
                />
              )}
              {videoUrl && (
                <div style={{ marginTop: 8, color: '#52c41a' }}>
                  <VideoCameraOutlined /> 视频已上传
                </div>
              )}
            </Form.Item>
            <Form.Item
              name="link"
              label="视频链接"
              rules={[
                { required: true, message: '请上传视频或输入视频链接' },
                { type: 'url', message: '请输入有效的URL' }
              ]}
            >
              <Input placeholder="或直接输入视频URL" disabled={uploading} />
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
