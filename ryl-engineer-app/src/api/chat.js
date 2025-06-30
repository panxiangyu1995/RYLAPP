import http from './http'

/**
 * 获取会话列表
 * @param {Object} params 查询参数
 * @param {string} params.type 会话类型(single-单聊, group-群聊, all-全部)，默认all
 * @param {boolean} params.isTaskRelated 是否只显示任务相关会话，默认false
 * @param {string} params.keyword 搜索关键词(会话名称/成员名称)
 * @param {boolean} params.onlyUnread 是否只显示有未读消息的会话，默认false
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @returns {Promise} 返回会话列表
 */
export function getConversationList(params) {
  return http.get('/api/v1/chat/conversation/list', { params })
}

/**
 * 创建聊天会话
 * @param {Object} data 会话数据
 * @param {string} data.type 会话类型(single-单聊, group-群聊)
 * @param {string} data.name 会话名称（群聊时必填）
 * @param {number[]} data.memberIds 会话成员ID数组
 * @param {boolean} data.isTaskRelated 是否与任务相关，默认false
 * @param {string} data.relatedTaskId 关联任务ID（任务相关时必填）
 * @param {string} data.avatar 会话头像（群聊时可选）
 * @returns {Promise} 返回创建结果
 */
export function createConversation(data) {
  return http.post('/api/v1/chat/conversation/create', data)
}

/**
 * 获取聊天消息列表
 * @param {string} conversationId 会话ID
 * @param {Object} params 查询参数
 * @param {number} params.page 页码，默认1
 * @param {number} params.size 每页记录数，默认20
 * @param {string} params.startTime 开始时间，格式：YYYY-MM-DDTHH:mm:ss
 * @param {string} params.endTime 结束时间，格式：YYYY-MM-DDTHH:mm:ss
 * @returns {Promise} 返回消息列表
 */
export function getMessageList(conversationId, params) {
  return http.get(`/api/v1/chat/message/list/${conversationId}`, { params })
}

/**
 * 发送消息
 * @param {Object} data 消息数据
 * @param {string} data.conversationId 会话ID
 * @param {string} data.content 消息内容
 * @param {string} data.messageType 消息类型(chat/task/system)，默认chat
 * @param {string} data.contentType 内容类型(text/image/file)，默认text
 * @param {string} data.replyTo 回复的消息ID
 * @returns {Promise} 返回发送结果
 */
export function sendMessage(data) {
  return http.post('/api/v1/chat/message/send', data)
}

/**
 * 上传消息图片
 * @param {FormData} formData 包含图片文件的表单数据
 * @returns {Promise} 返回上传结果
 */
export function uploadMessageImage(formData) {
  return http.post('/api/v1/chat/message/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 标记消息已读
 * @param {Object} data 标记数据
 * @param {string} data.conversationId 会话ID
 * @param {string} data.messageId 消息ID，不传则标记整个会话为已读
 * @returns {Promise} 返回标记结果
 */
export function markMessageRead(data) {
  return http.post('/api/v1/chat/message/read', data)
}

/**
 * 更新会话设置
 * @param {Object} data 设置数据
 * @param {string} data.conversationId 会话ID
 * @param {boolean} data.isSticky 是否置顶
 * @param {boolean} data.isMute 是否静音
 * @returns {Promise} 返回更新结果
 */
export function updateConversationSettings(data) {
  return http.put('/api/v1/chat/conversation/settings/update', data)
}

/**
 * 修改群聊信息
 * @param {Object} data 群聊数据
 * @param {string} data.conversationId 会话ID
 * @param {string} data.name 群聊名称
 * @param {string} data.avatar 群聊头像URL
 * @returns {Promise} 返回修改结果
 */
export function updateConversation(data) {
  return http.put('/api/v1/chat/conversation/update', data)
}

/**
 * 管理会话成员
 * @param {Object} data 成员管理数据
 * @param {string} data.conversationId 会话ID
 * @param {string} data.action 操作类型(add-添加, remove-移除)
 * @param {number[]} data.memberIds 成员ID数组
 * @returns {Promise} 返回操作结果
 */
export function manageConversationMembers(data) {
  return http.post('/api/v1/chat/conversation/members/manage', data)
}

/**
 * 退出会话
 * @param {string} conversationId 会话ID
 * @returns {Promise} 返回退出结果
 */
export function exitConversation(conversationId) {
  return http.post('/api/v1/chat/conversation/exit', { conversationId })
} 