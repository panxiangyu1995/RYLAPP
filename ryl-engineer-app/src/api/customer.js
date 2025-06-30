import http from './http'

/**
 * 客户API模块
 */

// 基础路径
export const BASE_URL = '/api/v1/customer'

/**
 * 获取客户列表
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 响应结果
 */
export function getCustomerList(params) {
  return http.get(`${BASE_URL}/list`, { params })
}

/**
 * 获取客户详情
 * @param {number} id 客户ID
 * @returns {Promise<Object>} 客户详情
 */
export function getCustomerDetail(id) {
  return http.get(`${BASE_URL}/${id}`)
}

/**
 * 创建客户
 * @param {Object} customerData 客户数据
 * @returns {Promise<Object>} 响应结果
 */
export function createCustomer(customerData) {
  return http.post(BASE_URL, customerData)
}

/**
 * 更新客户
 * @param {number} id 客户ID
 * @param {Object} customerData 客户数据
 * @returns {Promise<Object>} 响应结果
 */
export function updateCustomer(id, customerData) {
  return http.put(`${BASE_URL}/${id}`, customerData)
}

/**
 * 删除客户
 * @param {number} id 客户ID
 * @returns {Promise<Object>} 响应结果
 */
export function deleteCustomer(id) {
  return http.delete(`${BASE_URL}/${id}`)
}

/**
 * 带密码确认的删除客户
 * @param {number} id 客户ID
 * @param {string} password 确认密码
 * @returns {Promise<Object>} 响应结果
 */
export function deleteCustomerWithConfirm(id, password) {
  return http.post(`${BASE_URL}/delete-with-confirm`, { id, password })
}

/**
 * 获取客户联系人列表
 * @param {string|number} customerId 客户ID
 * @returns {Promise<Object>} 联系人列表
 */
export function getCustomerContacts(customerId) {
  return http.get(`/api/v1/customer/${customerId}/contacts`)
}

/**
 * 获取客户任务列表
 * @param {string|number} customerId 客户ID
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 任务列表
 */
export function getCustomerTasks(customerId, params) {
  return http.get(`/api/v1/customer/${customerId}/tasks`, { params })
} 