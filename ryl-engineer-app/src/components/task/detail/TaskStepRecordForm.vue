<template>
  <div class="record-form-modal">
    <div class="modal-overlay" @click="cancel"></div>
    <div class="modal-container">
      <div class="modal-header">
        <h3 class="form-title">
          <i class="icon-pen"></i>
          添加工作记录
        </h3>
        <button class="close-btn" @click="cancel">
          <i class="icon-times"></i>
        </button>
      </div>
    
    <div class="form-content">
      <div class="form-header">
        <div class="step-info">
          <span class="step-label">步骤:</span>
          <span class="step-name">{{ stepName }}</span>
        </div>
        <div class="date-info">
          <span class="date-label">日期:</span>
          <span class="date-value">{{ currentDate }}</span>
        </div>
      </div>
      
      <div class="form-field">
        <label for="record-content" class="field-label">工作内容</label>
        <textarea 
          id="record-content"
          v-model="content"
          class="content-input"
          placeholder="请描述您完成的工作内容..."
          :class="{ 'has-error': contentError }"
        ></textarea>
        <div v-if="contentError" class="error-message">{{ contentError }}</div>
      </div>
      
      <div class="form-field">
        <label class="field-label">
          附件
          <span class="optional-text">(可选)</span>
        </label>
        <div class="attachment-uploader">
          <div class="upload-dropzone" @click="triggerFileInput" @drop.prevent="onFileDrop" @dragover.prevent>
            <i class="icon-cloud-upload"></i>
            <span class="upload-text">点击上传或拖放文件到此处</span>
            <span class="upload-hint">支持图片、PDF、文档等</span>
            <input
              ref="fileInput"
              type="file"
              multiple
              class="file-input"
              @change="handleFileUpload"
            />
          </div>
          
          <div v-if="attachments.length > 0" class="attachment-list">
            <div 
              v-for="(file, index) in attachments" 
              :key="index"
              class="attachment-item"
            >
              <div class="attachment-icon">
                <i :class="getFileTypeIcon(file.type)"></i>
              </div>
              <div class="attachment-info">
                <div class="attachment-name">{{ file.name }}</div>
                <div class="attachment-size">{{ formatFileSize(file.size) }}</div>
              </div>
              <button class="remove-btn" @click="removeFile(index)">
                <i class="icon-times"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="form-field">
        <label class="field-label">
          耗时
          <span class="optional-text">(可选)</span>
        </label>
        <div class="time-input-group">
          <input 
            v-model.number="hours" 
            type="number" 
            min="0"
            class="time-input"
            placeholder="0"
          />
          <span class="time-unit">小时</span>
          <input 
            v-model.number="minutes" 
            type="number" 
            min="0" 
            max="59" 
            class="time-input"
            placeholder="0"
          />
          <span class="time-unit">分钟</span>
        </div>
      </div>
      
      <div class="form-actions">
        <button class="cancel-btn" @click="cancel">
          取消
        </button>
        <button 
          class="submit-btn" 
          @click="submitRecord"
          :disabled="isSubmitting"
        >
          <i v-if="isSubmitting" class="icon-spinner icon-spin"></i>
          {{ isSubmitting ? '提交中...' : '提交记录' }}
        </button>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskStepRecordForm',
  props: {
    stepId: {
      type: [Number, String],
      required: true
    },
    stepName: {
      type: String,
      default: '未知步骤'
    },
    taskId: {
      type: [Number, String],
      required: true
    },
    maxFileSize: {
      type: Number,
      default: 10 // 10MB
    }
  },
  emits: ['submit', 'cancel'],
  setup(props, { emit }) {
    const fileInput = ref(null)
    const content = ref('')
    const contentError = ref('')
    const attachments = ref([])
    const hours = ref(0)
    const minutes = ref(0)
    const isSubmitting = ref(false)
    
    // 当前日期
    const currentDate = computed(() => {
      return format(new Date(), 'yyyy-MM-dd')
    })
    
    // 触发文件选择
    const triggerFileInput = () => {
      fileInput.value.click()
    }
    
    // 处理文件上传
    const handleFileUpload = (event) => {
      const files = event.target.files
      if (!files.length) return
      
      addFiles(files)
      
      // 重置文件输入，以便能够重复选择同一个文件
      event.target.value = null
    }
    
    // 处理文件拖放
    const onFileDrop = (event) => {
      const files = event.dataTransfer.files
      if (!files.length) return
      
      addFiles(files)
    }
    
    // 添加文件
    const addFiles = (files) => {
      Array.from(files).forEach(file => {
        // 检查文件大小
        if (file.size > props.maxFileSize * 1024 * 1024) {
          alert(`文件 "${file.name}" 超过最大限制 ${props.maxFileSize}MB`)
          return
        }
        
        // 添加文件到附件列表
        attachments.value.push({
          file,
          name: file.name,
          type: file.type,
          size: file.size,
          url: URL.createObjectURL(file)
        })
      })
    }
    
    // 移除文件
    const removeFile = (index) => {
      // 释放URL
      URL.revokeObjectURL(attachments.value[index].url)
      // 移除文件
      attachments.value.splice(index, 1)
    }
    
    // 获取文件类型图标
    const getFileTypeIcon = (type) => {
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
      if (!bytes) return '0 B'
      
      const units = ['B', 'KB', 'MB', 'GB']
      let i = 0
      
      while (bytes >= 1024 && i < units.length - 1) {
        bytes /= 1024
        i++
      }
      
      return `${bytes.toFixed(1)} ${units[i]}`
    }
    
    // 验证表单
    const validateForm = () => {
      // 重置错误
      contentError.value = ''
      
      // 验证内容
      if (!content.value.trim()) {
        contentError.value = '请输入工作内容'
        return false
      }
      
      return true
    }
    
    // 提交记录
    const submitRecord = async () => {
      if (!validateForm()) return
      
      isSubmitting.value = true
      
      try {
        // 准备提交数据
        const recordData = {
          taskId: props.taskId,
          stepId: props.stepId,
          content: content.value.trim(),
          spentTime: hours.value * 60 + minutes.value, // 转换为分钟
          attachments: attachments.value.map(attachment => ({
            name: attachment.name,
            type: attachment.type,
            size: attachment.size,
            file: attachment.file
          }))
        }
        
        // 触发提交事件
        emit('submit', recordData)
        
        // 重置表单
        resetForm()
      } catch (error) {
        console.error('提交记录时出错:', error)
        alert('提交记录失败，请重试！')
      } finally {
        isSubmitting.value = false
      }
    }
    
    // 取消
    const cancel = () => {
      // 释放所有URL
      attachments.value.forEach(attachment => {
        if (attachment.url) URL.revokeObjectURL(attachment.url)
      })
      
      // 触发取消事件
      emit('cancel')
    }
    
    // 重置表单
    const resetForm = () => {
      content.value = ''
      attachments.value = []
      hours.value = 0
      minutes.value = 0
      contentError.value = ''
    }
    
    return {
      fileInput,
      content,
      contentError,
      attachments,
      hours,
      minutes,
      isSubmitting,
      currentDate,
      triggerFileInput,
      handleFileUpload,
      onFileDrop,
      removeFile,
      getFileTypeIcon,
      formatFileSize,
      submitRecord,
      cancel
    }
  }
}
</script>

