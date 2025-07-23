<template>
  <div class="task-detail-page">
    <!-- 顶部导航 -->
    <task-detail-header
      :task="task"
      :canViewLocation="canViewLocation"
      :canEditTask="canEditTask"
      :showOptions="showOptions"
      @go-back="goBack"
      @edit-task="editTask"
      @show-task-options="showTaskOptions"
      @hide-task-options="hideTaskOptions"
    />

    <!-- 任务选项菜单 -->
    <div class="task-options-menu" v-if="showOptions">
      <div class="options-backdrop" @click="hideTaskOptions"></div>
      <div class="options-content">
        <div class="option-item" @click="editTask">
          <i class="icon-edit"></i>
          <span>编辑任务</span>
        </div>
        <div class="option-item" v-if="isAdmin" @click="handleAssignTask">
          <i class="icon-user-plus"></i>
          <span>重新分配</span>
        </div>
        <div class="option-item" v-if="isAdmin" @click="duplicateTask">
          <i class="icon-copy"></i>
          <span>复制任务</span>
        </div>
        <div class="option-item danger" v-if="isAdmin" @click="confirmCancelTask">
          <i class="icon-ban"></i>
          <span>取消任务</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <i class="icon-error"></i>
      <p>{{ error }}</p>
      <div class="error-actions">
        <button class="retry-btn" @click="loadTaskDetail">重试</button>
        <button class="back-btn" @click="goBack">返回列表</button>
      </div>
    </div>

    <template v-else-if="task">
      <!-- 任务基本信息 -->
      <task-base-info-card :task="task" />
      
      <!-- 客户信息 -->
      <task-customer-info-card
        :task="task"
        :canViewCustomer="canViewCustomer"
      />

      <!-- 销售信息 (新增) -->
      <task-sales-info-card
        v-if="task.salesId"
        :salesId="task.salesId"
        :salesName="salesName"
        :salesContact="salesContact"
        :salesAvatar="salesAvatar"
        :salesDepartment="salesDepartment"
      />
      
      <!-- 设备信息 -->
      <task-device-info-card
        v-if="task.deviceName"
        :task="task"
      />
      
      <!-- 客户评价 -->
      <task-customer-feedback-card 
        v-if="task.customerComments && task.customerComments.length > 0"
        :comments="task.customerComments"
      />
      
      <!-- 任务流程 -->
      <task-flow-controller
        v-if="task && task.steps && task.steps.length > 0"
        :task="task"
        :hasPrevStep="hasPrevStep"
        :hasNextStep="hasNextStep"
        :canManageFlow="canManageFlow"
        @prev-step="prevStep"
        @next-step="nextStep"
        @show-add-record="showAddRecordForm"
        @show-step-records="showStepRecords"
      />

      <!-- 协作工程师 -->
      <collaborators-panel
        v-if="task && task.engineers"
        :engineers="task.engineers"
        :canManageCollaborators="canManageCollaborators"
        @invite="showInviteDialog"
        @view-profile="viewEngineerProfile"
      />
      
      <!-- 转出任务卡片 -->
      <task-transfer-panel
        v-if="isResponsibleEngineer"
        @transfer="showTransferDialog"
      />
    </template>

    <!-- 无数据提示 -->
    <div v-if="!loading && !error && !task" class="no-data-container">
      <i class="icon-info-circle"></i>
      <p>未找到任务数据</p>
      <div class="error-actions">
        <button class="retry-btn" @click="goBack">返回任务列表</button>
        <button class="force-refresh-btn" @click="forceRefresh">强制刷新</button>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-actions" v-if="task">
      <button 
        class="prev-step-btn" 
        :disabled="!hasPrevStep" 
        @click="prevStep"
      >
        上一步
      </button>
      <button 
        class="next-step-btn" 
        :disabled="!hasNextStep" 
        @click="nextStep"
      >
        下一步
      </button>
    </div>

    <!-- 各种对话框组件，根据状态动态显示 -->
    <!-- 这些组件将通过动态导入实现 -->
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { ref, computed, onMounted } from 'vue'
// 导入子组件
import TaskDetailHeader from './TaskDetailHeader.vue'
import TaskBaseInfoCard from './TaskBaseInfoCard.vue'
import TaskCustomerInfoCard from './TaskCustomerInfoCard.vue'
import TaskSalesInfoCard from './TaskSalesInfoCard.vue'
import TaskDeviceInfoCard from './TaskDeviceInfoCard.vue'
import TaskCustomerFeedbackCard from './TaskCustomerFeedbackCard.vue'
import TaskFlowController from './TaskFlowController.vue'
import CollaboratorsPanel from './CollaboratorsPanel.vue'
import TaskTransferPanel from './TaskTransferPanel.vue'

