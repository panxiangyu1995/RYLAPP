<template>
  <div class="vehicle-list-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>车辆管理</h1>
      <div class="header-right">
        <button class="add-btn" @click="addVehicle">添加</button>
      </div>
    </header>

    <!-- 主要内容 -->
    <div class="content">
      <div v-if="loading" class="loading-container">
        <p>加载中...</p>
      </div>
      
      <template v-else>
        <!-- 车辆列表 -->
        <div class="vehicle-list" v-if="vehicles.length > 0">
          <div 
            v-for="(vehicle, index) in vehicles" 
            :key="index" 
            class="vehicle-card"
            @click="viewVehicleDetail(vehicle)"
          >
            <div class="vehicle-icon">
              <i :class="['icon-car', {'icon-car-company': vehicle.vehicleType === '公车'}]"></i>
            </div>
            
            <div class="vehicle-info">
              <h3 class="plate-number">{{ vehicle.plateNumber }}</h3>
              <p class="vehicle-type">{{ vehicle.vehicleType }}</p>
            </div>
            
            <div class="actions">
              <button class="edit-icon" @click.stop="editVehicle(vehicle)">
                <i class="icon-edit"></i>
              </button>
              <button class="delete-icon" @click.stop="confirmDelete(vehicle)">
                <i class="icon-delete"></i>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 无车辆信息提示 -->
        <div class="no-data-card" v-else>
          <div class="no-data-icon">
            <i class="icon-car-slash"></i>
          </div>
          <p class="no-data-text">暂无车辆信息</p>
          <button class="add-btn-large" @click="addVehicle">添加车辆信息</button>
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
      
      <!-- 删除确认弹窗 -->
      <div class="modal" v-if="showDeleteModal">
        <div class="modal-content">
          <h3>确认删除</h3>
          <p>确定要删除车牌号为"{{ selectedVehicle?.plateNumber }}"的车辆信息吗？</p>
          <div class="modal-actions">
            <button class="cancel-btn" @click="cancelDelete">取消</button>
            <button class="confirm-btn" @click="deleteVehicle">确认</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

// 车辆列表
const vehicles = ref([])
const selectedVehicle = ref(null)
const showDeleteModal = ref(false)

// 全局错误
const globalError = ref('')
const loading = ref(false)

// 检查token状态
onBeforeMount(() => {
  const token = localStorage.getItem('token')
  const tokenExpire = localStorage.getItem('tokenExpire')
  
  if (!token || !tokenExpire || new Date(tokenExpire) <= new Date()) {
    // token不存在或已过期，跳转到登录页
    router.push('/login')
    return
  }
})

// 页面加载时获取车辆信息
onMounted(async () => {
  await fetchVehicles()
})

// 获取车辆列表
async function fetchVehicles() {
  loading.value = true
  globalError.value = ''
  
  try {
    const result = await userStore.fetchUserVehicles()
    if (result.success) {
      vehicles.value = result.vehicles || []
      console.log('获取到车辆列表：', vehicles.value)
    } else {
      if (result.error && !result.error.includes('404')) {
        globalError.value = result.error
      }
    }
  } catch (error) {
    console.error('获取车辆信息失败:', error)
    globalError.value = '获取车辆信息失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 添加车辆
function addVehicle() {
  router.push('/profile/vehicle/edit?mode=add')
}

// 查看车辆详情
function viewVehicleDetail(vehicle) {
  router.push(`/profile/vehicle/info/${vehicle.vehicleId}`)
}

// 编辑车辆信息
function editVehicle(vehicle) {
  router.push(`/profile/vehicle/edit?id=${vehicle.vehicleId}`)
}

// 确认删除
function confirmDelete(vehicle) {
  selectedVehicle.value = vehicle
  showDeleteModal.value = true
}

// 取消删除
function cancelDelete() {
  selectedVehicle.value = null
  showDeleteModal.value = false
}

// 删除车辆
async function deleteVehicle() {
  if (!selectedVehicle.value) return
  
  try {
    const result = await userStore.deleteVehicle(selectedVehicle.value.vehicleId)
    if (result.success) {
      // 删除成功，刷新列表
      await fetchVehicles()
      showDeleteModal.value = false
      selectedVehicle.value = null
    } else {
      globalError.value = result.error || '删除车辆信息失败，请稍后再试'
    }
  } catch (error) {
    console.error('删除车辆信息失败:', error)
    globalError.value = '删除车辆信息失败，请稍后再试'
  }
}
</script>

<style scoped>
.vehicle-list-page {
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

.add-btn {
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

.vehicle-list {
  margin-bottom: 16px;
}

.vehicle-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.vehicle-icon {
  width: 48px;
  height: 48px;
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
  font-size: 20px;
  color: #3b82f6;
}

.icon-car-company::before {
  color: #10b981;
}

.vehicle-info {
  flex: 1;
}

.plate-number {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.vehicle-type {
  font-size: 14px;
  color: #666;
}

.actions {
  display: flex;
}

.edit-icon, .delete-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
}

.icon-edit::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
}

.icon-delete::before {
  content: '\f2ed';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #ef4444;
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

.add-btn-large {
  padding: 10px 20px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
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

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.modal-content {
  width: 80%;
  max-width: 320px;
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
}

.modal-content h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
}

.modal-content p {
  font-size: 14px;
  margin-bottom: 20px;
  color: #666;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
}

.cancel-btn, .confirm-btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  margin-left: 12px;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #374151;
  border: none;
}

.confirm-btn {
  background-color: #ef4444;
  color: white;
  border: none;
}
</style> 