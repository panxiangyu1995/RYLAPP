import http from './http'

/**
 * 创建打卡记录
 * @param {Object} data 打卡记录数据
 * @param {string} data.taskId 任务ID
 * @param {string} data.taskName 任务名称
 * @param {number} data.type 打卡类型（1-出发打卡，2-到达打卡，3-返程打卡）
 * @param {string} data.location 打卡地址
 * @param {string} data.longitude 经度
 * @param {string} data.latitude 纬度
 * @param {string} data.photos 照片URL，多个照片以逗号分隔
 * @param {number} data.distance 里程数（公里）
 * @param {string} data.transportType 交通工具类型（company-公车，private-私车，public-公共交通）
 * @param {string} data.remark 备注
 * @returns {Promise} 返回创建结果
 */
export function createVehicleRecord(data) {
  return http.post('/api/v1/vehicle-record', data)
}

/**
 * 根据ID获取打卡记录
 * @param {number} id 记录ID
 * @returns {Promise} 返回打卡记录
 */
export function getVehicleRecordById(id) {
  return http.get(`/api/v1/vehicle-record/${id}`)
}

/**
 * 获取当前用户的打卡记录列表
 * @returns {Promise} 返回打卡记录列表
 */
export function getMyVehicleRecords() {
  return http.get('/api/v1/vehicle-record/my-records')
}

/**
 * 根据任务ID获取打卡记录列表
 * @param {string} taskId 任务ID
 * @returns {Promise} 返回打卡记录列表
 */
export function getVehicleRecordsByTaskId(taskId) {
  return http.get(`/api/v1/vehicle-record/task/${taskId}`)
}

/**
 * 获取指定时间范围内的打卡记录列表
 * @param {string} startTime 开始时间，格式：yyyy-MM-dd HH:mm:ss
 * @param {string} endTime 结束时间，格式：yyyy-MM-dd HH:mm:ss
 * @returns {Promise} 返回打卡记录列表
 */
export function getVehicleRecordsByTimeRange(startTime, endTime) {
  return http.get('/api/v1/vehicle-record/time-range', {
    params: {
      startTime,
      endTime
    }
  })
}

/**
 * 获取指定月份的打卡统计信息
 * @param {number} year 年份
 * @param {number} month 月份
 * @returns {Promise} 返回打卡统计信息
 */
export function getMonthlyVehicleRecordStats(year, month) {
  return http.get('/api/v1/vehicle-record/stats/month', {
    params: {
      year,
      month
    }
  })
}

/**
 * 获取当月的打卡统计信息
 * @returns {Promise} 返回打卡统计信息
 */
export function getCurrentMonthVehicleRecordStats() {
  return http.get('/api/v1/vehicle-record/stats/current-month')
} 