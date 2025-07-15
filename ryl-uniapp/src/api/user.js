import request from '@/utils/request';

/**
 * 微信登录
 * @param {Object} data 登录数据，包含code
 * @returns {Promise}
 */
/**
 * 微信登录
 * @param {String} code 微信登录code
 * @returns {Promise}
 */
export function wechatLogin(code) {
  return request({
    url: '/wechat/login',
    method: 'post',
    data: { code }
  });
}

/**
 * 获取用户个人信息
 * @returns {Promise}
 */
/**
 * 获取用户个人信息
 * @returns {Promise}
 */
export function getUserProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  });
}

/**
 * 更新用户个人信息
 * @param {Object} data 用户资料数据
 * @returns {Promise}
 */
/**
 * 更新用户个人信息
 * @param {Object} data 用户资料数据
 * @returns {Promise}
 */
export function updateUserProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  });
} 

/**
 * 账号密码登录
 * @param {String} contact 登录用户名
 * @param {String} password 密码
 * @returns {Promise}
 */
export function accountLogin(contact, password) {
  return request({
    url: '/account/login',
    method: 'post',
    data: { contact, password }
  });
}

/**
 * 用户注册
 * @param {Object} data 注册数据
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/account/register',
    method: 'post',
    data
  });
}

/**
 * 检查用户名是否存在
 * @param {String} contact 登录用户名
 * @returns {Promise}
 */
export function checkContact(contact) {
  return request({
    url: `/account/check-contact?contact=${encodeURIComponent(contact)}`,
    method: 'get'
  });
}
