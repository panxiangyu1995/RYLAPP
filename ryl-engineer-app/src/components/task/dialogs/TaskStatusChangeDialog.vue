<template>
  <div class="status-dialog-overlay" @click.self="closeDialog"></div>
  <div class="status-dialog">
    <div class="dialog-header">
      <h3 class="dialog-title">更改任务状态</h3>
      <button class="close-btn" @click="closeDialog">
        <i class="icon-times"></i>
      </button>
    </div>
    
    <div class="dialog-content">
      <div class="current-status">
        <span class="label">当前状态:</span>
        <div class="status-badge" :class="getStatusClass(currentStatus)">
          {{ getStatusText(currentStatus) }}
        </div>
      </div>
      
      <div class="status-options">
        <div class="label">选择新状态:</div>
        <div class="options-grid">
          <div 
            v-for="status in availableStatuses" 
            :key="status.value"
            class="status-option"
            :class="{ 
              'selected': selectedStatus === status.value,
              'disabled': !status.allowed
            }"
            @click="selectStatus(status)"
          >
            <div class="option-icon" :class="getStatusIconClass(status.value)">
              <i :class="getStatusIcon(status.value)"></i>
            </div>
            <div class="option-text">
              <div class="option-name">{{ status.label }}</div>
              <div class="option-desc">{{ status.description }}</div>
            </div>
            <div class="option-check" v-if="selectedStatus === status.value">
              <i class="icon-check"></i>
            </div>
          </div>
        </div>
      </div>
      
      <div class="comment-section">
        <label for="status-comment" class="label">备注 (可选):</label>
        <textarea 
          id="status-comment"
          v-model="statusComment"
          placeholder="添加关于此次状态变更的备注信息..."
          class="comment-input"
          rows="3"
        ></textarea>
      </div>
    </div>
    
    <div class="dialog-actions">
      <button class="cancel-btn" @click="closeDialog">取消</button>
      <button 
        class="confirm-btn" 
        :disabled="!isValidSelection || isSubmitting" 
        @click="confirmStatusChange"
      >
        <i v-if="isSubmitting" class="icon-spinner icon-spin"></i>
        {{ isSubmitting ? '提交中...' : '确认更改' }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'TaskStatusChangeDialog',
  props: {
    currentStatus: {
      type: String,
      required: true
    },
    taskId: {
      type: [String, Number],
      required: true
    },
    allowedStatuses: {
      type: Array,
      default: () => ['pending', 'in-progress', 'completed', 'cancelled', 'waiting-parts', 'client-review']
    }
  },
  emits: ['close', 'status-change'],
  setup(props, { emit }) {
    const selectedStatus = ref('')
    const statusComment = ref('')
    const isSubmitting = ref(false)
    
    // 所有可能的状态选项
    const statusOptions = [
      {
        value: 'pending',
        label: '待处理',
        description: '任务已创建，但还未开始工作',
        icon: 'icon-clock',
        allowFrom: ['in-progress', 'waiting-parts', 'client-review']
      },
      {
        value: 'in-progress',
        label: '进行中',
        description: '任务正在处理中',
        icon: 'icon-play-circle',
        allowFrom: ['pending', 'waiting-parts', 'client-review']
      },
      {
        value: 'waiting-parts',
        label: '等待配件',
        description: '任务暂停，等待所需配件到货',
        icon: 'icon-truck',
        allowFrom: ['pending', 'in-progress']
      },
      {
        value: 'client-review',
        label: '客户确认中',
        description: '等待客户确认工作完成情况',
        icon: 'icon-user-check',
        allowFrom: ['in-progress', 'waiting-parts']
      },
      {
        value: 'completed',
        label: '已完成',
        description: '任务已成功完成',
        icon: 'icon-check-circle',
        allowFrom: ['in-progress', 'client-review']
      },
      {
        value: 'cancelled',
        label: '已取消',
        description: '任务已取消',
        icon: 'icon-ban',
        allowFrom: ['pending', 'waiting-parts']
      }
    ]
    
    // 根据当前状态过滤可用状态
    const availableStatuses = computed(() => {
      return statusOptions.map(status => {
        // 确定此状态是否允许从当前状态过渡
        const allowed = status.value !== props.currentStatus && 
                      (props.allowedStatuses.includes(status.value) && 
                       (status.allowFrom.includes(props.currentStatus) || 
                        props.currentStatus === ''));
        
        return {
          ...status,
          allowed
        };
      });
    })
    
    // 是否为有效选择
    const isValidSelection = computed(() => {
      if (!selectedStatus.value) return false
      
      const selectedOption = availableStatuses.value.find(
        status => status.value === selectedStatus.value
      )
      
      return selectedOption && selectedOption.allowed
    })
    
    // 获取状态文本
    const getStatusText = (status) => {
      const option = statusOptions.find(opt => opt.value === status)
      return option ? option.label : '未知状态'
    }
    
    // 获取状态CSS类
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
    
    // 获取状态图标
    const getStatusIcon = (status) => {
      const option = statusOptions.find(opt => opt.value === status)
      return option ? option.icon : 'icon-question-circle'
    }
    
    // 获取状态图标CSS类
    const getStatusIconClass = (status) => {
      switch(status) {
        case 'pending': return 'icon-pending'
        case 'in-progress': return 'icon-progress'
        case 'completed': return 'icon-completed'
        case 'cancelled': return 'icon-cancelled'
        case 'waiting-parts': return 'icon-waiting'
        case 'client-review': return 'icon-review'
        default: return ''
      }
    }
    
    // 选择状态
    const selectStatus = (status) => {
      if (!status.allowed) return
      selectedStatus.value = status.value
    }
    
    // 关闭对话框
    const closeDialog = () => {
      emit('close')
    }
    
    // 确认状态变更
    const confirmStatusChange = async () => {
      if (!isValidSelection.value) return
      
      isSubmitting.value = true
      
      try {
        // 构建状态变更数据
        const statusChangeData = {
          taskId: props.taskId,
          oldStatus: props.currentStatus,
          newStatus: selectedStatus.value,
          comment: statusComment.value.trim() || null,
          timestamp: new Date().toISOString()
        }
        
        // 触发状态变更事件
        emit('status-change', statusChangeData)
        
        // 关闭对话框
        closeDialog()
      } catch (error) {
        console.error('状态变更时出错:', error)
      } finally {
        isSubmitting.value = false
      }
    }
    
    return {
      selectedStatus,
      statusComment,
      isSubmitting,
      availableStatuses,
      isValidSelection,
      getStatusText,
      getStatusClass,
      getStatusIcon,
      getStatusIconClass,
      selectStatus,
      closeDialog,
      confirmStatusChange
    }
  }
}
</script>

