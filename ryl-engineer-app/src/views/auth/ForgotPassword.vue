<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <div class="bg-white shadow-sm p-3 d-flex align-items-center">
      <router-link to="/login" class="text-secondary">
        <i class="fas fa-arrow-left"></i>
      </router-link>
      <h1 class="fs-5 fw-semibold ms-3 mb-0">忘记密码</h1>
    </div>

    <!-- 主要内容 -->
    <div class="p-3">
      <!-- 身份验证表单 -->
      <div class="card mb-3" v-if="!verifySuccess && !resetSuccess">
        <div class="card-body">
          <div class="pb-3 mb-3 border-bottom">
            <h2 class="fs-5 fw-medium mb-2">验证身份</h2>
            <p class="text-muted small">请输入您注册时使用的工号和手机号码</p>
          </div>
          
          <form @submit.prevent="handleVerifyIdentity">
            <div class="mb-3">
              <label class="form-label">工号</label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-id-card"></i>
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
              <label class="form-label">手机号码</label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-mobile-alt"></i>
                </span>
                <input 
                  type="tel" 
                  v-model="formData.mobile" 
                  placeholder="请输入手机号码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.mobile }"
                >
              </div>
              <div v-if="errors.mobile" class="invalid-feedback d-block">{{ errors.mobile }}</div>
            </div>
            
            <button 
              type="submit" 
              class="btn btn-primary w-100"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              {{ loading ? '处理中...' : '验证身份' }}
            </button>
          </form>
          
          <!-- 错误提示 -->
          <div v-if="resetError" class="alert alert-danger mt-3" role="alert">
            {{ resetError }}
          </div>
        </div>
      </div>
      
      <!-- 重置密码表单 -->
      <div v-if="verifySuccess && !resetSuccess" class="card">
        <div class="card-body text-center py-4">
          <div class="bg-success bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3" style="width: 80px; height: 80px;">
            <i class="fas fa-check text-success fs-1"></i>
          </div>
          <h2 class="fs-4 fw-medium text-dark mb-3">身份验证成功</h2>
          <p class="text-muted mb-4">您已成功验证身份，请设置新密码</p>
          <form @submit.prevent="handleResetPassword">
            <div class="mb-3">
              <label class="form-label">新密码</label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-lock"></i>
                </span>
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  v-model="formData.newPassword" 
                  placeholder="请输入新密码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.newPassword }"
                >
                <button class="btn btn-outline-secondary" type="button" @click="togglePasswordVisibility">
                  <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div v-if="errors.newPassword" class="invalid-feedback d-block">{{ errors.newPassword }}</div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">确认新密码</label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-lock"></i>
                </span>
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'" 
                  v-model="formData.confirmPassword" 
                  placeholder="请再次输入新密码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.confirmPassword }"
                >
                <button class="btn btn-outline-secondary" type="button" @click="toggleConfirmPasswordVisibility">
                  <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div v-if="errors.confirmPassword" class="invalid-feedback d-block">{{ errors.confirmPassword }}</div>
            </div>
            
            <button 
              type="submit" 
              class="btn btn-primary w-100"
              :disabled="loading"
            >
              <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              {{ loading ? '处理中...' : '重置密码' }}
            </button>
          </form>
        </div>
      </div>
      
      <!-- 重置成功消息 -->
      <div v-if="resetSuccess" class="card">
        <div class="card-body text-center py-4">
          <div class="bg-success bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3" style="width: 80px; height: 80px;">
            <i class="fas fa-check text-success fs-1"></i>
          </div>
          <h2 class="fs-4 fw-medium text-dark mb-3">密码重置成功</h2>
          <p class="text-muted mb-4">您已成功重置密码，请使用新密码登录系统</p>
          <router-link to="/login" class="btn btn-primary w-100">
            返回登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { validateWorkId, validatePassword, validateConfirmPassword, validateMobile } from '../../utils/validation'

const authStore = useAuthStore()
const loading = computed(() => authStore.loading)
const resetError = computed(() => authStore.error)
const resetSuccess = ref(false)
const verifySuccess = ref(false)

// 表单数据
const formData = reactive({
  workId: '',
  mobile: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单错误
const errors = reactive({
  workId: '',
  mobile: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码可见性
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 切换密码可见性
function togglePasswordVisibility() {
  showPassword.value = !showPassword.value
}

function toggleConfirmPasswordVisibility() {
  showConfirmPassword.value = !showConfirmPassword.value
}

// 验证表单
function validateForm() {
  errors.workId = validateWorkId(formData.workId)
  errors.mobile = validateMobile(formData.mobile)
  
  if (verifySuccess.value) {
    errors.newPassword = validatePassword(formData.newPassword)
    errors.confirmPassword = validateConfirmPassword(formData.newPassword, formData.confirmPassword)
    return !errors.workId && !errors.mobile && !errors.newPassword && !errors.confirmPassword
  }
  
  return !errors.workId && !errors.mobile
}

// 处理身份验证
async function handleVerifyIdentity() {
  if (!validateForm()) return
  
  console.log('验证身份参数:', {
    workId: formData.workId,
    mobile: formData.mobile
  })
  
  const result = await authStore.verifyForgotPasswordIdentity({
    workId: formData.workId,
    mobile: formData.mobile
  })
  
  console.log('验证身份结果:', result)
  
  if (result && result.success) {
    verifySuccess.value = true
    console.log('身份验证成功，verifySuccess =', verifySuccess.value)
  }
}

// 处理重置密码
async function handleResetPassword() {
  if (!validateForm()) return
  
  console.log('重置密码参数:', {
    workId: formData.workId,
    newPassword: formData.newPassword
  })
  
  const result = await authStore.resetUserPassword({
    workId: formData.workId,
    newPassword: formData.newPassword
  })
  
  console.log('重置密码结果:', result)
  
  if (result && result.success) {
    resetSuccess.value = true
    console.log('密码重置成功，resetSuccess =', resetSuccess.value)
  }
}
</script>

<style scoped>
.app-container {
  max-width: 414px;
  margin: 0 auto;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 30px;
  overflow: hidden;
  position: relative;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.form-control, .btn {
  height: 40px;
}
</style> 