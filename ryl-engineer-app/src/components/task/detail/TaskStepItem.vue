<template>
  <div 
    class="step-item" 
    :class="{
      'is-completed': step.status === 'completed',
      'is-active': step.status === 'in-progress',
      'is-pending': step.status === 'pending'
    }"
  >
    <div class="step-header" @click="toggleExpanded">
      <div class="step-status">
        <div class="status-circle">
          <i v-if="step.status === 'completed'" class="icon-check"></i>
          <i v-else-if="step.status === 'in-progress'" class="icon-play"></i>
          <span v-else class="step-number">{{ index + 1 }}</span>
        </div>
      </div>
      
      <div class="step-info">
        <h4 class="step-title">{{ step.title }}</h4>
        <span v-if="step.status === 'completed'" class="step-completion-date">
          完成于: {{ formatDate(step.completedDate) }}
        </span>
        <span v-else-if="step.status === 'in-progress'" class="step-active">
          进行中
        </span>
      </div>
      
      <div class="step-actions">
        <i :class="expanded ? 'icon-chevron-up' : 'icon-chevron-down'"></i>
      </div>
    </div>
    
    <transition name="slide">
      <div class="step-details" v-if="expanded">
        <div class="step-description">
          <p>{{ step.description }}</p>
        </div>
        
        <div class="step-time-info" v-if="showTimeInfo">
          <div class="time-item" v-if="step.estimatedTime">
            <i class="icon-clock"></i>
            <span>预计耗时: {{ step.estimatedTime }}</span>
          </div>
          <div class="time-item" v-if="step.startDate">
            <i class="icon-calendar-plus"></i>
            <span>开始日期: {{ formatDate(step.startDate) }}</span>
          </div>
          <div class="time-item" v-if="step.dueDate">
            <i class="icon-calendar-minus"></i>
            <span>截止日期: {{ formatDate(step.dueDate) }}</span>
          </div>
        </div>
        
        <div class="step-records" v-if="step.records && step.records.length > 0">
          <h5 class="records-title">
            <i class="icon-clipboard-list"></i>
            工作记录 ({{ step.records.length }})
          </h5>
          <div class="records-preview">
            <div class="record-item" v-for="(record, idx) in visibleRecords" :key="idx">
              <div class="record-header">
                <span class="record-creator">{{ record.createdBy }}</span>
                <span class="record-date">{{ formatDateTime(record.createdAt) }}</span>
              </div>
              <p class="record-content">{{ truncateText(record.content, 100) }}</p>
              
              <div class="record-attachments" v-if="record.attachments && record.attachments.length > 0">
                <a 
                  v-for="(attachment, attachIdx) in record.attachments" 
                  :key="attachIdx"
                  :href="attachment.url"
                  target="_blank"
                  class="attachment-link"
                >
                  <i :class="getAttachmentIcon(attachment.type)"></i>
                  <span>{{ attachment.name }}</span>
                </a>
              </div>
            </div>
            
            <div v-if="step.records.length > maxVisibleRecords" class="more-records">
              <button @click="viewAllRecords" class="view-all-btn">
                查看全部记录 ({{ step.records.length }})
              </button>
            </div>
          </div>
        </div>
        
        <div class="step-buttons" v-if="canManageStep && step.status !== 'completed'">
          <button @click="addRecord" class="add-record-btn">
            <i class="icon-plus"></i>
            添加记录
          </button>
          <button 
            v-if="step.status === 'in-progress'" 
            @click="completeStep"
            class="complete-btn"
          >
            <i class="icon-check"></i>
            完成此步骤
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskStepItem',
  props: {
    step: {
      type: Object,
      required: true
    },
    index: {
      type: Number,
      required: true
    },
    active: {
      type: Boolean,
      default: false
    },
    canManageStep: {
      type: Boolean,
      default: false
    },
    showTimeInfo: {
      type: Boolean,
      default: true
    }
  },
  emits: ['add-record', 'complete-step', 'view-all-records'],
  setup(props, { emit }) {
    const expanded = ref(props.active) // 如果是当前活动步骤，则默认展开
    const maxVisibleRecords = ref(2) // 最大显示记录数
    
    // 可见记录
    const visibleRecords = computed(() => {
      if (!props.step.records) return []
      return props.step.records.slice(0, maxVisibleRecords.value)
    })
    
    // 切换展开/折叠状态
    const toggleExpanded = () => {
      expanded.value = !expanded.value
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '未设置'
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
      if (!dateStr) return '未知时间'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd HH:mm')
      } catch (err) {
        console.error('日期时间格式化错误:', err)
        return dateStr
      }
    }
    
    // 截断文本，超出长度显示省略号
    const truncateText = (text, maxLength) => {
      if (!text) return ''
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
    }
    
    // 根据附件类型获取图标
    const getAttachmentIcon = (type) => {
      if (!type) return 'icon-file'
      
      type = type.toLowerCase()
      
      if (type.includes('image') || type.includes('jpg') || type.includes('png')) {
        return 'icon-file-image'
      } else if (type.includes('pdf')) {
        return 'icon-file-pdf'
      } else if (type.includes('word') || type.includes('doc')) {
        return 'icon-file-word'
      } else if (type.includes('excel') || type.includes('xls')) {
        return 'icon-file-excel'
      } else if (type.includes('video')) {
        return 'icon-file-video'
      } else {
        return 'icon-file'
      }
    }
    
    // 添加记录
    const addRecord = () => {
      emit('add-record', props.index)
    }
    
    // 完成步骤
    const completeStep = () => {
      emit('complete-step', props.index)
    }
    
    // 查看所有记录
    const viewAllRecords = () => {
      emit('view-all-records', props.index)
    }
    
    return {
      expanded,
      visibleRecords,
      toggleExpanded,
      formatDate,
      formatDateTime,
      truncateText,
      getAttachmentIcon,
      addRecord,
      completeStep,
      viewAllRecords
    }
  }
}
</script>