// 导入任务存储以使用缓存机制
import { useTaskStore } from '../../../stores/task'
// 导入API函数
import { 
  getTaskDetail, 
  getTaskFlow,
  uploadTaskAttachments,
  inviteCollaborator,
  removeCollaborator,
  transferTask,
  assignEngineers,
  BASE_URL
} from '../../../api/task'
// 导入任务流程相关API函数
import {
  addTaskFlowRecord,
  deleteTaskFlowRecord,
  downloadTaskFlowAttachment,
  decideSiteVisit
} from '../../../api/taskflow'
// 导入HTTP实例，用于直接API调用
import http from '../../../api/http'
// 导入日期格式化函数
import { format } from 'date-fns'
// 导入认证store
import { useAuthStore } from '../../../stores/auth'

export default {
  name: 'TaskDetailContainer',
  components: {
    TaskDetailHeader,
    TaskBaseInfoCard,
    TaskCustomerInfoCard,
    TaskSalesInfoCard,
    TaskDeviceInfoCard,
    TaskCustomerFeedbackCard,
    TaskFlowController,
    CollaboratorsPanel,
    TaskTransferPanel
  },
  props: {
    id: {
      type: String,
      required: false
    }
  },
  setup(props) {
    // 使用认证store
    const authStore = useAuthStore()
    
    const router = useRouter()
    const route = useRoute()
    const task = ref(null)
    const loading = ref(true) // 默认为加载中状态
    const error = ref(null)
    const selectedOptions = ref({})
    const showOptions = ref(false) // 任务选项菜单是否显示
    
    // 销售相关信息
    const salesName = ref('')
    const salesContact = ref('')
    const salesAvatar = ref('')
    const salesDepartment = ref('')
    
    // 角色权限相关
    const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'))
    const isEngineer = computed(() => authStore.hasRole('ROLE_ENGINEER'))
    const isSales = computed(() => authStore.hasRole('ROLE_CUSTOMER'))
    const isWarehouse = computed(() => authStore.hasRole('ROLE_WAREHOUSE'))
    
    // 任务负责与协作相关
    const isResponsibleEngineer = computed(() => {
      if (!task.value || !task.value.engineers) return false
      const currentUserId = authStore.user?.id
      return task.value.engineers.some(eng => eng.id === currentUserId && eng.isOwner)
    })
    
    const isCollaborator = computed(() => {
      if (!task.value || !task.value.engineers) return false
      const currentUserId = authStore.user?.id
      return task.value.engineers.some(eng => eng.id === currentUserId)
    })
    
    // 权限控制计算属性
    const canViewLocation = computed(() => true) // 所有角色都可查看位置
    const canEditTask = computed(() => {
      return isAdmin.value || 
             (isEngineer.value && isResponsibleEngineer.value) || 
             isSales.value
    })
    const canChangeTaskStatus = computed(() => {
      return isAdmin.value || 
             (isEngineer.value && isResponsibleEngineer.value)
    })
    const canViewCustomer = computed(() => {
      return isAdmin.value || isEngineer.value || isSales.value
    })
    const canManageFlow = computed(() => {
      return isAdmin.value || 
             (isEngineer.value && (isResponsibleEngineer.value || isCollaborator.value))
    })
    const canManageCollaborators = computed(() => {
      return isAdmin.value || 
             (isEngineer.value && isResponsibleEngineer.value)
    })
    
    // 计算任务步骤相关属性
    const hasPrevStep = computed(() => {
      if (!task.value || !task.value.steps) return false
      const currentStepIndex = task.value.steps.findIndex(step => step.status === 'in-progress')
      return currentStepIndex > 0
    })
    
    const hasNextStep = computed(() => {
      if (!task.value || !task.value.steps) return false
      const currentStepIndex = task.value.steps.findIndex(step => step.status === 'in-progress')
      return currentStepIndex !== -1 && currentStepIndex < task.value.steps.length - 1
    })

    // 加载任务详情
    const loadTaskDetail = async () => {
      loading.value = true
      error.value = null
      
      try {
        const taskId = props.id || route.params.id
        
        if (!taskId) {
          error.value = '未找到任务ID'
          loading.value = false
          return
        }
        
        // 从taskStore获取任务详情
        const taskStore = useTaskStore()
        const taskData = await taskStore.fetchTaskDetail(taskId)
        
        if (taskData) {
          task.value = taskData
          
          // 加载销售信息
          if (task.value.salesId) {
            await loadSalesInfo(task.value.salesId)
          }
          
          // 加载任务流程
          await loadTaskFlow(taskId)
        } else {
          error.value = taskStore.error || '加载任务详情失败'
        }
      } catch (err) {
        error.value = err.message || '加载任务详情时发生错误'
      } finally {
        loading.value = false
      }
    }
    
    // 加载销售信息（新增）
    const loadSalesInfo = async (salesId) => {
      if (!salesId) return
      
      try {
        // 这里应该调用API获取销售信息
        // 假设有一个获取用户信息的API
        const response = await http.get(`/api/v1/user/${salesId}`)
        
        if (response.data && response.data.code === 200) {
          const salesData = response.data.data
          salesName.value = salesData.name || '未知销售'
          salesContact.value = salesData.mobile || '无联系方式'
          salesAvatar.value = salesData.avatar || ''
          salesDepartment.value = salesData.department || '销售部'
        }
      } catch (err) {
        console.error('加载销售信息失败:', err)
        // 设置默认值
        salesName.value = '未知销售'
        salesContact.value = '无联系方式'
      }
    }
    
    // 加载任务流程
    const loadTaskFlow = async (taskId) => {
      if (!taskId) {
        if (task.value && task.value.taskId) {
          taskId = task.value.taskId
        } else {
          console.warn('尝试加载任务流程，但任务ID不存在')
          return
        }
      }
      
      try {
        const taskStore = useTaskStore()
        const flowData = await taskStore.fetchTaskFlow(taskId)
        
        if (flowData) {
          // 更新任务流程数据
          if (flowData.steps && Array.isArray(flowData.steps)) {
            task.value.steps = flowData.steps
          }
          
          if (flowData.records && Array.isArray(flowData.records)) {
            task.value.records = flowData.records
          }
        } else {
          console.warn('加载任务流程失败:', taskStore.error)
        }
      } catch (err) {
        console.error('加载任务流程错误:', err)
      }
    }
    
    // 导航返回
    const goBack = () => {
      router.back()
    }
    
    // 强制刷新
    const forceRefresh = async () => {
      console.log('强制刷新任务详情')
      await loadTaskDetail()
    }
    
    // 显示任务选项菜单
    const showTaskOptions = () => {
      showOptions.value = true
    }
    
    // 隐藏任务选项菜单
    const hideTaskOptions = () => {
      showOptions.value = false
    }
    
    // 编辑任务
    const editTask = () => {
      const taskId = route.params.id
      if (taskId) {
        router.push({
          name: 'EditTask',
          params: { id: taskId }
        })
      }
      hideTaskOptions()
    }
    
    // 分配任务
    const handleAssignTask = () => {
      // 打开分配对话框
      console.log('打开分配对话框')
      hideTaskOptions()
    }
    
    // 复制任务
    const duplicateTask = () => {
      // 实现复制任务功能
      console.log('复制任务')
      hideTaskOptions()
    }
    
    // 确认取消任务
    const confirmCancelTask = () => {
      // 显示取消任务确认对话框
      console.log('显示取消任务确认对话框')
      hideTaskOptions()
    }
    
    // 显示添加记录表单
    const showAddRecordForm = (stepIndex) => {
      console.log('显示添加记录表单', stepIndex)
    }
    
    // 显示步骤记录
    const showStepRecords = (stepIndex) => {
      console.log('显示步骤记录', stepIndex)
    }
    
    // 查看工程师资料
    const viewEngineerProfile = (engineerId) => {
      router.push(`/engineer/${engineerId}`)
    }
    
    // 显示邀请协作对话框
    const showInviteDialog = () => {
      console.log('显示邀请协作对话框')
    }
    
    // 显示转出任务对话框
    const showTransferDialog = () => {
      console.log('显示转出任务对话框')
    }
    
    // 上一步
    const prevStep = () => {
      if (!task.value || !task.value.steps) return
      
      const currentStepIndex = task.value.steps.findIndex(step => step.status === 'in-progress')
      console.log('上一步', currentStepIndex)
    }
    
    // 下一步
    const nextStep = () => {
      if (!task.value || !task.value.steps) return
      
      const currentStepIndex = task.value.steps.findIndex(step => step.status === 'in-progress')
      console.log('下一步', currentStepIndex)
    }
    
    // 组件挂载时加载任务详情
    onMounted(() => {
      console.log('TaskDetailContainer组件挂载，准备加载任务详情')
      loadTaskDetail()
    })
    
    return {
      task,
      loading,
      error,
      showOptions,
      salesName,
      salesContact,
      salesAvatar,
      salesDepartment,
      isAdmin,
      isEngineer,
      isSales,
      isWarehouse,
      isResponsibleEngineer,
      canViewLocation,
      canEditTask,
      canChangeTaskStatus,
      canViewCustomer,
      canManageFlow,
      canManageCollaborators,
      hasPrevStep,
      hasNextStep,
      goBack,
      loadTaskDetail,
      showTaskOptions,
      hideTaskOptions,
      editTask,
      handleAssignTask,
      duplicateTask,
      confirmCancelTask,
      showAddRecordForm,
      showStepRecords,
      viewEngineerProfile,
      showInviteDialog,
      showTransferDialog,
      prevStep,
      nextStep,
      forceRefresh
    }
  }
}
</script>

