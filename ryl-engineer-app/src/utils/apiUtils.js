/**
 * API工具函数
 * 封装常用的API请求模式，统一处理错误和响应格式
 */
import http from '../api/http'

/**
 * 标准GET请求
 * @param {string} url - 请求路径
 * @param {Object} params - 查询参数
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function get(url, params = {}, options = {}) {
  return http.get(url, { params, ...options })
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 标准POST请求
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function post(url, data = {}, options = {}) {
  return http.post(url, data, options)
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 标准PUT请求
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function put(url, data = {}, options = {}) {
  return http.put(url, data, options)
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 标准PATCH请求
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function patch(url, data = {}, options = {}) {
  return http.patch(url, data, options)
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 标准DELETE请求
 * @param {string} url - 请求路径
 * @param {Object} params - 查询参数
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function del(url, params = {}, options = {}) {
  return http.delete(url, { params, ...options })
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 分页查询
 * @param {string} url - 请求路径
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页记录数
 * @param {string} params.sort - 排序字段
 * @param {string} params.direction - 排序方向
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function getPage(url, params = {}, options = {}) {
  // 确保分页参数存在
  const pageParams = {
    page: params.page || 1,
    size: params.size || 10,
    ...params
  }
  
  return get(url, pageParams, options)
}

/**
 * 文件上传
 * @param {string} url - 请求路径
 * @param {FormData} formData - 表单数据
 * @param {Function} onProgress - 上传进度回调
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function upload(url, formData, onProgress = null, options = {}) {
  const uploadOptions = {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...options
  }
  
  if (onProgress) {
    uploadOptions.onUploadProgress = progressEvent => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      onProgress(percentCompleted, progressEvent)
    }
  }
  
  return http.post(url, formData, uploadOptions)
    .then(handleResponse)
    .catch(handleError)
}

/**
 * 批量操作
 * @param {string} url - 请求路径
 * @param {Array} items - 批量操作项
 * @param {Object} options - 请求选项
 * @returns {Promise<any>} - 响应数据
 */
export function batch(url, items, options = {}) {
  return post(url, { items }, options)
}

/**
 * 处理响应
 * @param {Object} response - 响应对象
 * @returns {any} - 处理后的响应数据
 */
function handleResponse(response) {
  // http.js中已经提取了response.data
  if (response.code === 200) {
    // 成功响应，返回数据
    return response.data
  } else {
    // 业务逻辑错误，抛出异常
    const error = new Error(response.message || '请求失败')
    error.code = response.code
    error.data = response.data
    throw error
  }
}

/**
 * 处理错误
 * @param {Error} error - 错误对象
 * @throws {Error} - 抛出处理后的错误
 */
function handleError(error) {
  // 已经在http.js中处理了网络错误，这里处理业务逻辑错误
  console.error('API请求错误:', error)
  
  // 如果是业务逻辑错误，直接抛出
  if (error.code) {
    throw error
  }
  
  // 其他错误，包装后抛出
  const wrappedError = new Error(error.message || '网络请求失败')
  wrappedError.originalError = error
  throw wrappedError
}

export default {
  get,
  post,
  put,
  patch,
  delete: del,
  getPage,
  upload,
  batch
} 