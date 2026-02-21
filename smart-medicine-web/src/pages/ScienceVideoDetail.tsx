import { Card, Space, Typography, Divider } from 'antd'
import { useEffect, useState } from 'react'
import { scienceVideoService } from '@/services/scienceVideoService'
import { historyService } from '@/services/historyService'
import { useParams } from 'react-router-dom'
import { getUserId } from '@/utils/auth'
import PageHeader from '@/components/PageHeader'

export default function ScienceVideoDetail() {
  const { id } = useParams()
  const [video, setVideo] = useState<any>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!id) return
    loadVideoDetail(Number(id))
  }, [id])

  const loadVideoDetail = async (videoId: number) => {
    setLoading(true)
    try {
      const res = await scienceVideoService.getDetail(videoId)
      const videoData = res.data?.data
      setVideo(videoData)

      // 异步记录浏览历史
      const userId = getUserId()
      if (userId && videoData) {
        historyService.record(userId, 5, videoData.id, videoData.title).catch((err) => {
          console.warn('记录浏览历史失败:', err)
        })
      }
    } catch (error) {
      console.error('加载视频详情失败', error)
    } finally {
      setLoading(false)
    }
  }

  if (!video && !loading) {
    return (
      <div className="page">
        <Card>
          <Typography.Text>视频不存在或已被删除</Typography.Text>
        </Card>
      </div>
    )
  }

  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <PageHeader title="视频详情" />

        <Card loading={loading}>
          {video && (
            <Space direction="vertical" style={{ width: '100%' }} size="middle">
              <div>
                <Typography.Title level={4} style={{ margin: 0 }}>
                  {video.title}
                </Typography.Title>
                <Space style={{ marginTop: 8 }}>
                  <Typography.Text type="secondary">
                    发布时间：{new Date(video.createTime).toLocaleString()}
                  </Typography.Text>
                </Space>
              </div>

              <Divider />

              {/* 视频播放区域 */}
              <div style={{
                width: '100%',
                backgroundColor: '#000',
                borderRadius: 8,
                overflow: 'hidden'
              }}>
                {video.link ? (
                  <video
                    src={video.link}
                    controls
                    style={{ width: '100%', height: 'auto' }}
                  />
                ) : (
                  <div style={{
                    height: 400,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: '#fff'
                  }}>
                    视频播放地址无效
                  </div>
                )}
              </div>

              <Divider />

              {/* 视频介绍 */}
              <Card size="small" title="视频介绍">
                <Typography.Paragraph>
                  {video.description || '暂无视频介绍'}
                </Typography.Paragraph>
              </Card>
            </Space>
          )}
        </Card>
      </Space>
    </div>
  )
}
