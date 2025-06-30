<template>
  <div class="item-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>物品详情</h1>
      <div class="header-right">
        <i class="icon-edit" @click="editItem"></i>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <template v-else>
      <!-- 物品基本信息 -->
      <div class="item-card">
        <div class="item-header">
          <h2 class="item-name">{{ item.name }}</h2>
          <span :class="['stock-badge', getStockStatusClass(item.quantity)]">
            {{ getStockStatusText(item.quantity, item.warningThreshold) }}
          </span>
        </div>
        
        <div class="info-list">
          <div class="info-item">
            <span class="info-label">型号</span>
            <span class="info-value">{{ item.model || '无型号' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">库存数量</span>
            <span class="info-value">{{ item.quantity }}个</span>
          </div>
          <div class="info-item">
            <span class="info-label">物品类别</span>
            <span class="info-value">{{ getCategoryName(item.categoryId) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">存放位置</span>
            <span class="info-value">{{ item.location || '未设置位置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">入库日期</span>
            <span class="info-value">{{ formatDate(item.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最后更新时间</span>
            <span class="info-value">{{ formatDate(item.updateTime) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">警戒库存</span>
            <span class="info-value">{{ item.warningThreshold || 5 }}个</span>
          </div>
          <div v-if="item.description" class="info-description">
            <span class="info-label">物品描述</span>
            <p class="description-text">{{ item.description || '暂无描述' }}</p>
          </div>
        </div>
      </div>

      <!-- 出入库记录 -->
      <div class="section-card">
        <div class="section-header">
          <h3 class="section-title">出入库记录</h3>
          <button class="view-all-btn" @click="viewAllRecords">查看全部</button>
        </div>
        
        <div class="records-list">
          <div v-if="stockRecords.length > 0">
            <div 
              v-for="(record, index) in stockRecords" 
              :key="record.id"
              class="record-item"
            >
              <div class="record-icon" :class="record.type === 'IN' ? 'in' : 'out'">
                <i :class="record.type === 'IN' ? 'icon-arrow-down' : 'icon-arrow-up'"></i>
              </div>
              <div class="record-details">
                <div class="record-header">
                  <span class="record-action">{{ record.type === 'IN' ? '入库' : '出库' }}</span>
                  <span class="record-amount">{{ record.quantity }}个</span>
                </div>
                <div class="record-info">
                  <span class="record-operator">{{ record.operatorName }}</span>
                  <span class="record-date">{{ formatDate(record.operateTime) }}</span>
                </div>
                <p v-if="record.remark" class="record-remark">备注: {{ record.remark }}</p>
              </div>
            </div>
          </div>
          <div v-else class="empty-records">
            <p>暂无出入库记录</p>
          </div>
        </div>
      </div>

      <!-- 底部操作栏 -->
      <div class="bottom-actions">
        <button class="btn-in" @click="showStockInForm">
          <i class="icon-arrow-down"></i>
          入库
        </button>
        <button class="btn-out" @click="showStockOutForm">
          <i class="icon-arrow-up"></i>
          出库
        </button>
      </div>
    </template>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useWarehouseStore } from '../../stores/warehouse'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'WarehouseItemDetailPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseStore = useWarehouseStore()
    const authStore = useAuthStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const itemId = computed(() => route.params.itemId)
    const loading = ref(true)
    
    // 物品详情数据
    const item = computed(() => warehouseStore.currentItem || {})
    
    // 是否有仓库管理权限
    const isWarehouseAdmin = computed(() => authStore.hasRole('ROLE_WAREHOUSE'))
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 编辑物品
    const editItem = () => {
      router.push(`/warehouse/${warehouseId.value}/item/${itemId.value}/edit`)
    }
    
    // 显示入库表单
    const showStockInForm = () => {
      router.push(`/warehouse/${warehouseId.value}/stock-in?itemId=${itemId.value}`)
    }
    
    // 显示出库表单
    const showStockOutForm = () => {
      router.push(`/warehouse/${warehouseId.value}/stock-out?itemId=${itemId.value}`)
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
    
    // 根据类别ID获取类别名称
    const getCategoryName = (categoryId) => {
      const categoryValue = warehouseStore.getCategoryValueById(categoryId)
      const category = warehouseStore.itemCategories.find(cat => cat.value === categoryValue)
      return category ? category.label : '未知类别'
    }
    
    // 出入库记录
    const stockRecords = computed(() => warehouseStore.stockRecords || [])
    
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
    
    // 查看所有记录
    const viewAllRecords = () => {
      router.push(`/warehouse/${warehouseId.value}/item/${itemId.value}/records`)
    }
    
    // 显示物品申请表单
    const showRequestForm = () => {
      router.push(`/warehouse/${warehouseId.value}/request?itemId=${itemId.value}`)
    }
    
    // 加载物品详情
    const loadItemDetail = async () => {
      loading.value = true
      try {
        // 加载物品详情
        await warehouseStore.fetchItemDetail(itemId.value)
        
        // 加载物品的出入库记录
        await warehouseStore.fetchStockRecords({ itemId: itemId.value, limit: 5 })
      } catch (error) {
        console.error('加载物品详情失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      loadItemDetail()
    })
    
    return {
      loading,
      item,
      stockRecords,
      isWarehouseAdmin,
      goBack,
      editItem,
      showStockInForm,
      showStockOutForm,
      showRequestForm,
      viewAllRecords,
      getStockStatusClass,
      getStockStatusText,
      getCategoryName,
      formatDate
    }
  }
}
</script>

<style scoped>
.item-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  width: 24px;
  text-align: right;
}

.icon-edit::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
}

.spinner {
  border: 3px solid #f3f4f6;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
  margin-bottom: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.item-card {
  background-color: #fff;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.stock-badge {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.status-sufficient {
  background-color: #d1fae5;
  color: #059669;
}

.status-low {
  background-color: #fef3c7;
  color: #d97706;
}

.status-out {
  background-color: #fee2e2;
  color: #dc2626;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
}

.info-label {
  color: #6b7280;
  font-size: 14px;
}

.info-value {
  font-weight: 500;
  font-size: 14px;
}

.info-description {
  margin-top: 8px;
}

.description-text {
  background-color: #f9fafb;
  padding: 12px;
  border-radius: 6px;
  color: #4b5563;
  font-size: 14px;
  line-height: 1.5;
  margin: 8px 0 0 0;
}

.section-card {
  background-color: #fff;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.view-all-btn {
  color: #3b82f6;
  background: none;
  border: none;
  font-size: 14px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  display: flex;
  gap: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.record-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.record-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.record-icon.in {
  background-color: #d1fae5;
  color: #059669;
}

.record-icon.out {
  background-color: #fee2e2;
  color: #dc2626;
}

.icon-arrow-down::before {
  content: '\f063';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-arrow-up::before {
  content: '\f062';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.record-details {
  flex: 1;
}

.record-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.record-action {
  font-weight: 500;
}

.record-amount {
  font-weight: 500;
}

.record-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 4px;
}

.record-remark {
  font-size: 12px;
  color: #6b7280;
  margin: 0;
  background-color: #f9fafb;
  padding: 4px 8px;
  border-radius: 4px;
}

.empty-records {
  text-align: center;
  color: #9ca3af;
  padding: 16px 0;
}

.bottom-actions {
  position: fixed;
  bottom: 56px; /* 底部导航高度 */
  left: 0;
  right: 0;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 16px;
  z-index: 10;
}

.bottom-actions button {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-in {
  background-color: #d1fae5;
  color: #059669;
}

.btn-out {
  background-color: #fee2e2;
  color: #dc2626;
}

.btn-request {
  background-color: #e0f2fe;
  color: #0ea5e9;
}

.icon-file-alt::before {
  content: '\f15c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}
</style> 