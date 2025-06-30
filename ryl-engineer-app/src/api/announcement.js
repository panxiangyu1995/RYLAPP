import http from './http'

/**
 * 获取系统公告列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @param {string} params.importance 重要程度(normal-普通, important-重要, urgent-紧急)
 * @param {string} params.status 状态(active-活跃, inactive-非活跃)
 * @param {boolean} params.onlyUnread 是否只显示未读公告，默认false
 * @param {string} params.keyword 搜索关键词(标题)
 * @returns {Promise} 返回公告列表
 */
export function getAnnouncementList(params) {
  return http.get('/api/v1/announcement/list', { params })
}

/**
 * 获取公告详情
 * @param {number|string} announcementId 公告ID
 * @returns {Promise} 返回公告详情
 */
export function getAnnouncementDetail(announcementId) {
  // 确保ID是数字类型
  const id = parseInt(announcementId, 10);
  return http.get(`/api/v1/announcement/detail/${id}`)
}

/**
 * 标记公告已读
 * @param {number|string} announcementId 公告ID
 * @returns {Promise} 返回标记结果
 */
export function markAnnouncementRead(announcementId) {
  // 确保ID是数字类型并使用路径参数
  const id = parseInt(announcementId, 10);
  return http.post(`/api/v1/announcement/read/${id}`)
}

/**
 * 发布系统公告(需要管理员权限)
 * @param {Object} data 公告数据
 * @param {string} data.title 公告标题
 * @param {string} data.content 公告内容
 * @param {string} data.importance 重要程度(normal-普通, important-重要, urgent-紧急)，默认normal
 * @param {string} data.startTime 生效时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {string} data.endTime 结束时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {boolean} data.isPopup 是否弹窗显示，默认false
 * @returns {Promise} 返回发布结果
 */
export function publishAnnouncement(data) {
  return http.post('/api/v1/announcement/publish', data)
}

/**
 * 更新系统公告(需要管理员权限)
 * @param {Object} data 公告数据
 * @param {number} data.id 公告ID
 * @param {string} data.title 公告标题
 * @param {string} data.content 公告内容
 * @param {string} data.importance 重要程度(normal-普通, important-重要, urgent-紧急)
 * @param {string} data.status 状态(active-活跃, inactive-非活跃)
 * @param {string} data.startTime 生效时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {string} data.endTime 结束时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {boolean} data.isPopup 是否弹窗显示
 * @returns {Promise} 返回更新结果
 */
export function updateAnnouncement(data) {
  return http.put('/api/v1/announcement/update', data)
}

/**
 * 删除系统公告(需要管理员权限)
 * @param {number} announcementId 公告ID
 * @returns {Promise} 返回删除结果
 */
export function deleteAnnouncement(announcementId) {
  return http.delete(`/api/v1/announcement/delete/${announcementId}`)
} 