<style scoped>
.record-form-modal {
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
  max-width: 500px;
  max-height: 90vh;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  z-index: 1001;
  animation: modal-appear 0.3s ease-out;
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

.record-form {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  padding: 16px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  color: #374151;
  display: flex;
  align-items: center;
}

.form-title i {
  margin-right: 8px;
  color: #2563eb;
}

.form-content {
  padding: 20px 16px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.step-info,
.date-info {
  font-size: 14px;
}

.step-label,
.date-label {
  font-weight: 500;
  color: #4b5563;
  margin-right: 4px;
}

.step-name {
  color: #111827;
  font-weight: 600;
}

.date-value {
  color: #111827;
}

.form-field {
  margin-bottom: 16px;
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.optional-text {
  font-weight: normal;
  color: #6b7280;
  font-size: 12px;
}

.content-input {
  width: 100%;
  height: 120px;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  transition: border-color 0.2s;
}

.content-input:focus {
  border-color: #3b82f6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.content-input.has-error {
  border-color: #ef4444;
}

.error-message {
  font-size: 13px;
  color: #ef4444;
  margin-top: 4px;
}

.attachment-uploader {
  margin-bottom: 16px;
}

.upload-dropzone {
  border: 2px dashed #d1d5db;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.upload-dropzone:hover {
  border-color: #93c5fd;
  background-color: #f0f9ff;
}

.upload-dropzone i {
  font-size: 24px;
  color: #6b7280;
  margin-bottom: 8px;
  display: block;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  display: block;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 13px;
  color: #6b7280;
}

.file-input {
  display: none;
}

.attachment-list {
  margin-top: 12px;
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
  padding: 8px 12px;
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

.remove-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  background-color: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.remove-btn:hover {
  background-color: #fee2e2;
  color: #b91c1c;
}

.time-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-input {
  width: 80px;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}

.time-input:focus {
  border-color: #3b82f6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.time-unit {
  font-size: 14px;
  color: #6b7280;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
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
  transition: all 0.2s;
}

.cancel-btn:hover {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.submit-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  background-color: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
}

.submit-btn:hover {
  background-color: #1d4ed8;
}

.submit-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.submit-btn i {
  margin-right: 8px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-spin {
  animation: spin 1s linear infinite;
}
</style> 