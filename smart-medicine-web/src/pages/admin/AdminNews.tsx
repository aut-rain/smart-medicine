import { Button, Card, Form, Input, message, Modal, Select, Space, Table, Upload, Tag, Spin } from 'antd'
import { useEffect, useState } from 'react'
import { UploadOutlined, EditOutlined, DeleteOutlined, PlusOutlined, EyeOutlined } from '@ant-design/icons'
import MDEditor from '@uiw/react-md-editor'
import { adminNewsService } from '@/services/adminService'
import { fileService } from '@/services/fileService'
import PageHeader from '@/components/PageHeader'
import ImageCropModal from '@/components/ImageCropModal'

const { Option } = Select
const { TextArea } = Input

export default function AdminNews() {
  const [news, setNews] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [pagination, setPagination] = useState({ page: 1, size: 10, total: 0 })
  const [searchForm] = Form.useForm()
  const [editModal, setEditModal] = useState({ visible: false, data: null as any })
  const [editForm] = Form.useForm()
  const [coverUploading, setCoverUploading] = useState(false)
  const [coverUrl, setCoverUrl] = useState('')
  const [markdownContent, setMarkdownContent] = useState('')
  const [markdownLoading, setMarkdownLoading] = useState(false)

  // 图片裁剪相关
  const [cropModalOpen, setCropModalOpen] = useState(false)
  const [tempImageFile, setTempImageFile] = useState<File | null>(null)
  const [tempImageUrl, setTempImageUrl] = useState('')
  // 用于比较markdown内容是否变化
  const [originalMarkdownContent, setOriginalMarkdownContent] = useState('')

  useEffect(() => {
    loadNews()
  }, [])

  const loadNews = async (page = 1, filters?: any) => {
    setLoading(true)
    try {
      const res = await adminNewsService.list({
        page,
        size: pagination.size,
        ...filters
      })
      const data = res.data?.data
      setNews(data?.records || [])
      setPagination({
        page: data?.current || 1,
        size: data?.size || 10,
        total: data?.total || 0
      })
    } catch (error) {
      console.error('加载资讯列表失败', error)
      message.error('加载资讯列表失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (values: any) => {
    loadNews(1, values)
  }

  const handleReset = () => {
    searchForm.resetFields()
    loadNews(1)
  }

  const handleCreate = () => {
    setEditModal({ visible: true, data: null })
    editForm.resetFields()
    setCoverUrl('')
    setMarkdownContent('')
    setOriginalMarkdownContent('')
  }

  const handleEdit = (record: any) => {
    setEditModal({ visible: true, data: record })
    editForm.setFieldsValue({
      newsName: record.newsName,
      newsSummary: record.newsSummary,
      category: record.category,
      author: record.author,
      status: record.status,
      coverOssPath: record.coverOssPath  // 添加封面路径字段
    })
    setCoverUrl(record.coverOssPath || '')
    setMarkdownContent('')
    setOriginalMarkdownContent('') // 重置原始内容
    // 通过后端 API 加载 Markdown 内容
    if (record.id) {
      loadMarkdownContent(record.id)
    }
  }

  const handleDelete = (id: number) => {
    Modal.confirm({
      title: '确认删除资讯？',
      content: '此操作不可恢复，删除后无法恢复',
      onOk: async () => {
        try {
          await adminNewsService.delete(id)
          message.success('删除成功')
          loadNews(pagination.page)
        } catch (error: any) {
          message.error(error?.response?.data?.message || '删除失败')
        }
      }
    })
  }

  const handleCoverUpload = async (file: any) => {
    const selectedFile = file.file
    if (!selectedFile) return

    // 创建预览 URL
    const previewUrl = URL.createObjectURL(selectedFile)
    setTempImageFile(selectedFile)
    setTempImageUrl(previewUrl)
    setCropModalOpen(true)
  }

  // 裁剪确认后上传
  const handleCropConfirm = async (croppedBlob: Blob) => {
    setCropModalOpen(false)
    setCoverUploading(true)

    try {
      // 创建新的 File 对象
      const croppedFile = new File([croppedBlob], 'cover.jpg', { type: 'image/jpeg' })

      // 上传裁剪后的图片
      const res = await fileService.uploadImage(croppedFile)
      const uploadedUrl = res.data?.data?.url || ''

      setCoverUrl(uploadedUrl)
      // 同步更新表单字段值（修复验证问题）
      editForm.setFieldValue('coverOssPath', uploadedUrl)
      message.success('封面上传成功')
    } catch (error: any) {
      message.error('封面上传失败')
    } finally {
      setCoverUploading(false)
      // 清理临时 URL
      if (tempImageUrl) {
        URL.revokeObjectURL(tempImageUrl)
        setTempImageUrl('')
      }
      setTempImageFile(null)
    }
  }

  // 取消裁剪
  const handleCropCancel = () => {
    setCropModalOpen(false)
    if (tempImageUrl) {
      URL.revokeObjectURL(tempImageUrl)
      setTempImageUrl('')
    }
    setTempImageFile(null)
  }

  // 加载 Markdown 内容（通过后端 API）
  const loadMarkdownContent = async (newsId: number) => {
    if (!newsId) {
      message.warning('未找到资讯ID')
      return
    }
    setMarkdownLoading(true)
    try {
      const res = await adminNewsService.getMarkdown(newsId)
      console.log('Markdown API 响应:', res)
      console.log('res.data:', res.data)
      console.log('res.data.data:', res.data?.data)

      const content = res.data?.data || ''
      console.log('解析到的内容长度:', content.length)

      setMarkdownContent(content)
      setOriginalMarkdownContent(content) // 保存原始内容用于比较
    } catch (error: any) {
      message.error(`加载内容失败: ${error?.response?.data?.message || error?.message}`)
      console.error('加载 Markdown 内容异常', error)
      console.error('错误详情:', error?.response)
    } finally {
      setMarkdownLoading(false)
    }
  }

  // 上传 Markdown 内容到 OSS
  const uploadMarkdownContent = async (content: string, filename: string): Promise<string> => {
    const blob = new Blob([content], { type: 'text/markdown' })
    const file = new File([blob], filename, { type: 'text/markdown' })

    try {
      const res = await fileService.uploadFile(file)
      return res.data?.data?.url || ''
    } catch (error: any) {
      throw new Error('Markdown 内容上传失败')
    }
  }

  const handleSubmit = async (values: any) => {
    // 验证 Markdown 内容
    if (!markdownContent || markdownContent.trim() === '') {
      message.error('请输入资讯内容')
      return
    }

    try {
      // 生成 Markdown 文件名
      const timestamp = Date.now()
      const filename = `news-${timestamp}.md`

      // 上传 Markdown 内容到 OSS（仅当内容变化时）
      let markdownOssPath = values.markdownOssPath
      if (editModal.data && editModal.data.markdownOssPath) {
        // 更新模式：只有内容变化时才重新上传
        if (markdownContent !== originalMarkdownContent) {
          markdownOssPath = await uploadMarkdownContent(markdownContent, filename)
        } else {
          markdownOssPath = editModal.data.markdownOssPath
        }
      } else {
        // 创建模式：总是上传
        markdownOssPath = await uploadMarkdownContent(markdownContent, filename)
      }

      const submitData = {
        ...values,
        coverOssPath: coverUrl || values.coverOssPath,
        markdownOssPath
      }

      if (editModal.data) {
        await adminNewsService.update(editModal.data.id, submitData)
        message.success('更新成功')
      } else {
        await adminNewsService.create(submitData)
        message.success('创建成功')
      }
      setEditModal({ visible: false, data: null })
      setCoverUrl('')
      setMarkdownContent('')
      setOriginalMarkdownContent('')
      loadNews(pagination.page)
    } catch (error: any) {
      message.error(error?.response?.data?.message || error?.message || '操作失败')
    }
  }

  const columns = [
    {
      title: '封面',
      dataIndex: 'coverOssPath',
      key: 'cover',
      width: 120,
      render: (url: string) => (
        <img
          src={url}
          alt="封面"
          style={{ width: 80, height: 60, objectFit: 'cover', borderRadius: 4 }}
          onError={(e) => {
            ;(e.target as HTMLImageElement).src = 'https://via.placeholder.com/80x60'
          }}
        />
      )
    },
    {
      title: '标题',
      dataIndex: 'newsName',
      key: 'newsName',
      ellipsis: true
    },
    {
      title: '摘要',
      dataIndex: 'newsSummary',
      key: 'newsSummary',
      ellipsis: true,
      width: 200
    },
    {
      title: '分类',
      dataIndex: 'category',
      key: 'category',
      width: 100
    },
    {
      title: '作者',
      dataIndex: 'author',
      key: 'author',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: number) => (
        status === 1 ? (
          <Tag color="green">已发布</Tag>
        ) : (
          <Tag color="orange">草稿</Tag>
        )
      )
    },
    {
      title: '浏览量',
      dataIndex: 'viewCount',
      key: 'viewCount',
      width: 80
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right' as const,
      render: (_: any, record: any) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => window.open(`/news/${record.id}`, '_blank')}
          >
            预览
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            danger
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id)}
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
        <PageHeader title="资讯管理" />

        {/* 搜索栏 */}
        <Card>
          <Form
            form={searchForm}
            layout="inline"
            onFinish={handleSearch}
          >
            <Form.Item name="keyword" label="关键词">
              <Input placeholder="搜索标题或摘要" allowClear style={{ width: 200 }} />
            </Form.Item>
            <Form.Item name="status" label="状态">
              <Select placeholder="全部" allowClear style={{ width: 120 }}>
                <Option value={1}>已发布</Option>
                <Option value={0}>草稿</Option>
              </Select>
            </Form.Item>
            <Form.Item name="category" label="分类">
              <Input placeholder="分类" allowClear style={{ width: 120 }} />
            </Form.Item>
            <Form.Item>
              <Space>
                <Button type="primary" htmlType="submit">
                  搜索
                </Button>
                <Button onClick={handleReset}>
                  重置
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>

        {/* 资讯列表 */}
        <Card
          title="资讯列表"
          extra={
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleCreate}
            >
              新建资讯
            </Button>
          }
        >
          <Table
            columns={columns}
            dataSource={news}
            loading={loading}
            rowKey="id"
            pagination={{
              current: pagination.page,
              pageSize: pagination.size,
              total: pagination.total,
              showSizeChanger: true,
              showTotal: (total) => `共 ${total} 条`,
              onChange: (page, pageSize) => {
                setPagination({ ...pagination, page, size: pageSize })
                loadNews(page, searchForm.getFieldsValue())
              }
            }}
          />
        </Card>

      {/* 编辑弹窗 */}
      <Modal
        title={editModal.data ? '编辑资讯' : '新建资讯'}
        open={editModal.visible}
        onCancel={() => setEditModal({ visible: false, data: null })}
        footer={null}
        width={600}
        destroyOnClose
      >
        <Form
          form={editForm}
          layout="vertical"
          initialValues={{ status: 0 }}
          onFinish={handleSubmit}
        >
          <Form.Item
            name="newsName"
            label="标题"
            rules={[{ required: true, message: '请输入标题' }]}
          >
            <Input placeholder="请输入资讯标题" maxLength={200} showCount />
          </Form.Item>

          <Form.Item
            name="newsSummary"
            label="摘要"
            rules={[{ required: true, message: '请输入摘要' }]}
          >
            <TextArea
              placeholder="请输入资讯摘要（用于列表展示）"
              rows={2}
              maxLength={500}
              showCount
            />
          </Form.Item>

          <Form.Item
            name="coverOssPath"
            label="封面图片"
            rules={[{ required: true, message: '请上传封面图片' }]}
          >
            <Upload
              listType="picture-card"
              showUploadList={false}
              beforeUpload={() => false}
              onChange={(info) => handleCoverUpload(info)}
              maxCount={1}
              accept="image/*"
            >
              {coverUrl ? (
                <img src={coverUrl} alt="封面" style={{ width: '100%', maxHeight: 200, objectFit: 'contain' }} />
              ) : (
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传封面</div>
                </div>
              )}
            </Upload>
            {coverUrl && (
              <Button
                danger
                size="small"
                onClick={() => setCoverUrl('')}
                style={{ marginTop: 8 }}
              >
                删除封面
              </Button>
            )}
          </Form.Item>

          <Form.Item name="category" label="分类">
            <Input placeholder="请输入分类（如：健康科普、疾病预防等）" maxLength={50} />
          </Form.Item>

          <Form.Item name="author" label="作者">
            <Input placeholder="请输入作者" maxLength={100} />
          </Form.Item>

          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Option value={0}>草稿</Option>
              <Option value={1}>已发布</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="内容"
            required
          >
            {markdownLoading ? (
              <div style={{ textAlign: 'center', padding: '40px 0' }}>
                <Spin tip="加载内容中..." />
              </div>
            ) : (
              <MDEditor
                value={markdownContent}
                onChange={(val) => setMarkdownContent(val || '')}
                height={400}
                preview="live"
                textareaProps={{
                  placeholder: '请输入资讯内容（支持 Markdown 格式）...'
                }}
              />
            )}
            {!markdownContent && (
              <div style={{ color: '#ff4d4f', fontSize: 12, marginTop: 4 }}>
                请输入资讯内容
              </div>
            )}
          </Form.Item>

          <Form.Item style={{ marginTop: 24, textAlign: 'right' }}>
            <Space>
              <Button onClick={() => setEditModal({ visible: false, data: null })}>
                取消
              </Button>
              <Button type="primary" htmlType="submit">
                {editModal.data ? '更新' : '创建'}
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
      </Space>

      {/* 图片裁剪弹窗 */}
      <ImageCropModal
        open={cropModalOpen}
        imageUrl={tempImageUrl}
        onCancel={handleCropCancel}
        onConfirm={handleCropConfirm}
        aspect={4 / 3} // 4:3 比例适合轮播图
      />
    </div>
  )
}
