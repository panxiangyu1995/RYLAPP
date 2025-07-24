<template>
  <div class="engineer-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>工程师详情</h1>
      <div class="header-right">
        <button class="contact-btn" @click="contactEngineer">
          <i class="icon-chat"></i>联系
        </button>
      </div>
    </header>

    <!-- 加载中状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <i class="icon-error-circle"></i>
      <p>{{ error }}</p>
      <button class="retry-button" @click="loadEngineerDetail">重试</button>
    </div>

    <template v-else>
      <!-- 工程师基本信息 -->
      <div class="engineer-card">
        <img :src="engineer.avatar || defaultAvatar" :alt="engineer.name" class="engineer-avatar">
        <div class="engineer-info">
          <h2 class="engineer-name">{{ engineer.name }}</h2>
          <p class="engineer-title">{{ engineer.department }}</p>
          <div class="engineer-status">
            <span :class="['status-badge', getStatusClass(engineer.status)]">
              {{ engineer.status }}
            </span>
            <span class="task-count">当前任务数: {{ engineer.taskCount }}</span>
          </div>
        </div>
      </div>
      
      <!-- 任务列表标题 -->
      <div class="task-header">
        <h3>当前任务列表</h3>
        <div class="task-filters">
          <button 
            v-for="(filter, index) in taskFilters" 
            :key="index"
            :class="['filter-btn', { active: activeFilter === filter.value }]"
            @click="activeFilter = filter.value"
          >
            {{ filter.label }}
          </button>
        </div>
      </div>
      
      <!-- 任务列表 -->
      <div class="task-list">
        <div v-if="filteredTasks.length === 0" class="no-data">
          <p>暂无任务数据</p>
        </div>
        
        <div 
          v-for="(task, index) in filteredTasks" 
          :key="index"
          class="task-item"
        >
          <div class="task-header-info">
            <div>
              <h4 class="task-title">{{ task.title }}</h4>
              <p class="task-id">任务编号: {{ task.taskId }}</p>
            </div>
            <span :class="['task-status', getTaskStatusClass(task.status)]">
              {{ task.status === 'in-progress' ? '进行中' : '已完成' }}
            </span>
          </div>
          
          <div class="task-progress">
            <div class="progress-header">
              <span class="progress-label">当前步骤:</span>
              <span class="progress-step">{{ task.currentStep }}</span>
            </div>
            <div class="progress-content">
              <p class="progress-description">{{ task.stepDescription || '暂无描述' }}</p>
              <div v-if="task.images && task.images.length > 0" class="progress-images">
                <img 
                  v-for="(image, i) in task.images" 
                  :key="i"
                  :src="image"
                  alt="任务图片"
                  class="progress-image"
                >
              </div>
              <div class="progress-meta">
                <span class="progress-time">{{ formatDateTime(task.lastUpdateTime) }}</span>
                <span class="progress-duration">用时: {{ task.duration || '未记录' }}</span>
              </div>
            </div>
            <a class="view-flow-link" @click="viewTaskFlow(task.taskId)">
              <i class="icon-list"></i>查看完整流程
            </a>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="bottom-actions">
        <button class="bottom-btn primary" @click="requestAssistance">
          <i class="icon-user-plus"></i>请求协助
        </button>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getEngineerDetail } from '@/api/contacts'
import defaultAvatar from '@/assets/images/company-logo.png'

