<template>
  <div class="task-flow-card">
    <div class="card-header">
      <h3 class="card-title">
        <i class="fas fa-tasks"></i>
        任务流程
      </h3>
    </div>
    
    <div class="task-flow-content">
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

            <!-- 记录展示区 -->
            <div class="records-display-area" v-if="step.records && step.records.length > 0">
              <div
                v-for="record in step.records"
                :key="record.id"
                class="record-summary"
                @click="emitPreviewRecord(record)"
              >
                <span class="record-creator"><i class="fas fa-user-edit"></i> {{ record.creatorName }}</span>
                <span class="record-time">{{ record.createTime }}</span>
              </div>
            </div>
            
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
              
              <!-- 报价部分 -->
              <div class="quote-section" v-if="isQuoteStep(step)">
                <div class="quote-status">
                  <span>报价金额：</span>
                  <span v-if="task.price > 0" class="price-value">¥{{ task.price }}</span>
                  <span v-else class="no-price">未设置报价</span>
                  
                  <span v-if="task.price > 0" :class="['status-tag', task.priceConfirmed ? 'confirmed' : 'unconfirmed']">
                    {{ task.priceConfirmed ? '客户已确认' : '待确认' }}
                  </span>
                </div>

                <div class="site-visit-options">
                  <button class="site-visit-btn" @click="openQuoteDialog">
                    <i class="fas fa-edit"></i>
                    填写报价
                  </button>
                  <button class="site-visit-btn" @click="activeConfirmDialogIndex = index" :disabled="!task.price">
                    <i class="fas fa-paper-plane"></i>
                    发送给客户
                  </button>
                </div>
              </div>

              <!-- 内联发送报价确认对话框 -->
              <div v-if="activeConfirmDialogIndex === index" class="send-quote-dialog-overlay" @click="activeConfirmDialogIndex = null">
                <div class="send-quote-dialog" @click.stop>
                  <div class="dialog-header">
                    <h3 class="dialog-title">确认发送报价</h3>
                  </div>
                  <div class="dialog-content">
                    <p class="dialog-message">您确定要将此报价发送给客户吗？此操作将通过小程序通知客户。</p>
                  </div>
                  <div class="dialog-actions">
                    <button class="cancel-btn" @click="activeConfirmDialogIndex = null" :disabled="sendingQuote">取消</button>
                    <button class="confirm-btn" @click="confirmAndSendQuote" :disabled="sendingQuote">
                      <span v-if="sendingQuote" class="spinner"></span>
                      <span v-else>确认</span>
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
import { useToast } from 'vue-toastification'
import http from '../../../api/http'
import { useTaskFlowStore } from '../../../stores/taskFlow'
import eventBus, { TaskFlowEvents } from '../../../utils/eventBus'
import { TASK_TYPE_FLOW_STEPS } from '../../../constants/taskFlowTemplates'
import { notifyCustomer } from '../../../api/task' 

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
  emits: ['prev-step', 'next-step', 'show-add-record', 'show-record-preview', 'show-quote-dialog', 'reload-task', 'send-quote'],
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

    // 诊断用的watch，监控task.price的变化
    watch(() => props.task.price, (newPrice, oldPrice) => {
      console.log(`%c[TaskFlowController] Detected price change: ${oldPrice} -> ${newPrice}`, 'color: green; font-weight: bold;');
    });
    
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
    
    // 判断是否是报价步骤
    const isQuoteStep = (step) => {
      if (!step) return false;
      // 同时检查 step.name 和 step.title，并提供一个空字符串作为备用，防止其中一个为null或undefined
      const stepName = step.name || step.title || '';
      return stepName.includes('报价');
    };

    // 打开报价对话框
    const openQuoteDialog = () => {
      emit('show-quote-dialog');
    }

    // 发送报价给客户
    const sendQuoteToCustomer = () => {
      console.log('sendQuoteToCustomer in TaskFlowController called');
      emit('send-quote');
    };

    // 调试用的发送报价函数
    const debugSendQuote = () => {
      console.log('Debug: Button clicked!');
      alert('按钮已被点击，即将触发事件');
      sendQuoteToCustomer();
    };
    
    // 用于直接在组件内处理报价发送的状态和方法
    const activeConfirmDialogIndex = ref(null); // <--- Changed from showSendQuoteConfirm
    const sendingQuote = ref(false);
    const sendError = ref('');
    
    // 确认并发送报价
    const confirmAndSendQuote = async () => {
      if (!props.task || !props.task.taskId) {
        alert('无法获取任务ID，请刷新页面后重试');
        return;
      }
      
      try {
        sendingQuote.value = true;
        sendError.value = '';
        
        console.log('直接调用notifyCustomer API，taskId:', props.task.taskId);
        const response = await notifyCustomer(props.task.taskId);
        console.log('API响应:', response);
        
        if (response && response.code === 200) {
          alert('报价已成功发送给客户');
          activeConfirmDialogIndex.value = null; // <--- Changed
          // 通知父组件刷新任务数据
          emit('reload-task');
        } else {
          throw new Error(response?.message || '发送失败，请重试');
        }
      } catch (err) {
        console.error('发送报价通知失败:', err);
        sendError.value = err.message || '发送失败，请检查网络连接或联系管理员';
        alert(sendError.value);
      } finally {
        sendingQuote.value = false;
      }
    };
    
    // 处理发送报价按钮点击
    const handleSendQuoteClick = () => {
      console.log('发送报价按钮被点击');
      if (!props.task.price) {
        console.log('任务没有报价，按钮应该被禁用');
        return;
      }
      alert('发送报价按钮已点击，将显示确认对话框');
      activeConfirmDialogIndex.value = currentStepIndex.value; // Use the correct index
    };

    // 显示添加记录表单
    const showAddRecordForm = (stepIndex) => {
      emit('show-add-record', stepIndex)
    }

    const emitPreviewRecord = (record) => {
      emit('show-record-preview', record);
    };
    
    // 显示步骤记录
    const showStepRecords = (stepIndex) => {
      // 此方法已废弃，由 emitPreviewRecord 替代
      console.warn("showStepRecords is deprecated.");
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
      // getImageAttachments, // 移除
      // getNonImageAttachments, // 移除
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
      localDecision,
      isQuoteStep,
      openQuoteDialog,
      activeConfirmDialogIndex, // <--- Changed
      sendingQuote,
      confirmAndSendQuote,
      handleSendQuoteClick,
      emitPreviewRecord
    }
  },
};
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

