<template>
  <div class="task-detail-page">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <i class="fas fa-arrow-left" @click="goBack"></i>
      <h1 class="page-title">任务详情</h1>
      <div class="nav-actions" v-if="showOptions">
        <i class="fas fa-ellipsis-v" @click="hideTaskOptions"></i>
      </div>
      <div class="nav-actions" v-else>
        <i class="fas fa-ellipsis-v" @click="showTaskOptions"></i>
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
      <!-- 任务状态卡片 -->
      <task-status-card
        :task="task"
        :statusHistory="taskStatusHistory"
        :canChangeStatus="canChangeTaskStatus"
        @change-status="showStatusChangeDialog"
      />

    <!-- 任务基本信息 -->
      <task-base-info-card :task="task" />
      
      <!-- 客户信息 -->
      <task-customer-info-card
        :task="task"
        :canViewCustomer="canViewCustomer"
      />

      <!-- 销售信息 -->
      <task-sales-info-card
        v-if="task.salesPerson"
        :salesId="task.salesPerson?.id"
        :salesName="task.salesPerson?.name"
        :salesContact="task.salesPerson?.contactNumber"
        :salesAvatar="task.salesPerson?.avatar"
        :salesDepartment="task.salesPerson?.department"
      />
        
        <!-- 设备信息 -->
      <task-device-info-card
        v-if="task.deviceName"
        :task="task"
        :canEditDevice="canEditDevice"
        @edit-device="editDeviceInfo"
      />
      
      <!-- 客户评价 -->
      <task-customer-feedback-card 
        v-if="task.customerComments && task.customerComments.length > 0"
        :comments="task.customerComments"
      />
                  
    <!-- 任务流程 -->
    <task-flow-controller
      v-if="task"
      ref="taskFlowControllerRef"
      :task="task"
      :taskType="task.taskType"
      :canManageFlow="canManageFlow"
      :hasPrevStep="hasPrevStep"
      :hasNextStep="hasNextStep"
      @prev-step="prevStep"
      @next-step="nextStep"
      @show-add-record="showAddRecord"
      @show-step-records="showStepRecords"
      @show-quote-dialog="showQuoteDialog = true"
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
        v-if="task"
        @transfer="handleTransfer"
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

    <!-- 底部导航按钮 -->
    <div class="bottom-actions" v-if="task">
        <button 
          class="prev-step-btn" 
          :disabled="!hasPrevStep" 
          @click="prevStep"
        >
          <i class="fas fa-arrow-left"></i>
          上一步
        </button>
        <button 
          class="next-step-btn" 
          :disabled="!hasNextStep" 
          @click="nextStep"
        >
          下一步
          <i class="fas fa-arrow-right"></i>
        </button>
    </div>
    
    <!-- 任务记录表单对话框 -->
    <task-step-record-form
      v-if="showRecordForm"
      :taskId="taskId"
      :stepId="task && task.steps && activeRecordStep >= 0 ? task.steps[activeRecordStep].stepId : ''"
      :stepName="task && task.steps && activeRecordStep >= 0 ? task.steps[activeRecordStep].title : '未知步骤'"
      @cancel="cancelAddRecord"
      @submit="submitRecord"
    />

    <!-- 任务状态更改对话框 -->
    <task-status-change-dialog
      v-if="showStatusDialog"
      :currentStatus="task?.status || ''"
      :taskId="taskId"
      @close="cancelStatusChange"
      @status-change="confirmStatusChange"
    />

    <!-- 协作工程师对话框 -->
    <collaboration-dialog
      v-if="showInviteForm"
      :taskId="taskId"
      :excludeEngineers="task?.engineers?.map(e => e.id) || []"
      @close="cancelInvite"
      @invite="confirmInvite"
    />

    <!-- 任务分配对话框 -->
    <task-assign-dialog
      v-if="showAssignDialog"
      :taskId="taskId"
      :currentEngineer="task?.mainEngineer?.id"
      @close="cancelAssign"
      @assign="confirmAssign"
    />

    <!-- 通用确认对话框 -->
    <confirm-dialog
      v-if="showConfirmDialog"
      :title="confirmDialogTitle"
      :message="confirmDialogMessage"
      :customContent="dialogCustomContent"
      @close="cancelDialog"
      @confirm="confirmDialog"
    />

    <!-- 图片预览对话框 -->
    <image-preview-dialog
      v-if="previewImageUrl"
      :imageUrl="previewImageUrl"
      @close="closePreview"
    />

    <!-- 任务转出对话框 -->
    <task-transfer-dialog
      v-if="task"
      :show="showTransferDialog"
      :task="task"
      @close="closeTransferDialog"
      @transfer="confirmTransferTask"
    />

    <!-- 报价对话框 -->
    <quote-dialog
      v-if="showQuoteDialog"
      :taskId="taskId"
      @close="closeQuoteDialog"
      @submit="submitQuote"
    />
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { ref, onMounted, computed, watch, onBeforeUnmount } from 'vue'
import { useToast } from 'vue-toastification'
// 导入子组件
import TaskDetailHeader from '../../components/task/detail/TaskDetailHeader.vue'
import TaskBaseInfoCard from '../../components/task/detail/TaskBaseInfoCard.vue'
import TaskCustomerInfoCard from '../../components/task/detail/TaskCustomerInfoCard.vue'
import TaskDeviceInfoCard from '../../components/task/detail/TaskDeviceInfoCard.vue'
import TaskCustomerFeedbackCard from '../../components/task/detail/TaskCustomerFeedbackCard.vue'
import TaskStepItem from '../../components/task/detail/TaskStepItem.vue'
import TaskStepRecordForm from '../../components/task/detail/TaskStepRecordForm.vue'
import TaskStatusCard from '../../components/task/detail/TaskStatusCard.vue'
import TaskFlowController from '../../components/task/detail/TaskFlowController.vue'
import CollaboratorsPanel from '../../components/task/detail/CollaboratorsPanel.vue'
import TaskTransferPanel from '../../components/task/detail/TaskTransferPanel.vue'
import TaskSalesInfoCard from '../../components/task/detail/TaskSalesInfoCard.vue'

