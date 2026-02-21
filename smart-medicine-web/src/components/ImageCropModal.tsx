import { Button, Modal, Space, message } from 'antd'
import { useState, useRef } from 'react'
import React, { FC } from 'react'
import ReactCrop, { Crop, PixelCrop } from 'react-image-crop'
import 'react-image-crop/dist/ReactCrop.css'

interface ImageCropModalProps {
  open: boolean
  imageUrl: string
  onCancel: () => void
  onConfirm: (croppedBlob: Blob) => void
  aspect?: number // 裁剪比例，默认 16:9
}

const ImageCropModal: FC<ImageCropModalProps> = ({
  open,
  imageUrl,
  onCancel,
  onConfirm,
  aspect = 16 / 9
}) => {
  // 根据比例初始化裁剪框
  const [crop, setCrop] = useState<Crop>(() => {
    if (aspect > 1) {
      // 横向比例（16:9），宽度100%，高度按比例计算
      return { unit: '%', width: 100, height: 100 / aspect, x: 0, y: (100 - 100 / aspect) / 2 }
    } else {
      // 纵向比例，高度100%，宽度按比例计算
      return { unit: '%', width: 100 * aspect, height: 100, x: (100 - 100 * aspect) / 2, y: 0 }
    }
  })
  const [completedCrop, setCompletedCrop] = useState<PixelCrop>()
  const imgRef = useRef<HTMLImageElement>(null)
  const [loading, setLoading] = useState(false)

  // 获取裁剪后的图片
  const getCroppedImg = (
    image: HTMLImageElement,
    crop: PixelCrop
  ): Promise<Blob> => {
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')

    if (!ctx) {
      return Promise.reject(new Error('无法获取 canvas context'))
    }

    // 设置 canvas 尺寸为裁剪区域的实际尺寸
    canvas.width = crop.width
    canvas.height = crop.height

    // 计算缩放比例（原图尺寸 / 显示尺寸）
    const scaleX = image.naturalWidth / image.width
    const scaleY = image.naturalHeight / image.height

    // 绘制裁剪区域
    ctx.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      0,
      0,
      crop.width,
      crop.height
    )

    return new Promise((resolve) => {
      canvas.toBlob((blob) => {
        if (blob) {
          resolve(blob)
        } else {
          // 降级处理：使用 PNG 格式
          canvas.toBlob((blob) => {
            resolve(blob as Blob)
          }, 'image/png')
        }
      }, 'image/jpeg', 0.95)
    })
  }

  const handleConfirm = async () => {
    if (!completedCrop || !imgRef.current) {
      message.error('请先选择裁剪区域')
      return
    }

    setLoading(true)
    try {
      const croppedBlob = await getCroppedImg(imgRef.current, completedCrop)
      onConfirm(croppedBlob)
    } catch (error) {
      message.error('裁剪图片失败')
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = () => {
    setCrop({ unit: '%', width: 100, height: 100, x: 0, y: 0 })
    setCompletedCrop(undefined)
    onCancel()
  }

  return (
    <Modal
      title="裁剪封面图片"
      open={open}
      onCancel={handleCancel}
      width={800}
      footer={null}
      zIndex={1050}
      style={{ zIndex: 1050 }}
    >
      <div style={{ marginBottom: 16 }}>
        <p style={{ color: '#666', marginBottom: 16 }}>
          拖动图片选择要显示的区域，建议选择 16:9 比例的内容
        </p>
        <div
          style={{
            maxHeight: 500,
            overflow: 'auto',
            background: '#f5f5f5',
            borderRadius: 8,
            padding: 16
          }}
        >
          <ReactCrop
            crop={crop}
            onChange={(c) => setCrop(c)}
            onComplete={(c) => setCompletedCrop(c)}
            aspect={aspect}
            keepSelection
          >
            <img
              ref={imgRef}
              src={imageUrl}
              alt="待裁剪"
              style={{ maxWidth: '100%', maxHeight: 450, display: 'block' }}
            />
          </ReactCrop>
        </div>
      </div>
      <div style={{ textAlign: 'right' }}>
        <Space>
          <Button onClick={handleCancel}>取消</Button>
          <Button type="primary" onClick={handleConfirm} loading={loading}>
            确认裁剪
          </Button>
        </Space>
      </div>
    </Modal>
  )
}

export default ImageCropModal
