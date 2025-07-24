import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as taskApi from '../api/task'
import * as taskFlowApi from '../api/taskflow'
import { useToast } from 'vue-toastification'
import { TASK_TYPE_FLOW_STEPS } from '../constants/taskFlowTemplates'

/**
 * 任务流程状态管理
 */
export const useTaskFlowStore = defineStore('taskFlow', () => {
  // 注入toast通知
  const toast = useToast()
  
  // 状态
  const currentTaskId = ref(null)
  const flowSteps = ref([])
  const currentStepIndex = ref(-1)
  const loading = ref(false)
  const updating = ref(false)
  const error = ref(null)
  const lastOperation = ref(null)
  
  // 计算属性
  const currentStep = computed(() => {
    if (currentStepIndex.value >= 0 && currentStepIndex.value < flowSteps.value.length) {
      return flowSteps.value[currentStepIndex.value]
    }
    return null
  })
  
  const hasPrevStep = computed(() => {
    return currentStepIndex.value > 0
  })
  
  const hasNextStep = computed(() => {
    return currentStepIndex.value < flowSteps.value.length - 1
  })
  
  // 获取任务流程
  const fetchTaskFlow = async (taskId, isRetry = false) => {
    if (!taskId) {
      console.error('获取任务流程失败: 任务ID为空')
      error.value = '任务ID不能为空'
      return null
    }
    
    loading.value = true
    error.value = null
    
    try {
      console.log(`开始获取任务流程, 任务ID: ${taskId}, 是否重试: ${isRetry}`)
      // 从正确的 taskApi 中调用 getTaskFlow
      const response = await taskApi.getTaskFlow(taskId)
      console.log('任务流程API响应:', response)
      
      if (response && response.code === 200 && response.data) {
        currentTaskId.value = taskId
        
        // 检查任务是否需要初始化步骤 (来自小程序的任务)
        if (Array.isArray(response.data.steps) && response.data.steps.length === 0 && !isRetry) {
          console.warn(`任务 ${taskId} 没有步骤, 可能是来自小程序的任务，尝试初始化...`)
          
          const taskTypeKey = response.data.taskType.toLowerCase();
          const stepTemplate = TASK_TYPE_FLOW_STEPS[taskTypeKey];

          if (stepTemplate) {
            const stepsToCreate = stepTemplate.map((step, index) => ({
              stepIndex: index,
              title: step.title
            }));

            try {
              toast.info('检测到新任务，正在初始化流程步骤...')
              const initResponse = await taskApi.initializeTaskSteps(taskId, stepsToCreate);
              if (initResponse && initResponse.code === 200) {
                console.log(`任务 ${taskId} 步骤初始化成功，重新获取流程...`)
                toast.success('任务流程已成功初始化！')
                // 初始化成功后，再次获取流程数据
                return await fetchTaskFlow(taskId, true); 
              } else {
                throw new Error(initResponse.message || '初始化步骤失败')
              }
            } catch (initError) {
              console.error(`初始化任务 ${taskId} 的步骤失败:`, initError)
              error.value = `无法初始化任务步骤: ${initError.message}`
              toast.error(`无法初始化任务步骤: ${initError.message}`)
              // 初始化失败，将步骤设置为空数组
              flowSteps.value = []
              currentStepIndex.value = -1
              return null;
            }
          } else {
             console.error(`在模板中找不到任务类型 "${taskTypeKey}" 的步骤定义`)
             error.value = `缺少任务类型 "${response.data.taskType}" 的流程模板。`
             toast.error(`缺少任务类型 "${response.data.taskType}" 的流程模板。`)
          }
        }
        
        // 确保steps字段存在
        if (!response.data.steps || !Array.isArray(response.data.steps)) {
          console.warn('任务流程数据中缺少有效的steps字段')
          flowSteps.value = []
          currentStepIndex.value = -1
        } else {
          console.log('获取到任务步骤数据:', response.data.steps)
          
          // 更新整个步骤数组，确保响应性
          // 后端已经统一返回 title, 无需兼容 name
          // 后端同时返回了 records, 无需手动处理
          flowSteps.value = response.data.steps
          
          // 查找当前活动步骤
          const inProgressIndex = flowSteps.value.findIndex(step => step.status === 'in-progress')
          if (inProgressIndex !== -1) {
            console.log('找到进行中的步骤，索引:', inProgressIndex)
            currentStepIndex.value = inProgressIndex
          } else {
            // 找第一个未完成的步骤
            const pendingIndex = flowSteps.value.findIndex(step => step.status === 'pending')
            if (pendingIndex !== -1) {
              console.log('找到待执行的步骤，索引:', pendingIndex)
              currentStepIndex.value = pendingIndex
            } else {
              // 如果都完成了，就设为最后一个
              console.log('所有步骤都已完成，设置当前步骤为最后一个')
              currentStepIndex.value = flowSteps.value.length - 1
            }
          }
        }
        
        return response.data
      } else {
        console.error('获取任务流程失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '获取任务流程失败') : '服务器无响应'
        return null
      }
    } catch (err) {
      console.error('获取任务流程错误:', err)
      error.value = err.message || '获取任务流程时发生错误'
      return null
    } finally {
      loading.value = false
    }
  }
  
  // 更新任务步骤状态 - 前往上一步
  const prevStep = async () => {
    if (!hasPrevStep.value || !currentTaskId.value) {
      console.warn('无法前往上一步：没有上一步或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    lastOperation.value = 'prev'
    
    // 保存当前索引，用于错误回滚
    const previousIndex = currentStepIndex.value
    
    try {
      // 乐观更新：先更新UI状态
      const targetIndex = currentStepIndex.value - 1
      
      // 临时更新状态，提供即时反馈
      currentStepIndex.value = targetIndex
      
      // 调用API更新任务步骤
      const response = await taskFlowApi.prevStep(currentTaskId.value)
      
      if (response && response.code === 200) {
        console.log('成功前往上一步')
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success('已返回上一步')
        return true
      } else {
        // API调用失败，回滚UI状态
        console.error('前往上一步失败:', response ? response.message : '无响应')
        currentStepIndex.value = previousIndex
        error.value = response ? (response.message || '前往上一步失败') : '服务器无响应'
        toast.error('返回上一步失败，请重试')
        return false
      }
    } catch (err) {
      // 发生异常，回滚UI状态
      console.error('前往上一步错误:', err)
      currentStepIndex.value = previousIndex
      error.value = err.message || '前往上一步时发生错误'
      toast.error('返回上一步时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 更新任务步骤状态 - 前往下一步
  const nextStep = async () => {
    if (!hasNextStep.value || !currentTaskId.value) {
      console.warn('无法前往下一步：没有下一步或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    lastOperation.value = 'next'
    
    // 保存当前索引，用于错误回滚
    const previousIndex = currentStepIndex.value
    
    try {
      // 乐观更新：先更新UI状态
      const targetIndex = currentStepIndex.value + 1
      
      // 临时更新状态，提供即时反馈
      currentStepIndex.value = targetIndex
      
      // 调用API更新任务步骤
      const response = await taskFlowApi.nextStep(currentTaskId.value)
      
      if (response && response.code === 200) {
        console.log('成功前往下一步')
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success('已前进到下一步')
        return true
      } else {
        // API调用失败，回滚UI状态
        console.error('前往下一步失败:', response ? response.message : '无响应')
        currentStepIndex.value = previousIndex
        error.value = response ? (response.message || '前往下一步失败') : '服务器无响应'
        toast.error('前进到下一步失败，请重试')
        return false
      }
    } catch (err) {
      // 发生异常，回滚UI状态
      console.error('前往下一步错误:', err)
      currentStepIndex.value = previousIndex
      error.value = err.message || '前往下一步时发生错误'
      toast.error('前进到下一步时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 完成当前步骤
  const completeCurrentStep = async () => {
    if (currentStepIndex.value < 0 || !currentTaskId.value) {
      console.warn('无法完成当前步骤：当前步骤无效或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    lastOperation.value = 'complete'
    
    try {
      // 调用API完成当前步骤
      // 原有API "updateTaskFlowStatus" 不存在，适配为调用 nextStep
      return await this.nextStep()
      
    } catch (err) {
      console.error('完成当前步骤错误:', err)
      error.value = err.message || '完成当前步骤时发生错误'
      toast.error('完成当前步骤时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 决定是否需要上门并推进流程
  const commitDecisionAndProceed = async (decision) => {
    if (currentStepIndex.value < 0 || !currentTaskId.value) {
      console.warn('无法提交决策并继续：当前步骤无效或任务ID为空')
      return false
    }

    if (decision.requiresVisit === null) {
      toast.info('请先选择需要上门或远程协助')
      return false
    }

    updating.value = true
    error.value = null
    lastOperation.value = 'commit-and-proceed'

    try {
      // 1. 先提交决策
      const payload = {
        stepIndex: currentStepIndex.value,
        requiresVisit: decision.requiresVisit,
        visitAppointmentTime: decision.visitAppointmentTime
      }
      const decisionResponse = await taskApi.decideSiteVisit(currentTaskId.value, payload)

      if (!decisionResponse || decisionResponse.code !== 200) {
        throw new Error(decisionResponse.message || '保存您的选择时失败')
      }
      
      toast.info('您的选择已保存')

      // 2. 再调用下一步
      const nextStepResponse = await taskFlowApi.nextStep(currentTaskId.value)
      if (!nextStepResponse || nextStepResponse.code !== 200) {
        throw new Error(nextStepResponse.message || '进入下一步时失败')
      }

      // 3. 成功后，刷新整个流程状态
      await fetchTaskFlow(currentTaskId.value)
      toast.success('已进入下一步')
      return true

    } catch (err) {
      console.error('提交决策并进入下一步时发生错误:', err)
      error.value = err.message || '操作失败'
      toast.error(err.message || '操作失败，请重试')
      // 尝试刷新数据以恢复到后端最新状态
      await fetchTaskFlow(currentTaskId.value)
      return false
    } finally {
      updating.value = false
    }
  }

  // 重置上门决策并返回上一步
  const resetDecisionAndGoBack = async () => {
    if (!hasPrevStep.value || !currentTaskId.value) {
      console.warn('无法返回上一步')
      return false
    }

    updating.value = true
    error.value = null
    lastOperation.value = 'reset-and-go-back'

    try {
      // 1. 先调用API重置决策
      const resetResponse = await taskApi.resetSiteVisitDecision(currentTaskId.value)
      if (!resetResponse || resetResponse.code !== 200) {
        throw new Error(resetResponse.message || '清空您的选择时失败')
      }

      toast.info('已清空您的选择')

      // 2. 再调用上一步
      const prevStepResponse = await taskFlowApi.prevStep(currentTaskId.value)
      if (!prevStepResponse || prevStepResponse.code !== 200) {
        throw new Error(prevStepResponse.message || '返回上一步时失败')
      }

      // 3. 成功后，刷新整个流程状态
      await fetchTaskFlow(currentTaskId.value)
      toast.success('已返回上一步')
      return true

    } catch (err) {
      console.error('重置决策并返回上一步时发生错误:', err)
      error.value = err.message || '操作失败'
      toast.error(err.message || '操作失败，请重试')
      // 尝试刷新数据以恢复到后端最新状态
      await fetchTaskFlow(currentTaskId.value)
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 添加步骤记录
  const addStepRecord = async (recordData) => {
    if (currentStepIndex.value < 0 || !currentTaskId.value) {
      console.warn('无法添加步骤记录：当前步骤无效或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    
    try {
      // API需要 FormData
      const formData = new FormData()
      formData.append('content', recordData.content)
      formData.append('spentTime', recordData.spentTime)
      formData.append('taskId', currentTaskId.value)
      // BUGFIX: 后端需要 stepId 而不是 stepIndex
      formData.append('stepId', currentStep.value.id)

      // 添加图片文件
      if (recordData.images && recordData.images.length > 0) {
        recordData.images.forEach(file => {
          formData.append('images', file)
        })
      }

      // 添加附件文件
      if (recordData.attachments && recordData.attachments.length > 0) {
        recordData.attachments.forEach(file => {
          formData.append('attachments', file)
        })
      }

      // 调用API添加记录 - API在task.js中
      const response = await taskApi.addTaskStepRecord(formData)
      
      if (response && response.code === 200) {
        console.log('成功添加步骤记录')
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success('已添加工作记录')
        return true
      } else {
        console.error('添加步骤记录失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '添加步骤记录失败') : '服务器无响应'
        toast.error('添加工作记录失败，请重试')
        return false
      }
    } catch (err) {
      console.error('添加步骤记录错误:', err)
      error.value = err.message || '添加步骤记录时发生错误'
      toast.error('添加工作记录时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 添加报价
  const setTaskPrice = async (price) => {
    if (currentStepIndex.value < 0 || !currentTaskId.value) {
      console.warn('无法设置报价：当前步骤无效或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    lastOperation.value = 'set-price'
    
    try {
      // 调用API设置报价
      const response = await taskFlowApi.setTaskPrice(currentTaskId.value, {
        stepIndex: currentStepIndex.value,
        price
      })
      
      if (response && response.code === 200) {
        console.log(`成功设置报价: ${price}`)
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success(`已设置报价: ${price}元`)
        return true
      } else {
        console.error('设置报价失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '设置报价失败') : '服务器无响应'
        toast.error('设置报价失败，请重试')
        return false
      }
    } catch (err) {
      console.error('设置报价错误:', err)
      error.value = err.message || '设置报价时发生错误'
      toast.error('设置报价时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 获取报价确认状态
  const getTaskPriceConfirmation = async () => {
    if (!currentTaskId.value) {
      console.warn('无法获取报价确认状态：任务ID为空')
      return null
    }
    
    try {
      const response = await taskFlowApi.getTaskPriceConfirmation(currentTaskId.value)
      
      if (response && response.code === 200) {
        console.log('获取报价确认状态成功:', response.data)
        return response.data
      } else {
        console.error('获取报价确认状态失败:', response ? response.message : '无响应')
        return null
      }
    } catch (err) {
      console.error('获取报价确认状态错误:', err)
      return null
    }
  }
  
  // 重置状态
  const resetState = () => {
    currentTaskId.value = null
    flowSteps.value = []
    currentStepIndex.value = -1
    loading.value = false
    updating.value = false
    error.value = null
    lastOperation.value = null
  }
  
  return {
    // 状态
    currentTaskId,
    flowSteps,
    currentStepIndex,
    loading,
    updating,
    error,
    lastOperation,
    
    // 计算属性
    currentStep,
    hasPrevStep,
    hasNextStep,
    
    // 方法
    fetchTaskFlow,
    prevStep,
    nextStep,
    completeCurrentStep,
    commitDecisionAndProceed,
    resetDecisionAndGoBack,
    addStepRecord,
    setTaskPrice,
    getTaskPriceConfirmation,
    resetState
  }
}) 