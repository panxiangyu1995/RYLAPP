<template>
  <div class="task-flow-card">
    <div class="card-header">
      <h3 class="card-title">
        <i class="fas fa-tasks"></i>
        任务流程
      </h3>
    </div>
    
    <div class="task-flow-content">
      <!-- 当前步骤详情 -->
      <div class="current-step" v-if="currentStep">
        <div class="step-header">
          <h4 class="step-title">{{ currentStep.name || currentStep.title }}</h4>
          <span class="step-status" :class="`status-${currentStep.status}`">
            {{ getStepStatusText(currentStep.status) }}
          </span>
        </div>
        
        <div class="step-meta">
          <div class="meta-item" v-if="currentStep.startDate">
            <i class="fas fa-calendar"></i>
            <span>开始日期: {{ formatDate(currentStep.startDate) }}</span>
          </div>
          <div class="meta-item" v-if="currentStep.dueDate">
            <i class="fas fa-clock"></i>
            <span>预计完成: {{ formatDate(currentStep.dueDate) }}</span>
          </div>
          <div class="meta-item" v-if="currentStep.completedDate">
            <i class="fas fa-check-circle"></i>
            <span>完成日期: {{ formatDate(currentStep.completedDate) }}</span>
          </div>
        </div>
        
        <!-- 步骤记录概览 -->
        <div class="step-records" v-if="currentStepRecords && currentStepRecords.length > 0">
          <div class="records-header">
            <h5>工作记录</h5>
            <button @click="showStepRecords(currentStepIndex)" class="view-all-btn">
              查看全部
            </button>
          </div>
          
          <div class="records-preview">
            <div 
              v-for="(record, recordIndex) in visibleStepRecords"
              :key="recordIndex"
              class="record-item"
              @click="previewRecord(record)"
            >
              <div class="record-content">
                <div class="record-date">{{ formatDateTime(record.createdAt) }}</div>
                <p class="record-text">{{ truncateText(record.content, 100) }}</p>
                
                <!-- 图片预览缩略图 -->
                <div class="record-images-preview" v-if="record.attachments && getImageAttachments(record.attachments).length > 0">
                  <div class="images-grid">
                    <div 
                      v-for="(image, imgIndex) in getImageAttachments(record.attachments).slice(0, 3)"
                      :key="imgIndex"
                      class="image-thumbnail-container"
                    >
                      <img :src="image.url" :alt="image.name" class="image-thumbnail" />
                    </div>
                    <div v-if="getImageAttachments(record.attachments).length > 3" class="more-images">
                      +{{ getImageAttachments(record.attachments).length - 3 }}
                    </div>
                  </div>
                </div>
                
                <!-- 附件信息 -->
                <div class="record-attachments" v-if="record.attachments && record.attachments.length > 0">
                  <i class="fas fa-paperclip"></i>
                  <span>{{ record.attachments.length }}个附件</span>
                </div>
              </div>
            </div>
            
            <div v-if="currentStepRecords.length > 2" class="records-more">
              <button @click="showStepRecords(currentStepIndex)" class="more-btn">
                查看更多记录 ({{ currentStepRecords.length - 2 }})
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 流程进度 -->
      <div class="flow-steps">
        <div 
          v-for="(step, index) in stepsList" 
          :key="index"
          class="flow-step"
          :class="{
            'completed': step.status === 'completed',
            'in-progress': step.status === 'in-progress',
            'pending': step.status === 'pending',
            'skipped': step.status === 'skipped'
          }"
        >
          <div class="step-indicator">
            <div class="step-node">
              <i v-if="step.status === 'completed'" class="fas fa-check"></i>
              <i v-else-if="step.status === 'in-progress'" class="fas fa-play"></i>
              <i v-else-if="step.status === 'skipped'" class="fas fa-forward"></i>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="step-line" v-if="index < stepsList.length - 1"></div>
          </div>
          <div class="step-info">
            <div class="step-name">{{ step.name || step.title }}</div>
            <div class="step-status-text">{{ getStepStatusText(step.status) }}</div>
            
            <!-- 步骤操作按钮 -->
            <div class="step-actions-container" v-if="canManageFlow">
              <!-- 是否需要上门选择（只在"判断是否需要上门"步骤且未完成时显示） -->
              <div class="site-visit-decision-buttons" v-if="step.stepKey === 'site-visit-decision' && step.status !== 'completed'">
                <div class="site-visit-options">
                <button 
                  class="site-visit-btn need-visit" 
                  @click="showVisitTimeSelector = true"
                  :class="{ 'selected': localDecision.requiresVisit === true }"
                >
                  <i class="fas fa-address-card"></i>
                  需要上门
                </button>
                <button 
                  class="site-visit-btn no-visit" 
                  @click="updateLocalDecision(false, null)"
                  :class="{ 'selected': localDecision.requiresVisit === false }"
                >
                  <i class="fas fa-video"></i>
                  远程协助
                </button>
                </div>
                
                <!-- 约定上门时间选择器 -->
                <div class="visit-time-selector" v-if="showVisitTimeSelector">
                  <div class="selector-header">
                    <h5>选择约定上门时间</h5>
                  </div>
                  <div class="selector-content">
                    <div class="datetime-input-group">
                      <input 
                        v-model="visitDate" 
                        type="date"
                        class="date-input"
                      />
                      <input 
                        v-model="visitTime" 
                        type="time"
                        class="time-input"
                      />
                    </div>
                  </div>
                  <div class="selector-actions">
                    <button 
                      class="cancel-btn" 
                      @click="showVisitTimeSelector = false"
                    >
                      取消
                    </button>
                    <button 
                      class="confirm-btn" 
                      @click="confirmLocalSiteVisit"
                    >
                      <i v-if="loading" class="fas fa-spinner fa-spin"></i>
                      确认
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 编辑记录按钮 -->
              <button 
                class="step-record-btn" 
                @click="showAddRecordForm(index)"
              >
                <i class="fas fa-edit"></i>
                编辑记录
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, defineExpose } from 'vue'
import { format } from 'date-fns'
import http from '../../../api/http'
import { useTaskFlowStore } from '../../../stores/taskFlow'
import eventBus, { TaskFlowEvents } from '../../../utils/eventBus'
import { TASK_TYPE_FLOW_STEPS } from '../../../constants/taskFlowTemplates'

