<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="header">
      <h1>任务中心</h1>
      <div class="header-icons">
        <i class="icon-notification" @click="goToNotifications"></i>
        <i class="icon-filter" @click="toggleFilterPanel"></i>
      </div>
    </header>

    <!-- 任务过滤器 -->
    <div class="task-filter">
      <div class="filter-buttons">
        <button 
          v-for="(filter, index) in filters" 
          :key="index"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="handleFilterChange(filter.value)"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- 高级筛选面板 -->
    <div class="advanced-filter-panel" v-if="showFilterPanel">
      <div class="filter-panel-header">
        <h3>筛选条件</h3>
        <i class="icon-times" @click="toggleFilterPanel"></i>
      </div>
      <div class="filter-form">
        <div class="filter-item">
          <label>任务类型</label>
          <div class="task-type-filter">
            <div 
              v-for="(type, index) in taskTypes" 
              :key="index"
              :class="['type-option', { selected: selectedTypes.includes(type.value) }]"
              @click="toggleTypeFilter(type.value)"
            >
              <i :class="['type-icon', type.icon]"></i>
              <span>{{ type.name }}</span>
            </div>
          </div>
        </div>
        
        <div class="filter-item">
          <label>时间范围</label>
          <div class="date-range">
            <input 
              type="date" 
              v-model="dateRange.start" 
              placeholder="开始日期"
            >
            <span class="date-separator">至</span>
            <input 
              type="date" 
              v-model="dateRange.end" 
              placeholder="结束日期"
            >
          </div>
        </div>
        
        <div class="filter-item">
          <label>优先级</label>
          <div class="priority-filter">
            <div 
              v-for="(priority, index) in priorities" 
              :key="index"
              :class="['priority-option', { selected: selectedPriorities.includes(priority.value) }]"
              @click="togglePriorityFilter(priority.value)"
            >
              <span class="priority-dot" :style="{ backgroundColor: priority.color }"></span>
              <span>{{ priority.name }}</span>
            </div>
          </div>
        </div>
        
        <!-- 工程师筛选 (仅管理员可见) -->
        <div class="filter-item" v-if="isAdmin">
          <label>工程师</label>
          <select v-model="selectedEngineer" class="engineer-select">
            <option value="">所有工程师</option>
            <option v-for="(engineer, index) in engineers" :key="index" :value="engineer.id">
              {{ engineer.name }}
            </option>
          </select>
        </div>
        
        <div class="filter-actions">
          <button class="reset-btn" @click="resetFilters">重置</button>
          <button class="apply-btn" @click="applyFilters">应用</button>
        </div>
      </div>
    </div>

    <!-- 任务列表 -->
    <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh" class="task-list-container">
      <div class="task-list">
        <div v-if="loading" class="loading-container">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">加载中...</span>
          </div>
          <p>加载中...</p>
        </div>
        
        <template v-else-if="filteredTasks && filteredTasks.length > 0">
          <div class="task-count">找到 {{ filteredTasks.length }} 个任务</div>
          <TaskCard 
            v-for="(task, index) in filteredTasks" 
            :key="task.taskId || index" 
            :task="task"
            @click="goToTaskDetail(task.taskId)"
            @call="callEngineer"
            @message="messageEngineer"
          />
        </template>

        <div v-else-if="error" class="error-state">
          <i class="icon-error"></i>
          <p>{{ error }}</p>
          <button class="btn btn-primary mt-3" @click="retryLoading">重试</button>
        </div>

        <div v-else class="empty-state">
          <i class="icon-empty-task"></i>
          <p>暂无{{ getFilterLabel() }}任务</p>
          <p class="pull-to-refresh-tip">下拉刷新获取最新任务</p>
        </div>
      </div>
    </van-pull-refresh>

    <!-- 悬浮操作按钮 - 根据角色权限显示 -->
    <div class="floating-action" v-if="canCreateTask">
      <router-link to="/task/create" class="fab-button">
        <i class="fas fa-plus"></i>
      </router-link>
    </div>

    <!-- 底部导航 -->
    <BottomNavigation />

    <!-- 缓存状态提示 -->
    <div v-if="showCacheStatus && taskStore.cacheLoading" class="cache-status-toast">
      <div class="toast-content">
        <div class="spinner-small"></div>
        <span>正在预加载任务数据，优化您的浏览体验...</span>
        <button class="close-toast" @click="hideCacheStatus">×</button>
      </div>
    </div>
    
    <!-- 缓存完成提示 -->
    <div v-if="showCacheStatus && !taskStore.cacheLoading && taskStore.isCacheLoaded" class="cache-status-toast success">
      <div class="toast-content">
        <i class="icon-check"></i>
        <span>已预加载 {{ taskStore.cacheCount }} 个任务详情，切换任务将更加流畅</span>
        <button class="close-toast" @click="hideCacheStatus">×</button>
      </div>
    </div>
  </div>
