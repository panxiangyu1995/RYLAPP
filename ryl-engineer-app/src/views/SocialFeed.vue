<template>
  <div class="social-feed-page">
    <!-- 顶部导航 -->
    <header class="header">
      <h1>工程师状态一览</h1>
      <div class="header-icons">
        <i class="icon-search"></i>
        <i class="icon-filter"></i>
      </div>
    </header>

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
    <div class="engineers-table">
      <table>
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
            v-for="(engineer, index) in filteredEngineers" 
            :key="index"
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

    <!-- 底部操作栏 -->
    <div class="bottom-actions">
      <button class="request-btn" @click="requestAssistance">
        <i class="icon-user-plus"></i>请求协助
      </button>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'SocialFeedPage',
  setup() {
    const router = useRouter()

    const viewEngineerDetail = (engineerId) => {
      router.push(`/engineer/${engineerId}`)
    }

    return {
      viewEngineerDetail
    }
  },
  data() {
    return {
      activeFilter: 'all',
      filters: [
        { label: '全部工程师', value: 'all' },
        { label: '可协助', value: 'available' },
        { label: '忙碌中', value: 'busy' }
      ],
      engineers: [
        {
          id: 1,
          name: '张工程师',
          avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '气相维修',
          taskCount: 3,
          status: '忙碌'
        },
        {
          id: 2,
          name: '李工程师',
          avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '液相保养',
          taskCount: 1,
          status: '可协助'
        },
        {
          id: 3,
          name: '王工程师',
          avatar: 'https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '质谱培训',
          taskCount: 2,
          status: '部分可协'
        },
        {
          id: 4,
          name: '刘工程师',
          avatar: 'https://images.unsplash.com/photo-1519244703995-f4e0f30006d5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '无任务',
          taskCount: 0,
          status: '可协助'
        },
        {
          id: 5,
          name: '赵工程师',
          avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '色谱仪检修',
          taskCount: 2,
          status: '忙碌'
        },
        {
          id: 6,
          name: '孙工程师',
          avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
          currentTask: '培训中',
          taskCount: 1,
          status: '部分可协'
        }
      ]
    }
  },
  computed: {
    filteredEngineers() {
      if (this.activeFilter === 'all') {
        return this.engineers;
      } else if (this.activeFilter === 'available') {
        return this.engineers.filter(engineer => 
          engineer.status === '可协助' || engineer.status === '部分可协'
        );
      } else if (this.activeFilter === 'busy') {
        return this.engineers.filter(engineer => 
          engineer.status === '忙碌'
        );
      }
      return this.engineers;
    }
  },
  methods: {
    getStatusClass(status) {
      switch(status) {
        case '忙碌': return 'status-busy';
        case '可协助': return 'status-available';
        case '部分可协': return 'status-partial';
        default: return '';
      }
    },
    requestAssistance() {
      console.log('请求协助');
      // 后续实现请求协助功能
    }
  }
}
</script>

<style scoped>
.social-feed-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
}

.header-icons {
  display: flex;
  gap: 16px;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.icon-filter::before {
  content: '\f0b0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.filter-bar {
  background-color: #fff;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
  border-bottom: 1px solid #f3f4f6;
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

.engineers-table {
  background-color: #fff;
  margin-top: 8px;
  padding: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  font-size: 12px;
  color: #6b7280;
  text-align: left;
}

th {
  padding-bottom: 8px;
  font-weight: 500;
}

tbody tr {
  border-top: 1px solid #f3f4f6;
}

td {
  padding: 8px 0;
  font-size: 14px;
}

.engineer-cell {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.engineer-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 8px;
}

.engineer-name {
  font-size: 14px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
  white-space: nowrap;
}

.status-busy {
  background-color: #fee2e2;
  color: #dc2626;
}

.status-available {
  background-color: #d1fae5;
  color: #059669;
}

.status-partial {
  background-color: #fef3c7;
  color: #d97706;
}

.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
  padding: 16px;
  padding-bottom: calc(16px + 56px); /* 增加底部padding，防止被导航栏遮挡 */
}

.request-btn {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-user-plus::before {
  content: '\f234';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}
</style> 