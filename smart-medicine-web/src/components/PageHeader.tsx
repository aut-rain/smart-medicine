import { ArrowLeftOutlined } from '@ant-design/icons'
import { Button, Space, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'

interface PageHeaderProps {
  title: string
  onBack?: () => void
}

export default function PageHeader({ title, onBack }: PageHeaderProps) {
  const navigate = useNavigate()

  const handleBack = () => {
    if (onBack) {
      onBack()
    } else {
      navigate(-1)
    }
  }

  return (
    <Space style={{ marginBottom: 16 }}>
      <Button 
        type="text" 
        icon={<ArrowLeftOutlined />} 
        onClick={handleBack}
        style={{ fontSize: 16 }}
      >
        返回
      </Button>
      <Typography.Title level={3} style={{ margin: 0 }}>
        {title}
      </Typography.Title>
    </Space>
  )
}