<style scoped>
.status-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
  animation: fadeIn 0.2s ease;
}

.status-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 500px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  z-index: 101;
  overflow: hidden;
  animation: slideIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { opacity: 0; transform: translate(-50%, -45%); }
  to { opacity: 1; transform: translate(-50%, -50%); }
}

.dialog-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f3f4f6;
  border: none;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background-color: #e5e7eb;
  color: #374151;
}

.dialog-content {
  padding: 16px;
  max-height: 70vh;
  overflow-y: auto;
}

.current-status {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  margin-right: 8px;
  margin-bottom: 6px;
  display: block;
}

.current-status .label {
  margin-bottom: 0;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
}

.status-pending {
  background-color: #dbeafe;
  color: #2563eb;
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
  background-color: #ffedd5;
  color: #9a3412;
}

.status-review {
  background-color: #fae8ff;
  color: #86198f;
}

.status-options {
  margin-bottom: 24px;
}

.options-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.status-option {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.status-option:hover:not(.disabled) {
  border-color: #93c5fd;
  background-color: #f0f9ff;
}

.status-option.selected {
  border-color: #2563eb;
  background-color: #eff6ff;
}

.status-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.option-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.option-icon i {
  font-size: 18px;
}

.icon-pending {
  background-color: #dbeafe;
  color: #2563eb;
}

.icon-progress {
  background-color: #fef3c7;
  color: #92400e;
}

.icon-completed {
  background-color: #d1fae5;
  color: #065f46;
}

.icon-cancelled {
  background-color: #fee2e2;
  color: #b91c1c;
}

.icon-waiting {
  background-color: #ffedd5;
  color: #9a3412;
}

.icon-review {
  background-color: #fae8ff;
  color: #86198f;
}

.option-text {
  flex: 1;
}

.option-name {
  font-size: 15px;
  font-weight: 500;
  color: #111827;
  margin-bottom: 2px;
}

.option-desc {
  font-size: 13px;
  color: #6b7280;
}

.option-check {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #2563eb;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.option-check i {
  font-size: 12px;
}

.comment-section {
  margin-top: 24px;
}

.comment-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
}

.comment-input:focus {
  border-color: #3b82f6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.dialog-actions {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #fff;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.confirm-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  background-color: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #1d4ed8;
}

.confirm-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.confirm-btn i {
  margin-right: 8px;
  font-size: 14px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-spin {
  animation: spin 1s linear infinite;
}
</style>
