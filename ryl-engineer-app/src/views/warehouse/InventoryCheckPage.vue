<template>
  <div class="inventory-check-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>仓库盘点</h1>
      <div class="header-right"></div>
    </header>

    <!-- 盘点信息 -->
    <div class="inventory-info">
      <h2>{{ warehouseName }}</h2>
      <div class="info-row">
        <span class="info-label">盘点日期:</span>
        <span class="info-value">{{ formattedDate }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">盘点人员:</span>
        <span class="info-value">{{ userName }}</span>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <button 
        v-for="(category, index) in categories"
        :key="index"
        :class="['category-tab', { active: selectedCategory === category.value }]"
        @click="selectedCategory = category.value"
      >
        <i :class="['category-icon', category.icon]"></i>
        <span>{{ category.label }}</span>
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <div class="search-input">
        <i class="icon-search"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索物品名称或编号"
        >
        <i v-if="searchQuery" class="icon-close" @click="searchQuery = ''"></i>
      </div>
      <button class="scan-btn" @click="goToScanner">
        <i class="icon-qrcode"></i>
      </button>
    </div>

    <!-- 物品列表 -->
    <div class="item-list-container">
      <div v-if="loading" class="loading-spinner">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="filteredItems.length === 0" class="empty-state">
        <i class="icon-box"></i>
        <p>当前分类下暂无物品</p>
      </div>
      
      <div v-else class="item-list">
        <div 
          v-for="item in filteredItems" 
          :key="item.id"
          class="item-card"
        >
          <div class="item-info">
            <div class="item-header">
              <h3 class="item-name">{{ item.name }}</h3>
              <span :class="['item-status', 
                item.status === 'normal' ? 'status-normal' : 
                item.status === 'warning' ? 'status-warning' : 'status-danger'
              ]">
                {{ getStatusText(item.status) }}
              </span>
            </div>
            <p class="item-model">型号: {{ item.model }}</p>
            <p class="item-code">物品编号: {{ item.code }}</p>
            <div class="item-quantity">
              <div class="quantity-row">
                <span class="system-quantity">系统库存: {{ item.systemQuantity }}</span>
                <span class="actual-quantity">实际库存: 
                  <input 
                    type="number" 
                    v-model="item.actualQuantity" 
                    min="0"
                    @input="updateItemDifference(item)"
                  >
                </span>
              </div>
              <div class="quantity-difference" :class="{'difference-warning': item.difference !== 0}">
                差异: {{ item.difference }}
              </div>
            </div>
          </div>
          <div class="remark-input">
            <label>备注:</label>
            <input 
              type="text" 
              v-model="item.remark" 
              placeholder="输入盘点备注"
            >
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-actions">
      <button class="btn-cancel" @click="goBack">取消</button>
      <button class="btn-confirm" @click="submitInventoryCheck">提交盘点</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useWarehouseStore } from '../../stores/warehouse'
import { useAuthStore } from '../../stores/auth'

