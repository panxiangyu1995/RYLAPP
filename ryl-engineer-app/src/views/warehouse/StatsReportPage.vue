<template>
  <div class="stats-report-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>统计报表</h1>
      <div class="header-right"></div>
    </header>

    <!-- 统计类型切换 -->
    <div class="stats-tabs">
      <button 
        v-for="(tab, index) in tabs"
        :key="index"
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <div class="filter-group">
        <label>日期范围:</label>
        <select v-model="dateRange" class="date-select">
          <option value="7">最近7天</option>
          <option value="30">最近30天</option>
          <option value="90">最近90天</option>
          <option value="180">最近半年</option>
          <option value="365">最近一年</option>
        </select>
      </div>
      
      <div class="filter-group" v-if="activeTab !== 'inventory'">
        <label>物品类别:</label>
        <select v-model="categoryFilter" class="category-select">
          <option value="">全部类别</option>
          <option 
            v-for="category in categories" 
            :key="category.value" 
            :value="category.value"
          >
            {{ category.label }}
          </option>
        </select>
      </div>

      <button class="filter-btn" @click="fetchStatsData">
        <i class="icon-filter"></i>
        应用筛选
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 库存统计 -->
    <div v-else-if="activeTab === 'inventory'" class="stats-container">
      <div class="stats-overview">
        <div class="stats-card total-items">
          <div class="card-value">{{ inventoryStats.totalItems || 0 }}</div>
          <div class="card-label">物品总数</div>
        </div>
        <div class="stats-card total-value">
          <div class="card-value">¥{{ formatNumber(inventoryStats.totalValue) }}</div>
          <div class="card-label">库存总价值</div>
        </div>
        <div class="stats-card warning-count">
          <div class="card-value">{{ inventoryStats.warningCount || 0 }}</div>
          <div class="card-label">预警物品</div>
        </div>
      </div>

      <!-- 分类统计 -->
      <div class="category-stats">
        <h3>按分类统计</h3>
        <div class="category-chart">
          <div 
            v-for="(cat, index) in inventoryStats.categories" 
            :key="index"
            class="chart-bar"
          >
            <div class="bar-label">{{ cat.name }}</div>
            <div class="bar-container">
              <div 
                class="bar-value" 
                :style="{ width: `${Math.min(100, cat.percentage)}%` }"
                :class="{ 'bar-parts': cat.value === 'parts',
                         'bar-instruments': cat.value === 'instruments',
                         'bar-consumables': cat.value === 'consumables',
                         'bar-assets': cat.value === 'assets' }"
              ></div>
              <span class="bar-text">{{ cat.count }} ({{ cat.percentage }}%)</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 预警物品列表 -->
      <div class="warning-list">
        <h3>库存预警物品</h3>
        <div v-if="inventoryStats.warningItems && inventoryStats.warningItems.length > 0" class="item-list">
          <div 
            v-for="item in inventoryStats.warningItems" 
            :key="item.id"
            class="warning-item"
            @click="navigateToItem(item.id)"
          >
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-model">{{ item.model }}</div>
            </div>
            <div :class="['item-quantity', 
              item.quantity <= 0 ? 'quantity-danger' : 'quantity-warning'
            ]">
              {{ item.quantity }}
              <small>库存</small>
            </div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无库存预警物品</p>
        </div>
      </div>
    </div>

    <!-- 出入库统计 -->
    <div v-else-if="activeTab === 'stock'" class="stats-container">
      <div class="stats-overview">
        <div class="stats-card in-count">
          <div class="card-value">{{ stockStats.inCount || 0 }}</div>
          <div class="card-label">入库次数</div>
        </div>
        <div class="stats-card out-count">
          <div class="card-value">{{ stockStats.outCount || 0 }}</div>
          <div class="card-label">出库次数</div>
        </div>
        <div class="stats-card balance">
          <div class="card-value" :class="{ 'negative': stockStats.balance < 0 }">
            {{ stockStats.balance > 0 ? '+' : '' }}{{ stockStats.balance || 0 }}
          </div>
          <div class="card-label">净变化量</div>
        </div>
      </div>

      <!-- 趋势图 -->
      <div class="trend-chart">
        <h3>出入库趋势</h3>
        <div class="chart-container">
          <!-- 简化版图表实现 -->
          <div class="chart-grid">
            <div v-for="(point, index) in stockStats.trend" :key="index" class="chart-point">
              <div class="chart-date">{{ point.date }}</div>
              <div class="chart-bars">
                <div class="in-bar" :style="{ height: `${point.inPercentage}%` }"></div>
                <div class="out-bar" :style="{ height: `${point.outPercentage}%` }"></div>
              </div>
            </div>
          </div>
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color in-color"></div>
              <div class="legend-label">入库</div>
            </div>
            <div class="legend-item">
              <div class="legend-color out-color"></div>
              <div class="legend-label">出库</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近出入库记录 -->
      <div class="recent-records">
        <h3>最近出入库记录</h3>
        <div v-if="stockStats.recentRecords && stockStats.recentRecords.length > 0" class="records-list">
          <div 
            v-for="record in stockStats.recentRecords" 
            :key="record.id"
            class="record-item"
          >
            <div class="record-type" :class="record.type === 'in' ? 'type-in' : 'type-out'">
              {{ record.type === 'in' ? '入库' : '出库' }}
            </div>
            <div class="record-info">
              <div class="record-item-name">{{ record.itemName }}</div>
              <div class="record-details">
                <span>{{ record.quantity }}个</span>
                <span>{{ formatDate(record.date) }}</span>
              </div>
            </div>
            <div class="record-operator">{{ record.operator }}</div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无出入库记录</p>
        </div>
      </div>
    </div>

    <!-- 物品使用统计 -->
    <div v-else class="stats-container">
      <div class="stats-overview">
        <div class="stats-card total-uses">
          <div class="card-value">{{ usageStats.totalUses || 0 }}</div>
          <div class="card-label">使用总次数</div>
        </div>
        <div class="stats-card avg-uses">
          <div class="card-value">{{ usageStats.avgUsesPerItem || 0 }}</div>
          <div class="card-label">平均使用次数</div>
        </div>
        <div class="stats-card popular-count">
          <div class="card-value">{{ usageStats.popularItemCount || 0 }}</div>
          <div class="card-label">热门物品数</div>
        </div>
      </div>

      <!-- 热门物品排行 -->
      <div class="popular-items">
        <h3>热门物品排行</h3>
        <div v-if="usageStats.popularItems && usageStats.popularItems.length > 0" class="ranking-list">
          <div 
            v-for="(item, index) in usageStats.popularItems" 
            :key="item.id"
            class="ranking-item"
            @click="navigateToItem(item.id)"
          >
            <div class="rank-number">{{ index + 1 }}</div>
            <div class="rank-info">
              <div class="rank-name">{{ item.name }}</div>
              <div class="rank-model">{{ item.model }}</div>
            </div>
            <div class="rank-value">
              <div class="value-number">{{ item.useCount }}</div>
              <div class="value-label">次使用</div>
            </div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无使用数据</p>
        </div>
      </div>

      <!-- 使用者统计 -->
      <div class="users-stats">
        <h3>使用者统计</h3>
        <div v-if="usageStats.topUsers && usageStats.topUsers.length > 0" class="users-list">
          <div 
            v-for="(user, index) in usageStats.topUsers" 
            :key="index"
            class="user-item"
          >
            <div class="user-info">
              <div class="user-name">{{ user.name }}</div>
              <div class="user-department">{{ user.department }}</div>
            </div>
            <div class="user-value">
              {{ user.useCount }}次
            </div>
          </div>
        </div>
        <div v-else class="empty-list">
          <p>暂无使用者数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useWarehouseStore } from '../../stores/warehouse'

