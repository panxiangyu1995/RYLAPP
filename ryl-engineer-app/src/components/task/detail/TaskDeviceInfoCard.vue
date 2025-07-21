<template>
  <div class="device-info-card" v-if="task.deviceId || task.deviceName">
    <h3 class="section-title">
      <i class="icon-microscope"></i>
      设备信息
    </h3>
    
    <div v-if="loading" class="loading-spinner">
      <div class="spinner-small"></div>
      <span>加载中...</span>
    </div>
    
    <div v-else-if="error" class="error-message">
      <i class="icon-exclamation-triangle"></i>
      <span>{{ error }}</span>
      <button @click="loadDeviceInfo" class="reload-btn">重试</button>
    </div>
    
    <div v-else class="device-content">
      <!-- 设备基本信息 -->
      <div class="device-header">
        <div class="device-image-container">
          <img 
            v-if="deviceImage" 
            :src="deviceImage" 
            :alt="device.deviceName || task.deviceName" 
            class="device-image"
            @click="previewImage(deviceImage)"
          >
          <div v-else class="device-image-placeholder">
            <i class="icon-medical-equipment"></i>
          </div>
        </div>
        
        <div class="device-info-container">
          <h4 class="device-name">{{ device.deviceName || task.deviceName || '未知设备' }}</h4>
          <div class="device-badges">
            <span class="device-badge device-model" v-if="device.model || task.deviceModel">
              型号: {{ device.model || task.deviceModel }}
            </span>
            <span class="device-badge device-brand" v-if="device.brand || task.deviceBrand">
              品牌: {{ device.brand || task.deviceBrand }}
            </span>
            <span class="device-badge device-quantity" v-if="device.quantity || task.deviceQuantity">
              数量: {{ device.quantity || task.deviceQuantity }}
            </span>
            <span class="device-badge" :class="getStatusClass(device.status)" v-if="device.status">
              {{ getStatusText(device.status) }}
            </span>
          </div>
          <div class="device-type" v-if="device.deviceType || task.deviceType">
            <span>设备类型：{{ device.deviceType || task.deviceType }}</span>
          </div>
        </div>
      </div>
      
      <!-- 设备详细信息 - 通用字段 -->
      <div class="device-details">
        <!-- 维修/保养特有字段 -->
        <div class="detail-item">
          <span class="detail-label">设备序列号:</span>
          <span class="detail-value">{{ device.deviceSn || task.deviceSn }}</span>
        </div>

        <div class="detail-item">
           <span class="detail-label">问题描述:</span>
           <span class="detail-value">{{ device.description || task.description }}</span>
        </div>

        <div class="detail-item" v-if="isSelectionTask">
          <span class="detail-label">设备用途:</span>
          <span class="detail-value">{{ device.purpose || task.purpose }}</span>
        </div>
      </div>
      
      <!-- 设备图片展示 -->
      <div class="device-images" v-if="deviceImages && deviceImages.length > 0">
        <h4 class="subsection-title">
          <i class="icon-images"></i>
          设备图片
        </h4>
        
        <div class="image-gallery">
          <div 
            v-for="(image, index) in deviceImages" 
            :key="index"
            class="gallery-item"
            @click="previewImage(image.url)"
          >
            <img :src="image.url" :alt="`设备图片 ${index + 1}`" class="gallery-image">
          </div>
        </div>
      </div>
      
      <!-- 设备附件 -->
      <div class="device-attachments" v-if="deviceAttachments && deviceAttachments.length > 0">
        <h4 class="subsection-title">
          <i class="icon-paperclip"></i>
          设备附件
        </h4>
        
        <div class="attachment-list">
          <div 
            v-for="(attachment, index) in deviceAttachments" 
            :key="index"
            class="attachment-item"
          >
            <div class="attachment-icon">
              <i :class="getAttachmentIcon(attachment.type)"></i>
            </div>
            <div class="attachment-info">
              <span class="attachment-name">{{ attachment.name }}</span>
              <span class="attachment-size">{{ formatFileSize(attachment.size) }}</span>
            </div>
            <div class="attachment-actions">
              <button class="preview-btn" @click="previewAttachment(attachment)" v-if="canPreviewAttachment(attachment)">
                <i class="icon-eye"></i>
              </button>
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
import { ref, onMounted, computed } from 'vue'
import { format } from 'date-fns'
import http from '../../../api/http'
import { useToast } from 'vue-toastification'
import { BASE_URL } from '../../../api/task'

