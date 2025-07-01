import request from '@/utils/request';

/**
 * 获取任务列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTaskList(params) {
  return request({
    url: '/tasks',
    method: 'get',
    params
  });
}

/**
 * 获取任务详情
 * @param {String|Number} id 任务ID
 * @returns {Promise}
 */
export function getTaskDetail(id) {
  return request({
    url: `/tasks/${id}`,
    method: 'get'
  });
}

/**
 * 创建任务
 * @param {FormData} data 任务数据（包括图片和附件）
 * @returns {Promise}
 */
export function createTask(data) {
  return request({
    url: '/tasks',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 提交服务评价
 * @param {String|Number} id 任务ID
 * @param {Object} data 评价数据
 * @returns {Promise}
 */
export function submitEvaluation(id, data) {
  return request({
    url: `/tasks/${id}/evaluation`,
    method: 'post',
    data
  });
} 