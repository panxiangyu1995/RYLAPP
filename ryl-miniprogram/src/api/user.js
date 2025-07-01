import request from '@/utils/request';

/**
 * 微信登录
 * @param {Object} data 登录数据，包含code
 * @returns {Promise}
 */
export function wechatLogin(data) {
  return request({
    url: '/wechat/login',
    method: 'post',
    data
  });
}

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
export function updateUserProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  });
} 