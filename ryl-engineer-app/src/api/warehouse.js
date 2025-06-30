/**
 * 仓库模块API接口
 */
import http from './http'

// 基础路径
const BASE_URL = '/api/v1/warehouse'

/**
 * 仓库管理API
 */

// 获取所有仓库
export function getAllWarehouses() {
  return http.get(`${BASE_URL}/list`)
}

// 获取仓库详情
export function getWarehouseDetail(id) {
  return http.get(`${BASE_URL}/${id}`)
}

// 添加仓库 (仅管理员)
export function addWarehouse(warehouseData) {
  return http.post(`${BASE_URL}/add`, warehouseData)
}

// 修改仓库 (仅管理员)
export function updateWarehouse(id, warehouseData) {
  const updateData = { ...warehouseData, id }
  return http.put(`${BASE_URL}/update`, updateData)
}

// 删除仓库 (仅管理员)
export function deleteWarehouse(id, password) {
  return http.delete(`${BASE_URL}/delete`, { data: { id, password } })
}

/**
 * 物品管理API
 */

// 获取物品列表
export function getWarehouseItems(warehouseId, params = {}) {
  const queryParams = { ...params, warehouseId }
  return http.get(`${BASE_URL}/item/list`, { params: queryParams })
}

// 获取物品详情
export function getItemDetail(itemId) {
  return http.get(`${BASE_URL}/item/${itemId}`)
}

// 添加物品 (仅仓库管理员)
export function addItem(itemData) {
  return http.post(`${BASE_URL}/item/add`, itemData)
}

// 修改物品 (仅仓库管理员)
export function updateItem(itemId, itemData) {
  const updateData = { ...itemData, id: itemId }
  return http.put(`${BASE_URL}/item/update`, updateData)
}

// 删除物品 (仅仓库管理员)
export function deleteItem(itemId, password) {
  return http.delete(`${BASE_URL}/item/delete`, { data: { id: itemId, password } })
}

/**
 * 出入库API
 */

// 物品入库
export function stockIn(stockInData) {
  return http.post(`${BASE_URL}/stock/in`, stockInData)
}

// 物品出库
export function stockOut(stockOutData) {
  return http.post(`${BASE_URL}/stock/out`, stockOutData)
}

// 获取出入库记录
export function getStockRecords(params) {
  return http.get(`${BASE_URL}/stock/records`, { params })
}

/**
 * 物品申请API
 */

// 提交物品使用申请
export function requestItemUse(requestData) {
  return http.post(`${BASE_URL}/request/use`, requestData)
}

// 提交物品采购申请
export function requestItemPurchase(requestData) {
  return http.post(`${BASE_URL}/request/purchase`, requestData)
}

// 获取申请列表
export function getItemRequests(params) {
  return http.get(`${BASE_URL}/request/list`, { params })
}

// 处理申请 (仅仓库管理员)
export function processItemRequest(requestId, processData) {
  return http.post(`${BASE_URL}/request/process`, processData)
}

/**
 * 盘库API
 */

// 创建盘库任务
export function createInventoryCheck(checkData) {
  return http.post(`${BASE_URL}/inventory-check/create`, checkData)
}

// 提交盘库结果
export function submitInventoryCheck(detailsData) {
  return http.post(`${BASE_URL}/inventory-check/submit`, detailsData)
}

// 获取盘库记录
export function getInventoryChecks(params) {
  return http.get(`${BASE_URL}/inventory-check/list`, { params })
}

// 获取盘库详情
export function getInventoryCheckDetail(checkId) {
  return http.get(`${BASE_URL}/inventory-check/${checkId}`)
}

/**
 * 统计报表API
 */

// 获取库存分类统计
export function getInventoryStats(params) {
  return http.get(`${BASE_URL}/stats/category`, { params })
}

// 获取出入库统计
export function getStockStats(params) {
  return http.get(`${BASE_URL}/stats/stock`, { params })
}

// 获取物品使用统计
export function getItemUsageStats(params) {
  return http.get(`${BASE_URL}/request/usage-stats`, { params })
}

/**
 * 二维码API
 */

// 解析二维码
export function parseQRCode(content) {
  return http.get(`${BASE_URL}/qrcode/parse`, { params: { code: content } })
}

// 生成二维码
export function generateQRCode(itemId) {
  return http.get(`${BASE_URL}/qrcode/generate/${itemId}`)
} 