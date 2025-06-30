<template>
  <div class="records-list">
    <div class="list-header">
      <h3 class="list-title">
        <i class="icon-clipboard-list"></i>
        {{ stepName }} - 工作记录
      </h3>
      <div class="list-actions">
        <button class="close-btn" @click="close">
          <i class="icon-times"></i>
        </button>
      </div>
    </div>
    
    <div class="list-content">
      <!-- 加载状态 -->
      <div class="loading-state" v-if="loading">
        <i class="icon-spinner icon-spin"></i>
        <span>正在加载记录...</span>
      </div>
      
      <!-- 空状态 -->
      <div class="empty-state" v-else-if="records.length === 0">
        <i class="icon-clipboard"></i>
        <h4>暂无工作记录</h4>
        <p>此步骤暂未添加任何工作记录</p>
      </div>
      
      <!-- 记录列表 -->
      <div class="records-wrapper" v-else>
        <div class="record-item" v-for="(record, index) in sortedRecords" :key="index">
          <div class="record-header">
            <div class="creator-info">
              <div class="avatar">
                {{ getInitials(record.createdBy) }}
              </div>
              <div class="creator-details">
                <h4 class="creator-name">{{ record.createdBy }}</h4>
                <span class="record-time">{{ formatDateTime(record.createdAt) }}</span>
              </div>
            </div>
            <div class="record-actions">
              <button 
                v-if="canManageRecord(record)" 
                class="delete-btn" 
                @click="confirmDeleteRecord(record.id)"
                :disabled="isDeletingRecord === record.id"
              >
                <i v-if="isDeletingRecord === record.id" class="icon-spinner icon-spin"></i>
                <i v-else class="icon-trash"></i>
              </button>
            </div>
          </div>
          
          <div class="record-content">
            {{ record.content }}
          </div>
          
          <div class="record-attachments" v-if="record.attachments && record.attachments.length > 0">
            <h5 class="attachments-title">附件 ({{ record.attachments.length }})</h5>
            <div class="attachments-list">
              <a 
                v-for="(attachment, attachIdx) in record.attachments" 
                :key="attachIdx"
                :href="attachment.url"
                target="_blank"
                class="attachment-link"
                :title="attachment.name"
              >
                <div class="attachment-icon">
                  <i :class="getAttachmentIcon(attachment.type)"></i>
                </div>
                <div class="attachment-info">
                  <div class="attachment-name">{{ attachment.name }}</div>
                  <div class="attachment-size">{{ formatFileSize(attachment.size) }}</div>
                </div>
              </a>
            </div>
          </div>
          
          <div class="record-meta" v-if="record.spentTime">
            <i class="icon-clock"></i>
            <span>耗时: {{ formatSpentTime(record.spentTime) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加记录按钮 -->
    <div class="add-record-container" v-if="canAddRecord">
      <button class="add-record-btn" @click="addRecord">
        <i class="icon-plus"></i>
        添加工作记录
      </button>
    </div>
    
    <!-- 确认删除对话框 -->
    <div class="dialog-overlay" v-if="showDeleteConfirm" @click="cancelDelete"></div>
    <div class="delete-confirm-dialog" v-if="showDeleteConfirm">
      <div class="dialog-header">
        <h3 class="dialog-title">确认删除</h3>
      </div>
      <div class="dialog-content">
        <p class="dialog-message">您确定要删除这条工作记录吗？此操作无法撤销。</p>
      </div>
      <div class="dialog-actions">
        <button class="cancel-btn" @click="cancelDelete">取消</button>
        <button class="delete-btn" @click="deleteRecord" :disabled="isDeleting">
          <i v-if="isDeleting" class="icon-spinner icon-spin"></i>
          {{ isDeleting ? '删除中...' : '确认删除' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskStepRecordList',
  props: {
    stepId: {
      type: [Number, String],
      required: true
    },
    stepName: {
      type: String,
      default: '未知步骤'
    },
    records: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    currentUserId: {
      type: [Number, String],
      required: true
    },
    canManageRecords: {
      type: Boolean,
      default: false
    },
    canAddRecord: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'add-record', 'delete-record'],
  setup(props, { emit }) {
    const showDeleteConfirm = ref(false)
    const recordIdToDelete = ref(null)
    const isDeleting = ref(false)
    const isDeletingRecord = ref(null)
    
    // 按创建时间排序的记录列表
    const sortedRecords = computed(() => {
      return [...props.records].sort((a, b) => {
        // 按创建时间降序排序（新的在前）
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    })
    
    // 格式化日期时间
    const formatDateTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd HH:mm')
      } catch (err) {
        console.error('日期格式化错误:', err)
        return dateStr
      }
    }
    
    // 获取用户名首字母
    const getInitials = (name) => {
      if (!name) return '?'
      
      const names = name.split(' ')
      if (names.length >= 2) {
        return `${names[0].charAt(0)}${names[1].charAt(0)}`.toUpperCase()
      }
      
      return name.charAt(0).toUpperCase()
    }
    
    // 根据附件类型获取图标
    const getAttachmentIcon = (type) => {
      if (!type) return 'icon-file'
      
      type = type.toLowerCase()
      
      if (type.includes('image')) {
        return 'icon-file-image'
      } else if (type.includes('pdf')) {
        return 'icon-file-pdf'
      } else if (type.includes('word') || type.includes('doc')) {
        return 'icon-file-word'
      } else if (type.includes('excel') || type.includes('sheet')) {
        return 'icon-file-excel'
      } else if (type.includes('powerpoint') || type.includes('presentation')) {
        return 'icon-file-powerpoint'
      } else if (type.includes('video')) {
        return 'icon-file-video'
      } else if (type.includes('audio')) {
        return 'icon-file-audio'
      } else if (type.includes('zip') || type.includes('archive') || type.includes('compressed')) {
        return 'icon-file-archive'
      } else if (type.includes('text') || type.includes('txt')) {
        return 'icon-file-alt'
      } else if (type.includes('code') || type.includes('javascript') || type.includes('html') || type.includes('css')) {
        return 'icon-file-code'
      } else {
        return 'icon-file'
      }
    }
    
    // 格式化文件大小
    const formatFileSize = (bytes) => {
      if (!bytes) return '未知大小'
      
      const units = ['B', 'KB', 'MB', 'GB']
      let i = 0
      
      while (bytes >= 1024 && i < units.length - 1) {
        bytes /= 1024
        i++
      }
      
      return `${bytes.toFixed(1)} ${units[i]}`
    }
    
    // 格式化耗时（分钟转为小时分钟）
    const formatSpentTime = (minutes) => {
      if (!minutes && minutes !== 0) return '未记录'
      
      const hours = Math.floor(minutes / 60)
      const remainingMinutes = minutes % 60
      
      if (hours === 0) {
        return `${remainingMinutes} 分钟`
      } else if (remainingMinutes === 0) {
        return `${hours} 小时`
      } else {
        return `${hours} 小时 ${remainingMinutes} 分钟`
      }
    }
    
    // 检查当前用户是否可以管理记录
    const canManageRecord = (record) => {
      // 如果有管理所有记录的权限，或者是记录创建者，则可以管理
      return props.canManageRecords || record.createdById === props.currentUserId
    }
    
    // 关闭记录列表
    const close = () => {
      emit('close')
    }
    
    // 添加记录
    const addRecord = () => {
      emit('add-record')
    }
    
    // 确认删除记录
    const confirmDeleteRecord = (recordId) => {
      recordIdToDelete.value = recordId
      showDeleteConfirm.value = true
    }
    
    // 取消删除
    const cancelDelete = () => {
      showDeleteConfirm.value = false
      recordIdToDelete.value = null
    }
    
    // 删除记录
    const deleteRecord = async () => {
      if (!recordIdToDelete.value) return
      
      isDeleting.value = true
      isDeletingRecord.value = recordIdToDelete.value
      
      try {
        // 触发删除事件
        emit('delete-record', recordIdToDelete.value)
        
        // 关闭确认对话框
        showDeleteConfirm.value = false
      } catch (error) {
        console.error('删除记录时出错:', error)
      } finally {
        isDeleting.value = false
        recordIdToDelete.value = null
        // 短暂延迟后重置删除状态，以便动画完成
        setTimeout(() => {
          isDeletingRecord.value = null
        }, 300)
      }
    }
    
    return {
      sortedRecords,
      showDeleteConfirm,
      isDeleting,
      isDeletingRecord,
      formatDateTime,
      getInitials,
      getAttachmentIcon,
      formatFileSize,
      formatSpentTime,
      canManageRecord,
      close,
      addRecord,
      confirmDeleteRecord,
      cancelDelete,
      deleteRecord
    }
  }
}
</script>

<style scoped>
.records-list {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  animation: slideIn 0.3s ease;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

@keyframes slideIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.list-header {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.list-title {
  flex: 1;
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  display: flex;
  align-items: center;
}

.list-title i {
  margin-right: 10px;
  color: #4b5563;
  font-size: 20px;
}

.list-actions {
  display: flex;
  align-items: center;
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

.list-content {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #6b7280;
}

.loading-state i,
.empty-state i {
  font-size: 36px;
  margin-bottom: 16px;
  display: block;
}

.loading-state span {
  font-size: 16px;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.records-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-item {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  position: relative;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.creator-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  margin-right: 12px;
}

.creator-details {
  display: flex;
  flex-direction: column;
}

.creator-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 2px 0;
}

.record-time {
  font-size: 13px;
  color: #6b7280;
}

.record-actions .delete-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.record-actions .delete-btn:hover {
  background-color: #fee2e2;
  border-color: #fca5a5;
  color: #b91c1c;
}

.record-actions .delete-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.record-content {
  font-size: 14px;
  line-height: 1.6;
  color: #374151;
  margin-bottom: 12px;
  white-space: pre-line;
}

.record-attachments {
  margin-top: 12px;
  margin-bottom: 12px;
}

.attachments-title {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  margin: 0 0 8px 0;
}

.attachments-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 8px;
}

.attachment-link {
  display: flex;
  align-items: center;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background-color: #fff;
  text-decoration: none;
  transition: all 0.2s ease;
}

.attachment-link:hover {
  background-color: #f0f9ff;
  border-color: #93c5fd;
}

.attachment-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  color: #6b7280;
}

.attachment-icon i {
  font-size: 20px;
}

.attachment-info {
  flex: 1;
  min-width: 0;
}

.attachment-name {
  font-size: 13px;
  color: #2563eb;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.attachment-size {
  font-size: 12px;
  color: #6b7280;
}

.record-meta {
  display: flex;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  font-size: 13px;
  color: #6b7280;
}

.record-meta i {
  margin-right: 6px;
}

.add-record-container {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  text-align: center;
}

.add-record-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border-radius: 6px;
  border: none;
  background-color: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-record-btn:hover {
  background-color: #1d4ed8;
}

.add-record-btn i {
  margin-right: 8px;
}

/* 删除确认对话框 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}

.delete-confirm-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 400px;
  z-index: 101;
  animation: fadeIn 0.2s ease;
}

.dialog-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.dialog-content {
  padding: 16px;
}

.dialog-message {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: #4b5563;
}

.dialog-actions {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-actions .cancel-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #fff;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dialog-actions .cancel-btn:hover {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.dialog-actions .delete-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background-color: #ef4444;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.dialog-actions .delete-btn:hover {
  background-color: #dc2626;
}

.dialog-actions .delete-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.dialog-actions .delete-btn i {
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-spin {
  animation: spin 1s linear infinite;
}
</style>
