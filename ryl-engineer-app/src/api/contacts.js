import http from './http'

/**
 * 获取联系人列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @param {string} params.keyword 搜索关键词(姓名/工号/部门)
 * @param {string} params.department 按部门筛选
 * @param {number} params.status 按状态筛选（0-离线，1-在线）
 * @returns {Promise} 返回联系人列表
 */
export function getContactsList(params) {
  return http.get('/api/v1/contacts/list', { params })
}

/**
 * 获取工程师状态列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认100
 * @param {string} params.status 按协助状态筛选（available-可协助，busy-忙碌）
 * @returns {Promise} 返回工程师状态列表
 */
export function getEngineerStatusList(params) {
  return http.get('/api/v1/contacts/engineers', { params })
}

/**
 * 获取其它联系人列表（非工程师角色）
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @param {string} params.keyword 搜索关键词(姓名/工号/部门)
 * @returns {Promise} 返回分页数据，格式为{code: 200, message: "成功", data: {total: 总数, list: [联系人列表]}}
 * @returns {Promise.data.list} 联系人列表，每项包含id, workId, name, avatar, department, mobile, status, role等字段
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

/**
 * 测试获取非工程师联系人列表（不分页）
 * @returns {Promise} 返回测试数据
 */
export function testOtherContacts() {
  return http.get('/api/v1/contacts/test/other')
} 