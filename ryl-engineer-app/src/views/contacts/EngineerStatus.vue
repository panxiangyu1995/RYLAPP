<template>
  <div class="engineer-status-page">
    <!-- 内容过滤器 -->
    <div class="filter-bar">
      <div class="filter-buttons">
        <button 
          v-for="(filter, index) in filters" 
          :key="index"
          :class="['filter-btn', { active: activeFilter === filter.value }]"
          @click="activeFilter = filter.value"
        >
          {{ filter.label }}
        </button>
      </div>
    </div>

    <!-- 工程师状态内容 -->
    <div class="engineers-container">
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="groupedEngineers.length === 0" class="no-data">
        <i class="icon-info-circle"></i>
        <p>暂无工程师数据</p>
      </div>
      
      <template v-else>
        <!-- 按工作地点分组显示 -->
        <div 
          v-for="(group, index) in groupedEngineers" 
          :key="index"
          class="location-group"
        >
          <div 
            class="location-header" 
            @click="toggleLocationGroup(group.location)"
          >
            <h3 class="location-name">
              {{ group.location }}
              <span class="engineer-count">({{ group.engineers.length }}人)</span>
            </h3>
            <i :class="['toggle-icon', expandedLocations.includes(group.location) ? 'icon-minus' : 'icon-plus']"></i>
          </div>
          
          <div v-show="expandedLocations.includes(group.location)" class="location-content">
            <table class="engineers-table">
              <thead>
                <tr>
                  <th>工程师</th>
                  <th>当前任务</th>
                  <th>任务数</th>
                  <th>可协助</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="(engineer, idx) in group.engineers" 
                  :key="idx"
                >
                  <td>
                    <a class="engineer-cell" @click="viewEngineerDetail(engineer.id)">
                      <img :src="engineer.avatar" :alt="engineer.name" class="engineer-avatar">
                      <span class="engineer-name">{{ engineer.name }}</span>
                    </a>
                  </td>
                  <td>{{ engineer.currentTask }}</td>
                  <td>{{ engineer.taskCount }}</td>
                  <td>
                    <span :class="['status-badge', getStatusClass(engineer.status)]">
                      {{ engineer.status }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部操作栏 -->
    <!-- 移除请求协助按钮 -->
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'EngineerStatusPage',
  setup() {
    const router = useRouter()
    const loading = ref(true)
    const activeFilter = ref('all')
    const engineers = ref([])
    const expandedLocations = ref([])
    
    const filters = [
      { label: '全部工程师', value: 'all' },
      { label: '可协助', value: 'available' },
      { label: '忙碌中', value: 'busy' }
    ]
    
    // 按工作地点分组的工程师
    const groupedEngineers = computed(() => {
      if (engineers.value.length === 0) return []
      
      // 先根据筛选条件过滤工程师
      let filteredEngineers = engineers.value
      if (activeFilter.value === 'available') {
        filteredEngineers = filteredEngineers.filter(engineer => 
          engineer.status === '可协助' || engineer.status === '部分可协'
        )
      } else if (activeFilter.value === 'busy') {
        filteredEngineers = filteredEngineers.filter(engineer => 
          engineer.status === '忙碌'
        )
      }
      
      // 按工作地点分组
      const groups = {}
      filteredEngineers.forEach(engineer => {
        if (!groups[engineer.location]) {
          groups[engineer.location] = []
        }
        groups[engineer.location].push(engineer)
      })
      
      // 转换为数组格式
      return Object.keys(groups).map(location => ({
        location,
        engineers: groups[location]
      }))
    })
    
    // 查看工程师详情
    const viewEngineerDetail = (engineerId) => {
      router.push(`/engineer/${engineerId}`)
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
    
    // 切换工作地点组的展开/折叠状态
    const toggleLocationGroup = (location) => {
      const index = expandedLocations.value.indexOf(location)
      if (index === -1) {
        expandedLocations.value.push(location)
      } else {
        expandedLocations.value.splice(index, 1)
      }
    }
    
    // 加载工程师数据
    const loadEngineers = () => {
      loading.value = true
      
      // 模拟API调用
      setTimeout(() => {
        engineers.value = [
          {
            id: 1,
            name: '张工程师',
            avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '气相维修',
            taskCount: 3,
            status: '忙碌',
            location: '泰州'
          },
          {
            id: 2,
            name: '李工程师',
            avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '液相保养',
            taskCount: 1,
            status: '可协助',
            location: '泰州'
          },
          {
            id: 3,
            name: '王工程师',
            avatar: 'https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '质谱培训',
            taskCount: 2,
            status: '部分可协',
            location: '苏州'
          },
          {
            id: 4,
            name: '刘工程师',
            avatar: 'https://images.unsplash.com/photo-1519244703995-f4e0f30006d5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '无任务',
            taskCount: 0,
            status: '可协助',
            location: '苏州'
          },
          {
            id: 5,
            name: '赵工程师',
            avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '色谱仪检修',
            taskCount: 2,
            status: '忙碌',
            location: '武汉'
          },
          {
            id: 6,
            name: '孙工程师',
            avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
            currentTask: '培训中',
            taskCount: 1,
            status: '部分可协',
            location: '四川'
          }
        ]
        
        // 默认展开所有分组
        expandedLocations.value = engineers.value
          .map(engineer => engineer.location)
          .filter((value, index, self) => self.indexOf(value) === index)
        
        loading.value = false
      }, 1000)
    }
    
    onMounted(() => {
      loadEngineers()
    })
    
    return {
      activeFilter,
      filters,
      groupedEngineers,
      expandedLocations,
      loading,
      getStatusClass,
      viewEngineerDetail,
      toggleLocationGroup
    }
  }
}
</script>

<style scoped>
.engineer-status-page {
  padding-bottom: 20px;
}

.filter-bar {
  background-color: #fff;
  padding: 16px;
}

.filter-buttons {
  display: flex;
  gap: 24px;
  overflow-x: auto;
}

.filter-btn {
  color: #6b7280;
  background: none;
  border: none;
  padding: 0;
  padding-bottom: 4px;
  white-space: nowrap;
}

.filter-btn.active {
  color: #3b82f6;
  font-weight: 500;
  border-bottom: 2px solid #3b82f6;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.spinner {
  border: 3px solid #f3f4f6;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #6b7280;
}

.icon-info-circle::before {
  content: '\f05a';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 24px;
  margin-bottom: 8px;
}

.location-group {
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.location-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f9fafb;
  cursor: pointer;
  border-bottom: 1px solid #f3f4f6;
}

.location-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.engineer-count {
  font-size: 14px;
  font-weight: normal;
  color: #6b7280;
}

.toggle-icon {
  color: #6b7280;
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-minus::before {
  content: '\f068';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.location-content {
  padding: 8px;
}

.engineers-container {
  padding: 12px;
}

.engineers-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #fff;
}

.engineers-table th {
  padding: 12px 8px;
  text-align: left;
  color: #6b7280;
  font-weight: 500;
  font-size: 14px;
  border-bottom: 1px solid #f3f4f6;
}

.engineers-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f3f4f6;
  font-size: 14px;
}

.engineers-table tr:last-child td {
  border-bottom: none;
}

.engineer-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.engineer-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.engineer-name {
  white-space: nowrap;
  font-weight: 500;
}

.status-badge {
  display: inline-block;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  text-align: center;
}

.status-available {
  background-color: #d1fae5;
  color: #047857;
}

.status-partial {
  background-color: #fef3c7;
  color: #92400e;
}

.status-busy {
  background-color: #fee2e2;
  color: #b91c1c;
}

/* 移除底部操作栏相关的CSS样式 */
</style> 