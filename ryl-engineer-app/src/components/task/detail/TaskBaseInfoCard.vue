<template>
  <div class="task-info-card">
    <div class="task-header">
      <div class="task-title-container">
        <h2 class="task-title">{{ task.title }}</h2>
        <p class="task-id">任务编号：{{ task.taskId }}</p>
      </div>
      <div class="task-badges">
        <span class="task-type-badge" :class="`type-${task.taskType}`">
          <i :class="getTaskTypeIcon(task.taskType)"></i>
          {{ taskTypeNames[task.taskType] || '未知类型' }}
        </span>
        <span v-if="task.priority" class="priority-badge" :class="`priority-${task.priority}`">
          {{ getPriorityText(task.priority) }}
        </span>
      </div>
    </div>
    
    <!-- 客户描述内容 -->
    <div class="customer-description" v-if="task.customerDescription">
      <h3 class="section-subtitle">客户描述</h3>
      <div class="description-content">
        {{ task.customerDescription }}
      </div>
    </div>
    
    <div class="task-times">
      <div class="detail-item">
        <i class="fas fa-calendar-plus"></i>
        <span>创建时间：{{ formatDate(task.createTime) }}</span>
      </div>
      <div class="detail-item">
        <i class="fas fa-calendar"></i>
        <span>开始日期：{{ formatDate(task.startDate) }}</span>
      </div>
      <div class="detail-item" v-if="task.deadline">
        <i class="fas fa-clock"></i>
        <span>预计截止：{{ formatDate(task.deadline) }}</span>
        <span v-if="isDeadlineApproaching(task.deadline)" class="deadline-warning">即将到期</span>
      </div>
      <div class="detail-item" v-if="task.expectedCompletionTime">
        <i class="fas fa-calendar-check"></i>
        <span>预计完成时间：{{ formatDate(task.expectedCompletionTime) }}</span>
      </div>
      <div class="detail-item" v-if="task.completedDate">
        <i class="fas fa-check-circle"></i>
        <span>完成时间：{{ formatDate(task.completedDate) }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, watch } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskBaseInfoCard',
  props: {
    task: {
      type: Object,
      required: true
    },
    canChangeTaskStatus: {
      type: Boolean,
      default: false
    }
  },
  emits: ['change-status'],
  setup(props, { emit }) {
    // 任务类型名称映射
    const taskTypeNames = {
      repair: '仪器维修',
      maintenance: '仪器保养',
      recycle: '仪器回收',
      leasing: '仪器租赁',
      training: '培训预约',
      verification: '仪器验证',
      selection: '仪器选型',
      installation: '仪器安装'
    }
    
    // 获取任务状态对应的样式类
    const getStatusClass = (status) => {
      switch(status) {
        case 'pending': return 'status-pending'
        case 'in-progress': return 'status-progress'
        case 'completed': return 'status-completed'
        case 'cancelled': return 'status-cancelled'
        case 'waiting-parts': return 'status-waiting'
        case 'client-review': return 'status-review'
        default: return ''
      }
    }
    
    // 获取任务类型图标
    const getTaskTypeIcon = (taskType) => {
      switch(taskType) {
        case 'repair': return 'fas fa-tools'
        case 'maintenance': return 'fas fa-clipboard-check'
        case 'recycle': return 'fas fa-recycle'
        case 'leasing': return 'fas fa-key'
        case 'training': return 'fas fa-graduation-cap'
        case 'verification': return 'fas fa-check-square'
        case 'selection': return 'fas fa-shopping-cart'
        case 'installation': return 'fas fa-wrench'
        default: return 'fas fa-tasks'
      }
    }
    
    // 获取优先级文本
    const getPriorityText = (priority) => {
      switch(priority) {
        case 'high': return '高优先级'
        case 'medium': return '中等优先级'
        case 'low': return '低优先级'
        default: return '普通优先级'
      }
    }
    
    // 日期格式化函数
    const formatDate = (dateStr) => {
      if (!dateStr) return '无'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd HH:mm')
      } catch (err) {
        console.error('日期格式化错误:', err)
        return dateStr
      }
    }
    
    // 判断截止日期是否接近
    const isDeadlineApproaching = (deadline) => {
      if (!deadline) return false
      try {
        const deadlineDate = new Date(deadline)
        const now = new Date()
        const diffDays = Math.floor((deadlineDate - now) / (1000 * 60 * 60 * 24))
        return diffDays >= 0 && diffDays <= 3 // 3天内算接近截止
      } catch (err) {
        return false
      }
    }
    
    // 监听任务状态变化
    watch(() => props.task.status, (newStatus, oldStatus) => {
      if (newStatus !== oldStatus) {
        console.log(`任务状态从 ${oldStatus} 变为 ${newStatus}`);
      }
    });
    
    // 显示状态更改对话框
    const showStatusChangeDialog = () => {
      emit('change-status')
    }
    
    return {
      taskTypeNames,
      getStatusClass,
      getTaskTypeIcon,
      getPriorityText,
      formatDate,
      isDeadlineApproaching,
      showStatusChangeDialog
    }
  }
}
</script>

<style scoped>
.task-info-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.task-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.task-title-container {
  flex: 1;
}

.task-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.task-id {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.task-badges {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.task-type-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f3f4f6;
  color: #4b5563;
}

.task-type-badge i {
  margin-right: 4px;
  font-size: 12px;
}

.type-repair { background-color: #e6f2ff; color: #1a73e8; }
.type-maintenance { background-color: #e8fdf5; color: #03875f; }
.type-recycle { background-color: #e3f9e5; color: #1e8e3e; }
.type-leasing { background-color: #fff6e0; color: #ea8600; }
.type-training { background-color: #f2eefa; color: #673ab7; }
.type-verification { background-color: #ffebee; color: #c62828; }
.type-selection { background-color: #e8f0fe; color: #174ea6; }
.type-installation { background-color: #f9e4ff; color: #9c27b0; }

.priority-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.priority-high { background-color: #ffebee; color: #c62828; }
.priority-medium { background-color: #fff8e1; color: #f57c00; }
.priority-low { background-color: #e8f5e9; color: #2e7d32; }

.customer-description {
  margin-top: 12px;
  margin-bottom: 16px;
}

.section-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  margin-bottom: 8px;
}

.description-content {
  background-color: #f9f9f9;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.task-times {
  margin-top: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
}

.detail-item i {
  margin-right: 8px;
  width: 16px;
  text-align: center;
  color: #777;
}

.deadline-warning {
  margin-left: 8px;
  padding: 2px 6px;
  background-color: #ffebee;
  color: #c62828;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
}

@media (max-width: 480px) {
  .task-header {
    flex-direction: column;
  }
  
  .task-badges {
    margin-top: 8px;
    flex-direction: row;
    justify-content: flex-start;
    gap: 8px;
  }
}
</style> 