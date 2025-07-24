<template>
  <div class="device-info-card" v-if="task && task.deviceName">
    <h3 class="section-title">
      <i class="icon-microscope"></i>
      设备信息
    </h3>
    
    <div class="device-content">
      <!-- 设备基本信息 -->
      <div class="device-header">
        <div class="device-image-container">
          <img 
            v-if="mainImageUrl" 
            :src="mainImageUrl" 
            :alt="task.deviceName" 
            class="device-image"
            @click="previewImage(mainImageUrl)"
          >
          <div v-else class="device-image-placeholder">
            <i class="icon-medical-equipment"></i>
          </div>
        </div>
        
        <div class="device-info-container">
          <h4 class="device-name">{{ task.deviceName || '未知设备' }}</h4>
          <div class="device-badges">
            <span class="device-badge device-model" v-if="task.deviceModel">
              型号: {{ task.deviceModel }}
            </span>
            <span class="device-badge device-brand" v-if="task.deviceBrand">
              品牌: {{ task.deviceBrand }}
            </span>
            <span class="device-badge device-quantity" v-if="task.quantity">
              数量: {{ task.quantity }}
            </span>
          </div>
        </div>
      </div>
      
      <!-- 设备详细信息 -->
      <div class="device-details">
        <div class="detail-item">
          <span class="detail-label">设备序列号:</span>
          <span class="detail-value">{{ task.deviceSn || '未提供' }}</span>
        </div>
        <div class="detail-item">
           <span class="detail-label">问题描述:</span>
           <span class="detail-value">{{ task.description || '无' }}</span>
        </div>
        <div class="detail-item" v-if="isSelectionTask">
          <span class="detail-label">设备用途:</span>
          <span class="detail-value">{{ task.purpose || '未提供' }}</span>
        </div>
      </div>
      
      <!-- 客户上传图片 -->
      <div class="device-images" v-if="task.imageUrls && task.imageUrls.length > 0">
        <h4 class="subsection-title">
          <i class="icon-images"></i>
          客户上传的图片
        </h4>
        <div class="image-gallery">
          <div 
            v-for="(imageUrl, index) in task.imageUrls" 
            :key="index"
            class="gallery-item"
          >
            <img :src="imageUrl" :alt="`客户图片 ${index + 1}`" class="gallery-image" @click="previewImage(imageUrl)">
            <div class="gallery-item-actions">
              <button class="action-btn" @click.stop="downloadFile(imageUrl, `客户图片-${index+1}`)"><i class="icon-download"></i></button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 客户上传附件 -->
      <div class="device-attachments" v-if="task.attachments && task.attachments.length > 0">
        <h4 class="subsection-title">
          <i class="icon-paperclip"></i>
          客户上传的附件
        </h4>
        <div class="attachment-list">
          <div 
            v-for="attachment in task.attachments" 
            :key="attachment.id"
            class="attachment-item"
          >
            <div class="attachment-icon">
              <i :class="getAttachmentIcon(attachment.fileType)"></i>
            </div>
            <div class="attachment-info">
              <span class="attachment-name">{{ attachment.fileName }}</span>
              <span class="attachment-size">{{ formatFileSize(attachment.fileSize) }}</span>
            </div>
            <div class="attachment-actions">
              <button class="download-btn" @click="downloadAttachment(attachment)">
                <i class="icon-download"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useToast } from 'vue-toastification'

export default {
  name: 'TaskDeviceInfoCard',
  props: {
    task: {
      type: Object,
      required: true
    }
  },
  emits: ['preview-image'],
  setup(props, { emit }) {
    const toast = useToast()
    
    const mainImageUrl = computed(() => {
      if (props.task && props.task.imageUrls && props.task.imageUrls.length > 0) {
        return props.task.imageUrls[0];
      }
      return null;
    });

    const isSelectionTask = computed(() => props.task.taskType === 'selection');
    
    const formatFileSize = (bytes) => {
      if (!bytes || bytes === 0) return '0 B'
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(1024))
      return `${(bytes / Math.pow(1024, i)).toFixed(2)} ${sizes[i]}`
    }
    
    const getAttachmentIcon = (type) => {
      if (!type) return 'icon-file'
      const lowerType = type.toLowerCase()
      if (lowerType.includes('pdf')) return 'icon-file-pdf'
      if (lowerType.includes('word') || lowerType.includes('doc')) return 'icon-file-word'
      if (lowerType.includes('excel') || lowerType.includes('xls')) return 'icon-file-excel'
      return 'icon-file'
    }
    
    const previewImage = (imageUrl) => {
      emit('preview-image', imageUrl)
    }

    const downloadFile = async (url, fileName) => {
      try {
        const response = await fetch(url);
        if (!response.ok) throw new Error('网络响应错误');
        const blob = await response.blob();
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName || url.substring(url.lastIndexOf('/') + 1);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(link.href);
      } catch (err) {
        console.error('下载文件失败:', err);
        toast.error('下载文件失败，可能是跨域问题或网络错误');
      }
    };
    
    const downloadAttachment = (attachment) => {
      if (!attachment || !attachment.id) {
        toast.error('无效的附件');
        return;
      }
      const downloadUrl = `/api/v1/task-attachments/${attachment.id}/download`;
      window.open(downloadUrl, '_blank');
    }
    
    return {
      mainImageUrl,
      isSelectionTask,
      formatFileSize,
      getAttachmentIcon,
      previewImage,
      downloadFile,
      downloadAttachment
    }
  }
}
</script>