<style scoped>
.step-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-item.is-active {
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.1);
  border-left: 3px solid #f59e0b;
}

.step-item.is-completed {
  border-left: 3px solid #10b981;
}

.step-item.is-pending {
  opacity: 0.8;
}

.step-header {
  display: flex;
  align-items: center;
  padding: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.step-header:hover {
  background-color: #f9fafb;
}

.step-status {
  margin-right: 16px;
}

.status-circle {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  background-color: #f3f4f6;
  color: #6b7280;
  border: 2px solid #e5e7eb;
}

.is-active .status-circle {
  background-color: #fef3c7;
  color: #92400e;
  border-color: #f59e0b;
}

.is-completed .status-circle {
  background-color: #d1fae5;
  color: #065f46;
  border-color: #10b981;
}

.step-number {
  font-size: 12px;
}

.step-info {
  flex: 1;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px 0;
}

.is-completed .step-title {
  color: #10b981;
}

.is-active .step-title {
  color: #92400e;
}

.step-completion-date {
  font-size: 13px;
  color: #10b981;
}

.step-active {
  font-size: 13px;
  color: #f59e0b;
  font-weight: 500;
}

.step-actions {
  margin-left: 16px;
}

.step-actions i {
  font-size: 16px;
  color: #6b7280;
  transition: transform 0.3s ease;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
  max-height: 600px;
  overflow: hidden;
}

.slide-enter-from,
.slide-leave-to {
  max-height: 0;
  opacity: 0;
}

.step-details {
  padding: 0 16px 16px 60px;
  border-top: 1px solid #f3f4f6;
}

.step-description {
  margin-bottom: 16px;
}

.step-description p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #4b5563;
}

.step-time-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.time-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
}

.time-item i {
  margin-right: 6px;
  font-size: 14px;
}

.records-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
}

.records-title i {
  margin-right: 8px;
  color: #6b7280;
  font-size: 14px;
}

.records-preview {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.record-item {
  background-color: #f9fafb;
  border-radius: 6px;
  padding: 12px;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
}

.record-creator {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
}

.record-date {
  font-size: 12px;
  color: #6b7280;
}

.record-content {
  font-size: 13px;
  line-height: 1.5;
  color: #4b5563;
  margin: 0 0 8px 0;
}

.record-attachments {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.attachment-link {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #2563eb;
  text-decoration: none;
  background-color: #eff6ff;
  border-radius: 4px;
  padding: 4px 8px;
  transition: all 0.2s ease;
}

.attachment-link:hover {
  background-color: #dbeafe;
}

.attachment-link i {
  margin-right: 4px;
  font-size: 14px;
}

.more-records {
  display: flex;
  justify-content: center;
  margin-top: 12px;
}

.view-all-btn {
  background: transparent;
  border: none;
  color: #2563eb;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.view-all-btn:hover {
  background-color: #eff6ff;
}

.step-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-top: 16px;
}

.add-record-btn,
.complete-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.add-record-btn {
  background-color: #eff6ff;
  color: #2563eb;
}

.add-record-btn:hover {
  background-color: #dbeafe;
}

.complete-btn {
  background-color: #ecfdf5;
  color: #065f46;
}

.complete-btn:hover {
  background-color: #d1fae5;
}

.add-record-btn i,
.complete-btn i {
  margin-right: 6px;
  font-size: 14px;
}
</style> 