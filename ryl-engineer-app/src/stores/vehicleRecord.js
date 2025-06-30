import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  createVehicleRecord,
  getMyVehicleRecords,
  getVehicleRecordById,
  getVehicleRecordsByTaskId,
  getVehicleRecordsByTimeRange,
  getMonthlyVehicleRecordStats,
  getCurrentMonthVehicleRecordStats
} from '../api/vehicleRecord'

export const useVehicleRecordStore = defineStore('vehicleRecord', () => {
  const records = ref([])
  const currentRecord = ref(null)
  const stats = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // 创建打卡记录
  async function addVehicleRecord(recordData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await createVehicleRecord(recordData)
      
      if (response.code === 200) {
        // 创建成功后刷新记录列表
        await fetchMyRecords()
        return { success: true, recordId: response.data }
      } else {
        throw new Error(response.message || '创建打卡记录失败')
      }
    } catch (err) {
      error.value = err.message || '创建打卡记录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取当前用户的打卡记录列表
  async function fetchMyRecords() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getMyVehicleRecords()
      
      if (response.code === 200) {
        records.value = response.data || []
        return { success: true, records: records.value }
      } else {
        throw new Error(response.message || '获取打卡记录失败')
      }
    } catch (err) {
      error.value = err.message || '获取打卡记录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 根据ID获取打卡记录
  async function fetchRecordById(id) {
    loading.value = true
    error.value = null
    
    try {
      const response = await getVehicleRecordById(id)
      
      if (response.code === 200) {
        currentRecord.value = response.data
        return { success: true, record: currentRecord.value }
      } else {
        throw new Error(response.message || '获取打卡记录详情失败')
      }
    } catch (err) {
      error.value = err.message || '获取打卡记录详情过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 根据任务ID获取打卡记录列表
  async function fetchRecordsByTaskId(taskId) {
    loading.value = true
    error.value = null
    
    try {
      const response = await getVehicleRecordsByTaskId(taskId)
      
      if (response.code === 200) {
        records.value = response.data || []
        return { success: true, records: records.value }
      } else {
        throw new Error(response.message || '获取任务打卡记录失败')
      }
    } catch (err) {
      error.value = err.message || '获取任务打卡记录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取指定时间范围内的打卡记录列表
  async function fetchRecordsByTimeRange(startTime, endTime) {
    loading.value = true
    error.value = null
    
    try {
      const response = await getVehicleRecordsByTimeRange(startTime, endTime)
      
      if (response.code === 200) {
        records.value = response.data || []
        return { success: true, records: records.value }
      } else {
        throw new Error(response.message || '获取时间范围内打卡记录失败')
      }
    } catch (err) {
      error.value = err.message || '获取时间范围内打卡记录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取指定月份的打卡统计信息
  async function fetchMonthlyStats(year, month) {
    loading.value = true
    error.value = null
    
    try {
      const response = await getMonthlyVehicleRecordStats(year, month)
      
      if (response.code === 200) {
        stats.value = response.data
        return { success: true, stats: stats.value }
      } else {
        throw new Error(response.message || '获取月度统计信息失败')
      }
    } catch (err) {
      error.value = err.message || '获取月度统计信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取当月的打卡统计信息
  async function fetchCurrentMonthStats() {
    loading.value = true
    error.value = null
    
    try {
      const response = await getCurrentMonthVehicleRecordStats()
      
      if (response.code === 200) {
        stats.value = response.data
        return { success: true, stats: stats.value }
      } else {
        throw new Error(response.message || '获取当月统计信息失败')
      }
    } catch (err) {
      error.value = err.message || '获取当月统计信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    records,
    currentRecord,
    stats,
    loading,
    error,
    addVehicleRecord,
    fetchMyRecords,
    fetchRecordById,
    fetchRecordsByTaskId,
    fetchRecordsByTimeRange,
    fetchMonthlyStats,
    fetchCurrentMonthStats
  }
}) 