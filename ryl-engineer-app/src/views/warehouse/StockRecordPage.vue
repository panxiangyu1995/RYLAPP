<template>
  <div class="stock-record-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>出入库记录</h1>
      <div class="header-right">
        <i class="icon-filter" @click="showFilter = !showFilter"></i>
      </div>
    </header>

    <!-- 物品信息 -->
    <div v-if="item" class="item-info-card">
      <div class="item-header">
        <h2 class="item-name">{{ item.name }}</h2>
        <span :class="['stock-badge', getStockStatusClass(item.quantity)]">
          {{ getStockStatusText(item.quantity, item.warningThreshold) }}
        </span>
      </div>
      
      <div class="info-row">
        <div class="info-item">
          <span class="info-label">型号</span>
          <span class="info-value">{{ item.model || '无型号' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">库存数量</span>
          <span class="info-value">{{ item.quantity }}个</span>
        </div>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div v-if="showFilter" class="filter-container">
      <div class="filter-group">
        <label class="filter-label">记录类型</label>
        <div class="filter-options">
          <button 
            :class="['filter-option', { active: recordType === '' }]" 
            @click="recordType = ''"
          >
            全部
          </button>
          <button 
            :class="['filter-option', { active: recordType === 'IN' }]" 
            @click="recordType = 'IN'"
          >
            入库
          </button>
          <button 
            :class="['filter-option', { active: recordType === 'OUT' }]" 
            @click="recordType = 'OUT'"
          >
            出库
          </button>
        </div>
      </div>
      
      <div class="filter-group">
        <label class="filter-label">时间范围</label>
        <div class="filter-options date-range">
          <select v-model="dateRange">
            <option value="7">最近7天</option>
            <option value="30">最近30天</option>
            <option value="90">最近90天</option>
            <option value="180">最近180天</option>
            <option value="365">最近一年</option>
            <option value="0">全部</option>
          </select>
        </div>
      </div>
      
      <button class="apply-filter-btn" @click="applyFilter">应用筛选</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 记录列表 -->
    <div v-else class="records-container">
      <div v-if="records.length > 0" class="records-list">
        <div 
          v-for="(record, index) in records" 
          :key="record.id"
          class="record-item"
        >
          <div class="record-icon" :class="record.recordType === 'IN' ? 'in' : 'out'">
            <i :class="record.recordType === 'IN' ? 'icon-arrow-down' : 'icon-arrow-up'"></i>
          </div>
          <div class="record-details">
            <div class="record-header">
              <span class="record-action">{{ record.recordType === 'IN' ? '入库' : '出库' }}</span>
              <span class="record-amount">{{ record.quantity }}个</span>
            </div>
            <div class="record-info">
              <span class="record-code">{{ record.recordCode }}</span>
              <span class="record-operator">操作人: {{ record.operatorName }}</span>
              <span class="record-date">{{ formatDate(record.operationTime) }}</span>
              <span v-if="record.taskId" class="record-task">
                关联任务: {{ record.taskId }}
              </span>
            </div>
            <p v-if="record.description" class="record-remark">
              备注: {{ record.description }}
            </p>
          </div>
        </div>
      </div>
      <div v-else class="empty-records">
        <p>暂无出入库记录</p>
      </div>
      
      <!-- 分页控件 -->
      <div v-if="totalPages > 1" class="pagination">
        <button 
          :disabled="currentPage === 1" 
          @click="changePage(currentPage - 1)"
          class="page-btn prev"
        >
          上一页
        </button>
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage === totalPages" 
          @click="changePage(currentPage + 1)"
          class="page-btn next"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useWarehouseStore } from '../../stores/warehouse'

export default {
  name: 'StockRecordPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseStore = useWarehouseStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const itemId = computed(() => route.params.itemId)
    const loading = ref(true)
    const item = computed(() => warehouseStore.currentItem || null)
    const records = ref([])
    const showFilter = ref(false)
    
    // 筛选条件
    const recordType = ref('')
    const dateRange = ref('30')
    
    // 分页
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 根据库存数量获取对应的CSS类
    const getStockStatusClass = (quantity) => {
      if (quantity <= 0) return 'status-out'
      if (quantity <= 5) return 'status-low'
      return 'status-sufficient'
    }
    
    // 根据库存数量获取状态文本
    const getStockStatusText = (quantity, warningThreshold) => {
      if (quantity <= 0) return '缺货'
      if (quantity <= (warningThreshold || 5)) return '库存紧张'
      return '库存充足'
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return '未知'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    
    // 应用筛选条件
    const applyFilter = () => {
      currentPage.value = 1
      fetchRecords()
      showFilter.value = false
    }
    
    // 切换分页
    const changePage = (page) => {
      currentPage.value = page
      fetchRecords()
    }
    
    // 获取出入库记录
    const fetchRecords = async () => {
      loading.value = true
      
      try {
        // 构建查询参数
        const params = {
          itemId: itemId.value,
          page: currentPage.value,
          size: pageSize.value
        }
        
        // 添加筛选条件
        if (recordType.value) {
          params.recordType = recordType.value
        }
        
        if (dateRange.value !== '0') {
          const endDate = new Date()
          const startDate = new Date()
          startDate.setDate(startDate.getDate() - parseInt(dateRange.value))
          
          params.startDate = startDate.toISOString().split('T')[0]
          params.endDate = endDate.toISOString().split('T')[0]
        }
        
        const result = await warehouseStore.fetchStockRecords(params)
        
        if (result.success) {
          records.value = result.data
          total.value = result.pagination.total
        } else {
          console.error('获取出入库记录失败:', result.error)
        }
      } catch (error) {
        console.error('获取出入库记录错误:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 加载物品详情
    const loadItemDetail = async () => {
      if (!itemId.value) return
      
      try {
        await warehouseStore.fetchItemDetail(itemId.value)
      } catch (error) {
        console.error('加载物品详情失败:', error)
      }
    }
    
    // 监听筛选条件变化
    watch([recordType, dateRange], () => {
      // 自动应用简单的筛选条件
      if (!showFilter.value) {
        applyFilter()
      }
    })
    
    onMounted(() => {
      loadItemDetail()
      fetchRecords()
    })
    
    return {
      loading,
      item,
      records,
      showFilter,
      recordType,
      dateRange,
      currentPage,
      totalPages,
      goBack,
      applyFilter,
      changePage,
      getStockStatusClass,
      getStockStatusText,
      formatDate
    }
  }
}
</script>

<style scoped>
.stock-record-page {
  padding-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  border-bottom: 1px solid #eaecef;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left, .header-right {
  width: 40px;
  display: flex;
  align-items: center;
}

.header h1 {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
  flex: 1;
  text-align: center;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 16px;
  color: #333;
}

.icon-filter::before {
  content: '\f0b0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 16px;
  color: #333;
}

.item-info-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-name {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.stock-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-out {
  background-color: #fee2e2;
  color: #ef4444;
}

.status-low {
  background-color: #fef3c7;
  color: #f59e0b;
}

.status-sufficient {
  background-color: #d1fae5;
  color: #10b981;
}

.info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 120px;
}

.info-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #111827;
}

.filter-container {
  background-color: #f9fafb;
  margin: 0 12px 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-group {
  margin-bottom: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  display: block;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-option {
  flex: 1;
  min-width: 80px;
  padding: 8px 12px;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
  color: #4b5563;
  cursor: pointer;
}

.filter-option.active {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #fff;
}

.date-range select {
  width: 100%;
  padding: 8px 12px;
  background-color: #fff;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  color: #4b5563;
}

.apply-filter-btn {
  width: 100%;
  padding: 10px;
  background-color: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 3px solid #e5e7eb;
  border-top-color: #2563eb;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.records-container {
  padding: 0 12px;
}

.records-list {
  margin-bottom: 20px;
}

.record-item {
  display: flex;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.record-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  flex-shrink: 0;
}

.record-icon.in {
  background-color: #d1fae5;
  color: #10b981;
}

.record-icon.out {
  background-color: #fee2e2;
  color: #ef4444;
}

.icon-arrow-down::before {
  content: '\f063';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 16px;
}

.icon-arrow-up::before {
  content: '\f062';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 16px;
}

.record-details {
  flex: 1;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.record-action {
  font-size: 16px;
  font-weight: 500;
}

.record-amount {
  font-size: 16px;
  font-weight: 600;
}

.record-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
  color: #6b7280;
  font-size: 13px;
}

.record-code, .record-operator, .record-date, .record-task {
  display: inline-block;
  margin-right: 12px;
}

.record-remark {
  font-size: 14px;
  color: #4b5563;
  margin: 8px 0 0;
}

.empty-records {
  text-align: center;
  padding: 40px 0;
  color: #6b7280;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 12px;
  font-size: 14px;
  color: #4b5563;
}
</style> 