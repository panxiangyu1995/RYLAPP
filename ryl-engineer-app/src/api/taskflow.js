import http from './http'

/**
 * 任务流程API模块
 */

// 基础路径
const BASE_URL = '/api/v1/task-flow'

/**
 * 将任务推进到下一步
 * @param {string} taskId 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function nextStep(taskId) {
  return http.post(`${BASE_URL}/${taskId}/next`)
}

/**
 * 将任务回退到上一步
 * @param {string} taskId 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function prevStep(taskId) {
  return http.post(`${BASE_URL}/${taskId}/prev`)
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
 * 设置任务报价
 * @param {string} taskId - 任务ID
 * @param {Object} data - 包含price和stepIndex的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function setTaskPrice(taskId, data) {
  console.log(`发送设置报价请求: taskId=${taskId}, data=`, data);
  return http.post(`${BASE_URL}/${taskId}/set-price`, data)
}

/**
 * 确认任务报价
 * @param {string} taskId - 任务ID
 * @param {boolean} confirmed - 是否确认
 * @returns {Promise<Object>} 响应结果
 */
export function confirmTaskPrice(taskId, confirmed) {
  console.log(`发送确认报价请求: taskId=${taskId}, confirmed=${confirmed}`);
  return http.post(`${BASE_URL}/${taskId}/confirm-price`, { confirmed })
}

/**
 * 获取任务报价确认状态
 * @param {string} taskId - 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function getTaskPriceConfirmation(taskId) {
  return http.get(`${BASE_URL}/${taskId}/price-confirmation`)
} 