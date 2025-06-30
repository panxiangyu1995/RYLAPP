import http from './http'

// 登录
export function login(data) {
  // 实际API调用
  return http.post('/api/v1/user/login', {
    workId: data.workId,
    password: data.password
  })
}

// 注册
export function register(data) {
  return http.post('/api/v1/user/register', {
    workId: data.workId,
    name: data.name,
    mobile: data.mobile,
    password: data.password,
    confirmPassword: data.confirmPassword,
    department: data.department,
    location: data.location
  })
}

// 忘记密码验证
export function verifyForgotPassword(data) {
  return http.post('/api/v1/user/forgot-password/verify', null, {
    params: {
      workId: data.workId,
      mobile: data.mobile
    }
  })
}

// 忘记密码重置
export function forgotPassword(data) {
  return http.post('/api/v1/user/forgot-password', {
    workId: data.workId,
    mobile: data.mobile,
    newPassword: data.newPassword,
    confirmPassword: data.confirmPassword
  })
}

// 重置密码
export function resetPassword(data) {
  return http.post('/api/v1/user/reset-password', null, {
    params: {
      workId: data.workId,
      newPassword: data.newPassword
    }
  })
}

// 修改密码
export function changePassword(data) {
  return http.put('/api/v1/user/password', {
    oldPassword: data.oldPassword,
    newPassword: data.newPassword
  })
}

// 获取用户信息
export function getUserInfo() {
  return http.get('/api/v1/user/info')
}

// 退出登录
export function logout() {
  return http.post('/api/v1/user/logout')
} 