// 导入对话框组件
import TaskStatusChangeDialog from '../../components/task/dialogs/TaskStatusChangeDialog.vue'
import CollaborationDialog from '../../components/task/dialogs/CollaborationDialog.vue'
import TaskAssignDialog from '../../components/task/dialogs/TaskAssignDialog.vue'
import ConfirmDialog from '../../components/task/dialogs/ConfirmDialog.vue'
import ImagePreviewDialog from '../../components/task/dialogs/ImagePreviewDialog.vue'
import TaskTransferDialog from '../../components/task/dialogs/TaskTransferDialog.vue'
import QuoteDialog from '../../components/task/dialogs/QuoteDialog.vue'

// 导入状态管理
import { useTaskStore } from '../../stores/task'
import { useTaskFlowStore } from '../../stores/taskFlow'
import { useAuthStore } from '../../stores/auth'

// 导入API函数
import { 
  getTaskDetail, 
  uploadTaskAttachments,
  inviteCollaborator,
  removeCollaborator,
  transferTask,
  assignEngineers,
  setTaskPrice,
  notifyCustomer,
  confirmPayment,
  BASE_URL
} from '../../api/task'

// 导入HTTP实例，用于直接API调用
import http from '../../api/http'

// 导入事件总线
import eventBus, { TaskFlowEvents } from '../../utils/eventBus'

// 导入日期格式化函数
import { format } from 'date-fns'

// 导入 addTaskFlowRecord 函数
import { addTaskFlowRecord } from '@/api/taskflow'

