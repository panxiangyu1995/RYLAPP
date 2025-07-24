<template>
  <div class="record-preview-modal" v-if="visible">
    <div class="modal-overlay" @click="close"></div>
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="preview-title">
          <i class="fas fa-clipboard-list"></i>
          步骤记录详情
        </h3>
        <button class="close-btn" @click="close">
          <i class="fas fa-times"></i>
        </button>
      </div>
      
      <div class="record-content">
        <!-- 步骤和记录信息 -->
        <div class="record-info">
          <div class="step-badge" :class="`status-${record.status || 'unknown'}`">
            {{ getStatusText(record.status) }}
          </div>
          <div class="record-meta">
            <div class="meta-item">
              <i class="fas fa-calendar"></i>
              <span>{{ formatDateTime(record.createdAt) }}</span>
            </div>
            <div class="meta-item" v-if="record.engineer">
              <i class="fas fa-user"></i>
              <span>{{ record.engineer.name }}</span>
            </div>
            <div class="meta-item" v-if="record.spentTime">
              <i class="fas fa-clock"></i>
              <span>{{ formatTime(record.spentTime) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 记录内容 -->
        <div class="record-description">
          <p v-if="record.content">{{ record.content }}</p>
          <div v-else class="no-content">无内容描述</div>
        </div>
        
        <!-- 图片预览 -->
        <div class="record-images" v-if="record.images && record.images.length > 0">
          <h4 class="section-title">图片</h4>
          <div class="image-grid">
            <div 
              v-for="image in record.images" 
              :key="image.url"
              class="image-item"
              @click="previewImage(image.url)"
            >
              <img :src="image.url" alt="步骤图片" class="thumbnail">
            </div>
          </div>
        </div>
        
        <!-- 附件列表 -->
        <div class="record-attachments" v-if="record.attachments && record.attachments.length > 0">
          <h4 class="section-title">附件</h4>
          <div class="attachment-list">
            <div 
              v-for="file in record.attachments" 
              :key="file.url"
              class="attachment-item"
            >
              <div class="attachment-icon">
                <i class="fas fa-file-alt"></i>
              </div>
              <div class="attachment-info">
                <div class="attachment-name">{{ file.name }}</div>
              </div>
              <a 
                :href="file.url" 
                download 
                class="download-btn"
                @click.stop
              >
                <i class="fas fa-download"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button class="close-btn-secondary" @click="close">关闭</button>
      </div>
    </div>
    
    <!-- 图片预览器 -->
    <div class="image-viewer" v-if="previewImageUrl" @click="closeImagePreview">
      <div class="image-viewer-content">
        <img :src="previewImageUrl" alt="图片预览" class="full-image">
        <button class="close-preview-btn" @click="closeImagePreview">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'StepRecordPreview',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    // 图片预览URL
    const previewImageUrl = ref(null)
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch(status) {
        case 'completed': return '已完成'
        case 'in-progress': return '进行中'
        case 'pending': return '待处理'
        default: return '未知状态'
      }
    }
    
    // 格式化日期时间
    const formatDateTime = (dateStr) => {
      if (!dateStr) return '未知时间'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd HH:mm')
      } catch (err) {
        return dateStr
      }
    }
    
    // 格式化耗时
    const formatTime = (minutes) => {
      if (!minutes) return '无耗时记录'
      
      const hours = Math.floor(minutes / 60)
      const mins = minutes % 60
      
      if (hours > 0 && mins > 0) {
        return `${hours}小时${mins}分钟`
      } else if (hours > 0) {
        return `${hours}小时`
      } else {
        return `${mins}分钟`
      }
    }
    
    // 获取文件类型图标
    const getFileTypeIcon = (type) => {
      if (!type) return 'fas fa-file'
      
      type = type.toLowerCase()
      
      if (type.includes('pdf')) {
        return 'fas fa-file-pdf'
      } else if (type.includes('word') || type.includes('doc')) {
        return 'fas fa-file-word'
      } else if (type.includes('excel') || type.includes('sheet')) {
        return 'fas fa-file-excel'
      } else if (type.includes('powerpoint') || type.includes('presentation')) {
        return 'fas fa-file-powerpoint'
      } else if (type.includes('video')) {
        return 'fas fa-file-video'
      } else if (type.includes('audio')) {
        return 'fas fa-file-audio'
      } else if (type.includes('zip') || type.includes('archive') || type.includes('compressed')) {
        return 'fas fa-file-archive'
      } else if (type.includes('text') || type.includes('txt')) {
        return 'fas fa-file-alt'
      } else if (type.includes('code') || type.includes('javascript') || type.includes('html') || type.includes('css')) {
        return 'fas fa-file-code'
      } else {
        return 'fas fa-file'
      }
    }
    
    // 格式化文件大小
    const formatFileSize = (bytes) => {
      if (!bytes) return '0 B'
      
      const units = ['B', 'KB', 'MB', 'GB']
      let i = 0
      
      while (bytes >= 1024 && i < units.length - 1) {
        bytes /= 1024
        i++
      }
      
      return `${bytes.toFixed(1)} ${units[i]}`
    }
    
    // 预览图片
    const previewImage = (url) => {
      previewImageUrl.value = url
    }
    
    // 关闭图片预览
    const closeImagePreview = () => {
      previewImageUrl.value = null
    }
    
    // 关闭模态框
    const close = () => {
      emit('close')
    }
    
    return {
      previewImageUrl,
      getStatusText,
      formatDateTime,
      formatTime,
      getFileTypeIcon,
      formatFileSize,
      previewImage,
      closeImagePreview,
      close
    }
  }
}
</script>

