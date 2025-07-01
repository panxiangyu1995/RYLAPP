import http from './http'


/**
 * 获取工程师状态列表（按工作地点分组，不分页）
 * @param {Object} params 查询参数
 * @param {string} params.status 按协助状态筛选（available-可协助，busy-忙碌）
 * @param {string} params.keyword 搜索关键词(姓名/工号/部门)
 * @returns {Promise} 返回工程师状态列表，按工作地点分组
 */
export function getEngineerStatusList(params) {
  return http.get('/api/v1/contacts/engineers', { params })
}

/**
 * 获取其它联系人列表（非工程师角色）
 * @param {Object} params 查询参数
 * @param {string} params.keyword 搜索关键词(姓名/工号/部门)
 * @returns {Promise} 返回联系人数据数组
 */
export function getOtherContacts(params) {
  return http.get('/api/v1/contacts/other', { params })
}

/**
 * 获取联系人详情
 * @param {number} userId 联系人ID
 * @returns {Promise} 返回联系人详情
 */
export function getContactDetail(userId) {
  return http.get(`/api/v1/contacts/detail/${userId}`)
}

/**
 * 获取联系人分组列表
 * @returns {Promise} 返回联系人分组列表
 */
export function getContactGroups() {
  return http.get('/api/v1/contacts/groups')
}

/**
 * 创建联系人分组
 * @param {Object} data 分组数据
 * @param {string} data.name 分组名称
 * @returns {Promise} 返回创建结果
 */
export function createContactGroup(data) {
  return http.post('/api/v1/contacts/groups', data)
}

/**
 * 更新联系人分组
 * @param {Object} data 分组数据
 * @param {number} data.id 分组ID
 * @param {string} data.name 分组名称
 * @returns {Promise} 返回更新结果
 */
export function updateContactGroup(data) {
  return http.put('/api/v1/contacts/groups', data)
}

/**
 * 删除联系人分组
 * @param {number} groupId 分组ID
 * @returns {Promise} 返回删除结果
 */
export function deleteContactGroup(groupId) {
  return http.delete(`/api/v1/contacts/groups/${groupId}`)
}

/**
 * 向分组添加联系人
 * @param {Object} data 添加数据
 * @param {number} data.groupId 分组ID
 * @param {number[]} data.contactIds 联系人ID数组
 * @returns {Promise} 返回添加结果
 */
export function addContactsToGroup(data) {
  return http.post('/api/v1/contacts/groups/add', data)
}

/**
 * 从分组移除联系人
 * @param {Object} data 移除数据
 * @param {number} data.groupId 分组ID
 * @param {number[]} data.contactIds 联系人ID数组
 * @returns {Promise} 返回移除结果
 */
export function removeContactsFromGroup(data) {
  return http.post('/api/v1/contacts/groups/remove', data)
} 