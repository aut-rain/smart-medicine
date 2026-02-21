import { ArrowLeftOutlined, CalendarOutlined, EyeOutlined, UserOutlined } from '@ant-design/icons'
import { Button, Card, Descriptions, Divider, Spin, Tag, Typography, Space } from 'antd'
import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import rehypeSanitize from 'rehype-sanitize'
import { newsService } from '@/services/newsService'
import { historyService } from '@/services/historyService'
import { getUserId } from '@/utils/auth'

const { Title, Paragraph } = Typography

export default function NewsDetail() {
  const { id } = useParams<{ id: string }>()
  const [news, setNews] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  // Markdown 渲染配置 - 添加自定义样式
  const markdownComponents = {
    h1: ({ children }: any) => (
      <h1 style={{ fontSize: 28, fontWeight: 700, margin: '32px 0 16px', paddingBottom: 12, borderBottom: '2px solid #d0d7de' }}>
        {children}
      </h1>
    ),
    h2: ({ children }: any) => (
      <h2 style={{ fontSize: 24, fontWeight: 600, margin: '28px 0 16px', paddingBottom: 10, borderBottom: '1px solid #d0d7de' }}>
        {children}
      </h2>
    ),
    h3: ({ children }: any) => (
      <h3 style={{ fontSize: 20, fontWeight: 600, margin: '24px 0 16px', paddingBottom: 8, borderBottom: '1px solid #d0d7de' }}>
        {children}
      </h3>
    ),
    h4: ({ children }: any) => (
      <h4 style={{ fontSize: 18, fontWeight: 600, margin: '20px 0 12px', paddingBottom: 6, borderBottom: '1px solid #d0d7de' }}>
        {children}
      </h4>
    ),
    h5: ({ children }: any) => (
      <h5 style={{ fontSize: 16, fontWeight: 600, margin: '18px 0 10px' }}>
        {children}
      </h5>
    ),
    h6: ({ children }: any) => (
      <h6 style={{ fontSize: 14, fontWeight: 600, margin: '16px 0 8px' }}>
        {children}
      </h6>
    ),
    p: ({ children }: any) => (
      <p style={{ margin: '16px 0', lineHeight: 1.6, fontSize: 16 }}>
        {children}
      </p>
    ),
    strong: ({ children }: any) => (
      <strong style={{ fontWeight: 600 }}>
        {children}
      </strong>
    ),
    img: ({ node, ...props }: any) => (
      <div style={{ textAlign: 'center', margin: '20px 0' }}>
        <img {...props} style={{ maxWidth: '100%', maxHeight: 600, borderRadius: 8 }} alt={props.alt} />
      </div>
    ),
    a: ({ node, ...props }: any) => (
      <a {...props} target="_blank" rel="noopener noreferrer" style={{ color: '#0969da', textDecoration: 'none' }} />
    ),
    blockquote: ({ children }: any) => (
      <blockquote style={{ margin: '16px 0', padding: '0 16px', borderLeft: '4px solid #d0d7de', color: '#656d76' }}>
        {children}
      </blockquote>
    ),
    code: ({ node, inline, className, children, ...props }: any) => {
      if (inline) {
        return (
          <code style={{ background: 'rgba(175,184,193,0.2)', padding: '2px 6px', borderRadius: 6, fontSize: '85%', fontFamily: 'ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, monospace' }} {...props}>
            {children}
          </code>
        )
      }
      return (
        <code style={{ display: 'block', background: '#f6f8fa', padding: 16, borderRadius: 6, overflowX: 'auto', fontSize: 14, fontFamily: 'ui-monospace, SFMono-Regular, SF Mono, Menlo, Consolas, monospace' }} {...props}>
          {children}
        </code>
      )
    },
    pre: ({ children }: any) => (
      <pre style={{ background: '#f6f8fa', padding: 16, borderRadius: 6, overflowX: 'auto', margin: '16px 0' }}>
        {children}
      </pre>
    ),
    ul: ({ children }: any) => (
      <ul style={{ margin: '16px 0', paddingLeft: 20 }}>
        {children}
      </ul>
    ),
    ol: ({ children }: any) => (
      <ol style={{ margin: '16px 0', paddingLeft: 20 }}>
        {children}
      </ol>
    ),
    li: ({ children }: any) => (
      <li style={{ margin: '4px 0' }}>
        {children}
      </li>
    ),
    hr: () => (
      <hr style={{ margin: '24px 0', border: 'none', borderTop: '1px solid #d0d7de' }} />
    )
  }

  useEffect(() => {
    if (id) {
      loadNews(parseInt(id))
    }
  }, [id])

  const loadNews = async (newsId: number) => {
    setLoading(true)
    try {
      const res = await newsService.getDetail(newsId)
      const newsData = res.data?.data
      setNews(newsData)

      // 异步记录浏览历史 (operateType: 6 = 查看资讯)
      const userId = getUserId()
      if (userId && newsData) {
        historyService.record(userId, 6, newsData.id, newsData.newsName).catch((err) => {
          console.warn('记录浏览历史失败:', err)
        })
      }
    } catch (error) {
      console.error('加载资讯详情失败', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!news) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <p style={{ color: '#999' }}>资讯不存在</p>
        <Link to="/">
          <Button type="primary" icon={<ArrowLeftOutlined />}>
            返回首页
          </Button>
        </Link>
      </div>
    )
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  return (
    <div>
      {/* 自定义头部 */}
      <div style={{ background: 'white', padding: '16px 24px', borderBottom: '1px solid #f0f0f0' }}>
        <Space>
          <Link to="/">
            <Button icon={<ArrowLeftOutlined />}>
              返回首页
            </Button>
          </Link>
        </Space>
      </div>

      <div style={{ maxWidth: 900, margin: '0 auto', padding: '0 24px', marginTop: 16 }}>
        {/* 封面图 */}
        {news.coverOssPath && (
          <img
            src={news.coverOssPath}
            alt={news.newsName}
            style={{
              width: '100%',
              height: 400,
              objectFit: 'cover',
              borderRadius: 12,
              marginBottom: 24
            }}
          />
        )}

        {/* 标题和元信息 */}
        <Card style={{ marginBottom: 24 }}>
          <Title level={2} style={{ marginBottom: 16 }}>
            {news.newsName}
          </Title>

          <Descriptions column={{ xs: 1, sm: 2 }} size="small">
            <Descriptions.Item label={<Space><UserOutlined /> 作者</Space>}>
              {news.author || '佚名'}
            </Descriptions.Item>
            <Descriptions.Item label={<Space><CalendarOutlined /> 发布时间</Space>}>
              {formatDate(news.createTime)}
            </Descriptions.Item>
            <Descriptions.Item label={<Space><EyeOutlined /> 浏览量</Space>}>
              {news.viewCount || 0} 次
            </Descriptions.Item>
            {news.category && (
              <Descriptions.Item label="分类">
                <Tag color="blue">{news.category}</Tag>
              </Descriptions.Item>
            )}
          </Descriptions>

          {news.newsSummary && (
            <>
              <Divider />
              <Paragraph style={{ fontSize: 16, color: '#666' }}>
                {news.newsSummary}
              </Paragraph>
            </>
          )}
        </Card>

        {/* 正文内容 */}
        <Card title="正文" style={{ marginBottom: 24 }}>
          <div className="markdown-content" style={{ color: '#24292f' }}>
            <ReactMarkdown
              remarkPlugins={[remarkGfm]}
              rehypePlugins={[rehypeSanitize]}
              components={markdownComponents}
            >
              {news.markdownContent || '*暂无内容*'}
            </ReactMarkdown>
          </div>
        </Card>

        {/* 相关资讯 */}
        {news.relatedNews && news.relatedNews.length > 0 && (
          <Card title="相关资讯">
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {news.relatedNews.map((related: any) => (
                <Link
                  key={related.id}
                  to={`/news/${related.id}`}
                  style={{ display: 'block' }}
                >
                  <Card
                    hoverable
                    size="small"
                    style={{ overflow: 'hidden' }}
                  >
                    <div style={{ display: 'flex', gap: 16 }}>
                      {related.coverOssPath && (
                        <img
                          src={related.coverOssPath}
                          alt={related.newsName}
                          style={{
                            width: 120,
                            height: 80,
                            objectFit: 'cover',
                            borderRadius: 8
                          }}
                        />
                      )}
                      <div style={{ flex: 1 }}>
                        <div style={{ fontWeight: 500, marginBottom: 8 }}>
                          {related.newsName}
                        </div>
                        {related.newsSummary && (
                          <div style={{ fontSize: 12, color: '#999' }}>
                            {related.newsSummary}
                          </div>
                        )}
                      </div>
                    </div>
                  </Card>
                </Link>
              ))}
            </div>
          </Card>
        )}
      </div>
    </div>
  )
}