<style scoped>
.record-preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
}

.modal-container {
  position: relative;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  z-index: 1001;
  animation: modal-appear 0.3s ease-out;
  display: flex;
  flex-direction: column;
}

@keyframes modal-appear {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.preview-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  display: flex;
  align-items: center;
}

.preview-title i {
  margin-right: 8px;
  color: #4b5563;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.close-btn:hover {
  background-color: #f3f4f6;
}

.record-content {
  padding: 16px;
  flex: 1;
  overflow-y: auto;
}

.record-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.step-badge {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

.status-completed {
  background-color: #d1fae5;
  color: #065f46;
}

.status-in-progress {
  background-color: #fef3c7;
  color: #92400e;
}

.status-pending, .status-unknown {
  background-color: #f3f4f6;
  color: #6b7280;
}

.record-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
}

.meta-item i {
  margin-right: 6px;
}

.record-description {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.record-description p {
  font-size: 14px;
  line-height: 1.6;
  color: #374151;
  margin: 0;
  white-space: pre-wrap;
}

.no-content {
  font-style: italic;
  color: #9ca3af;
  font-size: 14px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
  margin-bottom: 20px;
}

.image-item {
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
}

.image-item:hover::after {
  content: '预览';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
}

.thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 10px 12px;
}

.attachment-icon {
  margin-right: 12px;
  font-size: 18px;
  color: #6b7280;
  width: 24px;
  text-align: center;
}

.attachment-info {
  flex: 1;
}

.attachment-name {
  font-size: 14px;
  color: #374151;
  margin-bottom: 2px;
  word-break: break-all;
}

.attachment-size {
  font-size: 12px;
  color: #6b7280;
}

.download-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  background-color: #eff6ff;
  border-radius: 6px;
  transition: all 0.2s;
}

.download-btn:hover {
  background-color: #dbeafe;
}

.modal-footer {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.close-btn-secondary {
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.close-btn-secondary:hover {
  background-color: #e5e7eb;
}

/* 图片预览器 */
.image-viewer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1010;
}

.image-viewer-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.full-image {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
}

.close-preview-btn {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
}
</style> 