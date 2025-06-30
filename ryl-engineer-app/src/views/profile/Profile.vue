<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar-container">
        <img :src="userInfo.avatar || '/img/default-avatar.png'" class="avatar" />
      </div>
      <div class="user-info">
        <div class="name">{{ userInfo.name || '未设置姓名' }}</div>
        <div class="department">{{ userInfo.department || '未设置部门' }}</div>
        <div class="phone">{{ userInfo.phone || '未设置手机号' }}</div>
      </div>
      <div class="edit-profile" @click="editProfile">
        <i class="icon-edit"></i>
      </div>
    </div>

    <div class="profile-stats">
      <div class="stat-card">
        <div class="stat-value">{{ stats.taskCount }}</div>
        <div class="stat-label">任务数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.completionRate }}%</div>
        <div class="stat-label">完成率</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.rating }}</div>
        <div class="stat-label">评分</div>
      </div>
    </div>

    <div class="menu-list">
      <div class="menu-item" @click="navigateTo('/profile/info')">
        <div class="menu-icon user-icon"></div>
        <div class="menu-text">个人信息</div>
        <div class="icon-chevron-right"></div>
      </div>
      
      <div class="menu-item" @click="navigateTo('/profile/vehicle/list')">
        <div class="menu-icon car-icon"></div>
        <div class="menu-text">车辆管理</div>
        <div class="icon-chevron-right"></div>
      </div>

      <div class="menu-item" @click="navigateTo('/profile/vehicle/records')">
        <div class="menu-icon location-icon"></div>
        <div class="menu-text">上门打卡记录</div>
        <div class="icon-chevron-right"></div>
      </div>

      <div class="menu-item" @click="navigateTo('/profile/change-password')">
        <div class="menu-icon lock-icon"></div>
        <div class="menu-text">修改密码</div>
        <div class="icon-chevron-right"></div>
      </div>
    </div>

    <div class="logout-container">
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <!-- 错误提示 -->
    <div v-if="globalError" class="global-error">
      {{ globalError }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const authStore = useAuthStore()
const userStore = useUserStore()

const userInfo = ref({
  name: '加载中...',
  department: '加载中...',
  phone: '加载中...',
  avatar: '/img/default-avatar.png'
})

const stats = ref({
  taskCount: 0,
  completionRate: 0,
  rating: 0
})

const globalError = ref('')
const loading = ref(false)

// 检查token状态
onBeforeMount(() => {
  const token = localStorage.getItem('token')
  const tokenExpire = localStorage.getItem('tokenExpire')
  
  if (!token || !tokenExpire || new Date(tokenExpire) <= new Date()) {
    // token不存在或已过期，跳转到登录页
    router.push('/login')
    return
  }
  
  // 尝试从本地存储获取基本用户信息
  try {
    const userJson = localStorage.getItem('user')
    if (userJson) {
      const userData = JSON.parse(userJson)
      userInfo.value = {
        name: userData.name || '未设置姓名',
        department: userData.department || '未设置部门',
        phone: userData.mobile || '未设置手机号',
        avatar: userData.avatar || '/img/default-avatar.png'
      }
    }
  } catch (error) {
    console.error('读取本地用户信息失败:', error)
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
    const result = await userStore.fetchUserProfile()
    if (result.success) {
      const profile = result.profile
      userInfo.value = {
        name: profile.name,
        department: profile.department,
        phone: profile.mobile,
        avatar: profile.avatar || '/img/default-avatar.png'
      }
      
      if (profile.taskStats) {
        stats.value = {
          taskCount: profile.taskStats.ongoing + profile.taskStats.completed,
          completionRate: profile.taskStats.completed > 0 
            ? Math.round((profile.taskStats.completed / (profile.taskStats.ongoing + profile.taskStats.completed)) * 100) 
            : 0,
          rating: profile.taskStats.rating || 0
        }
      }
    } else {
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

function editProfile() {
  router.push('/profile/info')
}

function navigateTo(route) {
  router.push(route)
}

async function logout() {
  try {
    // 调用authStore的logoutUser方法清除登录状态
    await authStore.logoutUser()
    console.log('已退出登录')
    // 不需要手动导航到登录页面，authStore.logoutUser()方法会处理
  } catch (error) {
    console.error('退出登录失败:', error)
    // 如果退出登录失败，仍然尝试导航到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('tokenExpire')
    router.push('/login')
  }
}
</script>

<style scoped>
.profile-page {
  padding: 16px;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px; /* 增加底部padding，避免被导航栏遮挡 */
}

.profile-header {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.avatar-container {
  margin-right: 16px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.department, .phone {
  font-size: 14px;
  color: #666;
  margin-bottom: 2px;
}

.edit-profile {
  padding: 8px;
}

.icon-edit::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.profile-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  background-color: #fff;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  margin: 0 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.menu-list {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 24px;
  height: 24px;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-icon::before {
  content: '\f007';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #60a5fa;
}

.car-icon::before {
  content: '\f1b9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #60a5fa;
}

.location-icon::before {
  content: '\f3c5';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #60a5fa;
}

.lock-icon::before {
  content: '\f023';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #60a5fa;
}

.menu-text {
  flex: 1;
  font-size: 16px;
}

.icon-chevron-right::before {
  content: '\f054';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
  font-size: 12px;
}

.logout-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.logout-btn {
  width: 100%;
  max-width: 320px;
  padding: 12px;
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

.global-error {
  margin-top: 16px;
  padding: 12px;
  background-color: #fee2e2;
  color: #b91c1c;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
}
</style> 