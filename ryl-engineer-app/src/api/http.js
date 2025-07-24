import axios from 'axios'
import router from '../router'

// 创建axios实例
const http = axios.create({
  baseURL: 'http://192.168.0.109:8089', // 测试用本地IP地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      // 修改认证头格式，确保与后端期望的格式一致
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 添加调试信息
    console.log(`发送${config.method.toUpperCase()}请求: ${config.url}`, {
      params: config.params,
      headers: config.headers,
      data: config.data
    })
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  response => {
    // 记录响应
    console.log(`收到响应: ${response.config.url}`, response.data)
    // 直接返回响应数据
    return response.data
  },
  error => {
    console.error('HTTP请求错误:', error)
    
    // 增加详细错误日志
    console.error('详细错误信息:', {
      message: error.message,
      code: error.code,
      url: error.config?.url,
      baseURL: error.config?.baseURL,
      method: error.config?.method,
      timeout: error.config?.timeout
    })

    if (error.response) {
      const { status, data } = error.response

      console.error(`HTTP错误状态: ${status}`, data)

      switch (status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          localStorage.removeItem('tokenExpire')
          router.push('/login')
          break
        case 403:
          // 权限不足
          alert(data.message || '您没有执行此操作的权限');
          console.error('权限不足')
          break
        case 404:
          // 资源不存在
          console.error('请求的资源不存在')
          break
        case 500:
          // 服务器错误 - 增强处理
          console.error('服务器错误:', data)
          // 检查是否是SQL Server特定错误
          if (data && data.message && (
              data.message.includes('SQL Server') || 
              data.message.includes('SQLServer') || 
              data.message.includes('Database')
          )) {
            console.error('数据库错误:', data.message)
          }
          break
        default:
          console.error(`未知错误: ${status}`)
      }

      // 返回格式化后的错误信息
      return Promise.reject({
        status,
        message: data && data.message ? data.message : '请求失败',
        data: data
      })
    }

    // 处理请求被取消的情况
    if (axios.isCancel(error)) {
      console.log('请求被取消:', error.message)
      return Promise.reject({
        message: '请求被取消',
        isCancel: true
      })
    }

    // 处理网络错误
    if (error.message && error.message.includes('Network Error')) {
      console.error('网络错误，请检查网络连接')
      return Promise.reject({
        message: '网络错误，请检查网络连接',
        isNetworkError: true
      })
    }

    // 处理超时
    if (error.message && error.message.includes('timeout')) {
      console.error('请求超时')
      return Promise.reject({
        message: '请求超时，请稍后再试',
        isTimeout: true
      })
    }

    // 其他错误
    return Promise.reject({
      message: error.message || '未知错误',
      error
    })
  }
)

export default http 