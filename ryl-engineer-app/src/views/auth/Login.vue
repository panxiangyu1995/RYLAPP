<template>
  <div class="login-container">
    <!-- 顶部背景图 -->
    <div class="login-bg">
      <div class="logo-container">
        <img src="../../assets/images/company-logo.png" alt="Company Logo" class="logo">
        <h1 class="text-white fw-bold platform-title">科研仪器维修工程师平台</h1>
      </div>
    </div>

    <!-- 登录表单 -->
    <div class="login-form">
      <h2 class="text-center fw-bold mb-4">欢迎登录</h2>
      
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label class="form-label">工号</label>
          <div class="input-group">
            <span class="input-group-text">
              <i class="fas fa-user"></i>
            </span>
            <input 
              type="text" 
              v-model="formData.workId" 
              placeholder="请输入工号" 
              class="form-control"
              :class="{ 'is-invalid': errors.workId }"
            >
          </div>
          <div v-if="errors.workId" class="invalid-feedback d-block">{{ errors.workId }}</div>
        </div>
        
        <div class="mb-3">
          <label class="form-label">密码</label>
          <div class="input-group">
            <span class="input-group-text">
              <i class="fas fa-lock"></i>
            </span>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              v-model="formData.password" 
              placeholder="请输入密码" 
              class="form-control"
              :class="{ 'is-invalid': errors.password }"
            >
            <button class="btn btn-outline-secondary" type="button" @click="togglePasswordVisibility">
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
          <div v-if="errors.password" class="invalid-feedback d-block">{{ errors.password }}</div>
        </div>
        
        <div class="d-flex justify-content-between mb-3">
          <div class="form-check">
            <input type="checkbox" class="form-check-input" id="rememberMe" v-model="formData.rememberMe">
            <label class="form-check-label" for="rememberMe">记住我</label>
          </div>
          <router-link to="/forgot-password" class="text-decoration-none">忘记密码?</router-link>
        </div>
        
        <button 
          type="submit" 
          class="btn btn-primary w-100 mb-3"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
        
        <div class="text-center pt-3 border-top">
          <span class="text-muted">还没有账号?</span>
          <router-link to="/register" class="text-decoration-none ms-1">注册</router-link>
        </div>
      </form>

      <!-- 错误提示 -->
      <div v-if="loginError" class="alert alert-danger mt-3" role="alert">
        {{ loginError }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { validateWorkId, validatePassword } from '../../utils/validation'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const loading = computed(() => authStore.loading)
const loginError = computed(() => authStore.error)

// 表单数据
const formData = reactive({
  workId: '',
  password: '',
  rememberMe: false
})

// 表单错误
const errors = reactive({
  workId: '',
  password: ''
})

// 密码可见性
const showPassword = ref(false)

// 切换密码可见性
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

// 在组件挂载时检查是否有保存的登录信息
onMounted(() => {
  const savedUser = localStorage.getItem('rememberedUser')
  if (savedUser) {
    try {
      const userData = JSON.parse(savedUser)
      formData.workId = userData.workId || ''
      formData.rememberMe = true
    } catch (e) {
      console.error('Failed to parse saved user data', e)
    }
  }
})

// 验证表单
function validateForm() {
  errors.workId = validateWorkId(formData.workId)
  errors.password = validatePassword(formData.password)
  
  return !errors.workId && !errors.password
}

// 处理登录
async function handleLogin() {
  if (!validateForm()) return
  
  // 处理"记住我"功能
  if (formData.rememberMe) {
    localStorage.setItem('rememberedUser', JSON.stringify({
      workId: formData.workId
    }))
  } else {
    localStorage.removeItem('rememberedUser')
  }
  
  const result = await authStore.loginUser({
    workId: formData.workId,
    password: formData.password,
    rememberMe: formData.rememberMe
  })
  
  if (result && result.success) {
    // 获取重定向地址
    const redirect = route.query.redirect || '/home'
    router.replace(redirect)
  }
}
</script>

<style scoped>
.login-container {
  max-width: 414px;
  margin: 0 auto;
  overflow: hidden;
  position: relative;
  height: 100vh; /* 使用固定高度 */
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  /* 禁止滚动 */
  overflow-y: hidden;
}

.login-bg {
  height: 200px;
  background-color: #808080;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-container {
  z-index: 10;
  text-align: center;
}

.logo {
  width: 170px; /* 调整为原来的1.5倍 */
  height: auto;
  margin-bottom: 10px;
  /* 去掉圆形边框和背景 */
  border-radius: 0;
  background-color: transparent;
  padding: 0;
}

.platform-title {
  font-size: 0.9em; /* 标题字号调整为原来的0.75倍 */
}

.login-form {
  background-color: white;
  border-top-left-radius: 25px;
  border-top-right-radius: 25px;
  margin-top: -25px;
  position: relative;
  z-index: 20;
  padding: 30px 20px;
  flex: 1;
  /* 确保内容不会溢出 */
  overflow-y: auto;
}

/* 输入框样式调整 */
.form-control {
  height: 45px;
  border-radius: 8px;
}

/* 按钮样式 */
.btn-primary {
  height: 45px;
  border-radius: 8px;
  font-weight: 500;
  background-color: #0d6efd;
}

/* 表单项样式 */
.input-group-text {
  background-color: transparent;
  border-right: none;
}

.form-control {
  border-left: none;
  background-color: #f0f7ff;
}

.form-check-input:checked {
  background-color: #0d6efd;
  border-color: #0d6efd;
}
</style> 