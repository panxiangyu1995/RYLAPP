import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as taskApi from '../api/task'
import * as taskFlowApi from '../api/taskflow'
import { useToast } from 'vue-toastification'

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
  const fetchTaskFlow = async (taskId) => {
    if (!taskId) {
      console.error('获取任务流程失败: 任务ID为空')
      error.value = '任务ID不能为空'
      return null
    }
    
    loading.value = true
    error.value = null
    
    try {
      console.log('开始获取任务流程, 任务ID:', taskId)
      const response = await taskFlowApi.getTaskFlow(taskId)
      console.log('任务流程API响应:', response)
      
      if (response && response.code === 200 && response.data) {
        currentTaskId.value = taskId
        
        // 确保steps字段存在
        if (!response.data.steps || !Array.isArray(response.data.steps)) {
          console.warn('任务流程数据中缺少有效的steps字段')
          flowSteps.value = []
          currentStepIndex.value = -1
        } else {
          console.log('获取到任务步骤数据:', response.data.steps)
          
          // 确保每个步骤都有title字段（兼容后端返回的name字段）
          flowSteps.value = response.data.steps.map(step => {
            if (!step.title && step.name) {
              console.log(`步骤 ${step.index || '未知'} 使用name字段 "${step.name}" 作为title`)
              return { ...step, title: step.name }
            }
            return step
          })
          
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
      const response = await taskFlowApi.updateTaskFlowStatus({
        taskId: currentTaskId.value,
        currentStepIndex: previousIndex,
        nextStepIndex: targetIndex,
        action: 'update'
      })
      
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
      const response = await taskFlowApi.updateTaskFlowStatus({
        taskId: currentTaskId.value,
        currentStepIndex: previousIndex,
        nextStepIndex: targetIndex,
        action: 'update'
      })
      
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
      const response = await taskFlowApi.updateTaskFlowStatus({
        taskId: currentTaskId.value,
        currentStepIndex: currentStepIndex.value,
        nextStepIndex: currentStepIndex.value,
        action: 'complete'
      })
      
      if (response && response.code === 200) {
        console.log('成功完成当前步骤')
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success('已完成当前步骤')
        return true
      } else {
        console.error('完成当前步骤失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '完成当前步骤失败') : '服务器无响应'
        toast.error('完成当前步骤失败，请重试')
        return false
      }
    } catch (err) {
      console.error('完成当前步骤错误:', err)
      error.value = err.message || '完成当前步骤时发生错误'
      toast.error('完成当前步骤时出现错误，请重试')
      return false
    } finally {
      updating.value = false
    }
  }
  
  // 决定是否需要上门
  const decideSiteVisit = async (requiresVisit) => {
    if (currentStepIndex.value < 0 || !currentTaskId.value) {
      console.warn('无法决定是否需要上门：当前步骤无效或任务ID为空')
      return false
    }
    
    updating.value = true
    error.value = null
    lastOperation.value = 'decide-visit'
    
    try {
      // 调用API决定是否需要上门
      const response = await taskFlowApi.decideSiteVisit(currentTaskId.value, {
        stepIndex: currentStepIndex.value,
        requiresVisit
      })
      
      if (response && response.code === 200) {
        console.log(`成功决定${requiresVisit ? '需要' : '不需要'}上门`)
        
        // 重新获取最新的流程数据，确保前后端一致
        await fetchTaskFlow(currentTaskId.value)
        
        toast.success(`已确认${requiresVisit ? '需要' : '不需要'}上门服务`)
        return true
      } else {
        console.error('决定是否需要上门失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '决定是否需要上门失败') : '服务器无响应'
        toast.error('更新上门决定失败，请重试')
        return false
      }
    } catch (err) {
      console.error('决定是否需要上门错误:', err)
      error.value = err.message || '决定是否需要上门时发生错误'
      toast.error('更新上门决定时出现错误，请重试')
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
      // 确保记录数据包含任务ID和步骤索引
      const record = {
        ...recordData,
        taskId: currentTaskId.value,
        stepIndex: currentStepIndex.value
      }
      
      // 调用API添加记录
      const response = await taskFlowApi.addTaskFlowRecord(record)
      
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
    decideSiteVisit,
    addStepRecord,
    resetState
  }
}) 