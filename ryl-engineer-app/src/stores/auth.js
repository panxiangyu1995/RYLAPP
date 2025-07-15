import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, register, forgotPassword, verifyForgotPassword, resetPassword, logout, getUserInfo, changePassword } from '../api/auth'
import router from '../router'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  const token = ref(localStorage.getItem('token') || null)
  const tokenExpire = ref(localStorage.getItem('tokenExpire') || null)
  const loading = ref(false)
  const error = ref(null)

  // 登录
  async function loginUser(credentials) {
    loading.value = true
    error.value = null
    
    try {
      const response = await login(credentials)
      
      if (response.code === 200) {
        const userData = response.data
        
        // 存储用户信息和令牌
        user.value = {
          workId: userData.workId,
          name: userData.name,
          mobile: userData.mobile,
          avatar: userData.avatar,
          department: userData.department,
          location: userData.location
        }
        
        token.value = userData.token
        tokenExpire.value = userData.tokenExpire
        
        // 保存到本地存储
        localStorage.setItem('user', JSON.stringify(user.value))
        localStorage.setItem('token', userData.token)
        localStorage.setItem('tokenExpire', userData.tokenExpire)
        
        // 如果用户选择了"记住我"，将登录信息保存到localStorage
        if (credentials.rememberMe) {
          localStorage.setItem('rememberedUser', JSON.stringify({
            workId: credentials.workId
          }))
        } else {
          // 如果没有选择"记住我"，清除之前保存的信息
          localStorage.removeItem('rememberedUser')
        }
        
        return { success: true }
      } else {
        throw new Error(response.message || '登录失败')
      }
    } catch (err) {
      error.value = err.message || '登录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getUserInfo()
      
      if (response.code === 200) {
        const userData = response.data
        
        // 更新用户信息
        user.value = {
          id: userData.id,
          workId: userData.workId,
          name: userData.name,
          mobile: userData.mobile,
          avatar: userData.avatar,
          department: userData.department,
          location: userData.location,
          status: userData.status,
          lastLoginTime: userData.lastLoginTime
        }
        
        // 更新本地存储
        localStorage.setItem('user', JSON.stringify(user.value))
        
        return { success: true, user: user.value }
      } else {
        throw new Error(response.message || '获取用户信息失败')
      }
    } catch (err) {
      error.value = err.message || '获取用户信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 注册
  async function registerUser(userData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await register(userData)
      
      if (response.code === 200 && response.data && response.data.registered) {
        return true
      } else {
        throw new Error(response.message || '注册失败')
      }
    } catch (err) {
      error.value = err.message || '注册过程中发生错误'
      return false
    } finally {
      loading.value = false
    }
  }

  // 验证忘记密码身份
  async function verifyForgotPasswordIdentity(verifyData) {
    loading.value = true
    error.value = null
    
    try {
      console.log('调用验证身份API，参数:', verifyData)
      const response = await verifyForgotPassword(verifyData)
      console.log('验证身份API响应:', response)
      
      if (response.code === 200 && response.data && response.data.reset) {
        return { success: true }
      } else {
        throw new Error(response.message || '身份验证失败')
      }
    } catch (err) {
      console.error('验证身份API错误:', err)
      error.value = err.message || '身份验证过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 重置密码（忘记密码流程）
  async function resetPasswordWithForgot(resetData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await forgotPassword(resetData)
      
      if (response.code === 200) {
        return { success: true }
      } else {
        throw new Error(response.message || '密码重置失败')
      }
    } catch (err) {
      error.value = err.message || '密码重置过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 重置密码（已验证身份后）
  async function resetUserPassword(resetData) {
    loading.value = true
    error.value = null
    
    try {
      console.log('调用重置密码API，参数:', resetData)
      const response = await resetPassword({
        workId: resetData.workId,
        newPassword: resetData.newPassword
      })
      console.log('重置密码API响应:', response)
      
      if (response.code === 200) {
        return { success: true }
      } else {
        throw new Error(response.message || '密码重置失败')
      }
    } catch (err) {
      console.error('重置密码API错误:', err)
      error.value = err.message || '密码重置过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 退出登录
  async function logoutUser() {
    loading.value = true
    error.value = null
    
    try {
      if (token.value) {
        try {
          // 尝试调用后端API，但不阻塞退出流程
          await logout()
        } catch (err) {
          console.log('后端登出API调用失败，但仍继续本地登出流程')
        }
      }
      
      // 清除登陆会话相关的存储和状态，但保留"记住我"的用户信息
      user.value = null
      token.value = null
      tokenExpire.value = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      localStorage.removeItem('tokenExpire')
      
      // 不再删除rememberedUser，保留"记住我"的用户信息
      // localStorage.removeItem('rememberedUser')
      
      // 跳转到登录页
      router.push('/login')
      return { success: true }
    } catch (err) {
      console.error('退出登录过程中发生错误:', err)
      
      // 即使发生错误，也清除会话数据，但保留"记住我"的用户信息
      user.value = null
      token.value = null
      tokenExpire.value = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      localStorage.removeItem('tokenExpire')
      
      // 不再删除rememberedUser，保留"记住我"的用户信息
      // localStorage.removeItem('rememberedUser')
      
      // 跳转到登录页
      router.push('/login')
      return { success: true }
    } finally {
      loading.value = false
    }
  }

  // 检查是否已登录
  function isLoggedIn() {
    // 检查token是否存在
    if (!token.value) return false;
    
    // 检查token是否过期
    return !isTokenExpired();
  }

  // 检查token是否过期
  function isTokenExpired() {
    if (!tokenExpire.value) return true;
    
    try {
      // 将字符串转换为日期对象并比较
      const expireDate = new Date(tokenExpire.value);
      const now = new Date();
      return expireDate < now;
    } catch (e) {
      // 如果解析失败，视为过期
      console.error('Token expire date parsing error:', e);
      return true;
    }
  }

  // 检查用户是否拥有指定角色
  function hasRole(roleName) {
    // 如果用户未登录，返回false
    if (!user.value) return false;
    
    try {
      // 从本地存储获取用户信息（可能包含更多数据）
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}');
      console.log('检查角色权限:', { roleName, userInfo });
      
      // 检查用户角色
      const roles = userInfo.roles || [];
      
      // 为了开发测试，临时返回true
      // TODO: 实际部署时移除此行，使用下面的真实角色检查
      return true;
      
      // 实际角色检查逻辑
      // return roles.includes(roleName);
    } catch (err) {
      console.error('角色检查错误:', err);
      // 发生错误时默认返回false
      return false;
    }
  }

  // 修改密码
  async function updatePassword(passwordData) {
    loading.value = true
    error.value = null
    
    console.log('Store - 准备调用密码更新API', { 
      oldPassword: '******', 
      newPassword: '******' 
    })
    
    try {
      const response = await changePassword(passwordData)
      console.log('密码更新响应:', response)
      
      if (response.code === 200 && response.data && response.data.updated) {
        return { success: true }
      } else {
        throw new Error(response.message || '密码修改失败')
      }
    } catch (err) {
      console.error('密码更新错误:', err)
      error.value = err.message || '密码修改过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    user,
    token,
    tokenExpire,
    loading,
    error,
    loginUser,
    registerUser,
    logoutUser,
    fetchUserInfo,
    isLoggedIn,
    isTokenExpired,
    updatePassword,
    verifyForgotPasswordIdentity,
    resetUserPassword,
    hasRole
  }
})