export default {
  name: 'TaskDeviceInfoCard',
  props: {
    task: {
      type: Object,
      required: true
    },
    canEditDevice: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const toast = useToast()
    const device = ref({})
    const loading = ref(true)
    const error = ref(null)
    
    // 设备图片和附件
    const deviceImage = ref(null)
    const deviceImages = ref([])
    const deviceAttachments = ref([])
    
    // 计算任务类型
    const isRepairTask = computed(() => props.task.taskType === 'repair')
    const isMaintenanceTask = computed(() => props.task.taskType === 'maintenance')
    const isRecycleTask = computed(() => props.task.taskType === 'recycle')
    const isLeasingTask = computed(() => props.task.taskType === 'leasing')
    const isTrainingTask = computed(() => props.task.taskType === 'training')
    const isVerificationTask = computed(() => props.task.taskType === 'verification')
    const isSelectionTask = computed(() => props.task.taskType === 'selection')
    const isInstallationTask = computed(() => props.task.taskType === 'installation')
    
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
    
    // 文件大小格式化
    const formatFileSize = (bytes) => {
      if (!bytes || bytes === 0) return '0 B'
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(1024))
      return `${(bytes / Math.pow(1024, i)).toFixed(2)} ${sizes[i]}`
    }
    
    // 获取状态对应的样式类
    const getStatusClass = (status) => {
      switch(status?.toLowerCase()) {
        case 'active': return 'status-active'
        case 'maintenance': return 'status-maintenance'
        case 'out-of-service': return 'status-outofservice'
        case 'retired': return 'status-retired'
        default: return ''
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch(status?.toLowerCase()) {
        case 'active': return '正常使用'
        case 'maintenance': return '维护中'
        case 'out-of-service': return '停止使用'
        case 'retired': return '已报废'
        default: return status || '未知状态'
      }
    }
    
    // 获取附件图标
    const getAttachmentIcon = (type) => {
      if (!type) return 'icon-file'
      
      type = type.toLowerCase()
      if (type.includes('pdf')) return 'icon-file-pdf'
      if (type.includes('word') || type.includes('doc')) return 'icon-file-word'
      if (type.includes('excel') || type.includes('xls')) return 'icon-file-excel'
      if (type.includes('image') || type.includes('jpg') || type.includes('png')) return 'icon-file-image'
      if (type.includes('zip') || type.includes('rar')) return 'icon-file-archive'
      
      return 'icon-file'
    }
    
    // 判断是否可以预览附件
    const canPreviewAttachment = (attachment) => {
      if (!attachment || !attachment.type) return false
      
      const type = attachment.type.toLowerCase()
      return type.includes('image') || type.includes('pdf')
    }
    
    // 预览图片
    const previewImage = (imageUrl) => {
      emit('preview-image', imageUrl)
    }
    
    // 预览附件
    const previewAttachment = (attachment) => {
      if (attachment.type.toLowerCase().includes('image')) {
        previewImage(attachment.url)
      } else {
        // 打开新窗口预览PDF等文件
        window.open(attachment.url, '_blank')
      }
    }
    
    // 下载附件
    const downloadAttachment = (attachment) => {
      try {
        // 创建一个隐藏的a标签用于下载
        const link = document.createElement('a')
        link.href = attachment.url
        link.download = attachment.name
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      } catch (err) {
        console.error('下载附件失败:', err)
        toast.error('下载附件失败，请稍后再试')
      }
    }
    
    // 加载设备信息
    const loadDeviceInfo = async () => {
      loading.value = true
      error.value = null
      
      try {
        // 如果任务中有设备ID，则从API加载设备详情
        if (props.task.deviceId) {
          const response = await http.get(`/api/v1/devices/${props.task.deviceId}`)
          if (response && response.code === 200) {
            device.value = response.data || {}
          } else {
            console.error('API返回错误:', response)
            error.value = '加载设备信息失败'
          }
        } else {
          // 否则使用任务中的设备信息
          device.value = {
            deviceName: props.task.deviceName,
            deviceType: props.task.deviceType,
            brand: props.task.deviceBrand,
            model: props.task.deviceModel,
            quantity: props.task.deviceQuantity,
            faultDescription: props.task.faultDescription
          }
        }
        
        // 加载设备图片
        await loadDeviceImages()
        
        // 加载设备附件
        await loadDeviceAttachments()
        
      } catch (err) {
        console.error('加载设备信息失败:', err)
        error.value = '加载设备信息失败，请稍后再试'
      } finally {
        loading.value = false
      }
    }
    
    // 加载设备图片
    const loadDeviceImages = async () => {
      try {
        // 如果任务中有设备图片，则加载
        if (props.task.deviceImages && props.task.deviceImages.length > 0) {
          deviceImages.value = props.task.deviceImages.map(img => ({
            url: img.startsWith('http') ? img : `${BASE_URL}${img}`,
            name: img.split('/').pop()
          }))
          
          // 设置第一张图片为设备主图
          if (deviceImages.value.length > 0) {
            deviceImage.value = deviceImages.value[0].url
          }
        } else if (props.task.deviceImage) {
          // 如果只有一张设备图片
          const imgUrl = props.task.deviceImage.startsWith('http') 
            ? props.task.deviceImage 
            : `${BASE_URL}${props.task.deviceImage}`
          
          deviceImage.value = imgUrl
          deviceImages.value = [{ url: imgUrl, name: props.task.deviceImage.split('/').pop() }]
        } else if (props.task.faultImages && props.task.faultImages.length > 0) {
          // 如果有故障图片
          deviceImages.value = props.task.faultImages.map(img => ({
            url: img.startsWith('http') ? img : `${BASE_URL}${img}`,
            name: img.split('/').pop()
          }))
          
          // 设置第一张图片为设备主图
          if (deviceImages.value.length > 0) {
            deviceImage.value = deviceImages.value[0].url
          }
        }
      } catch (err) {
        console.error('加载设备图片失败:', err)
      }
    }
    
    // 加载设备附件
    const loadDeviceAttachments = async () => {
      try {
        // 如果任务中有设备附件，则加载
        if (props.task.attachments && props.task.attachments.length > 0) {
          deviceAttachments.value = props.task.attachments.map(att => ({
            url: att.url.startsWith('http') ? att.url : `${BASE_URL}${att.url}`,
            name: att.name || att.url.split('/').pop(),
            type: att.type || getMimeType(att.url),
            size: att.size || 0
          }))
        }
      } catch (err) {
        console.error('加载设备附件失败:', err)
      }
    }
    
    // 根据文件扩展名获取MIME类型
    const getMimeType = (filename) => {
      if (!filename) return ''
      
      const ext = filename.split('.').pop().toLowerCase()
      const mimeTypes = {
        'pdf': 'application/pdf',
        'doc': 'application/msword',
        'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'xls': 'application/vnd.ms-excel',
        'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'jpg': 'image/jpeg',
        'jpeg': 'image/jpeg',
        'png': 'image/png',
        'gif': 'image/gif',
        'zip': 'application/zip',
        'rar': 'application/x-rar-compressed'
      }
      
      return mimeTypes[ext] || ''
    }
    
    // 编辑设备信息
    const editDevice = () => {
      emit('edit-device', device.value)
    }
    
    // 页面加载时获取设备信息
    onMounted(() => {
      loadDeviceInfo()
    })
    
    return {
      device,
      loading,
      error,
      deviceImage,
      deviceImages,
      deviceAttachments,
      isRepairTask,
      isMaintenanceTask,
      isRecycleTask,
      isLeasingTask,
      isTrainingTask,
      isVerificationTask,
      isSelectionTask,
      isInstallationTask,
      
      // 方法
      formatDate,
      formatFileSize,
      getStatusClass,
      getStatusText,
      getAttachmentIcon,
      canPreviewAttachment,
      previewImage,
      previewAttachment,
      downloadAttachment,
      loadDeviceInfo,
      editDevice
    }
  }
}
</script>

<style scoped>
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

.gallery-item {
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