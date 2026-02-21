import { Card, Skeleton, Space } from 'antd'

export default function PageSkeleton() {
  return (
    <div className="page">
      <Space direction="vertical" style={{ width: '100%' }} size="large">
        <Skeleton.Input active size="large" style={{ width: 200 }} />
        <Card>
          <Skeleton active paragraph={{ rows: 4 }} />
        </Card>
        <Card>
          <Skeleton active paragraph={{ rows: 6 }} />
        </Card>
      </Space>
    </div>
  )
}
