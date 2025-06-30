<template>
  <div class="vehicle-info-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>车辆信息</h1>
      <div class="header-right">
        <button class="edit-btn" @click="editVehicle">编辑</button>
      </div>
    </header>

    <!-- 主要内容 -->
    <div class="content">
      <div v-if="loading" class="loading-container">
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <!-- 车辆信息卡片 -->
        <div class="info-card" v-if="hasVehicle">
          <div class="vehicle-icon">
            <i :class="['icon-car', {'icon-car-company': vehicleInfo.vehicleType === '公车'}]"></i>
          </div>
          
          <div class="info-list">
            <div class="info-item">
              <div class="info-label">车牌号</div>
              <div class="info-value">{{ vehicleInfo.plateNumber }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">车辆类型</div>
              <div class="info-value">{{ vehicleInfo.vehicleType }}</div>
            </div>
            
            <div class="info-item">
              <div class="info-label">车辆ID</div>
              <div class="info-value">{{ vehicleInfo.vehicleId }}</div>
            </div>
          </div>
        </div>
        
        <!-- 无车辆信息提示 -->
        <div class="no-data-card" v-else>
          <div class="no-data-icon">
            <i class="icon-car-slash"></i>
          </div>
          <p class="no-data-text">暂无车辆信息</p>
          <button class="add-btn" @click="addVehicle">添加车辆信息</button>
        </div>
        
        <!-- 查看更多车辆 -->
        <div class="action-card" v-if="hasVehicle">
          <button class="view-more-btn" @click="viewAllVehicles">
            <i class="icon-list"></i>
            查看所有车辆
          </button>
        </div>
        
        <!-- 车辆使用须知 -->
        <div class="notice-card">
          <h3>车辆使用须知</h3>
          <ul class="notice-list">
            <li>请确保车辆信息真实有效，以便公司进行管理和报销</li>
            <li>上门服务时，请按要求进行打卡记录</li>
            <li>公车使用需提前申请，并遵守公司车辆使用管理规定</li>
          </ul>
        </div>
      </template>
      
      <!-- 错误提示 -->
      <div v-if="globalError" class="global-error">
        {{ globalError }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 车辆信息
const vehicleInfo = reactive({
  vehicleId: '',
  plateNumber: '',
  vehicleType: ''
})

// 全局错误
const globalError = ref('')
const loading = ref(false)

// 是否有车辆信息
const hasVehicle = computed(() => {
  return !!vehicleInfo.vehicleId
})

// 页面加载时获取车辆信息
onMounted(async () => {
  loading.value = true
  try {
    // 检查是否有ID参数
    const vehicleId = route.params.id
    
    if (vehicleId) {
      // 如果有ID参数，获取指定车辆信息
      const result = await userStore.fetchVehicleById(vehicleId)
      if (result.success) {
        const vehicle = result.vehicle
        vehicleInfo.vehicleId = vehicle.vehicleId
        vehicleInfo.plateNumber = vehicle.plateNumber
        vehicleInfo.vehicleType = vehicle.vehicleType
      } else {
        console.log('获取指定车辆信息失败')
        globalError.value = result.error
      }
    } else {
      // 如果没有ID参数，获取默认车辆信息
      const result = await userStore.fetchUserVehicle()
      if (result.success) {
        const vehicle = result.vehicle
        vehicleInfo.vehicleId = vehicle.vehicleId
        vehicleInfo.plateNumber = vehicle.plateNumber
        vehicleInfo.vehicleType = vehicle.vehicleType
      } else {
        console.log('没有车辆信息')
        // 如果是404错误，表示没有车辆信息，不显示错误
        if (result.error && !result.error.includes('404')) {
          globalError.value = result.error
        }
      }
    }
  } catch (error) {
    console.error('获取车辆信息失败:', error)
    globalError.value = '获取车辆信息失败，请稍后再试'
  } finally {
    loading.value = false
  }
})

// 返回上一页
function goBack() {
  router.back()
}

// 编辑当前车辆信息
function editVehicle() {
  if (vehicleInfo.vehicleId) {
    router.push(`/profile/vehicle/edit?id=${vehicleInfo.vehicleId}`)
  } else {
    router.push('/profile/vehicle/edit?mode=add')
  }
}

// 添加新车辆
function addVehicle() {
  router.push('/profile/vehicle/edit?mode=add')
}

// 查看所有车辆
function viewAllVehicles() {
  router.push('/profile/vehicle/list')
}
</script>

<style scoped>
.vehicle-info-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px; /* 增加底部padding，防止被导航栏遮挡 */
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
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
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.edit-btn {
  color: #3b82f6;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
}

.content {
  padding: 16px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
}

.vehicle-icon {
  width: 60px;
  height: 60px;
  background-color: #f0f9ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.icon-car::before {
  content: '\f1b9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 24px;
  color: #3b82f6;
}

.icon-car-company::before {
  color: #10b981;
}

.info-list {
  flex: 1;
}

.info-item {
  margin-bottom: 12px;
  display: flex;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  width: 80px;
  font-size: 14px;
  color: #666;
}

.info-value {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.no-data-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.no-data-icon {
  width: 64px;
  height: 64px;
  background-color: #f0f9ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.icon-car-slash::before {
  content: '\f5e1';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 24px;
  color: #9ca3af;
}

.no-data-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 16px;
}

.add-btn {
  padding: 10px 20px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
}

.action-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

.view-more-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px;
  background-color: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}

.icon-list::before {
  content: '\f0ca';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.notice-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.notice-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.notice-list {
  list-style-type: none;
  padding: 0;
}

.notice-list li {
  position: relative;
  padding-left: 16px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
}

.notice-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  top: 0;
  color: #3b82f6;
}

.global-error {
  margin-top: 16px;
  padding: 12px;
  background-color: #fee2e2;
  color: #b91c1c;
  border-radius: 8px;
  font-size: 14px;
}
</style> 