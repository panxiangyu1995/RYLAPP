import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as taskApi from '../api/task'

/**
 * 任务状态管理
 */
export const useTaskStore = defineStore('task', () => {
  // 状态
  const tasks = ref([])
  const loading = ref(false)
  const error = ref(null)
  const currentTask = ref(null)
  const taskCache = ref({}) // 用于缓存任务详情
  const cacheLoading = ref(false)
  const cacheCount = ref(0)
  const isCacheLoaded = ref(false)

  // 获取任务列表
  const fetchTasks = async (params = {}) => {
    loading.value = true
    error.value = null

    try {
      console.log('开始获取任务列表，参数:', params)
      const response = await taskApi.getTaskList(params)
      console.log('获取任务列表原始响应:', response)
      
      if (response.code === 200) {
        if (response.data) {
          // 检查响应数据结构
          console.log('响应数据结构:', Object.keys(response.data))
          
          // 后端返回的是list字段
          if (Array.isArray(response.data)) {
            console.log('响应数据是数组类型')
            tasks.value = response.data
          } else if (response.data.list && Array.isArray(response.data.list)) {
            console.log('响应数据中包含list字段，长度:', response.data.list.length)
            tasks.value = response.data.list
          } else if (response.data.records && Array.isArray(response.data.records)) {
            console.log('响应数据中包含records字段，长度:', response.data.records.length)
            tasks.value = response.data.records
          } else if (response.data.content && Array.isArray(response.data.content)) {
            console.log('响应数据中包含content字段，长度:', response.data.content.length)
            tasks.value = response.data.content
          } else {
            // 兜底处理
            console.warn('任务列表数据格式不符合预期，尝试查找数组类型的属性')
            
            // 尝试查找数组类型的属性
            const arrayProps = Object.keys(response.data).filter(key => 
              Array.isArray(response.data[key]) && response.data[key].length > 0
            )
            
            if (arrayProps.length > 0) {
              console.log('找到可能的任务数组:', arrayProps[0], '长度:', response.data[arrayProps[0]].length)
              tasks.value = response.data[arrayProps[0]]
            } else {
              console.error('无法找到任务数组，设置为空数组')
              tasks.value = []
            }
          }
        } else {
          console.warn('响应成功但没有数据')
          tasks.value = []
        }
        
        console.log('解析后的任务列表:', tasks.value)
        return response.data
      } else {
        console.error('获取任务列表失败:', response.message)
        error.value = response.message || '获取任务列表失败'
        tasks.value = []
        return null
      }
    } catch (err) {
      console.error('获取任务列表错误:', err)
      error.value = err.message || '获取任务列表时发生错误'
      tasks.value = []
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取工程师任务列表
  const fetchEngineerTasks = async (params = {}) => {
    // 假设当前用户ID存储在本地
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
    if (!userInfo.id) {
      error.value = '未找到工程师信息'
      return null
    }

    // 添加工程师ID参数
    params.engineerId = userInfo.id
    return fetchTasks(params)
  }

  // 获取销售任务列表
  const fetchSalesTasks = async (params = {}) => {
    // 假设当前用户ID存储在本地
    const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
    if (!userInfo.id) {
      error.value = '未找到销售信息'
      return null
    }

    // 添加销售ID参数
    params.salesId = userInfo.id
    return fetchTasks(params)
  }

  // 获取任务详情
  const fetchTaskDetail = async (taskId) => {
    // 检查是否有缓存
    if (taskCache.value[taskId]) {
      console.log('从缓存获取任务详情:', taskId)
      currentTask.value = taskCache.value[taskId]
      return taskCache.value[taskId]
    }

    loading.value = true
    error.value = null

    try {
      console.log('开始从API获取任务详情:', taskId)
      const response = await taskApi.getTaskDetail(taskId)
      console.log('任务详情API响应:', response)
      
      if (response && response.code === 200 && response.data) {
        console.log('任务详情获取成功')
        currentTask.value = response.data
        // 添加到缓存
        taskCache.value[taskId] = response.data
        return response.data
      } else {
        console.error('获取任务详情失败:', response ? response.message : '无响应')
        error.value = response ? (response.message || '获取任务详情失败') : '服务器无响应'
        return null
      }
    } catch (err) {
      console.error('获取任务详情错误:', err)
      error.value = err.message || '获取任务详情时发生错误'
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取任务流程
  const fetchTaskFlow = async (taskId) => {
    loading.value = true
    error.value = null

    try {
      console.log('开始从API获取任务流程, 任务ID:', taskId)
      
      if (!taskId) {
        console.error('获取任务流程失败: 任务ID为空')
        error.value = '任务ID不能为空'
        return null
      }
      
      // 显示请求详情
      console.log('请求URL:', `${taskApi.BASE_URL}/${taskId}/flow`)
      
      const response = await taskApi.getTaskFlow(taskId)
      console.log('任务流程API原始响应:', response)
      
      if (response && response.code === 200) {
        if (response.data) {
          console.log('任务流程获取成功, 数据:', response.data)
          
          // 检查数据结构
          if (!response.data.steps) {
            console.warn('任务流程数据中缺少steps字段')
            
            // 尝试查找可能的步骤数据
            const possibleStepsFields = ['flowSteps', 'taskSteps', 'process', 'workflow']
            for (const field of possibleStepsFields) {
              if (response.data[field] && Array.isArray(response.data[field])) {
                console.log(`找到可能的步骤数据: ${field}`)
                response.data.steps = response.data[field]
                break
              }
            }
            
            // 如果仍然没有找到步骤数据，创建空数组
            if (!response.data.steps) {
              console.warn('无法找到步骤数据，创建空数组')
              response.data.steps = []
            }
          }
          
          return response.data
        } else {
          console.error('获取任务流程失败: 响应中没有数据')
          error.value = '获取任务流程失败: 服务器返回空数据'
          return null
        }
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

  // 更新任务步骤
  const updateTaskStep = async (stepData) => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.updateTaskStep(stepData)
      if (response.code === 200) {
        // 如果更新成功，刷新任务详情和流程
        await fetchTaskDetail(stepData.taskId)
        return true
      } else {
        error.value = response.message || '更新任务步骤失败'
        return false
      }
    } catch (err) {
      console.error('更新任务步骤错误:', err)
      error.value = err.message || '更新任务步骤时发生错误'
      return false
    } finally {
      loading.value = false
    }
  }

  // 上传任务图片
  const uploadTaskImage = async (taskId, file) => {
    // loading.value = true
    // error.value = null

    // try {
    //   const response = await taskApi.uploadTaskImage(taskId, file)
    //   if (response.code === 200 && response.data) {
    //     return response.data
    //   } else {
    //     error.value = response.message || '上传任务图片失败'
    //     return null
    //   }
    // } catch (err) {
    //   console.error('上传任务图片错误:', err)
    //   error.value = err.message || '上传任务图片时发生错误'
    //   return null
    // } finally {
    //   loading.value = false
    // }
    return Promise.resolve();
  }

  // 批量上传任务图片
  const batchUploadTaskImages = async (taskId, files) => {
    // loading.value = true
    // error.value = null

    // try {
    //   const response = await taskApi.batchUploadTaskImages(taskId, files)
    //   if (response.code === 200 && response.data) {
    //     return response.data
    //   } else {
    //     error.value = response.message || '批量上传任务图片失败'
    //     return null
    //   }
    // } catch (err) {
    //   console.error('批量上传任务图片错误:', err)
    //   error.value = err.message || '批量上传任务图片时发生错误'
    //   return null
    // } finally {
    //   loading.value = false
    // }
    return Promise.resolve();
  }

  // 创建任务
  const createTask = async (taskData) => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.createTask(taskData)
      if (response.code === 200 && response.data) {
        return response.data
      } else {
        error.value = response.message || '创建任务失败'
        return null
      }
    } catch (err) {
      console.error('创建任务错误:', err)
      error.value = err.message || '创建任务时发生错误'
      return null
    } finally {
      loading.value = false
    }
  }

  // 更新任务
  const updateTask = async (taskId, taskData) => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.updateTask(taskId, taskData)
      if (response.code === 200) {
        // 如果更新成功，刷新任务详情
        await fetchTaskDetail(taskId)
        return true
      } else {
        error.value = response.message || '更新任务失败'
        return false
      }
    } catch (err) {
      console.error('更新任务错误:', err)
      error.value = err.message || '更新任务时发生错误'
      return false
    } finally {
      loading.value = false
    }
  }

  // 取消任务
  const cancelTask = async (taskId, reason) => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.cancelTask(taskId, reason)
      if (response.code === 200) {
        // 如果取消成功，刷新任务详情
        await fetchTaskDetail(taskId)
        return true
      } else {
        error.value = response.message || '取消任务失败'
        return false
      }
    } catch (err) {
      console.error('取消任务错误:', err)
      error.value = err.message || '取消任务时发生错误'
      return false
    } finally {
      loading.value = false
    }
  }

  // 分配工程师
  const assignEngineers = async (taskId, engineerIds) => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.assignEngineers(taskId, engineerIds)
      if (response.code === 200) {
        // 如果分配成功，刷新任务详情
        await fetchTaskDetail(taskId)
        return true
      } else {
        error.value = response.message || '分配工程师失败'
        return false
      }
    } catch (err) {
      console.error('分配工程师错误:', err)
      error.value = err.message || '分配工程师时发生错误'
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取任务统计信息
  const getTaskStatistics = async () => {
    loading.value = true
    error.value = null

    try {
      const response = await taskApi.getTaskStatistics()
      if (response.code === 200 && response.data) {
        return response.data
      } else {
        error.value = response.message || '获取任务统计信息失败'
        return null
      }
    } catch (err) {
      console.error('获取任务统计信息错误:', err)
      error.value = err.message || '获取任务统计信息时发生错误'
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取工程师列表
  const getEngineersList = async () => {
    loading.value = true
    error.value = null

    try {
      // 这里需要实现获取工程师列表的API调用
      // 暂时返回模拟数据
      return {
        code: 200,
        message: "操作成功",
        data: [
          { id: 2, name: "张三" }
        ]
      }
    } catch (err) {
      console.error('获取工程师列表错误:', err)
      error.value = err.message || '获取工程师列表时发生错误'
      return null
    } finally {
      loading.value = false
    }
  }

  // 预加载用户相关任务详情（用于提高用户体验）
  const preloadUserTaskDetails = async () => {
    if (tasks.value.length === 0 || cacheLoading.value) {
      return
    }

    cacheLoading.value = true
    
    try {
      // 只预加载前5个任务的详情
      const tasksToLoad = tasks.value.slice(0, 5)
      
      for (const task of tasksToLoad) {
        if (!taskCache.value[task.taskId]) {
          // 使用Promise.all并行加载任务详情和流程
          const [detailResponse] = await Promise.all([
            taskApi.getTaskDetail(task.taskId)
          ])
          
          if (detailResponse.code === 200 && detailResponse.data) {
            taskCache.value[task.taskId] = detailResponse.data
            cacheCount.value++
          }
        }
      }
      
      isCacheLoaded.value = true
    } catch (err) {
      console.error('预加载任务详情错误:', err)
    } finally {
      cacheLoading.value = false
    }
  }

  // 清除缓存
  const clearCache = () => {
    taskCache.value = {}
    cacheCount.value = 0
    isCacheLoaded.value = false
  }

  return {
    // 状态
    tasks,
    loading,
    error,
    currentTask,
    cacheLoading,
    cacheCount,
    isCacheLoaded,
    
    // 方法
    fetchTasks,
    fetchEngineerTasks,
    fetchSalesTasks,
    fetchTaskDetail,
    fetchTaskFlow,
    updateTaskStep,
    uploadTaskImage,
    batchUploadTaskImages,
    createTask,
    updateTask,
    cancelTask,
    assignEngineers,
    getTaskStatistics,
    getEngineersList,
    preloadUserTaskDetails,
    clearCache
  }
}) 