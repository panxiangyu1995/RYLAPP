import http from './http'

/**
 * 销售人员API模块
 */

// 基础路径
export const BASE_URL = '/api/v1/user/sales'

/**
 * 获取所有销售人员列表
 * @returns {Promise<Object>} 响应结果
 */
export function getSalesList() {
  return http.get(BASE_URL)
}

/**
 * 获取销售人员详情
 * @param {number} id 销售人员ID
 * @returns {Promise<Object>} 销售人员详情
 */
export function getSalesDetail(id) {
  return http.get(`${BASE_URL}/${id}`)
}

/**
 * 获取销售人员负责的客户列表
 * @param {number} id 销售人员ID
 * @returns {Promise<Object>} 客户列表
 */
export function getSalesCustomers(id) {
  return http.get(`${BASE_URL}/${id}/customers`)
}

/**
 * 获取销售人员的任务列表
 * @param {number} id 销售人员ID
 * @param {Object} params 查询参数
 * @returns {Promise<Object>} 任务列表
 */
export function getSalesTasks(id, params) {
  return http.get(`${BASE_URL}/${id}/tasks`, { params })
}

/**
 * 根据条件搜索销售人员
 * @param {Object} params 搜索条件
 * @returns {Promise<Object>} 销售人员列表
 */
export function searchSales(params) {
  return http.get('/api/v1/user/sales/search', { params })
} 