export default {
  name: 'TaskDetailPage',
  components: {
    TaskDetailHeader,
    TaskBaseInfoCard,
    TaskCustomerInfoCard,
    TaskDeviceInfoCard,
    TaskCustomerFeedbackCard,
    TaskStepItem,
    TaskStepRecordForm,
    TaskStatusCard,
    TaskFlowController,
    CollaboratorsPanel,
    TaskTransferPanel,
    TaskStatusChangeDialog,
    CollaborationDialog,
    TaskAssignDialog,
    ConfirmDialog,
    ImagePreviewDialog,
    TaskSalesInfoCard,
    TaskTransferDialog,
    QuoteDialog
  },
  props: {
    id: {
      type: String,
      required: false
    }
  },
  setup(props) {
    // 使用状态管理
    const authStore = useAuthStore()
    const taskStore = useTaskStore()
    const taskFlowStore = useTaskFlowStore()
    
    const router = useRouter()
    const route = useRoute()
    const taskId = ref(props.id || route.params.id) // 从路由或props获取任务ID
    const task = ref(null)
    const loading = ref(true) // 默认为加载中状态
    const error = ref(null)
    const showOptions = ref(false) // 任务选项菜单是否显示
    const toast = useToast()
    const showQuoteDialog = ref(false)

    // 任务状态历史
    const taskStatusHistory = ref([])
    
    // 获取对TaskFlowController子组件的引用
    const taskFlowControllerRef = ref(null)
    
    // 权限相关变量
    const isAdmin = computed(() => authStore.hasRole('ROLE_ADMIN'))
    const canViewLocation = ref(true)
    const canEditTask = ref(true)
    const canChangeTaskStatus = ref(true)
    const canViewCustomer = ref(true)
    const canManageFlow = ref(true)
    const canManageCollaborators = ref(true)
    const canEditDevice = computed(() => {
      const userRole = authStore.user?.role
      return isAdmin.value || userRole === 'ROLE_ENGINEER' || userRole === 'ROLE_SALES'
    })
    
    // 步骤相关状态 - 使用taskFlowStore
    const activeStepIndex = computed(() => taskFlowStore.currentStepIndex)
    const hasPrevStep = computed(() => taskFlowStore.hasPrevStep)
    const hasNextStep = computed(() => taskFlowStore.hasNextStep)

    // 记录相关状态
    const showRecordForm = ref(false)
    const activeRecordStep = ref(-1)
    const editingRecordIndex = ref(-1)
    const editingRecord = ref(null)
    
    // 对话框相关状态
    const showStatusDialog = ref(false)
    const showInviteForm = ref(false)
    const showAssignDialog = ref(false)
    const showConfirmDialog = ref(false)
    const confirmDialogTitle = ref('')
    const confirmDialogMessage = ref('')
    const dialogCallback = ref(null)
    const dialogCustomContent = ref(null)
    
    // 图片预览
    const previewImageUrl = ref(null)
    
    // 任务负责相关
    const canTransferTask = computed(() => {
      if (!task.value || !authStore.user) return false
      const isOwner = task.value.engineers?.some(eng => eng.id === authStore.user.id && eng.isOwner)
      const isAdminRole = authStore.hasRole('ROLE_ADMIN')
      return isOwner || isAdminRole
    })
    
    // 对话框显示状态
    const showTransferDialog = ref(false)
    const showImagePreviewDialog = ref(false)
    const previewImages = ref([])
    const currentImageIndex = ref(0)

    // 任务转出相关状态
    const transferLoading = ref(false)
    const transferError = ref('')
    
    // 加载任务详情
    const loadTaskDetail = async () => {
      loading.value = true
      error.value = null
      
      try {
        if (!taskId.value) {
          error.value = '任务ID不存在'
          return
        }
        
        // 从API加载任务详情
        const response = await getTaskDetail(taskId.value)
        task.value = response.data
        
        // 加载任务状态历史
        await loadTaskStatusHistory()
        
        // 设置权限
        setupPermissions()
        
        // 使用taskFlowStore加载任务流程
        await loadTaskFlow()
        
        // 发布任务详情加载完成事件
        eventBus.emit(TaskFlowEvents.FLOW_UPDATED, {
          taskId: taskId.value,
          task: task.value,
          flowSteps: taskFlowStore.flowSteps
        })
      } catch (err) {
        console.error('加载任务详情出错:', err)
        error.value = '加载任务详情失败，请重试'
      } finally {
        loading.value = false
      }
    }
    
    // 加载任务流程
    const loadTaskFlow = async () => {
      try {
        console.log(`加载任务 ${taskId.value} 的流程`)
        
        // 使用taskFlowStore加载任务流程
        const flowData = await taskFlowStore.fetchTaskFlow(taskId.value)
        
        if (flowData && flowData.steps) {
          console.log('获取到流程步骤:', flowData.steps)
          // 更新任务的步骤数据，保持一致性
          task.value.steps = flowData.steps
        }
        
        return flowData
      } catch (err) {
        console.error('加载任务流程失败:', err)
        return null
      }
    }
    
    // 加载任务状态历史
    const loadTaskStatusHistory = async () => {
      try {
        console.log(`正在获取任务状态历史，taskId: ${taskId.value}`)
        const response = await http.get(`/api/v1/tasks/${taskId.value}/status-history`)
        console.log('任务状态历史API响应:', response)
        
        if (response && response.code === 200) {
          taskStatusHistory.value = response.data || []
          console.log('获取到任务状态历史:', taskStatusHistory.value)
        } else {
          console.error('API返回错误:', response)
          taskStatusHistory.value = []
        }
      } catch (err) {
        console.error('加载任务状态历史失败:', err)
        taskStatusHistory.value = []
      }
    }
    
    // 设置权限
    const setupPermissions = () => {
      const userRole = authStore.user?.role
      const isOwner = task.value?.engineers?.some(eng => eng.id === authStore.user?.id && eng.isOwner)
      
      // 根据角色和任务状态设置各种权限
      canViewLocation.value = true // 所有人都可以查看位置
      canEditTask.value = isAdmin.value || (isOwner && task.value.status !== 'completed' && task.value.status !== 'cancelled')
      canChangeTaskStatus.value = isAdmin.value || isOwner
      canViewCustomer.value = isAdmin.value || userRole === 'ROLE_ENGINEER' || userRole === 'ROLE_SALES'
      canManageFlow.value = isAdmin.value || isOwner
      canManageCollaborators.value = isAdmin.value || isOwner
    }
    
    // 返回上一页
    const goBack = () => {
      const fromRoute = route.query.from
      if (fromRoute) {
        router.push({ path: `/${fromRoute}` })
        } else {
        router.go(-1)
      }
    }
    
    // 编辑任务
    const editTask = () => {
      router.push({ path: `/tasks/edit/${taskId.value}` })
    }
    
    // 显示任务选项菜单
    const showTaskOptions = () => {
      showOptions.value = true
    }
    
    // 隐藏任务选项菜单
    const hideTaskOptions = () => {
      showOptions.value = false
    }
    
    // 获取任务类型标题
    const getTaskTypeTitle = (taskType) => {
      switch(taskType) {
        case 'repair': return '维修'
        case 'maintenance': return '保养'
        case 'installation': return '安装'
        case 'selection': return '选型'
        case 'leasing': return '租赁'
        case 'recycle': return '回收'
        case 'training': return '培训'
        case 'verification': return '验证'
        default: return '任务'
      }
    }
    
    // 上一步
    const prevStep = async () => {
      if (!hasPrevStep.value) return

      // 检查即将返回到的步骤是否是决策步骤
      const targetStepIndex = activeStepIndex.value - 1
      const targetStep = taskFlowStore.flowSteps[targetStepIndex]
      const isReturningToDecisionStep = targetStep?.stepKey === 'site-visit-decision'

      let action
      if (isReturningToDecisionStep) {
        action = () => taskFlowStore.resetDecisionAndGoBack()
      } else {
        action = () => taskFlowStore.prevStep()
      }

      try {
        await action()
        // 成功后的UI反馈和事件总线调用可以在action内部处理
      } catch (err) {
        console.error('处理上一步操作失败:', err)
        // 错误提示也由action内部处理
      }
    }
    
    // 下一步
    const nextStep = async () => {
      if (!hasNextStep.value) return

      const currentStep = taskFlowStore.currentStep
      const isDecisionStep = currentStep?.stepKey === 'site-visit-decision'

      let action
      if (isDecisionStep) {
        // 从子组件获取本地决策
        const localDecision = taskFlowControllerRef.value?.localDecision
        if (!localDecision) {
          toast.error("无法获取决策状态，请重试")
          return
        }
        action = () => taskFlowStore.commitDecisionAndProceed(localDecision)
      } else {
        action = () => taskFlowStore.nextStep()
      }
      
      try {
        await action()
        // 成功后的UI反馈和事件总线调用可以在action内部处理
      } catch (err) {
        console.error('处理下一步操作失败:', err)
        // 错误提示也由action内部处理
      }
    }
    
    // 滚动到当前活动步骤
    const scrollToActiveStep = () => {
      // 等待DOM更新后执行滚动
      setTimeout(() => {
        const activeStep = document.querySelector('.is-active.step-item')
        if (activeStep) {
          activeStep.scrollIntoView({ behavior: 'smooth', block: 'center' })
        }
      }, 100)
    }
    
    // 显示添加记录表单
    const showAddRecord = (stepIndex) => {
      console.log('打开添加记录表单，步骤索引:', stepIndex)
      activeRecordStep.value = stepIndex
      editingRecordIndex.value = -1
      editingRecord.value = null
      showRecordForm.value = true
    }
    
    // 取消添加记录
    const cancelAddRecord = () => {
      showRecordForm.value = false
      activeRecordStep.value = -1
      editingRecordIndex.value = -1
      editingRecord.value = null
    }
    
    // 提交记录
    const submitRecord = async (recordData) => {
      try {
        // 分离文件和文本数据
        const files = recordData.attachments.map(att => att.file);
        const textData = {
          stepId: recordData.stepId,
          content: recordData.content,
          spentTime: recordData.spentTime,
          taskId: taskId.value // 确保taskId在文本数据中
        };

        // 如果包含上门时间，也加入文本数据
        if (recordData.visitAppointmentTime) {
          textData.visitAppointmentTime = recordData.visitAppointmentTime;
        }
        
        // 调用新的API函数
        const response = await addTaskFlowRecord(textData, files);
        
        if (response && response.code === 200) {
          toast.success('工作记录已提交');
          
          // 重新加载任务流程
          await loadTaskFlow()
          
          // 关闭记录表单
          cancelAddRecord()
        } else {
          throw new Error(response?.message || '提交工作记录失败')
        }
      } catch (err) {
        console.error('提交工作记录失败:', err)
        toast.error('提交工作记录失败，请重试')
      }
    }
    
    // 显示状态变更对话框
    const showStatusChangeDialog = () => {
      showStatusDialog.value = true
    }
    
    // 取消状态变更
    const cancelStatusChange = () => {
      showStatusDialog.value = false
    }
    
    // 确认状态变更
    const confirmStatusChange = async (statusData) => {
      try {
        console.log('确认更改任务状态:', statusData);
        
        // 创建一个符合后端API期望的新对象
        const payload = {
          status: statusData.newStatus, // 后端需要 'status' 键
          note: statusData.comment      // 后端需要 'note' 键
        };

        // 调用API更新任务状态
        const response = await http.put(`/api/v1/tasks/${taskId.value}/status`, payload);
        
        if (response && response.code === 200) {
          // 显示成功通知
          toast.success(`任务状态已更新为: ${getStatusText(statusData.newStatus)}`);
          
          // 重新加载任务详情，确保UI更新
          await loadTaskDetail();
        } else {
          throw new Error(response?.message || '更新任务状态失败');
        }
        
        // 关闭对话框
        cancelStatusChange();
      } catch (err) {
        console.error('更新任务状态失败:', err);
        toast.error('更新任务状态失败，请稍后再试');
      }
    }
    
    // 显示邀请协作对话框
    const showInviteDialog = () => {
      showInviteForm.value = true
    }
    
    // 取消邀请
    const cancelInvite = () => {
      showInviteForm.value = false
    }
    
    // 确认邀请
    const confirmInvite = async (engineerIds) => {
      try {
        // 调用API邀请协作工程师
        await inviteCollaborator(taskId.value, engineerIds)
        
        // 重新加载任务详情
          await loadTaskDetail()
          
        // 关闭对话框
        cancelInvite()
      } catch (err) {
        console.error('邀请协作工程师失败:', err)
        // 显示错误通知
      }
    }
    
    // 查看工程师资料
    const viewEngineerProfile = (engineerId) => {
      router.push(`/engineer/${engineerId}`)
    }
    
    // 显示转出任务对话框
    const handleTransfer = () => {
      if (!canTransferTask.value) {
        toast.error('您没有权限转出此任务，请联系任务负责人或系统管理员。')
        return
      }
      showTransferDialog.value = true
    }
    
    // 执行任务转出
    const confirmTransferTask = async (transferData) => {
      try {
        transferLoading.value = true
        transferError.value = ''
        
        await transferTask({
          taskId: taskId.value,
          engineerId: transferData.engineerId,
          note: transferData.note,
          operatorId: authStore.user?.id,
          isOperatorAdmin: authStore.hasRole('ROLE_ADMIN')
        })
        
        toast.success('任务转出成功')
        showTransferDialog.value = false
        
        // 转出成功后重新加载任务数据
        await loadTaskDetail()
      } catch (err) {
        console.error('任务转出失败:', err)
        transferError.value = err.response?.data?.message || '任务转出失败，请稍后再试'
      } finally {
        transferLoading.value = false
      }
    }
    
    // 关闭任务转出对话框
    const closeTransferDialog = () => {
      showTransferDialog.value = false
      transferError.value = ''
    }
    
    // 显示分配任务对话框
    const handleAssignTask = () => {
      showAssignDialog.value = true
      hideTaskOptions()
    }
    
    // 取消分配
    const cancelAssign = () => {
      showAssignDialog.value = false
    }
    
    // 确认分配
    const confirmAssign = async (assignData) => {
      try {
        // 调用API分配任务
        await assignEngineers(taskId.value, assignData.engineerId, assignData.note)
        
        // 重新加载任务详情
          await loadTaskDetail()
          
        // 关闭对话框
        cancelAssign()
      } catch (err) {
        console.error('分配任务失败:', err)
        // 显示错误通知
      }
    }
    
    // 复制任务
    const duplicateTask = () => {
      // 实现复制任务功能
      showDialog(
        '复制任务',
        '暂未实现复制任务功能。',
        null,
        true
      )
      hideTaskOptions()
    }
    
    // 确认取消任务
    const confirmCancelTask = () => {
      // 显示取消任务确认对话框
      showDialog(
        '确认取消任务',
        '您确定要取消此任务吗？此操作不可撤销。',
        () => {
          // 实现取消任务功能
          console.log('执行取消任务')
        }
      )
      hideTaskOptions()
    }
    
    // 显示通用对话框
    const showDialog = (title, message, callback = null, isCustomContent = false) => {
      confirmDialogTitle.value = title
      confirmDialogMessage.value = message
      dialogCallback.value = callback
      dialogCustomContent.value = isCustomContent ? () => message : null
      showConfirmDialog.value = true
    }
    
    // 取消对话框
    const cancelDialog = () => {
      showConfirmDialog.value = false
      dialogCallback.value = null
    }
    
    // 确认对话框
    const confirmDialog = () => {
      if (dialogCallback.value) {
        dialogCallback.value()
      }
      cancelDialog()
    }
    
    // 显示图片预览
    const showImagePreview = (imageUrl) => {
      previewImageUrl.value = imageUrl
    }
    
    // 关闭图片预览
    const closePreview = () => {
      previewImageUrl.value = null
    }
    
    // 强制刷新
    const forceRefresh = () => {
      loadTaskDetail()
    }
    
    // 编辑设备信息
    const editDeviceInfo = (deviceData) => {
      // 确保记录编辑人信息
      const currentUser = authStore.user
      deviceData.editorId = currentUser.id
      deviceData.editorName = currentUser.name
      deviceData.editTime = new Date().toISOString()
      
      // 调用API更新设备信息
      http.put(`/api/v1/devices/${deviceData.deviceId}`, deviceData)
        .then(() => {
          // 重新加载任务详情
          loadTaskDetail()
          // 显示成功提示
          showDialog(
            '更新成功',
            '设备信息已成功更新',
            null
          )
        })
        .catch(err => {
          console.error('更新设备信息失败:', err)
          // 显示错误提示
          showDialog(
            '更新失败',
            `更新设备信息时发生错误: ${err.message || '未知错误'}`,
            null
          )
        })
    }
    
    // 处理"需要上门"决策
    const handleSiteVisitDecision = async (stepIndex, requiresVisit) => {
      try {
        loading.value = true
        error.value = null
        
        // 使用taskFlowStore处理上门决策
        const success = await taskFlowStore.decideSiteVisit(requiresVisit)
        
        if (success) {
          // 发布上门决策事件
          eventBus.emit(TaskFlowEvents.SITE_VISIT_DECIDED, {
            taskId: taskId.value,
            requiresVisit
          })
        }
      } catch (err) {
        console.error('处理上门决策失败:', err)
      } finally {
        loading.value = false
      }
    }
    
    // 监听taskFlowStore的状态变化
    watch(() => taskFlowStore.currentStepIndex, (newIndex, oldIndex) => {
      if (newIndex !== oldIndex && newIndex !== -1) {
        console.log(`步骤索引变化: ${oldIndex} -> ${newIndex}`)
        
        // 当步骤变化时，更新任务对象中的步骤状态，保持一致性
        if (task.value && task.value.steps && taskFlowStore.flowSteps) {
          task.value.steps = [...taskFlowStore.flowSteps]
        }
      }
    })
    
    // 注册事件监听
    const setupEventListeners = () => {
      // 监听步骤变化事件
      eventBus.on(TaskFlowEvents.STEP_CHANGED, (data) => {
        console.log('收到步骤变化事件:', data)
        
        // 如果是当前任务的事件，滚动到当前步骤
        if (data.taskId === taskId.value) {
          scrollToActiveStep()
        }
      })
    }
    
    // 清理事件监听
    const cleanupEventListeners = () => {
      eventBus.off(TaskFlowEvents.STEP_CHANGED)
      eventBus.off(TaskFlowEvents.STEP_COMPLETED)
      eventBus.off(TaskFlowEvents.FLOW_UPDATED)
      eventBus.off(TaskFlowEvents.SITE_VISIT_DECIDED)
    }
    
    // 页面加载时获取任务详情并设置事件监听
    onMounted(() => {
      loadTaskDetail()
      setupEventListeners()
    })
    
    // 组件卸载时清理
    onBeforeUnmount(() => {
      cleanupEventListeners()
      taskFlowStore.resetState()
    })
    
    // 显示步骤记录
    const showStepRecords = (stepIndex) => {
      console.log('显示步骤记录:', stepIndex)
      // 实现显示步骤记录的逻辑
    }

    // 处理自动任务状态更新
    const handleTaskStatusUpdate = async (statusData) => {
      try {
        console.log('自动更新任务状态:', statusData)
        // 调用API更新任务状态
        const response = await http.put(`/api/v1/tasks/${taskId.value}/status`, statusData)
        
        if (response && response.code === 200) {
          console.log('任务状态更新成功:', response)
          toast.success(`任务状态已更新为: ${getStatusText(statusData.status)}`)
          
          // 重新加载任务详情
          await loadTaskDetail()
        } else {
          console.error('任务状态API返回错误:', response)
          toast.error(`状态更新失败: ${response?.message || '未知错误'}`)
        }
      } catch (err) {
        console.error('自动更新任务状态失败:', err)
        toast.error('更新任务状态失败，请稍后再试')
      }
    }
    
    // 获取状态文本
    const getStatusText = (status) => {
      switch(status) {
        case 'pending': return '待处理'
        case 'in-progress': return '进行中'
        case 'completed': return '已完成'
        case 'cancelled': return '已取消'
        default: return '未知状态'
      }
    }

    const closeQuoteDialog = () => {
      showQuoteDialog.value = false;
    };

    const submitQuote = async (price) => {
      console.log(`[TaskDetail] submitQuote called with price: ${price}`);
      if (!taskId.value) {
        toast.error('任务ID不存在，无法提交报价');
        console.error('[TaskDetail] Missing taskId in submitQuote');
        return;
      }

      try {
        console.log(`[TaskDetail] Calling setTaskPrice API for taskId: ${taskId.value}`);
        await setTaskPrice(taskId.value, {
          price: price,
          stepIndex: activeStepIndex.value,
        });
        toast.success('报价已提交');
        
        // 手动更新本地任务对象的报价，以便UI立即响应
        if (task.value) {
          task.value.price = price;
          // 报价后，通常需要客户确认，所以重置确认状态
          task.value.priceConfirmed = 0; 
          console.log('[TaskDetail] Local task price updated to:', task.value.price);
        }
        
        showQuoteDialog.value = false;
        // 可以在后台异步加载详情，但UI已更新
        loadTaskDetail();
      } catch (err) {
        console.error('提交报价失败:', err);
        toast.error(err.response?.data?.message || '提交报价失败，请重试');
      }
    };
    
    return {
      task,
      taskId,
      loading,
      error,
      showOptions,
      canViewLocation,
      canEditTask,
      canChangeTaskStatus,
      canViewCustomer,
      canManageFlow,
      canManageCollaborators,
      canEditDevice,
      canTransferTask,
      activeStepIndex,
      hasPrevStep,
      hasNextStep,
      showRecordForm,
      activeRecordStep,
      editingRecordIndex,
      editingRecord,
      showStatusDialog,
      showInviteForm,
      showAssignDialog,
      showConfirmDialog,
      confirmDialogTitle,
      confirmDialogMessage,
      dialogCustomContent,
      previewImageUrl,
      showTransferDialog,
      transferLoading,
      transferError,
      taskStatusHistory,
      
      // 方法
      goBack,
      loadTaskDetail,
      forceRefresh,
      editTask,
      showTaskOptions,
      hideTaskOptions,
      getTaskTypeTitle,
      prevStep,
      nextStep,
      showAddRecord,
      cancelAddRecord,
      submitRecord,
      showStatusChangeDialog,
      cancelStatusChange,
      confirmStatusChange,
      showInviteDialog,
      cancelInvite,
      confirmInvite,
      viewEngineerProfile,
      handleTransfer,
      confirmTransferTask,
      closeTransferDialog,
      handleAssignTask,
      cancelAssign,
      confirmAssign,
      duplicateTask,
      confirmCancelTask,
      showDialog,
      cancelDialog,
      confirmDialog,
      showImagePreview,
      closePreview,
      editDeviceInfo,
      showStepRecords,
      handleTaskStatusUpdate,
      getStatusText,
      taskFlowControllerRef,
      showQuoteDialog,
      closeQuoteDialog,
      submitQuote,
    }
  }
}
</script>

