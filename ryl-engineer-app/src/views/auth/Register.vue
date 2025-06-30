<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <div class="bg-white shadow-sm p-3 d-flex align-items-center">
      <router-link to="/login" class="text-secondary">
        <i class="fas fa-arrow-left"></i>
      </router-link>
      <h1 class="fs-5 fw-semibold ms-3 mb-0">注册账号</h1>
    </div>

    <!-- 主要内容 -->
    <div class="p-3">
      <p class="text-muted small mb-3">请填写以下信息完成注册，带 <span class="text-danger">*</span> 为必填项</p>
      
      <form @submit.prevent="handleRegister">
        <!-- 基本信息 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">基本信息</h2>
            
            <div class="mb-3">
              <label class="form-label">
                工号 <span class="text-danger">*</span>
              </label>
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
              <small class="text-muted">工号是您登录的唯一凭证，请向管理员获取</small>
            </div>
            
            <div class="mb-3">
              <label class="form-label">
                姓名 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-user"></i>
                </span>
                <input 
                  type="text" 
                  v-model="formData.name" 
                  placeholder="请输入姓名" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.name }"
                >
              </div>
              <div v-if="errors.name" class="invalid-feedback d-block">{{ errors.name }}</div>
            </div>
            
            <div class="mb-3">
              <label class="form-label">
                手机号码 <span class="text-danger">*</span>
              </label>
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
          </div>
        </div>
        
        <!-- 账号安全 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">账号安全</h2>
            
            <div class="mb-3">
              <label class="form-label">
                设置密码 <span class="text-danger">*</span>
              </label>
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
            
            <div class="mb-3">
              <label class="form-label">
                确认密码 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-lock"></i>
                </span>
                <input 
                  :type="showConfirmPassword ? 'text' : 'password'" 
                  v-model="formData.confirmPassword" 
                  placeholder="请再次输入密码" 
                  class="form-control"
                  :class="{ 'is-invalid': errors.confirmPassword }"
                >
                <button class="btn btn-outline-secondary" type="button" @click="toggleConfirmPasswordVisibility">
                  <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div v-if="errors.confirmPassword" class="invalid-feedback d-block">{{ errors.confirmPassword }}</div>
            </div>
          </div>
        </div>
        
        <!-- 职业信息 -->
        <div class="card mb-3">
          <div class="card-body">
            <h2 class="fs-5 fw-medium mb-3">职业信息</h2>
            
            <div class="mb-3">
              <label class="form-label">
                所属部门 <span class="text-danger">*</span>
              </label>
              <div class="input-group">
                <span class="input-group-text">
                  <i class="fas fa-building"></i>
                </span>
                <select 
                  v-model="formData.department" 
                  class="form-select"
                  :class="{ 'is-invalid': errors.department }"
                >
                  <option value="" selected disabled>请选择部门</option>
                  <option>系统管理员</option>
                  <option>工程师</option>
                  <option>仓库管理员</option>
                  <option>客户经理</option>
                </select>
              </div>
              <div v-if="errors.department" class="invalid-feedback d-block">{{ errors.department }}</div>
            </div>
          </div>
        </div>
        
        <!-- 注册按钮 -->
        <button 
          type="submit" 
          class="btn btn-primary w-100 mb-3"
          :disabled="loading"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {{ loading ? '注册中...' : '完成注册' }}
        </button>
        
        <div class="text-center">
          <span class="text-muted small">已有账号?</span>
          <router-link to="/login" class="text-decoration-none ms-1">返回登录</router-link>
        </div>
      </form>
      
      <!-- 错误提示 -->
      <div v-if="registerError" class="alert alert-danger mt-3" role="alert">
        {{ registerError }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { validateWorkId, validatePassword, validateConfirmPassword, validateMobile, validateName, validateDepartment } from '../../utils/validation'

const authStore = useAuthStore()
const loading = computed(() => authStore.loading)
const registerError = computed(() => authStore.error)

// 表单数据
const formData = reactive({
  workId: '',
  name: '',
  mobile: '',
  password: '',
  confirmPassword: '',
  department: ''
})

// 表单错误
const errors = reactive({
  workId: '',
  name: '',
  mobile: '',
  password: '',
  confirmPassword: '',
  department: ''
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
  errors.name = validateName(formData.name)
  errors.mobile = validateMobile(formData.mobile)
  errors.password = validatePassword(formData.password)
  errors.confirmPassword = validateConfirmPassword(formData.password, formData.confirmPassword)
  errors.department = validateDepartment(formData.department)
  
  return !errors.workId && !errors.name && !errors.mobile && 
         !errors.password && !errors.confirmPassword && !errors.department
}

// 处理注册
async function handleRegister() {
  if (!validateForm()) return
  
  const result = await authStore.registerUser({
    workId: formData.workId,
    name: formData.name,
    mobile: formData.mobile,
    password: formData.password,
    confirmPassword: formData.confirmPassword,
    department: formData.department,
    location: formData.location || ''
  })
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

.form-control, .form-select, .btn {
  height: 40px;
}
</style> 