<style scoped>
.task-detail-page {
  min-height: 100vh;
  background-color: #f8fafc;
  padding-bottom: 60px;
  animation: fadeInPage 0.3s ease;
}

@keyframes fadeInPage {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* 底部操作栏样式 */
.bottom-actions {
  position: fixed;
  bottom: 8px;
  left: 12px;
  right: 12px;
  background-color: rgba(255, 255, 255, 0.95);
  padding: 12px 16px;
  box-shadow: 0 -1px 4px rgba(0, 0, 0, 0.05);
  z-index: 10;
  border-top: 1px solid #f3f4f6;
  border-radius: 12px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.prev-step-btn {
  flex: 1;
  padding: 12px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.prev-step-btn:hover {
  background-color: #e5e7eb;
}

.prev-step-btn:disabled {
  background-color: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
}

.next-step-btn {
  flex: 1;
  padding: 12px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.next-step-btn:hover {
  background-color: #2563eb;
}

.next-step-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

/* 任务选项菜单 */
.task-options-menu {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.options-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
}

.options-content {
  position: relative;
  margin-top: 60px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 80%;
  max-width: 300px;
  animation: slideDown 0.2s ease-out;
  overflow: hidden;
}

@keyframes slideDown {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.option-item {
  padding: 16px;
  display: flex;
  align-items: center;
  color: #1f2937;
  cursor: pointer;
  transition: background-color 0.2s;
}

.option-item:hover {
  background-color: #f9fafb;
}

.option-item i {
  margin-right: 12px;
  font-size: 18px;
  color: #4b5563;
  width: 24px;
  text-align: center;
}

.option-item.danger {
  color: #dc2626;
}

.option-item.danger i {
  color: #dc2626;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(59, 130, 246, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误容器 */
.error-container, .no-data-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.icon-error {
  font-size: 48px;
  color: #ef4444;
  margin-bottom: 16px;
}

.icon-info-circle {
  font-size: 48px;
  color: #3b82f6;
  margin-bottom: 16px;
}

.error-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

.retry-btn,
.back-btn,
.force-refresh-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn {
  background-color: #3b82f6;
  color: white;
}

.retry-btn:hover {
  background-color: #2563eb;
}

.back-btn {
  background-color: #6b7280;
  color: white;
}

.back-btn:hover {
  background-color: #4b5563;
}

.force-refresh-btn {
  background-color: #f59e0b;
  color: white;
}

.force-refresh-btn:hover {
  background-color: #d97706;
}
</style> 