import http from './http'

/**
 * 任务API模块
 */

// 基础路径
export const BASE_URL = '/api/v1/tasks'

/**
 * 获取任务的完整流程，包含所有步骤和记录
 * @param {string} taskId 任务ID
 * @returns {Promise<Object>} 响应结果，包含任务流程数据
 */
export function getTaskFlow(taskId) {
  return http.get(`${BASE_URL}/${taskId}/flow`)
}

/**
 * 创建任务
 * @param {Object} taskData 任务数据
 * @returns {Promise<Object>} 响应结果
 */
export function createTask(taskData) {
  console.log('发送任务创建请求，数据:', JSON.stringify(taskData));
  
  // 验证日期格式
  if (taskData.startTime) {
    console.log('开始时间:', taskData.startTime);
  } else {
    console.error('任务开始时间为空');
  }
  
  if (taskData.endTime) {
    console.log('结束时间:', taskData.endTime);
  } else {
    console.error('任务结束时间为空');
  }

  return http.post(BASE_URL, taskData)
    .catch(error => {
      console.error('任务创建请求失败:', error);
      
      if (error.response && error.response.data) {
        console.error('服务器返回的错误信息:', error.response.data);
      }
      
      // 重新抛出错误，让调用者处理
      throw error;
    });
}

/**
 * 获取任务列表（分页）
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 分页数据
 */
export function getTaskList(params) {
  return http.get(BASE_URL, { params })
}

/**
 * 获取任务详情
 * @param {string} taskId 任务ID
 * @returns {Promise<Object>} 任务详情
 */
export function getTaskDetail(taskId) {
  return http.get(`${BASE_URL}/${taskId}`)
}

/**
 * 初始化任务步骤（针对来自小程序端的任务）
 * @param {string} taskId 任务ID
 * @param {Array<Object>} steps 步骤定义数组
 * @returns {Promise<Object>} 响应结果
 */
export function initializeTaskSteps(taskId, steps) {
  return http.post(`${BASE_URL}/${taskId}/initialize-steps`, steps);
}

/**
 * 更新任务步骤
 * @param {Object} stepData 步骤数据
 * @returns {Promise<Object>} 响应结果
 */
export function updateTaskStep(stepData) {
  return http.post(`${BASE_URL}/steps`, stepData)
}

/**
 * 上传任务附件
 * @param {string} taskId 任务ID
 * @param {FormData} formData 包含文件的表单数据
 * @returns {Promise<Object>} 上传结果
 */
export function uploadTaskAttachments(taskId, formData) {
  return http.post(`${BASE_URL}/${taskId}/attachments`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新任务
 * @param {string} taskId 任务ID
 * @param {Object} taskData 任务数据
 * @returns {Promise<Object>} 更新结果
 */
export function updateTask(taskId, taskData) {
  return http.put(`${BASE_URL}/${taskId}`, taskData)
}

/**
 * 取消任务
 * @param {string} taskId 任务ID
 * @param {string} reason 取消原因
 * @returns {Promise<Object>} 响应结果
 */
export function cancelTask(taskId, reason) {
  return http.post(`${BASE_URL}/${taskId}/cancel`, null, {
    params: { reason }
  })
}

/**
 * 分配工程师
 * @param {string} taskId 任务ID
 * @param {Array<number>} engineerIds 工程师ID列表
 * @returns {Promise<Object>} 响应结果
 */
export function assignEngineers(taskId, engineerIds) {
  return http.post(`${BASE_URL}/${taskId}/engineers`, engineerIds)
}

/**
 * 邀请协作工程师
 * @param {Object} data 包含taskId和engineerIds的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function inviteCollaborator(data) {
  return http.post(`${BASE_URL}/${data.taskId}/collaborators`, {
    engineerIds: data.engineerIds
  })
}

/**
 * 移除协作工程师
 * @param {string} taskId 任务ID
 * @param {string} engineerId 工程师ID
 * @returns {Promise<Object>} 响应结果
 */
export function removeCollaborator(taskId, engineerId) {
  return http.delete(`${BASE_URL}/${taskId}/engineers/${engineerId}`)
}

/**
 * 转出任务
 * @param {Object} data 包含taskId、engineerId和note的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function transferTask(data) {
  return http.post(`${BASE_URL}/${data.taskId}/transfer`, {
    engineerId: data.engineerId,
    note: data.note
  })
}

/**
 * 获取任务统计信息
 * @returns {Promise<Object>} 统计信息
 */
export function getTaskStatistics() {
  return http.get(`${BASE_URL}/statistics`)
}

/**
 * 获取工程师列表
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 工程师列表
 */
export function getEngineersList(params) {
  return http.get('/api/v1/user/engineers', { params })
}

/**
 * 工程师接受任务
 * @param {number} taskId 任务ID (物理ID)
 * @returns {Promise<Object>} 响应结果
 */
export function acceptTask(taskId) {
  return http.post(`${BASE_URL}/${taskId}/accept`);
}

/**
 * 工程师拒绝任务
 * @param {number} taskId 任务ID (物理ID)
 * @param {Object} data 拒绝任务的数据，包含 reason 和 transferTarget
 * @returns {Promise<Object>} 响应结果
 */
export function rejectTask(taskId, data) {
  return http.post(`${BASE_URL}/${taskId}/reject`, data);
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
 * 重置任务的上门决策
 * @param {string} taskId - 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function resetSiteVisitDecision(taskId) {
  console.log(`发送重置上门决策请求: taskId=${taskId}`);
  return http.post(`${BASE_URL}/${taskId}/reset-decision`);
} 

/**
 * 设置任务报价
 * @param {string} taskId - 任务ID
 * @param {Object} data - 包含 price 和 stepIndex 的数据对象
 * @returns {Promise<Object>} 响应结果
 */
export function setTaskPrice(taskId, data) {
  return http.post(`${BASE_URL}/${taskId}/set-price`, data);
}

/**
 * 通知客户报价
 * @param {string} taskId - 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function notifyCustomer(taskId) {
  return http.post(`${BASE_URL}/${taskId}/notify-customer`);
}

/**
 * 确认任务已付款
 * @param {string} taskId - 任务ID
 * @returns {Promise<Object>} 响应结果
 */
export function confirmPayment(taskId) {
  return http.post(`${BASE_URL}/${taskId}/confirm-payment`);
}

/**
 * 添加任务步骤记录
 * @param {string} taskId - 任务ID
 * @param {number} stepId - 步骤ID
 * @param {FormData} formData - 包含记录内容和文件的表单数据
 * @returns {Promise<Object>} 响应结果
 */
export function addStepRecord(taskId, stepId, formData) {
  return http.post(`${BASE_URL}/${taskId}/steps/${stepId}/records`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}