<style scoped>
.gallery-item {
  position: relative;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.2s ease;
}

.gallery-item:hover .gallery-image {
  transform: scale(1.05);
}

.gallery-item-actions {
  position: absolute;
  top: 0;
  right: 0;
  background-color: rgba(0,0,0,0.4);
  padding: 4px;
  border-bottom-left-radius: 6px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.gallery-item:hover .gallery-item-actions {
  opacity: 1;
}

.action-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 16px;
}
.device-info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  border-left: 4px solid #e53935; /* 红色边框 */
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #e53935;
}

.loading-spinner, .error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 0;
}

.spinner-small {
  width: 24px;
  height: 24px;
  border: 2px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #ef4444;
}

.error-message i {
  font-size: 24px;
  margin-bottom: 8px;
}

.reload-btn {
  margin-top: 8px;
  padding: 6px 12px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.device-content {
  display: flex;
  flex-direction: column;
}

.device-header {
  display: flex;
  margin-bottom: 16px;
}

.device-image-container {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.device-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.device-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
}

.device-image-placeholder i {
  font-size: 32px;
}

.device-info-container {
  flex: 1;
}

.device-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px;
  color: #111827;
}

.device-badges {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.device-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f3f4f6;
  color: #4b5563;
}

.device-model {
  background-color: #e0f2fe;
  color: #0369a1;
}

.device-brand {
  background-color: #f0fdf4;
  color: #16a34a;
}

.device-quantity {
  background-color: #fef3c7;
  color: #b45309;
}

.status-active {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-maintenance {
  background-color: #fef3c7;
  color: #b45309;
}

.status-outofservice {
  background-color: #fee2e2;
  color: #b91c1c;
}

.status-retired {
  background-color: #f3f4f6;
  color: #6b7280;
}

.device-type {
  font-size: 14px;
  color: #6b7280;
}

.device-details {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
}

.detail-item i {
  margin-right: 12px;
  margin-top: 2px;
  color: #6b7280;
  width: 16px;
  text-align: center;
}

.detail-info {
  display: flex;
  flex-direction: column;
}

.detail-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 2px;
}

.detail-value {
  font-size: 14px;
  color: #111827;
}

.fault-description .detail-value {
  white-space: pre-line;
}

.warranty-expired {
  color: #ef4444;
  font-size: 12px;
  margin-left: 8px;
}

.warranty-expiring {
  color: #f59e0b;
  font-size: 12px;
  margin-left: 8px;
}

.subsection-title {
  font-size: 16px;
  font-weight: 500;
  color: #4b5563;
  margin: 20px 0 12px;
  display: flex;
  align-items: center;
}

.subsection-title i {
  margin-right: 8px;
  color: #6b7280;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 8px;
  border-radius: 6px;
  background-color: #f9fafb;
  border: 1px solid #f3f4f6;
}

.attachment-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #4b5563;
}

.attachment-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.attachment-name {
  font-size: 14px;
  color: #111827;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.attachment-size {
  font-size: 12px;
  color: #6b7280;
}

.attachment-actions {
  display: flex;
  gap: 8px;
}

.preview-btn, .download-btn {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  cursor: pointer;
}

.preview-btn {
  background-color: #e0f2fe;
  color: #0369a1;
}

.download-btn {
  background-color: #dcfce7;
  color: #16a34a;
}

.view-details-container {
  margin-top: 20px;
  text-align: center;
}

.view-details-link {
  color: #2563eb;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.view-details-link i {
  margin-left: 4px;
  font-size: 12px;
}
</style> 