export default {
  name: 'TaskFlowController',
  props: {
    task: {
      type: Object,
      required: true
    },
    taskType: {
      type: String,
      required: true
    },
    canManageFlow: {
      type: Boolean,
      default: false
    },
    hasPrevStep: {
      type: Boolean,
      default: false
    },
    hasNextStep: {
      type: Boolean,
      default: false
    }
  },
  emits: ['prev-step', 'next-step', 'show-add-record', 'show-step-records'],
  setup(props, { emit }) {
    const currentStepValue = ref(0)
    const loading = ref(false)
    const error = ref(null)
    
    // 上门时间选择器相关状态
    const showVisitTimeSelector = ref(false)
    const visitDate = ref(format(new Date(), 'yyyy-MM-dd'))
    const visitTime = ref('09:00')

    // 本地决策状态
    const localDecision = ref({
      requiresVisit: null,       // boolean | null
      visitAppointmentTime: null // string | null
    })

    // 监听任务属性，初始化本地决策状态
    watch(() => props.task, (newTask) => {
      if (newTask) {
        // 后端 1=true, 0=false, null=未定
        const requiresVisit = newTask.needSiteVisit === 1 ? true : (newTask.needSiteVisit === 0 ? false : null)
        localDecision.value = {
          requiresVisit: requiresVisit,
          visitAppointmentTime: newTask.visitAppointmentTime || null
        }
        // 如果有已保存的上门时间，则预填到选择器中
        if (newTask.visitAppointmentTime) {
          try {
            const d = new Date(newTask.visitAppointmentTime)
            visitDate.value = format(d, 'yyyy-MM-dd')
            visitTime.value = format(d, 'HH:mm')
          } catch(e) {
            console.error("无法解析已保存的上门时间", e)
          }
        }
      }
    }, { immediate: true, deep: true })
    
    // 任务类型映射表 - 将API返回的类型映射到我们定义的类型
    const taskTypeMapping = {
      'REPAIR': 'repair',
      'MAINTENANCE': 'maintenance',
      'RECYCLE': 'recycle',
      'RENTAL': 'rental',
      'TRAINING': 'training',
      'VERIFICATION': 'verification',
      'SELECTION': 'selection',
      'INSTALLATION': 'installation'
    }
    
    // 使用taskFlowStore
    const taskFlowStore = useTaskFlowStore()
    
    // 预览相关状态
    const previewingRecord = ref(null)
    const previewImageUrl = ref(null)
    
    // 步骤列表 - 使用taskFlowStore中的数据
    const stepsList = computed(() => {
      console.log('使用taskFlowStore中的步骤数据:', taskFlowStore.flowSteps)
      return taskFlowStore.flowSteps || []
    })
    
    // 监听任务变化，确保步骤数据与taskFlowStore同步
    watch(() => props.task, (newTask) => {
      if (newTask && newTask.id && (!taskFlowStore.currentTaskId || taskFlowStore.currentTaskId !== newTask.id)) {
        // 如果是新任务，加载任务流程
        taskFlowStore.fetchTaskFlow(newTask.id)
      }
    }, { immediate: true })
    
    // 监听taskFlowStore的状态变化
    watch(() => taskFlowStore.flowSteps, (newSteps) => {
      // 当taskFlowStore的步骤数据更新时，发出事件通知
      if (newSteps && newSteps.length > 0) {
        eventBus.emit(TaskFlowEvents.FLOW_UPDATED, {
          taskId: taskFlowStore.currentTaskId,
          flowSteps: newSteps
        })
      }
    })
    
    // 当前步骤索引
    const currentStepIndex = computed(() => taskFlowStore.currentStepIndex)
    
    // 当前步骤
    const currentStep = computed(() => {
      if (stepsList.value && currentStepIndex.value >= 0 && currentStepIndex.value < stepsList.value.length) {
        return stepsList.value[currentStepIndex.value]
      }
      return null
    })
    
    // 当前步骤的记录
    const currentStepRecords = computed(() => {
      if (!currentStep.value || !currentStep.value.stepId || !props.task.records) return []
      
      return props.task.records.filter(record => record.stepId === currentStep.value.stepId)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    })
    
    // 可见的步骤记录（最多显示前两个）
    const visibleStepRecords = computed(() => {
      return currentStepRecords.value.slice(0, 2)
    })

    // 是否是"是否需要上门"决策步骤
    const isSiteVisitStep = computed(() => {
      if (!currentStep.value) return false
      return currentStep.value.title === '判断是否需要上门' || 
             currentStep.value.type === 'site-visit-decision'
    })
    
    // 获取步骤图标
    const getStepIcon = (step) => {
      if (step.status === 'completed') {
        return 'fas fa-check-circle'
      } else if (step.status === 'in-progress') {
        return 'fas fa-circle-play'
      } else {
        return 'fas fa-circle-dot'
      }
    }
    
    // 获取步骤状态文本
    const getStepStatusText = (status) => {
      switch(status) {
        case 'completed': return '已完成'
        case 'in-progress': return '进行中'
        case 'pending': return '待执行'
        case 'skipped': return '已跳过'
        default: return '未知状态'
      }
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return '未设置'
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd')
      } catch (err) {
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
        return dateStr
      }
    }
    
    // 截断文本
    const truncateText = (text, maxLength) => {
      if (!text) return ''
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
    }
    
    // 显示添加记录表单
    const showAddRecordForm = (stepIndex) => {
      emit('show-add-record', stepIndex)
    }
    
    // 显示步骤记录
    const showStepRecords = (stepIndex) => {
      emit('show-step-records', stepIndex)
    }
    
    // 更新本地决策状态
    const updateLocalDecision = (requiresVisit, visitTime = null) => {
      localDecision.value = {
        requiresVisit: requiresVisit,
        visitAppointmentTime: visitTime
      }
    }

    // 确认上门访问并更新本地状态
    const confirmLocalSiteVisit = () => {
      const visitAppointmentTime = `${visitDate.value}T${visitTime.value}:00`
      updateLocalDecision(true, visitAppointmentTime)
      showVisitTimeSelector.value = false
    }
    
    // 过滤图片附件
    const getImageAttachments = (attachments) => {
      if (!attachments || !attachments.length) return []
      
      return attachments.filter(attachment => 
        (attachment.type && attachment.type.startsWith('image/')) || 
        (attachment.name && attachment.name.match(/\.(jpg|jpeg|png|gif|webp)$/i))
      )
    }
    
    // 过滤非图片附件
    const getNonImageAttachments = (attachments) => {
      if (!attachments || !attachments.length) return []
      
      return attachments.filter(attachment => 
        !(attachment.type && attachment.type.startsWith('image/')) && 
        !(attachment.name && attachment.name.match(/\.(jpg|jpeg|png|gif|webp)$/i))
      )
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
    
    // 预览记录
    const previewRecord = (record) => {
      previewingRecord.value = record
    }
    
    // 关闭记录预览
    const closeRecordPreview = () => {
      previewingRecord.value = null
    }
    
    // 预览图片
    const previewImage = (url) => {
      previewImageUrl.value = url
    }
    
    // 关闭图片预览
    const closeImagePreview = () => {
      previewImageUrl.value = null
    }
    
    // 更新任务步骤
    const updateStep = async (step) => {
      if (!props.canEdit) return
      
      // 如果当前步骤是"判断是否需要上门"，则不允许直接更新步骤
      if (currentStepValue.value === 1) {
        return
      }
      
      loading.value = true
      error.value = null
      
      try {
        const response = await http.post(`/api/v1/task/${props.task.id}/step`, {
          step: step
        })
        
        if (response && response.code === 200) {
          currentStepValue.value = step
          emit('update-step', step)
        } else {
          throw new Error(response?.message || '更新步骤失败')
        }
      } catch (err) {
        error.value = err.message || '更新步骤时发生错误'
        console.error('更新任务步骤错误:', err)
      } finally {
        loading.value = false
      }
    }
    
    // 添加记录
    const addRecord = (step) => {
      emit('add-record', step)
    }

    // 下一步
    const nextStep = () => {
      emit('next-step')
    }

    // 上一步
    const prevStep = () => {
      emit('prev-step')
    }
    
    // 暴露本地状态给父组件
    defineExpose({
      localDecision
    })
    
    return {
      currentStepValue,
      loading,
      error,
      stepsList,
      currentStepIndex,
      currentStep,
      currentStepRecords,
      visibleStepRecords,
      isSiteVisitStep,
      getStepIcon,
      getStepStatusText,
      formatDate,
      formatDateTime,
      truncateText,
      showAddRecordForm,
      showStepRecords,
      updateLocalDecision,
      confirmLocalSiteVisit,
      getImageAttachments,
      getNonImageAttachments,
      getFileTypeIcon,
      formatFileSize,
      previewRecord,
      closeRecordPreview,
      previewImage,
      closeImagePreview,
      previewingRecord,
      previewImageUrl,
      updateStep,
      addRecord,
      showVisitTimeSelector,
      visitDate,
      visitTime,
      prevStep,
      nextStep,
      localDecision
    }
  }
}
</script>

