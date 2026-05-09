import http from './http'

export interface ChatRequest {
    message: string
    conversationId?: string
    userId?: number
}

export const aiChatService = {
    query(data: ChatRequest) {
        return http.post('/api/v1/ai-chat/query', data)
    },
    clearSession(conversationId: string) {
        return http.delete(`/api/v1/ai-chat/session/${conversationId}`)
    },
    async stream(data: ChatRequest, onChunk: (text: string) => void) {
        try {
            const baseUrl = import.meta.env.VITE_API_BASE || 'http://localhost:8080'
            const url = `${baseUrl}/api/v1/ai-chat/stream`

            console.log('发送 AI 请求:', url, data)

            const resp = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...(localStorage.getItem('token') ? {Authorization: `Bearer ${localStorage.getItem('token')}`} : {}),
                },
                body: JSON.stringify(data),
            })

            if (!resp.ok) {
                const errorText = await resp.text()
                console.error('AI 请求失败:', resp.status, errorText)
                throw new Error(`请求失败: ${resp.status} ${resp.statusText}`)
            }

            const reader = resp.body?.getReader()
            if (!reader) {
                throw new Error('无法获取响应流')
            }

            const decoder = new TextDecoder('utf-8')
            let buffer = ''

            while (true) {
                const {done, value} = await reader.read()
                if (done) {
                    console.log('流式响应结束')
                    break
                }
                buffer += decoder.decode(value, {stream: true})

                // console.log('buffer:', JSON.stringify(buffer));//打印

                const messages = buffer.split(/\r?\n\r?\n/)
                buffer = messages.pop() || ''

                for (const msg of messages) {
                    const dataLines = msg
                        .split(/\r?\n/)
                        .filter((line) => line.startsWith('data:'))
                        .map((line) => line.slice(5).replace(/^ /, ''))

                    if (dataLines.length > 0) {
                        const payload = dataLines.join('\n')
                        if (payload === '[DONE]') return
                        if (payload) {
                            onChunk(payload)
                        }
                    }
                }

            }
        } catch (error) {
            console.error('SSE 流式请求错误:', error)
            throw error
        }
    },
}
