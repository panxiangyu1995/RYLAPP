import axios from 'axios';

// 创建axios实例
const request = axios.create({
  baseURL: '/api/v1',  // 与API规范一致
  timeout: 10000      // 请求超时10秒
});

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data;
    
    // 如果响应的状态码不是200，说明服务器出现了问题
    if (res.code !== 200) {
      // token过期或无效
      if (res.code === 401) {
        // 清除本地存储的token
        localStorage.removeItem('token');
        // 重新加载页面，触发路由守卫
        window.location.reload();
      }
      
      return Promise.reject(new Error(res.message || '服务器错误'));
    } else {
      return res;
    }
  },
  error => {
    return Promise.reject(error);
  }
);

export default request; 