</template>

<script>
import BottomNavigation from '../components/BottomNavigation.vue'
import TaskCard from '../components/TaskCard.vue'
import { useRouter } from 'vue-router'
import { onMounted, ref, watch, computed } from 'vue'
import { useTaskStore } from '../stores/task'
import { useUserStore } from '../stores/user'
import { useAuthStore } from '../stores/auth'
import { PullRefresh as VanPullRefresh } from 'vant'

export default {
  name: 'HomePage',
  components: {
    BottomNavigation,
    TaskCard,
    VanPullRefresh
  },
  setup() {
    const router = useRouter()
    const taskStore = useTaskStore()
    const userStore = useUserStore()
    const authStore = useAuthStore()
    const activeFilter = ref('all')
    const isRefreshing = ref(false)
    const engineers = ref([])
    const selectedEngineer = ref('')
    const loading = ref(false)
    const error = ref(null)
    
    // 筛选面板状态
    const showFilterPanel = ref(false)
    const selectedTypes = ref([])
    const selectedPriorities = ref([])
    const dateRange = ref({
      start: '',
      end: ''
    })
    
    const filters = [
      { label: '全部', value: 'all' },
      { label: '待处理', value: 'pending' },
      { label: '进行中', value: 'in-progress' },
      { label: '已完成', value: 'completed' }
    ]
    
    const taskTypes = [
      { name: '维修', value: 'repair', icon: 'icon-wrench' },
      { name: '保养', value: 'maintenance', icon: 'icon-tools' },
      { name: '安装', value: 'installation', icon: 'icon-box-open' },
      { name: '培训', value: 'training', icon: 'icon-chalkboard-teacher' },
      { name: '验证', value: 'verification', icon: 'icon-clipboard-check' },
      { name: '选型', value: 'selection', icon: 'icon-search' },
      { name: '租赁', value: 'leasing', icon: 'icon-hand-holding-usd' },
      { name: '回收', value: 'recycle', icon: 'icon-recycle' }
    ]
    
    const priorities = [
      { name: '低', value: 'low', color: '#60a5fa' },
      { name: '中', value: 'normal', color: '#fbbf24' },
      { name: '高', value: 'high', color: '#ef4444' }
    ]

    // 判断用户角色
    const isAdmin = computed(() => {
      try {
        return authStore.hasRole('ROLE_ADMIN');
      } catch (err) {
        console.error('检查管理员角色失败:', err);
        return false;
      }
    })
    
    const isEngineer = computed(() => {
      try {
        return authStore.hasRole('ROLE_ENGINEER');
      } catch (err) {
        console.error('检查工程师角色失败:', err);
        return false;
      }
    })
    
    const isSales = computed(() => {
      try {
        return authStore.hasRole('ROLE_CUSTOMER');
      } catch (err) {
        console.error('检查销售角色失败:', err);
        return false;
      }
    })
    
    const isWarehouse = computed(() => {
      try {
        return authStore.hasRole('ROLE_WAREHOUSE');
      } catch (err) {
        console.error('检查仓库角色失败:', err);
        return false;
      }
    })
    
    // 根据角色计算是否可以创建任务
    const canCreateTask = computed(() => {
      return isAdmin.value || isEngineer.value
    })
    
    // 缓存加载状态显示
    const showCacheStatus = ref(true)
    const hideCacheStatus = () => {
      showCacheStatus.value = false
    }
    
    // 切换筛选面板
    const toggleFilterPanel = () => {
      showFilterPanel.value = !showFilterPanel.value
    }
    
    // 切换任务类型筛选
    const toggleTypeFilter = (type) => {
      if (selectedTypes.value.includes(type)) {
        selectedTypes.value = selectedTypes.value.filter(t => t !== type)
      } else {
        selectedTypes.value.push(type)
      }
    }
    
    // 切换优先级筛选
    const togglePriorityFilter = (priority) => {
      if (selectedPriorities.value.includes(priority)) {
        selectedPriorities.value = selectedPriorities.value.filter(p => p !== priority)
      } else {
        selectedPriorities.value.push(priority)
      }
    }
    
    // 重置筛选条件
    const resetFilters = () => {
      selectedTypes.value = []
      selectedPriorities.value = []
      dateRange.value = { start: '', end: '' }
      selectedEngineer.value = ''
    }
    
    // 应用筛选条件
    const applyFilters = () => {
      loadTasks()
      toggleFilterPanel()
    }
    
    // 加载任务列表
    const loadTasks = async () => {
      loading.value = true
      error.value = null
      
      const params = {
        page: 1,
        size: 20
      }
      
      if (activeFilter.value !== 'all') {
        params.status = activeFilter.value;
      }
      
      if (selectedTypes.value.length > 0) {
        params.taskTypes = selectedTypes.value;
      }
      
      if (selectedPriorities.value.length > 0) {
        params.priorities = selectedPriorities.value;
      }
      
      if (dateRange.value.start) {
        params.startDate = dateRange.value.start;
      }
      
      if (dateRange.value.end) {
        params.endDate = dateRange.value.end;
      }
      
      if (selectedEngineer.value) {
        params.engineerId = selectedEngineer.value;
      }
      
      try {
        const result = await taskStore.fetchTasks(params)
        
        if (isAdmin.value && engineers.value.length === 0) {
          loadEngineers()
        }
        
        error.value = taskStore.error
      } catch (err) {
        error.value = err.message || '加载任务失败'
      } finally {
        loading.value = false
      }
    }
    
    // 加载工程师列表
    const loadEngineers = async () => {
      try {
        const response = await taskStore.getEngineersList()
        if (response && response.code === 200) {
          engineers.value = response.data || []
        }
      } catch (err) {
        console.error('加载工程师列表失败:', err)
      }
    }
    
    // 首次加载
    onMounted(async () => {
      await loadTasks()
      
      try {
        if (!taskStore.isCacheLoaded) {
          await taskStore.preloadUserTaskDetails()
          setTimeout(() => {
            hideCacheStatus()
          }, 5000)
        } else {
          showCacheStatus.value = false
        }
      } catch (err) {
        showCacheStatus.value = false
      }
    })
    
    // 根据角色和筛选条件过滤任务列表
    const filteredTasks = computed(() => {
      const allTasks = taskStore.tasks || [];
      
      // 如果没有任务，直接返回空数组
      if (allTasks.length === 0) {
        return [];
      }
      
      // 根据activeFilter过滤
      let filtered = allTasks;
      
      // 如果不是全部，则根据状态过滤
      if (activeFilter.value !== 'all') {
        filtered = filtered.filter(task => task.status === activeFilter.value);
      }
      
      return filtered;
    })
    
    // 监听过滤器变化
    const handleFilterChange = (filter) => {
      activeFilter.value = filter
      loadTasks()
    }
    
    // 重试加载
    const retryLoading = () => {
      loadTasks()
    }
    
    // 刷新数据
    const onRefresh = async () => {
      await loadTasks()
      isRefreshing.value = false
    }
    
    // 跳转到任务详情
    const goToTaskDetail = (taskId) => {
      if (!taskId) {
        console.error('任务ID为空，无法跳转到详情页');
        return;
      }
      
      const normalizedTaskId = String(taskId).trim();
      console.log('准备跳转到任务详情页，任务ID:', normalizedTaskId);
      
      // 使用路由对象跳转
      router.push({
        name: 'TaskDetail',
        params: { id: normalizedTaskId }
      }).then(() => {
        console.log('路由跳转成功，新路径:', router.currentRoute.value.fullPath);
      }).catch(err => {
        console.error('路由跳转失败:', err);
      });
    }
    
    // 跳转到通知页面
    const goToNotifications = () => {
      router.push('/notifications');
    }

    // 呼叫工程师
    const callEngineer = (task) => {
      if (task.engineers && task.engineers.length > 0) {
        const engineer = task.engineers[0]
        console.log('呼叫工程师:', engineer.name)
        window.location.href = `tel:${engineer.mobile}`
      }
    }
    
    // 发送消息给工程师
    const messageEngineer = (task) => {
      if (task.engineers && task.engineers.length > 0) {
        const engineer = task.engineers[0]
        console.log('发送消息给工程师:', engineer.name)
        router.push(`/message/engineer/${engineer.id}`)
      }
    }
    
    // 获取过滤器标签文本
    const getFilterLabel = () => {
      const filter = filters.find(f => f.value === activeFilter.value)
      return filter ? filter.label : '全部'
    }
    
    return {
      activeFilter,
      filters,
      showFilterPanel,
      selectedTypes,
      selectedPriorities,
      dateRange,
      taskTypes,
      priorities,
      engineers,
      selectedEngineer,
      isRefreshing,
      showCacheStatus,
      taskStore,
      loading,
      error,
      filteredTasks,
      isAdmin,
      isEngineer,
      isSales,
      isWarehouse,
      canCreateTask,
      handleFilterChange,
      toggleFilterPanel,
      toggleTypeFilter,
      togglePriorityFilter,
      resetFilters,
      applyFilters,
      loadTasks,
      retryLoading,
      onRefresh,
      goToTaskDetail,
      goToNotifications,
      callEngineer,
      messageEngineer,
      getFilterLabel,
      hideCacheStatus
    }
  }
}
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.header-icons {
  display: flex;
  gap: 16px;
}