<style scoped>
.task-detail-page {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 120px;
}

.top-nav {
  background-color: #fff;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.top-nav i {
  font-size: 20px;
  color: #555;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  flex: 1;
}

.nav-actions {
  display: flex;
  gap: 16px;
}

.loading-container, .error-container, .no-data-container {
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
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-actions, .no-data-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.retry-btn, .back-btn, .force-refresh-btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: 500;
  font-size: 14px;
}

.retry-btn, .force-refresh-btn {
  background-color: #3b82f6;
  color: white;
  border: none;
}

.back-btn {
  background-color: #e5e7eb;
  color: #4b5563;
  border: none;
}

.bottom-actions {
  position: fixed;
  bottom: 55px;
  left: 0;
  right: 0;
  display: flex;
  padding: 16px;
  background-color: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  gap: 16px;
  z-index: 50;
  min-height: 80px;
  width: 100%;
}

.prev-step-btn, .next-step-btn {
  flex: 1;
  padding: 14px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 16px;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.prev-step-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #e5e7eb;
}

.prev-step-btn:active:not(:disabled) {
  background-color: #e5e7eb;
}

.next-step-btn {
  background-color: #3b82f6;
  color: white;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.3);
}

.next-step-btn:active:not(:disabled) {
  background-color: #2563eb;
  transform: translateY(1px);
}

.prev-step-btn:disabled, .next-step-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 