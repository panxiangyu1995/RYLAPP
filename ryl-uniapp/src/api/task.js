import request from '@/utils/request';
import axios from 'axios';
import { API_PATHS } from '@/constants/api';

/**
 * 获取任务列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTaskList(params) {
  return request({
    url: API_PATHS.TASK_LIST,
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
    url: API_PATHS.TASK_DETAIL(id),
    method: 'get'
  });
}

/**
 * 创建任务
 * @param {Object} data 任务数据
 * @returns {Promise}
 */
export function createTask(data) {
  return request({
    url: API_PATHS.TASK_CREATE,
    method: 'post',
    data
  });
}

/**
 * 获取当前用户当天提交的订单数
 * @returns {Promise}
 */
export function getDailyTaskCount() {
  return request({
    url: API_PATHS.TASK_DAILY_COUNT,
    method: 'get'
  });
}

/**
 * 上传任务相关图片
 * @param {File|Blob} file 图片文件对象
 * @param {String|Number} taskId 任务ID
 * @param {Number} imageType 图片类型（0：任务图片，1：设备图片，2：结果图片）
 * @param {Number} sort 排序序号
 * @returns {Promise}
 */
export function uploadTaskImage(file, taskId, imageType = 0, sort = 0) {
  // 验证参数
  if (!file) {
    console.error('上传图片失败：文件对象为空');
    return Promise.reject(new Error('文件对象为空'));
  }
  
  if (!(file instanceof File || file instanceof Blob)) {
    console.error('上传图片失败：无效的文件类型', typeof file, file);
    return Promise.reject(new Error('无效的文件类型'));
  }
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/jpg'];
  if (!validTypes.includes(file.type)) {
    console.error('上传图片失败：不支持的图片类型', file.type);
    return Promise.reject(new Error(`不支持的图片类型: ${file.type}`));
  }
  
  // 验证文件大小
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    console.error('上传图片失败：图片过大', file.size);
    return Promise.reject(new Error(`图片大小不能超过5MB, 当前大小: ${(file.size / 1024 / 1024).toFixed(2)}MB`));
  }
  
  console.log(`准备上传图片: 任务ID=${taskId}, 类型=${imageType}, 排序=${sort}, 大小=${file.size}字节, 类型=${file.type}`);
  
  // 创建FormData对象
  const formData = new FormData();
  formData.append('file', file);
  formData.append('taskId', taskId);
  formData.append('imageType', imageType);
  formData.append('sort', sort);
  
  // 日志记录上传内容
  console.log('上传图片FormData内容:');
  for (let [key, value] of formData.entries()) {
    if (key === 'file') {
      console.log(`${key}: ${value.name || 'unnamed'}, 类型: ${value.type}, 大小: ${value.size} 字节`);
    } else {
      console.log(`${key}: ${value}`);
    }
  }
  
  return request({
    url: API_PATHS.TASK_IMAGE_UPLOAD,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 30000, // 增加超时时间到30秒
    onUploadProgress: progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      console.log(`图片上传进度: ${percentCompleted}%`);
    }
  });
}

/**
 * 获取任务图片列表
 * @param {String|Number} taskId 任务ID
 * @param {Number} imageType 图片类型
 * @returns {Promise}
 */
export function getTaskImages(taskId, imageType) {
  return request({
    url: API_PATHS.TASK_IMAGES(taskId, imageType),
    method: 'get',
  });
}

/**
 * 获取图片预览
 * @param {String|Number} imageId 图片ID
 * @returns {String} 图片URL
 */
export function getImagePreview(imageId) {
  return API_PATHS.IMAGE_PREVIEW(imageId);
}

/**
 * 下载图片
 * @param {String|Number} imageId 图片ID
 * @returns {String} 图片下载URL
 */
export function downloadImage(imageId) {
  return API_PATHS.IMAGE_DOWNLOAD(imageId);
}

/**
 * 下载附件
 * @param {String|Number} fileId 文件ID
 * @returns {String} 附件下载URL
 */
export function downloadAttachment(fileId) {
  return API_PATHS.ATTACHMENT_DOWNLOAD(fileId);
}

/**
 * 提交服务评价
 * @param {String|Number} id 任务ID
 * @param {Object} data 评价数据
 * @returns {Promise}
 */
export function submitEvaluation(id, data) {
  return request({
    url: API_PATHS.TASK_EVALUATION(id),
    method: 'post',
    data
  });
}

/**
 * 获取任务步骤
 * @param {String|Number} id 任务ID
 * @returns {Promise}
 */
export function getTaskSteps(id) {
  return request({
    url: API_PATHS.TASK_STEPS(id),
    method: 'get'
  });
}

/**
 * 更新任务步骤
 * @param {String|Number} id 任务ID
 * @param {Number} stepIndex 步骤索引
 * @param {String} remark 备注
 * @returns {Promise}
 */
export function updateTaskStep(id, stepIndex, remark) {
  return request({
    url: API_PATHS.TASK_STEP_UPDATE(id, stepIndex),
    method: 'post',
    data: { remark }
  });
}