.header-icons i {
  font-size: 20px;
  color: #333;
  cursor: pointer;
}

/* 任务过滤器 */
.task-filter {
  padding: 12px 16px;
  background-color: #ffffff;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.filter-buttons {
  display: flex;
  gap: 8px;
}

.filter-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 16px;
  background-color: #f0f2f5;
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.2s;
}

.filter-btn.active {
  background-color: #1890ff;
  color: white;
}

/* 高级筛选面板 */
.advanced-filter-panel {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 80%;
  max-width: 320px;
  background-color: white;
  z-index: 1000;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  transform: translateX(0);
  transition: transform 0.3s ease;
}

.filter-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.filter-panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.filter-panel-header i {
  font-size: 18px;
  cursor: pointer;
}

.filter-form {
  padding: 16px;
}

.filter-item {
  margin-bottom: 20px;
}

.filter-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

/* 任务类型筛选 */
.task-type-filter {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.type-option {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: #f5f7fa;
  cursor: pointer;
}

.type-option.selected {
  background-color: #e6f7ff;
  border: 1px solid #1890ff;
}

.type-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #666;
}

.selected .type-icon {
  color: #1890ff;
}

/* 日期范围 */
.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-range input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.date-separator {
  color: #666;
}

/* 优先级筛选 */
.priority-filter {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.priority-option {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: #f5f7fa;
  cursor: pointer;
}

.priority-option.selected {
  background-color: #e6f7ff;
  border: 1px solid #1890ff;
}

.priority-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 8px;
}

