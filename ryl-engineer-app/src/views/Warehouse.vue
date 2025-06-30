<template>
  <div class="warehouse-page">
    <!-- 顶部导航 -->
    <header class="header">
      <h1>仓库管理</h1>
      <div class="header-icons">
        <i class="icon-search" @click="showSearch"></i>
        <i class="icon-bell"></i>
      </div>
    </header>

    <!-- 搜索栏 -->
    <div v-if="showSearchBar" class="search-bar">
      <div class="search-input-container">
        <i class="icon-search-input"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索仓库或物品..."
          class="search-input"
        >
        <i v-if="searchQuery" class="icon-times" @click="clearSearch"></i>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-spinner">
      <div class="spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 仓库卡片列表 -->
    <div v-else class="warehouse-container">
      <div class="warehouse-header">
        <h2>仓库列表</h2>
        <div class="header-buttons">
          <button v-if="hasWarehousePermission" class="delete-btn" @click="deleteWarehouse">
            <i class="icon-trash"></i>
            删除仓库
          </button>
          <button v-if="hasWarehousePermission" class="add-btn" @click="createWarehouse">
            <i class="icon-plus"></i>
            新增仓库
          </button>
        </div>
      </div>
      
      <div v-if="warehouseLocations.length === 0" class="empty-state">
        <i class="icon-box-open"></i>
        <p>暂无仓库数据</p>
      </div>
      
      <div v-else class="warehouse-locations">
      <div 
        v-for="location in warehouseLocations" 
        :key="location.id"
        class="location-card"
        @click="goToWarehouseDetail(location.id)"
      >
        <div class="location-icon">
          <i class="icon-warehouse"></i>
        </div>
        <div class="location-info">
          <h3 class="location-name">{{ location.name }}</h3>
          <div class="location-stats">
            <span class="stats-item">
              <i class="icon-box"></i>
                {{ location.itemCount || 0 }}个物品
            </span>
            <span class="stats-item">
              <i class="icon-warning"></i>
                {{ location.warningCount || 0 }}个预警
            </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 新增仓库弹窗 -->
    <WarehouseAddModal 
      v-if="showAddWarehouseModal"
      @close="showAddWarehouseModal = false"
      @success="handleWarehouseAdded"
    />

    <!-- 删除仓库弹窗 -->
    <WarehouseDeleteModal 
      v-if="showDeleteWarehouseModal"
      @close="showDeleteWarehouseModal = false"
      @success="handleWarehouseDeleted"
    />
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useWarehouseStore } from '../stores/warehouse'
import { useAuthStore } from '../stores/auth'
import WarehouseAddModal from '../components/warehouse/WarehouseAddModal.vue'
import WarehouseDeleteModal from '../components/warehouse/WarehouseDeleteModal.vue'

export default {
  name: 'WarehousePage',
  components: {
    WarehouseAddModal,
    WarehouseDeleteModal
  },
  setup() {
    const router = useRouter()
    const warehouseStore = useWarehouseStore()
    const authStore = useAuthStore()
    
    const searchQuery = ref('')
    const showSearchBar = ref(false)
    const loading = ref(false)
    const showAddWarehouseModal = ref(false)
    const showDeleteWarehouseModal = ref(false)
    
    // 获取仓库列表
    const warehouseLocations = computed(() => {
      if (!searchQuery.value) {
        return warehouseStore.warehouses
      }
      
      const query = searchQuery.value.toLowerCase()
      return warehouseStore.warehouses.filter(warehouse => 
        warehouse.name.toLowerCase().includes(query)
      )
    })
    
    // 是否有仓库管理权限
    const hasWarehousePermission = computed(() => authStore.hasRole('ROLE_ADMIN') || authStore.hasRole('ROLE_WAREHOUSE'))

    // 显示/隐藏搜索栏
    const showSearch = () => {
      showSearchBar.value = !showSearchBar.value
    }

    // 清空搜索
    const clearSearch = () => {
      searchQuery.value = ''
    }

    // 跳转到仓库详情页面
    const goToWarehouseDetail = (locationId) => {
      router.push(`/warehouse/${locationId}`)
    }
    
    // 创建新仓库
    const createWarehouse = () => {
      showAddWarehouseModal.value = true
    }
    
    // 处理仓库添加成功
    const handleWarehouseAdded = (newWarehouse) => {
      showAddWarehouseModal.value = false
      // 刷新仓库列表
      loadWarehouseLocations()
    }

    // 删除仓库
    const deleteWarehouse = () => {
      showDeleteWarehouseModal.value = true
    }
    
    // 处理仓库删除成功
    const handleWarehouseDeleted = () => {
      showDeleteWarehouseModal.value = false
      // 显示删除成功提示
      alert('仓库删除成功')
    }

    // 加载仓库地点数据
    const loadWarehouseLocations = async () => {
      loading.value = true
      try {
        await warehouseStore.fetchAllWarehouses()
      } catch (error) {
        console.error('加载仓库列表失败:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadWarehouseLocations()
    })

    return {
      searchQuery,
      showSearchBar,
      warehouseLocations,
      loading,
      hasWarehousePermission,
      showSearch,
      clearSearch,
      goToWarehouseDetail,
      createWarehouse,
      showAddWarehouseModal,
      handleWarehouseAdded,
      deleteWarehouse,
      showDeleteWarehouseModal,
      handleWarehouseDeleted
    }
  }
}
</script>

<style scoped>
.warehouse-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 56px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
}

.header-icons {
  display: flex;
  gap: 16px;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.icon-bell::before {
  content: '\f0f3';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.search-bar {
  padding: 12px 16px;
  background-color: #fff;
  border-bottom: 1px solid #f3f4f6;
  margin-bottom: 12px;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.warehouse-container {
  padding: 12px 16px;
}

.warehouse-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-buttons {
  display: flex;
  gap: 8px;
}

.warehouse-header h2 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
}

.delete-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background-color: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-trash::before {
  content: '\f1f8';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.icon-box-open::before {
  content: '\f49e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 48px;
  color: #9ca3af;
  margin-bottom: 16px;
}

.empty-state p {
  color: #6b7280;
}

.warehouse-locations {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.location-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.location-card:active {
  transform: scale(0.98);
}

.location-icon {
  background-color: #eff6ff;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-warehouse::before {
  content: '\f494';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 24px;
  color: #3b82f6;
}

.location-info {
  flex: 1;
}

.location-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.location-stats {
  display: flex;
  gap: 16px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.icon-box::before {
  content: '\f466';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-warning::before {
  content: '\f071';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}
</style>