<style scoped>
.task-flow-card {
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

.task-flow-content {
  padding: 16px;
}

.current-step {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.step-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
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

.status-pending {
  background-color: #f3f4f6;
  color: #6b7280;
}

.step-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
}

.meta-item i {
  margin-right: 6px;
  color: #9ca3af;
}

.step-records {
  margin-bottom: 16px;
}

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.records-header h5 {
  font-size: 15px;
  font-weight: 600;
  color: #4b5563;
  margin: 0;
}

.view-all-btn {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 13px;
  cursor: pointer;
}

.view-all-btn:hover {
  text-decoration: underline;
}

.records-preview {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-item {
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
}

.record-date {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.record-text {
  font-size: 14px;
  color: #4b5563;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.record-attachments {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #6b7280;
}

.record-attachments i {
  margin-right: 4px;
}

.records-more {
  text-align: center;
  margin-top: 8px;
}

.more-btn {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 13px;
  cursor: pointer;
  padding: 4px 8px;
}

.more-btn:hover {
  text-decoration: underline;
}

.step-actions {
  display: flex;
  gap: 12px;
}

.flow-steps {
  margin-top: 20px;
}

.flow-step {
  display: flex;
  margin-bottom: 16px;
}

.flow-step:last-child {
  margin-bottom: 0;
}

.step-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 12px;
}

.step-node {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  background-color: #f3f4f6;
  color: #6b7280;
  z-index: 1;
}

.step-line {
  width: 2px;
  height: 30px;
  background-color: #e5e7eb;
  margin: 4px 0;
}

.flow-step.completed .step-node {
  background-color: #10b981;
  color: white;
}

.flow-step.completed .step-line {
  background-color: #10b981;
}

.flow-step.in-progress .step-node {
  background-color: #f59e0b;
  color: white;
}

.step-info {
  flex: 1;
  padding-top: 4px;
}

.step-name {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 4px;
  display: block;
  white-space: nowrap;
  overflow: visible;
}

.step-status-text {
  font-size: 12px;
  color: #6b7280;
}

.flow-step.completed .step-status-text {
  color: #10b981;
}

.flow-step.in-progress .step-status-text {
  color: #f59e0b;
}

/* 图片预览缩略图 */
.record-images-preview {
  margin-top: 8px;
}

.images-grid {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.image-thumbnail-container {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.image-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.more-images {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: rgba(0, 0, 0, 0.03);
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  font-size: 12px;
  color: #6b7280;
}

/* 记录预览模态框 */
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

.record-preview-content {
  padding: 16px;
  flex: 1;
  overflow-y: auto;
}

.record-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
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

.step-actions-container {
  margin-top: 8px;
}

.step-record-btn {
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  color: #4b5563;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.step-record-btn i {
  margin-right: 4px;
  font-size: 10px;
}

.step-record-btn:hover {
  background-color: #e5e7eb;
  color: #111827;
}

.skipped .step-node {
  background-color: #d1d5db;
  color: #6b7280;
}

.skipped .step-status-text {
  color: #6b7280;
}

.skipped .step-name {
  color: #6b7280;
  text-decoration: line-through;
}

.site-visit-decision-buttons {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  margin-bottom: 8px;
  position: relative;
}

.site-visit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid #d1d5db; /* Default border */
  cursor: pointer;
  transition: all 0.2s ease;
  /* Default unselected state */
  background-color: #f3f4f6;
  color: #4b5563;
}

.site-visit-btn i {
  margin-right: 4px;
  font-size: 10px;
}

.site-visit-btn:not(.selected):hover {
  background-color: #e5e7eb;
}

.site-visit-btn.selected {
  font-weight: 600;
  background-color: #e0f2fe;
  color: #0369a1;
  border-color: #0284c7;
  box-shadow: 0 0 5px rgba(2, 132, 199, 0.3);
}

.site-visit-btn.selected:hover {
  background-color: #bae6fd;
}

.site-visit-options {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.visit-time-selector {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 10;
  width: 280px;
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  margin-top: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.selector-header {
  margin-bottom: 10px;
}

.selector-header h5 {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0;
}

.selector-content {
  margin-bottom: 15px;
}

.datetime-input-group {
  display: flex;
  gap: 10px;
}

.date-input, .time-input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.date-input {
  flex: 1;
}

.selector-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.cancel-btn, .confirm-btn {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f3f4f6;
  border: 1px solid #d1d5db;
  color: #4b5563;
}

.confirm-btn {
  background-color: #2563eb;
  border: 1px solid #1d4ed8;
  color: white;
}

.cancel-btn:hover {
  background-color: #e5e7eb;
}

.confirm-btn:hover {
  background-color: #1d4ed8;
}

.cancel-btn:disabled, .confirm-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 