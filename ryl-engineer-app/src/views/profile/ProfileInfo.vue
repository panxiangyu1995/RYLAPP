<template>
  <div class="profile-info-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>个人信息</h1>
      <div class="header-right">
        <button class="save-btn" @click="saveInfo" :disabled="loading">
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </div>
    </header>

    <!-- 主要内容 -->
    <div class="content">
      <div v-if="loading && !userInfo.workId" class="loading-container">
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <!-- 头像和基本信息 -->
        <div class="avatar-card">
          <div class="avatar-container">
            <img :src="userInfo.avatar || '/img/default-avatar.png'" class="avatar" alt="工程师头像">
            <div class="avatar-edit-btn" @click="changeAvatar">
              <i class="icon-camera"></i>
            </div>
            <input
              type="file"
              ref="avatarInput"
              style="display: none"
              accept="image/*"
              @change="onAvatarSelected"
            />
          </div>
          <div class="avatar-info">
            <h2>{{ userInfo.name || '未设置姓名' }}</h2>
            <p>{{ userInfo.department || '未设置部门' }}</p>
          </div>
        </div>

        <!-- 基本信息卡片 -->
        <div class="info-card">
          <div class="info-section">
            <div class="form-group">
              <label>工号</label>
              <input type="text" v-model="userInfo.workId" class="form-input" disabled>
            </div>

            <div class="form-group">
              <label>姓名</label>
              <input type="text" v-model="userInfo.name" class="form-input" placeholder="请输入姓名">
              <div class="error-message" v-if="errors.name">{{ errors.name }}</div>
            </div>
            
            <div class="form-group">
              <label>手机号码</label>
              <input type="tel" v-model="userInfo.mobile" class="form-input" placeholder="请输入手机号">
              <div class="error-message" v-if="errors.mobile">{{ errors.mobile }}</div>
            </div>
            
            <div class="form-group">
              <label>所属部门</label>
              <input type="text" v-model="userInfo.department" class="form-input" placeholder="请输入所属部门">
            </div>
          </div>
        </div>

        <!-- 技术分类 (仅工程师可见) -->
        <div v-if="isEngineer" class="info-card">
          <div class="info-section">
            <h3 class="section-title">技术分类</h3>
            <p class="section-subtitle">请选择您擅长的技术领域</p>
            <div class="category-grid">
              <div v-for="category in technicalCategoryOptions" :key="category" class="form-check">
                <input 
                  class="form-check-input" 
                  type="checkbox" 
                  :value="category" 
                  :id="'cat_' + category"
                  v-model="userInfo.technicalCategory"
                >
                <label class="form-check-label" :for="'cat_' + category">
                  {{ category }}
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- 错误提示 -->
        <div v-if="globalError" class="global-error">
          {{ globalError }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useAuthStore } from '../../stores/auth'
import { toast } from 'vue-toastification'

const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()

// 用户信息
const userInfo = reactive({
  workId: '',
  name: '',
  department: '',
  mobile: '',
  avatar: '',
  technicalCategory: [], // 使用数组存储技术分类
})

// 表单错误
const errors = reactive({
  name: '',
  mobile: ''
})

// 全局错误
const globalError = ref('')
const loading = ref(false)
const avatarInput = ref(null)

// 部门列表
const departments = [
  '工程师',
  '仓库管理员',
  '客户经理',
]

// 选项
const technicalCategoryOptions = ref([
  '质谱', '色谱', '光谱', '电化学', '生命科学', '物性测试', '样品前处理', '通用设备'
])

const isEngineer = computed(() => userInfo.department === '工程师')

// 检查token状态
onBeforeMount(() => {
  const token = localStorage.getItem('token')
  const tokenExpire = localStorage.getItem('tokenExpire')
  
  if (!token || !tokenExpire || new Date(tokenExpire) <= new Date()) {
    // token不存在或已过期，跳转到登录页
    router.push('/login')
    return
  }
})

// 页面加载时获取用户信息
onMounted(async () => {
  await fetchUserProfile()
})

