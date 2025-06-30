<template>
    <div class="warehouse-detail-page">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left" @click="goBack">
          <i class="icon-arrow-left"></i>
        </div>
        <h1>{{ warehouseInfo.name }}</h1>
        <div class="header-right">
          <i class="icon-search" @click="toggleSearch"></i>
          <i class="icon-qrcode" @click="openScanner"></i>
        </div>
      </header>
  
      <!-- 搜索栏 -->
      <div v-if="showSearchBar" class="search-bar">
        <div class="search-input-container">
          <i class="icon-search-input"></i>
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="搜索物品名称、型号..."
            class="search-input"
          >
          <i v-if="searchQuery" class="icon-times" @click="clearSearch"></i>
        </div>
      </div>
  
      <!-- 类别选择 -->
      <div class="category-tabs">
        <button 
          v-for="(category, index) in categories"
          :key="index"
          :class="['tab-btn', { active: activeCategory === category.value }]"
          @click="activeCategory = category.value"
        >
          <i :class="['tab-icon', category.icon]"></i>
          <span>{{ category.label }}</span>
        </button>
      </div>
  
      <!-- 筛选条件 -->
      <div class="filter-bar">
        <button class="filter-btn" @click="toggleFilter">
          <i class="icon-filter"></i>
          筛选
        </button>
        <div class="sort-options">
          <span>排序:</span>
          <button 
            v-for="(option, index) in sortOptions" 
            :key="index"
            :class="['sort-btn', { active: activeSortOption === option.value }]"
            @click="activeSortOption = option.value"
          >
            {{ option.label }}
          </button>
        </div>
      </div>
      
      <!-- 添加物品按钮 - 仅仓库管理员可见 -->
      <div class="add-item-bar" v-if="isWarehouseAdmin">
        <button class="btn-add-item" @click="addNewItem">
          <i class="icon-plus"></i>
          新增物品
        </button>
      </div>
  
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
  
      <!-- 物品列表 -->
      <div v-else-if="filteredItems.length > 0" class="items-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id"
          class="item-card"
          @click="viewItemDetail(item.id)"
        >
          <div class="item-header">
            <span :class="['stock-badge', getStockStatusClass(item.quantity)]">
              {{ getStockStatusText(item.quantity, item.warningThreshold) }}
            </span>
          </div>
          <div class="item-info">
            <h3 class="item-name">{{ item.name }}</h3>
            <p class="item-model">{{ item.model || '无型号' }}</p>
            <div class="item-stock">
              <i class="icon-box"></i>
              <span>{{ item.quantity }}个</span>
            </div>
          </div>
        </div>
      </div>
  
      <!-- 无数据状态 -->
      <div v-else class="empty-state">
        <i class="icon-empty-box"></i>
        <p>{{ searchQuery ? '没有找到匹配的物品' : '该分类暂无物品' }}</p>
      </div>
  
      <!-- 底部操作按钮 - 仅对仓库管理员显示 -->
      <div v-if="isWarehouseAdmin" class="bottom-actions">
        <button class="btn-in" @click="showStockInForm">
          <i class="icon-arrow-down"></i>
          入库
        </button>
        <button class="btn-out" @click="showStockOutForm">
          <i class="icon-arrow-up"></i>
          出库
        </button>
      </div>
      
      <!-- 非仓库管理员的申请按钮 -->
      <div v-else class="bottom-actions">
        <button class="btn-request" @click="showRequestForm">
          <i class="icon-file-alt"></i>
          申请配件
        </button>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, computed, onMounted, watch } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { useAuthStore } from '../../stores/auth'
