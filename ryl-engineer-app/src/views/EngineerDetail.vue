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

    <!-- 工程师基本信息 -->
    <div class="engineer-card">
      <img :src="engineer.avatar" alt="工程师头像" class="engineer-avatar">
      <div class="engineer-info">
        <h2 class="engineer-name">{{ engineer.name }}</h2>
        <p class="engineer-title">{{ engineer.title }}</p>
        <div class="engineer-status">
          <span :class="['status-badge', getStatusClass(engineer.status)]">
            {{ engineer.status }}
          </span>
          <span class="task-count">当前任务数: {{ engineer.currentTaskCount }}</span>
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
            {{ task.status }}
          </span>
        </div>
        
        <div class="task-progress">
          <div class="progress-header">
            <span class="progress-label">当前步骤:</span>
            <span class="progress-step">{{ task.currentStep }}</span>
          </div>
          <div class="progress-content">
            <p class="progress-description">{{ task.stepDescription }}</p>
            <div class="progress-images">
              <img 
                v-for="(image, i) in task.images" 
                :key="i"
                :src="image"
                alt="任务图片"
                class="progress-image"
              >
            </div>
            <div class="progress-meta">
              <span class="progress-time">{{ task.lastUpdateTime }}</span>
              <span class="progress-duration">用时: {{ task.duration }}</span>
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
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'EngineerDetailPage',
  setup() {
    const router = useRouter()

    const goBack = () => {
      router.back()
    }
    
    const contactEngineer = () => {
      // 导航到聊天详情页，传递工程师ID作为参数
      router.push(`/chat/${router.currentRoute.value.params.id || 1}`)
    }

    return {
      goBack,
      contactEngineer
    }
  },
  data() {
    return {
      engineer: {
        id: 1,
        name: '张工程师',
        avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80',
        title: '高级维修工程师 | 气相色谱专家',
        status: '忙碌',
        currentTaskCount: 3
      },
      activeFilter: 'in-progress',
      taskFilters: [
        { label: '进行中', value: 'in-progress' },
        { label: '已完成', value: 'completed' }
      ],
      tasks: [
        {
          taskId: 'T20231120-001',
          title: '某大学 - 气相色谱仪维修',
          status: '进行中',
          currentStep: '检修结果填写',
          stepDescription: '检查发现进样口密封圈老化，需要更换。同时色谱柱温度控制不稳定，怀疑是温控板故障。',
          images: [
            'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
            'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ],
          lastUpdateTime: '2023-11-20 14:30',
          duration: '1小时20分钟',
          type: 'in-progress'
        },
        {
          taskId: 'T20231119-003',
          title: '某医院 - 气质联用仪保养',
          status: '进行中',
          currentStep: '真空系统检测',
          stepDescription: '真空度测试正常，分子泵运转良好，无异常噪音。前级泵油已更换，真空管路已清洁。',
          images: [
            'https://images.unsplash.com/photo-1581093196277-9f608bb3b4b9?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ],
          lastUpdateTime: '2023-11-20 10:15',
          duration: '45分钟',
          type: 'in-progress'
        },
        {
          taskId: 'T20231118-005',
          title: '某研究所 - 气相色谱仪安装培训',
          status: '进行中',
          currentStep: '软件操作培训',
          stepDescription: '已完成基础操作培训，客户能够独立进行简单样品分析。明天将进行高级功能培训和故障排除指导。',
          images: [
            'https://images.unsplash.com/photo-1581094794329-c8112a89af12?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
            'https://images.unsplash.com/photo-1581094794329-c8112a89af12?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
            'https://images.unsplash.com/photo-1581094794329-c8112a89af12?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ],
          lastUpdateTime: '2023-11-20 16:45',
          duration: '3小时30分钟',
          type: 'in-progress'
        },
        {
          taskId: 'T20231115-002',
          title: '某企业 - 液相色谱仪维修',
          status: '已完成',
          currentStep: '完成维修',
          stepDescription: '更换高压泵密封圈，清洗进样阀，校准检测器。设备已恢复正常运行。',
          images: [
            'https://images.unsplash.com/photo-1581094794329-c8112a89af12?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ],
          lastUpdateTime: '2023-11-15 15:30',
          duration: '2小时15分钟',
          type: 'completed'
        }
      ]
    }
  },
  computed: {
    filteredTasks() {
      return this.tasks.filter(task => task.type === this.activeFilter);
    }
  },
  methods: {
    getStatusClass(status) {
      switch(status) {
        case '忙碌': return 'status-busy';
        case '空闲': return 'status-free';
        case '休假': return 'status-vacation';
        default: return '';
      }
    },
    getTaskStatusClass(status) {
      switch(status) {
        case '进行中': return 'status-in-progress';
        case '已完成': return 'status-completed';
        case '待处理': return 'status-pending';
        default: return '';
      }
    },
    viewTaskFlow(taskId) {
      console.log('查看任务流程:', taskId);
      this.$router.push(`/task-flow/${taskId}`);
    },
    requestAssistance() {
      // 导航到请求协助页面，传递工程师ID和当前任务信息
      this.$router.push({
        path: `/request-assistance/${this.engineer.id}`,
        query: {
          engineerName: this.engineer.name,
          taskCount: this.engineer.currentTaskCount
        }
      });
    }
  }
}
</script>

<style scoped>
.engineer-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
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

.contact-btn {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #3b82f6;
  color: #fff;
  border-radius: 9999px;
  font-size: 14px;
  border: none;
}

.icon-chat::before {
  content: '\f075';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.engineer-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  border-bottom: 1px solid #e5e7eb;
}

.engineer-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 16px;
}

.engineer-info {
  flex: 1;
}

.engineer-name {
  font-size: 18px;
  font-weight: 600;
}

.engineer-title {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.engineer-status {
  display: flex;
  align-items: center;
  margin-top: 4px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: 12px;
  margin-right: 8px;
}

.status-busy {
  background-color: #fee2e2;
  color: #dc2626;
}

.status-free {
  background-color: #d1fae5;
  color: #059669;
}

.status-vacation {
  background-color: #dbeafe;
  color: #3b82f6;
}

.task-count {
  font-size: 12px;
  color: #6b7280;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  margin-top: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.task-header h3 {
  font-size: 16px;
  font-weight: 500;
}

.task-filters {
  display: flex;
  gap: 8px;
}

.filter-btn {
  font-size: 14px;
  color: #6b7280;
  background: none;
  border: none;
  padding: 0;
}

.filter-btn.active {
  color: #3b82f6;
  font-weight: 500;
  border-bottom: 2px solid #3b82f6;
}

.task-list {
  background-color: #fff;
}

.task-item {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.task-header-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.task-title {
  font-size: 16px;
  font-weight: 500;
}

.task-id {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.task-status {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
}

.status-in-progress {
  background-color: #fef3c7;
  color: #d97706;
}

.status-completed {
  background-color: #d1fae5;
  color: #059669;
}

.status-pending {
  background-color: #dbeafe;
  color: #3b82f6;
}

.task-progress {
  margin-top: 12px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}

.progress-label {
  color: #6b7280;
}

.progress-step {
  font-weight: 500;
}

.progress-content {
  background-color: #f3f4f6;
  padding: 12px;
  border-radius: 8px;
}

.progress-description {
  font-size: 14px;
}

.progress-images {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
  margin-top: 8px;
}

.progress-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.view-flow-link {
  display: flex;
  align-items: center;
  color: #3b82f6;
  font-size: 14px;
  margin-top: 8px;
  cursor: pointer;
}

.icon-list::before {
  content: '\f03a';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
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
  display: flex;
  gap: 16px;
}

.bottom-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border: none;
}

.bottom-btn.primary {
  background-color: #3b82f6;
  color: #fff;
}

.bottom-btn.secondary {
  background-color: #f3f4f6;
  color: #6b7280;
}

.icon-user-plus::before {
  content: '\f234';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.icon-calendar::before {
  content: '\f133';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}
</style> 