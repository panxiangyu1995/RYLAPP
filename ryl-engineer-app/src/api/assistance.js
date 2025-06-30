import http from './http'

/**
 * 获取协助请求列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @param {string} params.role 查询角色(requester-请求者, engineer-工程师)
 * @param {string} params.status 状态(pending-待处理, accepted-已接受, rejected-已拒绝, cancelled-已取消, completed-已完成)
 * @param {string} params.urgency 紧急程度(low-低, medium-中, high-高)
 * @param {string} params.keyword 搜索关键词(任务ID/描述)
 * @param {string} params.startTime 开始时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {string} params.endTime 结束时间，格式：YYYY-MM-DDTHH:mm:ss
 * @returns {Promise} 返回协助请求列表
 */
export function getAssistanceList(params) {
  return http.get('/api/v1/assistance/list', { params })
}

/**
 * 获取协助请求详情
 * @param {string} requestId 请求ID
 * @returns {Promise} 返回请求详情
 */
export function getAssistanceDetail(requestId) {
  return http.get(`/api/v1/assistance/detail/${requestId}`)
}

/**
 * 创建协助请求
 * @param {Object} data 请求数据
 * @param {number} data.engineerId 工程师ID
 * @param {string} data.taskId 相关任务ID
 * @param {string} data.urgency 紧急程度(low-低, medium-中, high-高)，默认medium
 * @param {string} data.description 问题描述
 * @returns {Promise} 返回创建结果
 */
export function createAssistanceRequest(data) {
  return http.post('/api/v1/assistance/create', data)
}

/**
 * 处理协助请求
 * @param {Object} data 处理数据
 * @param {string} data.requestId 请求ID
 * @param {string} data.action 处理动作(accept-接受, reject-拒绝)
 * @param {string} data.response 回复内容
 * @returns {Promise} 返回处理结果
 */
export function handleAssistanceRequest(data) {
  return http.post('/api/v1/assistance/handle', data)
}

/**
 * 完成协助请求
 * @param {Object} data 完成数据
 * @param {string} data.requestId 请求ID
 * @param {string} data.response 完成说明
 * @returns {Promise} 返回完成结果
 */
export function completeAssistanceRequest(data) {
  return http.post('/api/v1/assistance/complete', data)
}

/**
 * 取消协助请求
 * @param {Object} data 取消数据
 * @param {string} data.requestId 请求ID
 * @param {string} data.reason 取消原因
 * @returns {Promise} 返回取消结果
 */
export function cancelAssistanceRequest(data) {
  return http.post('/api/v1/assistance/cancel', data)
} 