export default {
  name: 'InventoryCheckPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseStore = useWarehouseStore()
    const authStore = useAuthStore()
    
    const warehouseId = computed(() => route.params.warehouseId)
    const warehouseName = ref('')
    const userName = computed(() => {
      const user = authStore.user
      return user ? user.name : ''
    })
    
    const currentDate = new Date()
    const formattedDate = computed(() => {
      const year = currentDate.getFullYear()
      const month = String(currentDate.getMonth() + 1).padStart(2, '0')
      const day = String(currentDate.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    })
    
    const selectedCategory = ref('parts')
    const searchQuery = ref('')
    const loading = ref(false)
    
    // 物品类别
    const categories = computed(() => warehouseStore.itemCategories)
    
    // 仓库物品列表
    const items = ref([])
    
    // 根据分类和搜索筛选物品
    const filteredItems = computed(() => {
      let result = items.value.filter(item => {
        const categoryId = warehouseStore.getCategoryIdByValue(selectedCategory.value)
        return item.categoryId === categoryId
      })
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(item => 
          item.name.toLowerCase().includes(query) || 
          item.code.toLowerCase().includes(query) ||
          item.model.toLowerCase().includes(query)
        )
      }
      
      return result
    })
    
    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        normal: '正常',
        warning: '预警',
        danger: '紧缺'
      }
      return statusMap[status] || '未知'
    }
    
    // 更新物品差异
    const updateItemDifference = (item) => {
      item.difference = item.actualQuantity - item.systemQuantity
    }
    
    // 获取仓库信息
    const fetchWarehouseInfo = async () => {
      if (!warehouseId.value) return
      
      try {
        const result = await warehouseStore.fetchWarehouseDetail(warehouseId.value)
        if (result.success) {
          warehouseName.value = result.data.name
        }
      } catch (error) {
        console.error('获取仓库信息失败:', error)
      }
    }
    
    // 获取仓库物品
    const fetchWarehouseItems = async () => {
      if (!warehouseId.value) return
      
      loading.value = true
      
      try {
        const result = await warehouseStore.fetchWarehouseItems(warehouseId.value)
        
        if (result.success) {
          // 转换为盘点所需的数据结构
          items.value = result.data.map(item => ({
            ...item,
            systemQuantity: item.quantity,
            actualQuantity: item.quantity,
            difference: 0,
            remark: '',
            status: item.quantity <= 0 ? 'danger' : 
                   item.quantity <= item.minQuantity ? 'warning' : 'normal'
          }))
        }
      } catch (error) {
        console.error('获取仓库物品失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 前往扫码页面
    const goToScanner = () => {
      router.push({ 
        name: 'Scanner',
        query: { 
          returnTo: 'InventoryCheck',
          warehouseId: warehouseId.value
        }
      })
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 提交盘点
    const submitInventoryCheck = async () => {
      // 验证所有物品都已盘点
      if (items.value.length === 0) {
        alert('没有物品可供盘点')
        return
      }
      
      // 构建盘点数据
      const checkData = {
        warehouseId: warehouseId.value,
        checkDate: formattedDate.value,
        checkUser: userName.value,
        items: items.value.map(item => ({
          itemId: item.id,
          systemQuantity: item.systemQuantity,
          actualQuantity: item.actualQuantity,
          difference: item.difference,
          remark: item.remark
        }))
      }
      
      try {
        const result = await warehouseStore.createInventoryCheck(checkData)
        
        if (result.success) {
          alert('盘点提交成功')
          router.push(`/warehouse/${warehouseId.value}`)
        } else {
          alert(`盘点提交失败: ${result.error}`)
        }
      } catch (error) {
        console.error('提交盘点错误:', error)
        alert('盘点提交过程中发生错误，请稍后重试')
      }
    }
    
    onMounted(() => {
      fetchWarehouseInfo()
      fetchWarehouseItems()
    })
    
    return {
      warehouseName,
      userName,
      formattedDate,
      selectedCategory,
      categories,
      searchQuery,
      loading,
      filteredItems,
      getStatusText,
      updateItemDifference,
      goToScanner,
      goBack,
      submitInventoryCheck
    }
  }
}
</script>

<style scoped>
.inventory-check-page {
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
  margin: 0;
}

.header-right {
  width: 24px;
}

.inventory-info {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

.inventory-info h2 {
  font-size: 16px;
  margin: 0 0 12px 0;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-label {
  color: #6b7280;
  width: 80px;
}

.info-value {
  font-weight: 500;
}

.category-tabs {
  display: flex;
  background-color: #fff;
  padding: 12px 16px;
  overflow-x: auto;
  gap: 12px;
}

.category-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 60px;
  background: none;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  color: #6b7280;
}

.category-tab.active {
  background-color: #e0f2fe;
  color: #3b82f6;
}

.category-icon {
  margin-bottom: 4px;
  font-size: 16px;
}

.icon-tools::before {
  content: '\f7d9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-microscope::before {
  content: '\f610';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-vial::before {
  content: '\f492';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-desktop::before {
  content: '\f108';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.search-bar {
  padding: 12px 16px;
  display: flex;
  gap: 12px;
}

.search-input {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #fff;
  border-radius: 8px;
  padding: 0 12px;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
}

.search-input input {
  flex: 1;
  border: none;
  padding: 10px 8px;
  font-size: 14px;
  outline: none;
}

.icon-close::before {
  content: '\f00d';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #9ca3af;
}

.scan-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #3b82f6;
  border: none;
  border-radius: 8px;
  color: #fff;
}

.icon-qrcode::before {
  content: '\f029';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.item-list-container {
  padding: 0 16px;
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #9ca3af;
}

.icon-box::before {
  content: '\f466';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 32px;
  margin-bottom: 12px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.item-info {
  padding: 16px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.item-status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-normal {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-warning {
  background-color: #fef3c7;
  color: #d97706;
}

.status-danger {
  background-color: #fee2e2;
  color: #dc2626;
}

.item-model, .item-code {
  margin: 4px 0;
  font-size: 14px;
  color: #6b7280;
}

.item-quantity {
  margin-top: 12px;
  border-top: 1px solid #f3f4f6;
  padding-top: 12px;
}

.quantity-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.system-quantity, .actual-quantity {
  font-size: 14px;
}

.actual-quantity input {
  width: 60px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  padding: 4px 8px;
  text-align: center;
}

.quantity-difference {
  font-size: 14px;
  text-align: right;
}

.difference-warning {
  color: #dc2626;
  font-weight: 500;
}

.remark-input {
  padding: 12px 16px;
  background-color: #f9fafb;
  border-top: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
}

.remark-input label {
  font-size: 14px;
  color: #6b7280;
  margin-right: 12px;
}

.remark-input input {
  flex: 1;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  padding: 8px;
  font-size: 14px;
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

.btn-cancel {
  flex: 1;
  padding: 12px 0;
  border: 1px solid #d1d5db;
  background-color: #fff;
  border-radius: 8px;
  font-weight: 500;
  color: #4b5563;
}

.btn-confirm {
  flex: 2;
  padding: 12px 0;
  border: none;
  background-color: #3b82f6;
  border-radius: 8px;
  font-weight: 500;
  color: #fff;
}
</style> 