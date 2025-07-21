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
 * @param {Object} record 记录数据，包含文本内容
 * @param {Array<File>} files 附件文件列表
 * @returns {Promise<Object>} 响应结果
 */
export function addTaskFlowRecord(record, files) {
  const formData = new FormData();

  // 1. 添加JSON部分
  // 后端需要能解析这个JSON字符串
  formData.append('record', new Blob([JSON.stringify(record)], { type: 'application/json' }));

  // 2. 添加文件部分
  if (files && files.length > 0) {
    files.forEach(file => {
      formData.append('files', file);
    });
  }

  return http.post(`${BASE_URL}/${record.taskId}/flow/records`, formData, {
    headers: {
      // Content-Type 会由浏览器自动设置，并包含boundary
      'Content-Type': 'multipart/form-data'
    }
  });
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
 * @param {Object} data - 包含stepIndex、requiresVisit和visitAppointmentTime(可选)的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function decideSiteVisit(taskId, data) {
  console.log(`发送上门决策请求: taskId=${taskId}, data=`, data);
  return http.post(`${BASE_URL}/${taskId}/decide-visit`, data)
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