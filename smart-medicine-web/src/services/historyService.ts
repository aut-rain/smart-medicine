import http from './http'

export const historyService = {
  list(page?: number, size?: number) {
    return http.get('/api/v1/histories', { params: { page, size } })
  },
  clear() {
    return http.delete('/api/v1/histories')
  },
  /**
   * 记录浏览历史
   * @param userId 用户ID
   * @param operateType 操作类型: 2-查看疾病, 4-查看药品, 5-观看视频
   * @param operateId 操作对象ID
   * @param operateName 操作对象名称
   */
  record(userId: number, operateType: number, operateId: number, operateName: string) {
    const params = new URLSearchParams()
    params.append('userId', userId.toString())
    params.append('operateType', operateType.toString())
    params.append('operateId', operateId.toString())
    params.append('operateName', operateName)

    return http.post('/api/v1/histories/record', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
  },
}