// 获取用户信息
async function fetchUserProfile() {
  loading.value = true
  globalError.value = ''
  
  try {
    // 先从本地存储获取基本用户信息
    const userJson = localStorage.getItem('user')
    if (userJson) {
      const userData = JSON.parse(userJson)
      userInfo.workId = userData.workId || ''
      userInfo.name = userData.name || ''
    }
    
    // 然后从API获取完整信息
    const result = await userStore.fetchUserProfile()
    if (result.success) {
      const profile = result.profile
      userInfo.workId = profile.workId
      userInfo.name = profile.name
      userInfo.department = profile.department
      userInfo.mobile = profile.mobile
      userInfo.avatar = profile.avatar
      // 将字符串转换为数组
      if (profile.technicalCategory && typeof profile.technicalCategory === 'string') {
        userInfo.technicalCategory = profile.technicalCategory.split(',')
      } else {
        userInfo.technicalCategory = []
      }
    } else {
      // API请求失败，但不影响表单显示
      console.error('获取用户信息失败:', result.error)
      
      // 只有在非401错误时显示错误信息
      if (!result.error.includes('401')) {
        globalError.value = result.error
      } else {
        // 401错误表示token失效，重新登录
        await authStore.logoutUser()
        router.push('/login')
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    globalError.value = '获取用户信息失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 验证表单
function validateForm() {
  let isValid = true
  
  // 重置错误
  errors.name = ''
  errors.mobile = ''
  globalError.value = ''
  
  // 验证姓名
  if (!userInfo.name.trim()) {
    errors.name = '请输入姓名'
    isValid = false
  }
  
  // 验证手机号
  if (!userInfo.mobile) {
    errors.mobile = '请输入手机号'
    isValid = false
  } else if (!/^1[3-9]\d{9}$/.test(userInfo.mobile)) {
    errors.mobile = '请输入有效的手机号'
    isValid = false
  }
  
  return isValid
}

// 保存个人信息
async function saveInfo() {
  if (!validateForm()) return
  
  loading.value = true
  try {
    const payload = {
      name: userInfo.name,
      mobile: userInfo.mobile,
      department: userInfo.department,
      // 将数组转换回字符串
      technicalCategory: userInfo.technicalCategory.join(','),
    }
    const result = await userStore.updateProfile(payload)
    
    if (result.success) {
      // 更新本地存储中的用户名
      try {
        const userJson = localStorage.getItem('user')
        if (userJson) {
          const userData = JSON.parse(userJson)
          userData.name = userInfo.name
          localStorage.setItem('user', JSON.stringify(userData))
        }
      } catch (e) {
        console.error('更新本地用户信息失败:', e)
      }
      
      toast.success('个人信息更新成功')
      router.back()
    } else {
      globalError.value = result.error
      toast.error(result.error || '更新失败')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    globalError.value = '更新个人信息失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

// 点击更换头像
function changeAvatar() {
  avatarInput.value.click()
}

// 头像文件选择后
async function onAvatarSelected(event) {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }
  
  // 验证文件后缀
  const fileName = file.name
  const fileExtension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase()
  const validExtensions = ['.jpg', '.jpeg', '.png', '.gif']
  if (!validExtensions.includes(fileExtension)) {
    alert('只支持JPG、JPEG、PNG和GIF格式的图片')
    return
  }
  
  // 验证文件大小（限制为5MB）
  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过5MB')
    return
  }
  
  loading.value = true
  globalError.value = ''
  try {
    console.log('开始上传头像', file.name, file.type, file.size)
    
    const formData = new FormData()
    // 使用'file'作为参数名，与后端接口匹配
    formData.append('file', file)
    
    // 添加详细日志
    for(let pair of formData.entries()) {
      console.log('FormData 内容:', pair[0], pair[1] instanceof File ? `文件: ${pair[1].name}` : pair[1])
    }
    
    const result = await userStore.updateAvatar(formData)
    console.log('头像上传结果:', result)
    
    if (result.success) {
      userInfo.avatar = result.avatarUrl
      
      // 更新本地存储中的用户头像
      try {
        const userJson = localStorage.getItem('user')
        if (userJson) {
          const userData = JSON.parse(userJson)
          userData.avatar = result.avatarUrl
          localStorage.setItem('user', JSON.stringify(userData))
        }
      } catch (e) {
        console.error('更新本地用户头像失败:', e)
      }
      
      alert('头像更新成功')
    } else {
      globalError.value = result.error || '更新头像失败，请稍后再试'
    }
  } catch (error) {
    console.error('更新头像失败:', error)
    globalError.value = '更新头像失败: ' + (error.message || '未知错误')
  } finally {
    loading.value = false
    // 重置文件输入，允许再次选择同一文件
    avatarInput.value.value = ''
  }
}
</script>

<style scoped>
.profile-info-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px; /* 增加底部padding，防止被导航栏遮挡 */
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.save-btn {
  color: #3b82f6;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
}

.save-btn:disabled {
  color: #93c5fd;
}

.content {
  padding: 16px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.avatar-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-container {
  position: relative;
  margin-bottom: 16px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background-color: #3b82f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.icon-camera::before {
  content: '\f030';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: white;
  font-size: 14px;
}

.avatar-info {
  text-align: center;
}

.avatar-info h2 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.avatar-info p {
  font-size: 14px;
  color: #666;
}

.info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-input, .form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
}

.form-input:disabled {
  background-color: #f3f4f6;
  color: #6b7280;
}

.error-message {
  color: #ef4444;
  font-size: 12px;
  margin-top: 4px;
}

.global-error {
  margin-top: 16px;
  padding: 12px;
  background-color: #fee2e2;
  color: #b91c1c;
  border-radius: 8px;
  font-size: 14px;
}
</style> 