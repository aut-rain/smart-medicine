import { Avatar, Button, message } from 'antd'
import { useEffect, useRef, useState } from 'react'
import { Streamdown } from 'streamdown'
import { aiChatService } from '@/services/aiChatService'
import { usersService } from '@/services/usersService'
import PageHeader from '@/components/PageHeader'
import { UserOutlined, RobotOutlined, SendOutlined, PlusOutlined, DeleteOutlined, MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons'
import './AiChat.css'

interface ChatMsg {
    role: 'user' | 'assistant';
    content: string
}

const linkEvidenceMarkers = (text: string) => {
    const markerLinks = new Map<string, string>()
    const markdownLinkPattern = /\[(【资料\d+】)\]\((\/(?:illness|medicine|science-video|news)\/\d+)\)/g
    let match: RegExpExecArray | null

    while ((match = markdownLinkPattern.exec(text)) !== null) {
        markerLinks.set(match[1], match[2])
    }

    if (markerLinks.size === 0) {
        return text
    }

    return text.replace(/(?<!\[)(【资料\d+】)(?!\]\()/g, (marker) => {
        const url = markerLinks.get(marker)
        return url ? `[${marker}](${url})` : marker
    })
}

const normalizeAssistantText = (text: string) => {
    let inCodeFence = false

    const normalizedText = text
        .replace(/\r\n/g, '\n')
        .replace(/\n{3,}/g, '\n\n')
        .split('\n')
        .map((line) => {
            if (line.trim().startsWith('```')) {
                inCodeFence = !inCodeFence
                return line
            }
            if (inCodeFence) {
                return line
            }

            return line
                .replace(/^(\s{0,3}#{1,6})([^\s#])/u, '$1 $2')
                .replace(/^(\s*\d+\.)([^\s])/u, '$1 $2')
                .replace(/^(\s*[-*+])(\S)/u, '$1 $2')
                .replace(/^(\s*>)([^\s])/u, '$1 $2')
        })
        .join('\n')

    return linkEvidenceMarkers(normalizedText)
}

export default function AiChat() {
    const [conversationId, setConversationId] = useState<string>('')
    const [messages, setMessages] = useState<ChatMsg[]>([])
    const [loading, setLoading] = useState(false)
    const [inputValue, setInputValue] = useState('')
    const [userInfo, setUserInfo] = useState<any>(null)
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false)
    const messagesEndRef = useRef<HTMLDivElement>(null)
    const textareaRef = useRef<HTMLTextAreaElement>(null)

    // 自动滚动到底部
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }

    useEffect(() => {
        scrollToBottom()
    }, [messages])

    useEffect(() => {
        if (!conversationId) {
            const id = 'conv_' + Date.now()
            setConversationId(id)
        }
    }, [conversationId])

    useEffect(() => {
        // 只在组件首次加载时执行一次
        // 设置初始的欢迎消息
        setMessages([{
            role: 'assistant',
            content: '您好！我是您的AI健康助手，请问有什么可以帮助您的吗？' // 这里可以自定义欢迎语
        }])
        // 加载用户信息
        loadUserInfo()
    }, [])

    const loadUserInfo = async () => {
        try {
            const res = await usersService.getCurrent()
            setUserInfo(res.data?.data)
        } catch (error) {
            console.error('加载用户信息失败', error)
        }
    }

    const onSend = async () => {
        const msg = inputValue.trim()
        if (!msg || loading) return

        // 添加用户消息
        setMessages((m) => [...m, { role: 'user', content: msg }])
        setLoading(true)
        setInputValue('')

        // 重置textarea高度
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto'
        }

        try {
            let aiContent = ''
            // 添加空的 AI 消息占位
            setMessages((m) => [...m, { role: 'assistant', content: '' }])

            await aiChatService.stream({ message: msg, conversationId }, (chunk) => {
                aiContent += chunk

                setMessages((m) => {
                    const copy = [...m]
                    const last = copy[copy.length - 1]
                    if (last && last.role === 'assistant') {
                        last.content = aiContent
                    }
                    return copy
                })
            })
        } catch (error: any) {
            console.error('AI 对话错误:', error)
            message.error(error?.message || 'AI 对话失败，请重试')
            // 移除失败的 AI 消息
            setMessages((m) => m.slice(0, -1))
        } finally {
            setLoading(false)
        }
    }

    const clearSession = async () => {
        if (!conversationId) return
        await aiChatService.clearSession(conversationId)
        setMessages([])
        message.success('会话已清空')
    }

    const handleKeyPress = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault()
            onSend()
        }
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        setInputValue(e.target.value)

        // 自动调整高度
        const textarea = e.target
        textarea.style.height = 'auto'
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
    }

    return (
        <div className="ai-chat-container">
            {/* 左侧边栏 */}
            <div className={`sidebar ${sidebarCollapsed ? 'collapsed' : ''}`}>
                <div className="sidebar-header">
                    {!sidebarCollapsed && <h3 className="sidebar-title">聊天记录</h3>}
                    <button
                        className="sidebar-toggle"
                        onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
                        title={sidebarCollapsed ? '展开侧边栏' : '收起侧边栏'}
                    >
                        {sidebarCollapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                    </button>
                </div>
                {!sidebarCollapsed && (
                    <div className="sidebar-content">
                        <div className="sidebar-placeholder">
                            <p>历史记录功能</p>
                            <p>即将上线...</p>
                        </div>
                    </div>
                )}
            </div>

            {/* 主聊天区域 */}
            <div className="chat-main">
                {/* 顶部标题栏 */}
                <div className="chat-header">
                    <h2 className="chat-title">AI 智能问诊</h2>
                    <div className="action-buttons">
                        <button
                            className="action-button"
                            onClick={() => {
                                setConversationId('conv_' + Date.now())
                                setMessages([])
                            }}
                        >
                            <PlusOutlined />
                            新建对话
                        </button>
                        <button
                            className="action-button danger"
                            onClick={clearSession}
                        >
                            <DeleteOutlined />
                            清空会话
                        </button>
                    </div>
                </div>

                {/* 消息区域 */}
                <div className="messages-container">
                    {messages.length === 0 ? (
                        <div className="empty-state">
                            <div className="empty-state-icon">💬</div>
                            <div className="empty-state-text">开始您的AI问诊</div>
                            <div className="empty-state-hint">请输入您的健康问题，AI医生将为您提供专业建议</div>
                        </div>
                    ) : (
                        <>
                            {messages.map((msg, index) => (
                                <div
                                    key={index}
                                    className={`message-item ${msg.role}`}
                                    style={{ animationDelay: `${index * 0.05}s` }}
                                >
                                    <div className="message-wrapper">
                                        <div className="message-avatar-wrapper">
                                            {msg.role === 'user' ? (
                                                <Avatar
                                                    size={32}
                                                    src={userInfo?.imgPath}
                                                    icon={<UserOutlined />}
                                                    className="message-avatar user"
                                                />
                                            ) : (
                                                <div className="message-avatar assistant">
                                                    <RobotOutlined />
                                                </div>
                                            )}
                                        </div>
                                        <div className="message-bubble">
                                            {msg.content ? (
                                                <Streamdown isAnimating={loading && msg.role === 'assistant'}>
                                                    {normalizeAssistantText(msg.content)}
                                                </Streamdown>
                                            ) : (
                                                <div className="typing-indicator">
                                                    <div className="typing-dot"></div>
                                                    <div className="typing-dot"></div>
                                                    <div className="typing-dot"></div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </>
                    )}
                </div>

                {/* 输入区域 */}
                <div className="input-container">
                    <div className="input-wrapper">
                        <div className="input-field">
                            <textarea
                                ref={textareaRef}
                                className="custom-textarea"
                                placeholder="例如：感冒了应该吃什么药？（按 Enter 发送，Shift+Enter 换行）"
                                value={inputValue}
                                onChange={handleInputChange}
                                onKeyPress={handleKeyPress}
                                rows={1}
                                disabled={loading}
                            />
                        </div>
                        <button
                            className="send-button"
                            onClick={onSend}
                            disabled={loading || !inputValue.trim()}
                        >
                            <SendOutlined />
                            {loading ? '发送中...' : '发送'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}