export default {
  name: 'EngineerDetailPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const engineerId = ref(parseInt(route.params.id) || 0)
    const engineer = ref({})
    const inProgressTasks = ref([])
    const completedTasks = ref([])
    const loading = ref(true)
    const error = ref(null)
    const activeFilter = ref('in-progress')
    
    const taskFilters = [
      { label: '进行中', value: 'in-progress' },
      { label: '已完成', value: 'completed' }
    ]
    
    // 根据筛选条件过滤任务
    const filteredTasks = computed(() => {
      if (activeFilter.value === 'in-progress') {
        return inProgressTasks.value
      } else {
        return completedTasks.value
      }
    })
    
    // 加载工程师详情和任务列表
    const loadEngineerDetail = async () => {
      if (!engineerId.value) {
        error.value = '工程师ID无效'
        loading.value = false
        return
      }
      
      loading.value = true
      error.value = null
      
      try {
        console.log('获取工程师详情，ID:', engineerId.value)
        const res = await getEngineerDetail(engineerId.value)
        console.log('工程师详情API返回:', res)
        
        if (res.code === 200 && res.data) {
          // 设置工程师信息
          engineer.value = res.data.engineer || {}
          
          // 设置任务列表
          inProgressTasks.value = res.data.inProgressTasks || []
          completedTasks.value = res.data.completedTasks || []
        } else {
          error.value = res.message || '获取工程师详情失败'
        }
      } catch (err) {
        console.error('获取工程师详情异常:', err)
        error.value = '获取工程师详情失败，请稍后再试'
      } finally {
        loading.value = false
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '未记录'
      
      try {
        const date = new Date(dateTime)
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).replace(/\//g, '-')
      } catch (e) {
        return dateTime.toString()
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 联系工程师
    const contactEngineer = () => {
      router.push(`/chat/${engineerId.value}`)
    }
    
    // 查看任务流程
    const viewTaskFlow = (taskId) => {
      console.log('查看任务流程:', taskId);
      router.push({ name: 'TaskFlowDetail', params: { id: taskId } });
    }
    
    // 请求协助
    const requestAssistance = () => {
      router.push({
        path: `/request-assistance/${engineerId.value}`,
        query: {
          engineerName: engineer.value.name,
          taskCount: engineer.value.taskCount
        }
      })
    }
    
    // 根据状态获取CSS类
    const getStatusClass = (status) => {
      switch(status) {
        case '忙碌': return 'status-busy'
        case '可协助': return 'status-available'
        case '部分可协': return 'status-partial'
        default: return ''
      }
    }
    
    // 获取任务状态CSS类
    const getTaskStatusClass = (status) => {
      switch(status) {
        case 'in-progress': return 'status-in-progress'
        case 'completed': return 'status-completed'
        case 'pending': return 'status-pending'
        default: return ''
      }
    }
    
    // 组件挂载时加载数据
    onMounted(() => {
      loadEngineerDetail()
    })
    
    return {
      engineer,
      loading,
      error,
      activeFilter,
      taskFilters,
      filteredTasks,
      defaultAvatar,
      formatDateTime,
      goBack,
      contactEngineer,
      viewTaskFlow,
      requestAssistance,
      getStatusClass,
      getTaskStatusClass,
      loadEngineerDetail
    }
  }
}
</script>

<style scoped>
.engineer-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120px;
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
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #000;
}

.header h1 {
  flex: 1;
  margin: 0;
  text-align: center;
  font-size: 18px;
  font-weight: 500;
}

.contact-btn {
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.contact-btn i {
  margin-right: 4px;
}

.engineer-card {
  background-color: #fff;
  margin: 16px;
  padding: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.engineer-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 16px;
  border: 1px solid #eaeaea;
}

.engineer-info {
  flex: 1;
}

.engineer-name {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 4px;
}

.engineer-title {
  color: #666;
  margin: 0 0 8px;
  font-size: 14px;
}

.engineer-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-busy {
  background-color: #fee2e2;
  color: #ef4444;
}

.status-available {
  background-color: #dcfce7;
  color: #22c55e;
}

.status-partial {
  background-color: #fef3c7;
  color: #f59e0b;
}

.task-count {
  font-size: 12px;
  color: #666;
}

.task-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}

.task-header h3 {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.task-filters {
  display: flex;
  gap: 12px;
}

.filter-btn {
  background: none;
  border: none;
  padding: 4px 0;
  font-size: 14px;
  cursor: pointer;
  color: #666;
  position: relative;
}

.filter-btn.active {
  color: #3b82f6;
  font-weight: 500;
}

.filter-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #3b82f6;
}

.task-list {
  padding: 0 16px;
}

.task-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.task-header-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.task-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 4px;
}

.task-id {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.task-status {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  align-self: flex-start;
}

.status-in-progress {
  background-color: #e0f2fe;
  color: #0284c7;
}

.status-completed {
  background-color: #dcfce7;
  color: #22c55e;
}

.status-pending {
  background-color: #fef3c7;
  color: #f59e0b;
}

.task-progress {
  background-color: #f9fafb;
  border-radius: 6px;
  padding: 12px;
}

.progress-header {
  display: flex;
  font-size: 14px;
  margin-bottom: 8px;
}

.progress-label {
  color: #666;
  margin-right: 8px;
}

.progress-step {
  font-weight: 500;
}

.progress-content {
  margin-bottom: 12px;
}

.progress-description {
  font-size: 14px;
  margin: 0 0 12px;
  line-height: 1.5;
}

.progress-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.progress-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eaeaea;
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.view-flow-link {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
  font-size: 14px;
  padding-top: 12px;
  border-top: 1px solid #eaeaea;
  cursor: pointer;
}

.view-flow-link i {
  margin-right: 4px;
}

.bottom-actions {
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 12px 16px;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

.bottom-btn {
  border: none;
  border-radius: 4px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.bottom-btn.primary {
  background-color: #3b82f6;
  color: #fff;
  width: 100%;
  justify-content: center;
}

.bottom-btn i {
  margin-right: 8px;
}

/* 加载中样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.spinner {
  border: 3px solid #f3f4f6;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误样式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.error-container i {
  font-size: 48px;
  color: #ef4444;
  margin-bottom: 16px;
}

.error-container p {
  color: #666;
  margin-bottom: 16px;
}

.retry-button {
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
}

/* 无数据样式 */
.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #666;
}

.no-data i {
  font-size: 36px;
  margin-bottom: 12px;
  color: #9ca3af;
}

.icon-arrow-left::before {
  content: "\f060";
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #000;
}
</style> 