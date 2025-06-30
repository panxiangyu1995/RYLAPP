<template>
  <div class="status-card" :class="getStatusClass(task.status)">
    <div class="card-header">
      <h3 class="card-title">
        <i class="fas fa-certificate"></i>
        任务状态
      </h3>
    </div>
    
    <div class="status-content">
      <div class="current-status">
        <div class="status-badge" :class="getStatusClass(task.status)">
          <i :class="getStatusIcon(task.status)"></i>
          <span class="status-text">{{ getStatusText(task.status) }}</span>
        </div>
        <div class="status-info">
          <p class="last-updated" v-if="task.statusUpdateTime">
            最后更新: {{ formatDate(task.statusUpdateTime) }}
          </p>
        </div>
        <div v-if="canChangeStatus" class="status-actions">
          <button class="change-status-btn" @click="showChangeStatusDialog">
            更改状态
          </button>
        </div>
      </div>
      
      <div class="status-history" v-if="statusHistory && statusHistory.length > 0">
        <h4 class="history-title">状态历史</h4>
        <div class="history-list">
          <div 
            v-for="(history, index) in visibleStatusHistory" 
            :key="index"
            class="history-item"
          >
            <div class="history-status" :class="getStatusChipClass(history.status)">
              {{ getStatusText(history.status) }}
            </div>
            <div class="history-info">
              <div class="history-time">{{ formatDateTime(history.timestamp) }}</div>
              <div class="history-user">由 {{ history.updatedByName || history.updatedBy }} 更新</div>
            </div>
            <div class="history-comment" v-if="history.comment">
              <i class="fas fa-comment"></i>
              <span>{{ history.comment }}</span>
            </div>
          </div>
          
          <div v-if="statusHistory.length > maxHistoryItems" class="more-history">
            <button @click="showAllHistory" class="more-btn">
              查看更多历史记录 ({{ statusHistory.length - maxHistoryItems }})
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskStatusCard',
  props: {
    task: {
      type: Object,
      required: true
    },
    statusHistory: {
      type: Array,
      default: () => []
    },
    canChangeStatus: {
      type: Boolean,
      default: false
    }
  },
  emits: ['change-status', 'view-history'],
  setup(props, { emit }) {
    const maxHistoryItems = ref(3) // 默认显示历史记录数量
    
    // 可见的历史记录
    const visibleStatusHistory = computed(() => {
      return props.statusHistory.slice(0, maxHistoryItems.value)
    })
    
    // 监听任务状态变化
    watch(() => props.task.status, (newStatus, oldStatus) => {
      if (newStatus !== oldStatus) {
        console.log(`TaskStatusCard: 状态已更新 - ${getStatusText(oldStatus)} → ${getStatusText(newStatus)}`);
      }
    });
    
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
    
    // 获取状态徽章样式类
    const getStatusChipClass = (status) => {
      switch(status) {
        case 'pending': return 'chip-pending'
        case 'in-progress': return 'chip-progress'
        case 'completed': return 'chip-completed'
        case 'cancelled': return 'chip-cancelled'
        case 'waiting-parts': return 'chip-waiting'
        case 'client-review': return 'chip-review'
        default: return ''
      }
    }
    
    // 获取任务状态图标
    const getStatusIcon = (status) => {
      switch(status) {
        case 'pending': return 'fas fa-clock'
        case 'in-progress': return 'fas fa-play-circle'
        case 'completed': return 'fas fa-check-circle'
        case 'cancelled': return 'fas fa-ban'
        case 'waiting-parts': return 'fas fa-truck'
        case 'client-review': return 'fas fa-user-check'
        default: return 'fas fa-question-circle'
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch(status) {
        case 'pending': return '待处理'
        case 'in-progress': return '进行中'
        case 'completed': return '已完成'
        case 'cancelled': return '已取消'
        case 'waiting-parts': return '等待配件'
        case 'client-review': return '客户确认中'
        default: return '未知状态'
      }
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '无'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd')
      } catch (err) {
        console.error('日期格式化错误:', err)
        return dateStr
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (dateStr) => {
      if (!dateStr) return '无'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd HH:mm')
      } catch (err) {
        console.error('日期时间格式化错误:', err)
        return dateStr
      }
    }
    
    // 显示状态变更对话框
    const showChangeStatusDialog = () => {
      emit('change-status')
    }
    
    // 显示所有历史记录
    const showAllHistory = () => {
      maxHistoryItems.value = props.statusHistory.length
    }
    
    return {
      maxHistoryItems,
      visibleStatusHistory,
      getStatusClass,
      getStatusChipClass,
      getStatusIcon,
      getStatusText,
      formatDate,
      formatDateTime,
      showChangeStatusDialog,
      showAllHistory
    }
  }
}
</script>

<style scoped>
.status-card {
  background-color: #fff;
  margin: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.card-title i {
  margin-right: 8px;
  color: #666;
}

.status-content {
  padding: 16px;
}

.current-status {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.status-badge {
  display: flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.status-badge i {
  margin-right: 6px;
}

.status-pending {
  background-color: #e6f2ff;
  color: #1a73e8;
}

.status-progress {
  background-color: #fef3c7;
  color: #92400e;
}

.status-completed {
  background-color: #d1fae5;
  color: #065f46;
}

.status-cancelled {
  background-color: #fee2e2;
  color: #b91c1c;
}

.status-waiting {
  background-color: #e9dff8;
  color: #6d28d9;
}

.status-review {
  background-color: #ffedd5;
  color: #c2410c;
}

.status-info {
  flex: 1;
  padding: 0 16px;
}

.last-updated {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.status-actions {
  display: flex;
  justify-content: flex-end;
}

.change-status-btn {
  padding: 6px 12px;
  border-radius: 6px;
  background-color: #f3f4f6;
  color: #374151;
  border: 1px solid #e5e7eb;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.change-status-btn:hover {
  background-color: #e5e7eb;
}

.history-title {
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  margin: 16px 0 12px;
}

.history-list {
  background-color: #f9fafb;
  border-radius: 8px;
  overflow: hidden;
}

.history-item {
  padding: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.history-item:last-child {
  border-bottom: none;
}

.history-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 6px;
}

.chip-pending {
  background-color: #e6f2ff;
  color: #1a73e8;
}

.chip-progress {
  background-color: #fef3c7;
  color: #92400e;
}

.chip-completed {
  background-color: #d1fae5;
  color: #065f46;
}

.chip-cancelled {
  background-color: #fee2e2;
  color: #b91c1c;
}

.chip-waiting {
  background-color: #e9dff8;
  color: #6d28d9;
}

.chip-review {
  background-color: #ffedd5;
  color: #c2410c;
}

.history-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.history-comment {
  font-size: 13px;
  color: #4b5563;
  margin-top: 6px;
  display: flex;
  align-items: flex-start;
}

.history-comment i {
  font-size: 12px;
  margin-right: 5px;
  margin-top: 2px;
}

.more-history {
  padding: 10px;
  text-align: center;
}

.more-btn {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 13px;
  cursor: pointer;
}

.more-btn:hover {
  text-decoration: underline;
}
</style> 