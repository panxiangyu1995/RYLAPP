import { defineStore } from 'pinia';
import request from '@/utils/request';
import { uniStorage } from './storage';
import { useTaskStore } from './task';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: {}
  }),
  // 启用持久化
  persist: true,
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    hasUserInfo: (state) => Object.keys(state.userInfo).length > 0,
  },
  
  actions: {
    // 账号密码登录
    async passwordLogin(contact, password) {
      try {
        const response = await request.post('/account/login', { contact, password });
        this.setToken(response.data.token);
        if (response.data.userInfo) {
          this.setUserInfo(response.data.userInfo);
        }
        return response.data;
      } catch (error) {
        console.error('账号密码登录失败:', error);
        throw new Error(error.message || '登录失败，请稍后重试');
      }
    },
    
    // 用户注册
    async register(userData) {
      try {
        const response = await request.post('/account/register', userData);
        return response.data;
      } catch (error) {
        console.error('注册失败:', error);
        // 提供更具体的错误信息
        if (error.response && error.response.data && error.response.data.message) {
          throw new Error(error.response.data.message);
        } else if (error.message) {
          throw new Error(error.message);
        } else {
          throw new Error('注册失败，请检查输入信息或稍后重试');
        }
      }
    },
    
    // 检查用户名是否可用
    async checkContact(contact) {
      try {
        // 添加时间戳参数，确保不使用缓存
        const timestamp = new Date().getTime();
        const response = await request.get(`/account/check-contact?contact=${encodeURIComponent(contact)}&_t=${timestamp}`, { suppressErrorToast: true });
        // 如果 code 是 200，说明请求成功，根据 response.data 判断用户名是否可用
        // response.data 为 true 表示用户名存在，因此返回 !response.data
        if (response.code === 200) {
          return !response.data; // 如果 response.data 为 true（用户名存在），返回 false（不可用）
        }
        // 对于其他 code（理论上不应该出现），认为不可用
        return false;
      } catch (error) {
        // 捕获到 reject 的情况
        console.error("检查用户名失败:", error);
        return false; // 发生错误，认为不可用
      }
    },
    
    // 微信登录
    async wechatLogin(code, nickname, avatarUrl, phoneCode) {
      try {
        const response = await request.post('/wechat/login', { code, nickname, avatarUrl, phoneCode });
        this.setToken(response.data.token);
        if (response.data.userInfo) {
          this.setUserInfo(response.data.userInfo);
        }
        return response.data;
      } catch (error) {
        console.error('微信登录失败:', error);
        throw new Error(error.message || '微信登录失败，请稍后重试');
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      if (!this.token) return null;
      
      try {
        const response = await request.get('/user/profile');
        this.setUserInfo(response.data);
        return response.data;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        throw new Error(error.message || '获取用户信息失败，请稍后重试');
      }
    },
    
    // 更新用户信息
    async updateUserInfo(userData) {
      try {
        console.log('正在更新用户信息:', userData);
        const response = await request.put('/user/profile', userData);
        this.setUserInfo(response.data);
        console.log('用户信息更新成功');
        return response.data;
      } catch (error) {
        console.error('更新用户信息失败:', error);
        
        // 区分不同类型的错误
        if (error.response) {
          const status = error.response.status;
          
          if (status === 401) {
            // 认证失败，清除用户状态
            this.logout();
            throw new Error('登录已过期，请重新登录');
          } else if (status === 400) {
            // 参数错误
            throw new Error(error.response.data?.message || '提交的信息有误，请检查后重试');
          } else if (status === 500) {
            // 服务器错误
            throw new Error('服务器处理请求时出错，请联系管理员');
          }
          
          // 如果有服务器返回的具体错误信息，优先使用
          if (error.response.data?.message) {
            throw new Error(error.response.data.message);
          }
        }
        
        // 默认错误信息
        throw new Error(error.message || '更新用户信息失败，请稍后重试');
      }
    },
    
    // 设置Token
    setToken(token) {
      this.token = token;
      // 同时保存到本地存储
      uni.setStorageSync('token', token);
    },
    
    // 设置用户信息
    setUserInfo(userInfo) {
      // 确保avatarUrl和wechat_avatar_url保持同步
      if (userInfo) {
        // 处理头像URL，确保不使用blob URL
        if (userInfo.avatarUrl && userInfo.avatarUrl.startsWith('blob:')) {
          console.warn('检测到blob类型的avatarUrl，将不使用此URL:', userInfo.avatarUrl);
          userInfo.avatarUrl = null;
        }
        
        if (userInfo.wechat_avatar_url && userInfo.wechat_avatar_url.startsWith('blob:')) {
          console.warn('检测到blob类型的wechat_avatar_url，将不使用此URL:', userInfo.wechat_avatar_url);
          userInfo.wechat_avatar_url = null;
        }
        
        // 如果有avatarUrl但没有wechat_avatar_url，则同步
        if (userInfo.avatarUrl && !userInfo.wechat_avatar_url) {
          userInfo.wechat_avatar_url = userInfo.avatarUrl;
        }
        // 如果有wechat_avatar_url但没有avatarUrl，则同步
        else if (userInfo.wechat_avatar_url && !userInfo.avatarUrl) {
          userInfo.avatarUrl = userInfo.wechat_avatar_url;
        }
        
        // 确保头像URL是完整的URL或正确的相对路径
        ['avatarUrl', 'wechat_avatar_url'].forEach(field => {
          if (userInfo[field] && !userInfo[field].startsWith('http://') && !userInfo[field].startsWith('https://')) {
            // 确保路径以/开头
            if (!userInfo[field].startsWith('/')) {
              userInfo[field] = '/' + userInfo[field];
            }
          }
        });
      }
      
      Object.assign(this.userInfo, userInfo);
      // 同时保存到本地存储
      uni.setStorageSync('userInfo', userInfo);
    },
    
    // 注销登录
    logout() {
      console.log('[用户Store] 执行登出操作');
      this.userInfo = {};
      this.token = '';
      // 清除所有相关的本地存储
      uni.removeStorageSync('token');
      uni.removeStorageSync('userInfo');
      // 跳转到登录页
      uni.reLaunch({ url: '/pages/login/login' });
    }
  },
}); 