export default {
  name: 'StatsReportPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseStore = useWarehouseStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const loading = ref(false)
    
    // 统计选项卡
    const tabs = [
      { label: '库存统计', value: 'inventory' },
      { label: '出入库统计', value: 'stock' },
      { label: '使用统计', value: 'usage' }
    ]
    
    const activeTab = ref('inventory')
    const dateRange = ref('30')
    const categoryFilter = ref('')
    
    // 统计数据
    const inventoryStats = ref({})
    const stockStats = ref({})
    const usageStats = ref({})
    
    // 物品类别
    const categories = computed(() => warehouseStore.itemCategories)
    
    // 格式化数字
    const formatNumber = (num) => {
      if (num === undefined || num === null) return '0'
      return new Intl.NumberFormat('zh-CN').format(Number(num).toFixed(2))
    }
    
    // 格式化日期
    const formatDate = (dateStr) => {
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
    
    // 获取统计数据
    const fetchStatsData = async () => {
      if (!warehouseId.value) return
      
      loading.value = true
      
      try {
        // 构建查询参数
        const params = {
          warehouseId: warehouseId.value,
          days: Number(dateRange.value)
        }
        
        if (categoryFilter.value && activeTab !== 'inventory') {
          params.categoryId = warehouseStore.getCategoryIdByValue(categoryFilter.value)
        }
        
        // 根据当前选项卡获取相应的统计数据
        if (activeTab.value === 'inventory') {
          const result = await warehouseStore.getInventoryStats(params)
          if (result.success) {
            inventoryStats.value = result.data
          }
        } else if (activeTab.value === 'stock') {
          const result = await warehouseStore.getStockStats(params)
          if (result.success) {
            stockStats.value = result.data
          }
        } else {
          const result = await warehouseStore.getItemUsageStats(params)
          if (result.success) {
            usageStats.value = result.data
          }
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 导航到物品详情页
    const navigateToItem = (itemId) => {
      router.push(`/warehouse/${warehouseId.value}/item/${itemId}`)
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 页面加载时获取统计数据
    onMounted(fetchStatsData)
    
    return {
      warehouseId,
      loading,
      tabs,
      activeTab,
      dateRange,
      categoryFilter,
      categories,
      inventoryStats,
      stockStats,
      usageStats,
      formatNumber,
      formatDate,
      fetchStatsData,
      navigateToItem,
      goBack
    }
  }
}
</script>

<style scoped>
.stats-report-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
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
  margin: 0;
}

.header-right {
  width: 24px;
}

.stats-tabs {
  display: flex;
  background-color: #fff;
  padding: 4px;
  margin: 12px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.tab-btn {
  flex: 1;
  padding: 12px 8px;
  border: none;
  background: none;
  border-radius: 6px;
  font-size: 14px;
  color: #6b7280;
}

.tab-btn.active {
  background-color: #e0f2fe;
  color: #0284c7;
  font-weight: 500;
}

.filter-bar {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  margin-bottom: 12px;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  color: #4b5563;
}

.date-select, .category-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #f9fafb;
  font-size: 14px;
}

.filter-btn {
  margin-left: auto;
  padding: 8px 12px;
  border: none;
  background-color: #3b82f6;
  color: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.icon-filter::before {
  content: '\f0b0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.stats-container {
  padding: 0 16px;
}

.stats-overview {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.stats-card {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.card-label {
  font-size: 14px;
  color: #6b7280;
}

.total-items .card-value {
  color: #0284c7;
}

.total-value .card-value {
  color: #059669;
}

.warning-count .card-value {
  color: #f59e0b;
}

.in-count .card-value {
  color: #059669;
}

.out-count .card-value {
  color: #dc2626;
}

.balance .card-value {
  color: #0284c7;
}

.balance .card-value.negative {
  color: #dc2626;
}

.total-uses .card-value, .avg-uses .card-value, .popular-count .card-value {
  color: #8b5cf6;
}

.category-stats, .warning-list, .trend-chart, .recent-records, .popular-items, .users-stats {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #1f2937;
}

.category-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chart-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 70px;
  font-size: 14px;
  color: #4b5563;
}

.bar-container {
  flex: 1;
  height: 24px;
  background-color: #f3f4f6;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.bar-value {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s;
}

.bar-parts {
  background-color: #3b82f6;
}

.bar-instruments {
  background-color: #8b5cf6;
}

.bar-consumables {
  background-color: #f59e0b;
}

.bar-assets {
  background-color: #10b981;
}

.bar-text {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #4b5563;
}

.warning-item, .record-item, .ranking-item, .user-item {
  padding: 12px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.warning-item:last-child, .record-item:last-child, .ranking-item:last-child, .user-item:last-child {
  border-bottom: none;
}

.warning-item:hover, .ranking-item:hover {
  background-color: #f9fafb;
}

.item-info, .record-info, .rank-info, .user-info {
  flex: 1;
}

.item-name, .record-item-name, .rank-name, .user-name {
  font-size: 14px;
  font-weight: 500;
}

.item-model, .record-details, .rank-model, .user-department {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.record-details {
  display: flex;
  gap: 12px;
}

.item-quantity, .record-type, .rank-value, .user-value {
  font-size: 14px;
  font-weight: 500;
  text-align: center;
  min-width: 60px;
}

.quantity-warning {
  color: #f59e0b;
}

.quantity-danger {
  color: #dc2626;
}

.item-quantity small {
  display: block;
  font-weight: normal;
  font-size: 12px;
  color: #6b7280;
}

.record-type {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  min-width: 40px;
}

.type-in {
  background-color: #dcfce7;
  color: #16a34a;
}

.type-out {
  background-color: #fee2e2;
  color: #dc2626;
}

.record-operator {
  font-size: 12px;
  color: #6b7280;
  margin-left: 12px;
}

.chart-container {
  position: relative;
  height: 200px;
}

.chart-grid {
  display: flex;
  height: 160px;
  justify-content: space-between;
  align-items: flex-end;
  padding-bottom: 20px;
}

.chart-point {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.chart-date {
  position: absolute;
  bottom: 0;
  font-size: 10px;
  color: #6b7280;
  transform: rotate(-45deg);
  transform-origin: top left;
  margin-top: 6px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  height: 100%;
  width: 24px;
  gap: 2px;
}

.in-bar, .out-bar {
  width: 10px;
  transition: height 0.3s;
  border-radius: 2px 2px 0 0;
}

.in-bar {
  background-color: #10b981;
}

.out-bar {
  background-color: #ef4444;
}

.chart-legend {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.in-color {
  background-color: #10b981;
}

.out-color {
  background-color: #ef4444;
}

.legend-label {
  font-size: 12px;
  color: #4b5563;
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #4b5563;
  margin-right: 12px;
}

.ranking-item:nth-child(1) .rank-number {
  background-color: #fef3c7;
  color: #d97706;
}

.ranking-item:nth-child(2) .rank-number {
  background-color: #e5e7eb;
  color: #4b5563;
}

.ranking-item:nth-child(3) .rank-number {
  background-color: #fecaca;
  color: #b91c1c;
}

.value-number {
  font-weight: 600;
  color: #8b5cf6;
}

.value-label {
  font-size: 12px;
  color: #6b7280;
}

.empty-list {
  padding: 20px 0;
  text-align: center;
  color: #9ca3af;
}
</style> 