/* 工程师选择 */
.engineer-select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
}

/* 筛选按钮 */
.filter-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
}

.reset-btn, .apply-btn {
  padding: 10px 16px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.reset-btn {
  background-color: #f0f2f5;
  border: 1px solid #d9d9d9;
  color: #666;
}

.apply-btn {
  background-color: #1890ff;
  border: none;
  color: white;
}

/* 任务列表 */
.task-list-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 加载和空状态 */
.loading-container, .empty-state, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  text-align: center;
  color: #666;
}

.spinner-border {
  width: 24px;
  height: 24px;
  border: 3px solid #1890ff;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state i, .error-state i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #d9d9d9;
}

.pull-to-refresh-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}

/* 悬浮操作按钮 */
.floating-action {
  position: fixed;
  right: 24px;
  bottom: 80px;
  z-index: 5;
}

.fab-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #1890ff;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  text-decoration: none;
}

.fab-button i {
  font-size: 24px;
}

/* 缓存状态提示 */
.cache-status-toast {
  position: fixed;
  bottom: 70px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  width: 90%;
  max-width: 400px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cache-status-toast.success {
  background-color: rgba(82, 196, 26, 0.9);
}

.toast-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
}

.spinner-small {
  width: 16px;
  height: 16px;
  border: 2px solid white;
  border-right-color: transparent;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 12px;
}

.icon-check {
  margin-right: 12px;
}

.close-toast {
  margin-left: auto;
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  padding: 0 4px;
}
</style> 