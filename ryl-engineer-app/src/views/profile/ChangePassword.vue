<template>
  <div class="change-password-page">
    <div class="page-header">
      <div class="back-btn" @click="goBack">
        <i class="icon-chevron-left"></i>
      </div>
      <div class="page-title">修改密码</div>
    </div>

    <div class="form-container">
      <div class="form-group">
        <label for="currentPassword">当前密码</label>
        <input 
          type="password" 
          id="currentPassword" 
          v-model="form.currentPassword"
          placeholder="请输入当前密码"
        />
        <div class="error-message" v-if="errors.currentPassword">
          {{ errors.currentPassword }}
        </div>
      </div>

      <div class="form-group">
        <label for="newPassword">新密码</label>
        <input 
          type="password" 
          id="newPassword" 
          v-model="form.newPassword"
          placeholder="请输入新密码"
        />
        <div class="error-message" v-if="errors.newPassword">
          {{ errors.newPassword }}
        </div>
      </div>

      <div class="form-group">
        <label for="confirmPassword">确认新密码</label>
        <input 
          type="password" 
          id="confirmPassword" 
          v-model="form.confirmPassword"
          placeholder="请再次输入新密码"
        />
        <div class="error-message" v-if="errors.confirmPassword">
          {{ errors.confirmPassword }}
        </div>
      </div>

      <div class="form-actions">
        <button 
          class="submit-btn" 
          @click="submitForm"
          :disabled="isSubmitting"
        >
          {{ isSubmitting ? '提交中...' : '确认修改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const isSubmitting = ref(false)

function goBack() {
  router.back()
}

function validateForm() {
  let isValid = true
  
  // 重置错误信息
  errors.currentPassword = ''
  errors.newPassword = ''
  errors.confirmPassword = ''
  
  // 验证当前密码
  if (!form.currentPassword) {
    errors.currentPassword = '请输入当前密码'
    isValid = false
  }
  
  // 验证新密码
  if (!form.newPassword) {
    errors.newPassword = '请输入新密码'
    isValid = false
  }
  
  // 验证确认密码
  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认新密码'
    isValid = false
  } else if (form.confirmPassword !== form.newPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }
  
  return isValid
}

async function submitForm() {
  if (!validateForm()) {
    return
  }
  
  isSubmitting.value = true
  
  try {
    console.log('准备提交密码修改请求')
    
    const result = await authStore.updatePassword({
      oldPassword: form.currentPassword,
      newPassword: form.newPassword
    })
    
    console.log('密码修改响应:', result)
    
    if (result.success) {
      alert('密码修改成功，请重新登录')
      await authStore.logoutUser()
      router.push('/login')
    } else {
      // 显示具体的错误消息
      if (result.error === '当前密码错误') {
        errors.currentPassword = '当前密码错误'
      } else {
        alert(result.error || '密码修改失败')
      }
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    alert('修改密码失败，请稍后再试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.change-password-page {
  padding: 16px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.back-btn {
  padding: 8px;
  margin-right: 8px;
}

.icon-chevron-left::before {
  content: '\f053';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 16px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}

.form-container {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-size: 15px;
  color: #333;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

input:focus {
  border-color: #3b82f6;
  outline: none;
}

.error-message {
  color: #ef4444;
  font-size: 14px;
  margin-top: 4px;
}

.form-actions {
  margin-top: 32px;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
}

.submit-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}
</style> 