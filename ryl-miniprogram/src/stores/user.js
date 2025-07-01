import { defineStore } from 'pinia';
import request from '@/utils/request';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    hasUserInfo: (state) => Object.keys(state.userInfo).length > 0,
  },
  
  actions: {
    // 微信登录
    async wechatLogin(code) {
      try {
        const response = await request.post('/wechat/login', { code });
        this.setToken(response.data.token);
        if (response.data.userInfo) {
          this.setUserInfo(response.data.userInfo);
        }
        return response.data;
      } catch (error) {
        console.error('微信登录失败:', error);
        throw error;
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
        throw error;
      }
    },
    
    // 更新用户信息
    async updateUserInfo(userData) {
      try {
        const response = await request.put('/user/profile', userData);
        this.setUserInfo(response.data);
        return response.data;
      } catch (error) {
        console.error('更新用户信息失败:', error);
        throw error;
      }
    },
    
    // 设置Token
    setToken(token) {
      this.token = token;
      localStorage.setItem('token', token);
    },
    
    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
    },
    
    // 注销登录
    logout() {
      this.token = '';
      this.userInfo = {};
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
    }
  }
}); 