.records-display-area {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.record-summary {
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-summary:hover {
  background-color: #f3f4f6;
  border-color: #d1d5db;
}

.record-creator i {
  margin-right: 6px;
  color: #9ca3af;
}

.record-time {
  color: #6b7280;
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

.quote-section {
  margin-top: 10px;
}

.quote-status {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.price-value {
  font-weight: 600;
  color: #10b981;
}

.no-price {
  color: #f59e0b;
}

.status-tag {
  margin-left: 10px;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.confirmed {
  background-color: #10b981;
}

.unconfirmed {
  background-color: #f59e0b;
}

/* 发送报价确认对话框 */
.send-quote-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
  z-index: 1000;
}

.send-quote-dialog {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  text-align: center;
  max-width: 400px;
  width: 90%;
}

.send-quote-dialog h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.send-quote-dialog p {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 20px;
  line-height: 1.5;
}

.dialog-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #2563eb;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.dialog-error {
  color: #dc3545;
  font-size: 14px;
  margin-bottom: 20px;
}

.dialog-actions {
  display: flex;
  justify-content: space-around;
  gap: 10px;
}

.dialog-actions .cancel-btn {
  background-color: #f3f4f6;
  border: 1px solid #d1d5db;
  color: #4b5563;
  padding: 8px 15px;
}

.dialog-actions .confirm-btn {
  background-color: #2563eb;
  border: 1px solid #1d4ed8;
  color: white;
  padding: 8px 15px;
}

/* 新的报价按钮样式 */
.new-quote-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
  border: 1px solid #d1d5db;
  cursor: pointer;
  background-color: #f3f4f6;
  color: #4b5563;
  transition: all 0.2s ease;
}

.new-quote-btn i {
  margin-right: 4px;
  font-size: 10px;
}

.new-quote-btn:hover:not(.disabled) {
  background-color: #e5e7eb;
}

.new-quote-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 