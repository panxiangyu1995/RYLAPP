/**
 * 仓库模块状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as warehouseApi from '../api/warehouse'

export const useWarehouseStore = defineStore('warehouse', () => {
  // 状态
  const warehouses = ref([])
  const currentWarehouse = ref(null)
  const warehouseItems = ref([])
  const currentItem = ref(null)
  const itemCategories = ref([
    { value: 'parts', label: '配件库', icon: 'icon-tools' },
    { value: 'instruments', label: '仪器库', icon: 'icon-microscope' },
    { value: 'consumables', label: '耗材库', icon: 'icon-vial' },
    { value: 'assets', label: '固定资产', icon: 'icon-desktop' }
  ])
  const loading = ref(false)
  const error = ref(null)
  const stockRecords = ref([])
  const itemRequests = ref([])
  const inventoryChecks = ref([])
  const currentInventoryCheck = ref(null)
  const statsData = ref({
    inventory: null,
    stock: null,
    usage: null
  })

  // 获取所有仓库
  async function fetchAllWarehouses() {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getAllWarehouses()
      
      if (response.code === 200 && response.data) {
        warehouses.value = response.data || []
        return { success: true, data: warehouses.value }
      } else {
        throw new Error(response.message || '获取仓库列表失败')
      }
    } catch (err) {
      console.error('获取仓库列表错误:', err)
      error.value = err.message || '获取仓库列表过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取仓库详情
  async function fetchWarehouseDetail(warehouseId) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getWarehouseDetail(warehouseId)
      
      if (response.code === 200 && response.data) {
        currentWarehouse.value = response.data
        return { success: true, data: currentWarehouse.value }
      } else {
        throw new Error(response.message || '获取仓库详情失败')
      }
    } catch (err) {
      console.error('获取仓库详情错误:', err)
      error.value = err.message || '获取仓库详情过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取仓库物品列表
  async function fetchWarehouseItems(warehouseId, params = {}) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getWarehouseItems(warehouseId, params)
      
      if (response.code === 200 && response.data) {
        warehouseItems.value = response.data.list || []
        return { 
          success: true, 
          data: warehouseItems.value,
          pagination: {
            total: response.data.total,
            pages: response.data.pages,
            current: response.data.current,
            size: response.data.size
          } 
        }
      } else {
        throw new Error(response.message || '获取物品列表失败')
      }
    } catch (err) {
      console.error('获取物品列表错误:', err)
      error.value = err.message || '获取物品列表过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取物品详情
  async function fetchItemDetail(itemId) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getItemDetail(itemId)
      
      if (response.code === 200 && response.data) {
        currentItem.value = response.data
        return { success: true, data: currentItem.value }
      } else {
        throw new Error(response.message || '获取物品详情失败')
      }
    } catch (err) {
      console.error('获取物品详情错误:', err)
      error.value = err.message || '获取物品详情过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 物品入库
  async function performStockIn(stockInData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.stockIn(stockInData)
      
      if (response.code === 200 && response.data) {
        // 更新物品库存
        if (currentItem.value && currentItem.value.id === stockInData.itemId) {
          currentItem.value.quantity += stockInData.quantity
        }
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '物品入库失败')
      }
    } catch (err) {
      console.error('物品入库错误:', err)
      error.value = err.message || '物品入库过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 物品出库
  async function performStockOut(stockOutData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.stockOut(stockOutData)
      
      if (response.code === 200 && response.data) {
        // 更新物品库存
        if (currentItem.value && currentItem.value.id === stockOutData.itemId) {
          currentItem.value.quantity -= stockOutData.quantity
        }
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '物品出库失败')
      }
    } catch (err) {
      console.error('物品出库错误:', err)
      error.value = err.message || '物品出库过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取出入库记录
  async function fetchStockRecords(params = {}) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getStockRecords(params)
      
      if (response.code === 200 && response.data) {
        stockRecords.value = response.data.list || []
        return { 
          success: true, 
          data: stockRecords.value,
          pagination: {
            total: response.data.total,
            pages: response.data.pages,
            current: response.data.current,
            size: response.data.size
          } 
        }
      } else {
        throw new Error(response.message || '获取出入库记录失败')
      }
    } catch (err) {
      console.error('获取出入库记录错误:', err)
      error.value = err.message || '获取出入库记录过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 提交物品使用申请
  async function submitItemUseRequest(requestData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.requestItemUse(requestData)
      
      if (response.code === 200 && response.data) {
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '提交物品使用申请失败')
      }
    } catch (err) {
      console.error('提交物品使用申请错误:', err)
      error.value = err.message || '提交物品使用申请过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 提交物品采购申请
  async function submitItemPurchaseRequest(requestData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.requestItemPurchase(requestData)
      
      if (response.code === 200 && response.data) {
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '提交物品采购申请失败')
      }
    } catch (err) {
      console.error('提交物品采购申请错误:', err)
      error.value = err.message || '提交物品采购申请过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 获取申请列表
  async function fetchItemRequests(params = {}) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.getItemRequests(params)
      
      if (response.code === 200 && response.data) {
        itemRequests.value = response.data.list || []
        return { 
          success: true, 
          data: itemRequests.value,
          pagination: {
            total: response.data.total,
            pages: response.data.pages,
            current: response.data.current,
            size: response.data.size
          } 
        }
      } else {
        throw new Error(response.message || '获取申请列表失败')
      }
    } catch (err) {
      console.error('获取申请列表错误:', err)
      error.value = err.message || '获取申请列表过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 处理申请
  async function processRequest(requestId, processData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.processItemRequest(requestId, processData)
      
      if (response.code === 200 && response.data) {
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '处理申请失败')
      }
    } catch (err) {
      console.error('处理申请错误:', err)
      error.value = err.message || '处理申请过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 计算属性：按分类分组的物品
  const itemsByCategory = computed(() => {
    const result = {}
    
    itemCategories.value.forEach(category => {
      result[category.value] = warehouseItems.value.filter(
        item => item.categoryId === getCategoryIdByValue(category.value)
      )
    })
    
    return result
  })

  // 辅助函数：根据分类值获取分类ID
  function getCategoryIdByValue(categoryValue) {
    const categoryMap = {
      'parts': 2,        // 配件库
      'instruments': 1,  // 仪器库
      'consumables': 3,  // 耗材库
      'assets': 4        // 固定资产
    }
    
    return categoryMap[categoryValue] || 0
  }

  // 辅助函数：根据分类ID获取分类值
  function getCategoryValueById(categoryId) {
    const categoryMap = {
      1: 'instruments',  // 仪器库
      2: 'parts',        // 配件库
      3: 'consumables',  // 耗材库
      4: 'assets'        // 固定资产
    }
    
    return categoryMap[categoryId] || ''
  }

  // 扫码解析
  async function parseQRCodeContent(content) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.parseQRCode(content)
      
      if (response.code === 200 && response.data) {
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '解析二维码失败')
      }
    } catch (err) {
      console.error('解析二维码错误:', err)
      error.value = err.message || '解析二维码过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 更新物品信息
  async function updateItemDetail(itemId, itemData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.updateItem(itemId, itemData)
      
      if (response.code === 200) {
        // 更新当前物品数据
        if (currentItem.value && currentItem.value.id === parseInt(itemId)) {
          currentItem.value = { ...currentItem.value, ...itemData }
        }
        return { success: true, data: response.data }
      } else {
        throw new Error(response.message || '更新物品信息失败')
      }
    } catch (err) {
      console.error('更新物品信息错误:', err)
      error.value = err.message || '更新物品信息过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 删除仓库
  async function deleteWarehouse(warehouseId, password) {
    loading.value = true
    error.value = null
    
    try {
      const response = await warehouseApi.deleteWarehouse(warehouseId, password)
      
      if (response.code === 200) {
        // 删除成功，从列表中移除
        warehouses.value = warehouses.value.filter(w => w.id !== parseInt(warehouseId))
        return { success: true }
      } else {
        throw new Error(response.message || '删除仓库失败')
      }
    } catch (err) {
      console.error('删除仓库错误:', err)
      error.value = err.message || '删除仓库过程中发生错误'
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    warehouses,
    currentWarehouse,
    warehouseItems,
    currentItem,
    itemCategories,
    loading,
    error,
    stockRecords,
    itemRequests,
    inventoryChecks,
    currentInventoryCheck,
    statsData,
    
    // 计算属性
    itemsByCategory,
    
    // 方法
    fetchAllWarehouses,
    fetchWarehouseDetail,
    fetchWarehouseItems,
    fetchItemDetail,
    performStockIn,
    performStockOut,
    fetchStockRecords,
    submitItemUseRequest,
    submitItemPurchaseRequest,
    fetchItemRequests,
    processRequest,
    parseQRCodeContent,
    getCategoryIdByValue,
    getCategoryValueById,
    updateItemDetail,
    deleteWarehouse
  }
}) 