import { useWarehouseStore } from '../../stores/warehouse'
  
  export default {
    name: 'WarehouseDetailPage',
    setup() {
      const router = useRouter()
      const route = useRoute()
      const authStore = useAuthStore()
    const warehouseStore = useWarehouseStore()
      
      const warehouseId = computed(() => route.params.id)
      const loading = ref(true)
      const showSearchBar = ref(false)
      const searchQuery = ref('')
      const activeCategory = ref('parts')
      const activeSortOption = ref('name')
    
    // 仓库信息
    const warehouseInfo = computed(() => warehouseStore.currentWarehouse || {})
      
      // 角色权限判断
      const isWarehouseAdmin = computed(() => authStore.hasRole('ROLE_WAREHOUSE'))
      
    // 物品类别
    const categories = computed(() => warehouseStore.itemCategories)
      
      const sortOptions = [
        { label: '名称', value: 'name' },
        { label: '库存', value: 'stock' }
      ]
      
      // 显示/隐藏搜索栏
      const toggleSearch = () => {
        showSearchBar.value = !showSearchBar.value
      }
      
      // 清空搜索
      const clearSearch = () => {
        searchQuery.value = ''
      }
      
      // 显示/隐藏筛选选项
      const toggleFilter = () => {
        console.log('显示筛选选项')
        // 后续实现筛选功能
      }
      
      // 返回上一页
      const goBack = () => {
        router.back()
      }
      
      // 查看物品详情
      const viewItemDetail = (itemId) => {
        router.push(`/warehouse/${warehouseId.value}/item/${itemId}`)
      }
      
      // 添加新物品
      const addNewItem = () => {
        router.push(`/warehouse/${warehouseId.value}/item/add`)
      }
      
      // 打开扫码器
      const openScanner = () => {
        console.log('打开扫码器')
        // 后续实现扫码功能
        router.push(`/warehouse/scanner?warehouseId=${warehouseId.value}`)
      }
      
      // 显示入库表单
      const showStockInForm = () => {
        if (!isWarehouseAdmin.value) {
          alert('只有仓库管理员才能执行入库操作')
          return
        }
        console.log('显示入库表单')
        // 后续实现入库功能
        router.push(`/warehouse/${warehouseId.value}/stock-in`)
      }
      
      // 显示出库表单
      const showStockOutForm = () => {
        if (!isWarehouseAdmin.value) {
          alert('只有仓库管理员才能执行出库操作')
          return
        }
        console.log('显示出库表单')
        // 后续实现出库功能
        router.push(`/warehouse/${warehouseId.value}/stock-out`)
      }
      
      // 显示申请表单
      const showRequestForm = () => {
        console.log('显示申请配件表单')
        // 后续实现申请配件功能
        router.push(`/warehouse/${warehouseId.value}/request`)
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
      
      // 过滤后的物品列表
      const filteredItems = computed(() => {
        // 按当前选择的类别筛选物品
        const categoryId = warehouseStore.getCategoryIdByValue(activeCategory.value)
        let result = warehouseStore.warehouseItems.filter(item => item.categoryId === categoryId)
        
        // 按搜索关键词筛选
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase()
          result = result.filter(item => 
            item.name.toLowerCase().includes(query) || 
            item.model.toLowerCase().includes(query)
          )
        }
        
        // 按选择的排序选项排序
        if (activeSortOption.value === 'name') {
          result.sort((a, b) => a.name.localeCompare(b.name))
        } else if (activeSortOption.value === 'stock') {
          result.sort((a, b) => b.quantity - a.quantity)
        }
        
        return result
      })
      
      // 加载仓库信息和物品
      const loadWarehouseData = async () => {
        loading.value = true
        try {
          // 加载仓库详细信息
          await warehouseStore.fetchWarehouseDetail(warehouseId.value)
          
          // 加载仓库内物品列表
          await warehouseStore.fetchWarehouseItems(warehouseId.value)
        } catch (error) {
          console.error('加载仓库数据失败:', error)
        } finally {
          loading.value = false
        }
      }
      
      // 当仓库ID变化时重新加载数据
      watch(warehouseId, () => {
        loadWarehouseData()
      })
      
      onMounted(() => {
        loadWarehouseData()
      })
      
      return {
        warehouseInfo,
        categories,
        sortOptions,
        activeCategory,
        activeSortOption,
        searchQuery,
        showSearchBar,
        loading,
        filteredItems,
        isWarehouseAdmin,
        toggleSearch,
        clearSearch,
        toggleFilter,
        goBack,
        viewItemDetail,
        openScanner,
        showStockInForm,
        showStockOutForm,
        showRequestForm,
        getStockStatusClass,
        getStockStatusText,
        addNewItem
      }
    }
  }
  </script>
  
  <style scoped>
  .warehouse-detail-page {
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
    display: flex;
    gap: 16px;
  }
  
  .icon-search::before {
    content: '\f002';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    color: #666;
  }
  
  .icon-qrcode::before {
    content: '\f029';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    color: #666;
  }
  
  .search-bar {
    padding: 12px 16px;
    background-color: #fff;
    border-bottom: 1px solid #f3f4f6;
  }
  
  .search-input-container {
    position: relative;
    display: flex;
    align-items: center;
    background-color: #f3f4f6;
    border-radius: 8px;
    padding: 0 12px;
  }
  
  .icon-search-input::before {
    content: '\f002';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    color: #9ca3af;
  }
  
  .search-input {
    flex: 1;
    border: none;
    background: transparent;
    height: 40px;
    font-size: 14px;
    margin-left: 8px;
    outline: none;
  }
  
  .icon-times::before {
    content: '\f00d';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    color: #9ca3af;
    cursor: pointer;
  }
  
  .category-tabs {
    display: flex;
    background-color: #fff;
    border-bottom: 1px solid #f3f4f6;
  }
  
  .tab-btn {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px 4px;
    background: none;
    border: none;
    color: #6b7280;
    font-size: 12px;
    transition: all 0.2s;
  }
  
  .tab-btn.active {
    color: #3b82f6;
    position: relative;
  }
  
  .tab-btn.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 30%;
    width: 40%;
    height: 2px;
    background-color: #3b82f6;
  }
  
  .tab-icon {
    font-size: 18px;
    margin-bottom: 4px;
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
  
  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background-color: #fff;
    border-bottom: 1px solid #f3f4f6;
  }
  
  .filter-btn {
    display: flex;
    align-items: center;
    background: none;
    border: none;
    color: #6b7280;
    font-size: 14px;
  }
  
  .icon-filter::before {
    content: '\f0b0';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    margin-right: 4px;
  }
  
  .sort-options {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #6b7280;
  }
  
  .sort-btn {
    background: none;
    border: none;
    color: #6b7280;
    font-size: 14px;
  }
  
  .sort-btn.active {
    color: #3b82f6;
    font-weight: 500;
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
  
  .items-grid {
    padding: 12px;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .item-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    padding: 8px;
    cursor: pointer;
    transition: transform 0.2s;
  }
  
  .item-card:active {
    transform: scale(0.98);
  }
  
  .item-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 4px;
  }
  
  .stock-badge {
    font-size: 9px;
    padding: 1px 5px;
    border-radius: 10px;
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
  
  .item-info {
    font-size: 14px;
  }
  
  .item-name {
    margin: 0 0 3px 0;
    font-size: 13px;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .item-model {
    color: #6b7280;
    margin: 0 0 4px 0;
    font-size: 11px;
  }
  
  .item-stock {
    display: flex;
    align-items: center;
    gap: 3px;
    color: #6b7280;
    font-size: 11px;
  }
  
  .icon-box::before {
    content: '\f466';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
  }
  
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 48px 0;
    color: #9ca3af;
  }
  
  .icon-empty-box::before {
    content: '\f466';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
    font-size: 48px;
    margin-bottom: 16px;
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
    color: #0369a1;
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
  
  .icon-file-alt::before {
    content: '\f15c';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
  }
  
  .add-item-bar {
    padding: 12px 16px;
    background-color: #fff;
    border-bottom: 1px solid #f3f4f6;
  }
  
  .btn-add-item {
    display: flex;
    align-items: center;
    gap: 8px;
    background-color: #3b82f6;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 10px 0;
    font-size: 14px;
    cursor: pointer;
    width: 100%;
    justify-content: center;
  }
  
  .icon-plus::before {
    content: '\f067';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
  }
  </style>