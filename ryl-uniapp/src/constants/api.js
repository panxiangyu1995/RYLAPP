/**
 * API路径常量
 * 统一管理所有API路径，确保一致性
 */
export const API_PATHS = {
  // 订单相关API
  TASK_LIST: '/task/list',
  TASK_DETAIL: (id) => `/task/${id}`,
  TASK_STEPS: (id) => `/task/${id}/steps`,
  TASK_CREATE: '/task/create',
  TASK_DAILY_COUNT: '/task/daily-count',
  TASK_EVALUATION: (id) => `/task/${id}/evaluation`,
  TASK_STEP_UPDATE: (id, step) => `/task/${id}/step/${step}`,
  TASK_CONFIRM_PRICE: (id) => `/task/${id}/confirm-price`,
  TASK_IMAGE_UPLOAD: '/files/task/image',
  TASK_ATTACHMENT_UPLOAD: '/files/task/attachment',
  
  // 文件相关API
  IMAGE_PREVIEW: (id) => `/files/${id}`,
  IMAGE_DOWNLOAD: (id) => `/files/${id}`,
  ATTACHMENT_DOWNLOAD: (fileId) => `/files/attachment/${fileId}`,
  TASK_IMAGES: (taskId, imageType) => `/files/task/${taskId}/images${imageType !== undefined ? `?imageType=${imageType}` : ''}`,
  TASK_ATTACHMENTS: (taskId) => `/files/task/${taskId}/attachments`,
  
  // 用户相关API
  USER_LOGIN: '/account/login',
  USER_REGISTER: '/account/register',
  USER_INFO: '/user/info',
  USER_PROFILE: '/user/profile',
  USER_UPDATE_PROFILE: '/user/profile',
  CHECK_CONTACT: '/account/check-contact',
  WECHAT_LOGIN: '/wechat/login'
}; 