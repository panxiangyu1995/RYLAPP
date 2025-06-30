<template>
  <div class="vehicle-edit-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>{{ isAddMode ? '添加车辆' : '编辑车辆' }}</h1>
      <div class="header-right">
        <button class="save-btn" @click="saveVehicle" :disabled="loading">
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </div>
    </header>

    <!-- 主要内容 -->
    <div class="content">
      <!-- 车辆信息卡片 -->
      <div class="info-card">
        <div class="info-section">
          <div class="form-group">
            <label>车牌号</label>
            <input type="text" v-model="vehicleInfo.plateNumber" class="form-input" placeholder="请输入车牌号，如：苏M12345">
            <div class="error-message" v-if="errors.plateNumber">{{ errors.plateNumber }}</div>
          </div>
          
          <div class="form-group">
            <label>车辆类型</label>
            <select v-model="vehicleInfo.vehicleType" class="form-select">
              <option v-for="(type, index) in vehicleTypes" :key="index" :value="type">
                {{ type }}
              </option>
            </select>
          </div>
        </div>
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
import { useUserStore } from '../../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 判断是添加模式还是编辑模式
const isAddMode = computed(() => {
  return route.query.mode === 'add' || !route.query.id
})

const vehicleId = computed(() => {
  return route.query.id
})

// 车辆信息
const vehicleInfo = reactive({
  vehicleId: '',
  plateNumber: '',
  vehicleType: '私车'
})

// 表单错误
const errors = reactive({
  plateNumber: ''
})

// 全局错误
const globalError = ref('')
const loading = ref(false)

// 车辆类型列表
const vehicleTypes = ['私车', '公车']

// 页面加载时获取车辆信息
onMounted(async () => {
  loading.value = true
  try {
    if (!isAddMode.value && vehicleId.value) {
      // 编辑模式，获取车辆信息
      const result = await userStore.fetchVehicleById(vehicleId.value)
      if (result.success) {
        const vehicle = result.vehicle
        vehicleInfo.vehicleId = vehicle.vehicleId
        vehicleInfo.plateNumber = vehicle.plateNumber
        vehicleInfo.vehicleType = vehicle.vehicleType
      } else {
        globalError.value = result.error || '获取车辆信息失败'
      }
    }
    // 如果是添加模式，使用默认值
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

// 验证表单
function validateForm() {
  let isValid = true
  
  // 重置错误
  errors.plateNumber = ''
  globalError.value = ''
  
  // 验证车牌号
  if (!vehicleInfo.plateNumber.trim()) {
    errors.plateNumber = '请输入车牌号'
    isValid = false
  } else if (vehicleInfo.plateNumber.length < 5) {
    errors.plateNumber = '车牌号格式有误，请重新输入'
    isValid = false
  }
  
  return isValid
}

// 保存车辆信息
async function saveVehicle() {
  if (!validateForm()) return
  
  loading.value = true
  globalError.value = ''
  
  try {
    let result
    
    if (isAddMode.value) {
      // 创建新车辆
      result = await userStore.createVehicle({
        plateNumber: vehicleInfo.plateNumber,
        vehicleType: vehicleInfo.vehicleType
      })
      
      if (result.success) {
        alert('车辆信息添加成功')
        // 跳转到车辆列表页
        router.push('/profile/vehicle/list')
      } else {
        globalError.value = result.error || '创建车辆信息失败，请稍后再试'
      }
    } else {
      // 更新现有车辆
      result = await userStore.updateVehicle({
        vehicleId: vehicleInfo.vehicleId,
        plateNumber: vehicleInfo.plateNumber,
        vehicleType: vehicleInfo.vehicleType
      })
      
      if (result.success) {
        alert('车辆信息更新成功')
        // 跳回到详情页
        router.push(`/profile/vehicle/info/${vehicleInfo.vehicleId}`)
      } else {
        globalError.value = result.error || '更新车辆信息失败，请稍后再试'
      }
    }
  } catch (error) {
    console.error('保存车辆信息失败:', error)
    globalError.value = '保存车辆信息失败，请稍后再试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.vehicle-edit-page {
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

.save-btn {
  color: #3b82f6;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
}

.save-btn:disabled {
  color: #93c5fd;
}

.content {
  padding: 16px;
}

.info-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.form-input, .form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
}

.form-select {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
}

.error-message {
  color: #ef4444;
  font-size: 12px;
  margin-top: 4px;
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