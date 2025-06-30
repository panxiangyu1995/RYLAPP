import http from './http'

/**
 * 任务流程API模块
 */

// 基础路径
const BASE_URL = '/api/v1/tasks'

/**
 * 获取任务流程
 * @param {string} taskId 任务ID
 * @returns {Promise<Object>} 任务流程
 */
export function getTaskFlow(taskId) {
  return http.get(`${BASE_URL}/${taskId}/flow`)
}

/**
 * 更新任务流程状态
 * @param {Object} data 包含taskId、currentStepIndex、nextStepIndex和action的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function updateTaskFlowStatus(data) {
  return http.post(`${BASE_URL}/${data.taskId}/flow/status`, data)
}

/**
 * 添加任务流程记录
 * @param {Object} record 记录数据，包含taskId、stepIndex、description、images、files等
 * @returns {Promise<Object>} 响应结果
 */
export function addTaskFlowRecord(record) {
  return http.post(`${BASE_URL}/${record.taskId}/flow/records`, record)
}

/**
 * 删除任务流程记录
 * @param {string} recordId 记录ID
 * @returns {Promise<Object>} 响应结果
 */
export function deleteTaskFlowRecord(recordId) {
  return http.delete(`${BASE_URL}/flow/records/${recordId}`)
}

/**
 * 下载任务流程附件
 * @param {string} fileId 文件ID
 * @returns {string} 下载URL
 */
export function downloadTaskFlowAttachment(fileId) {
  return `${BASE_URL}/flow/attachments/${fileId}/download`
}

/**
 * 决定任务是否需要上门
 * @param {string} taskId - 任务ID
 * @param {Object} data - 包含stepIndex和requiresVisit的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function decideSiteVisit(taskId, data) {
  console.log(`发送上门决策请求: taskId=${taskId}, data=`, data);
  return http.post(`${BASE_URL}/${taskId}/decide-visit`, data)
} 