<template>
  <div class="location-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>位置打卡</h1>
    </header>

    <!-- 地图区域 -->
    <div class="map-container">
      <div class="map-placeholder">
        <!-- 实际项目中这里会集成地图SDK -->
        <div class="map-content">
          <i class="icon-map"></i>
          <p>地图加载中...</p>
        </div>
      </div>
    </div>

    <!-- 位置信息 -->
    <div class="location-info">
      <div class="location-header">
        <h2 class="section-title">当前位置</h2>
        <button class="refresh-btn" @click="refreshLocation">
          <i class="icon-refresh"></i>刷新
        </button>
      </div>
      
      <div class="info-row">
        <i class="icon-marker"></i>
        <span>{{ locationData.address }}</span>
      </div>
      
      <div class="coordinates">
        <div class="coordinate-item">
          <span class="coordinate-label">经度</span>
          <span class="coordinate-value">{{ locationData.longitude }}</span>
        </div>
        <div class="coordinate-item">
          <span class="coordinate-label">纬度</span>
          <span class="coordinate-value">{{ locationData.latitude }}</span>
        </div>
      </div>
      
      <div class="accuracy-info">
        <i class="icon-signal"></i>
        <span>精确度: {{ locationData.accuracy }}米</span>
      </div>
    </div>

    <!-- 任务信息 -->
    <div class="task-info">
      <h2 class="section-title">任务信息</h2>
      
      <div class="task-selector">
        <select v-model="selectedTaskId" @change="onTaskChange" class="task-select">
          <option value="" disabled>请选择任务</option>
          <option v-for="task in availableTasks" :key="task.taskId" :value="task.taskId">
            {{ task.taskId }} - {{ task.customerName }}
          </option>
          <option value="other">其他</option>
        </select>
      </div>
      
      <div v-if="selectedTaskId === 'other'" class="other-task-info">
        <div class="form-group">
          <label>任务编号</label>
          <input type="text" v-model="customTaskData.taskId" placeholder="请输入任务编号" />
        </div>
        <div class="form-group">
          <label>客户名称</label>
          <input type="text" v-model="customTaskData.customerName" placeholder="请输入客户名称" />
        </div>
        <div class="form-group">
          <label>目标地址</label>
          <input type="text" v-model="customTaskData.targetAddress" placeholder="请输入目标地址" />
        </div>
        <div class="form-group">
          <label>预约时间</label>
          <input type="datetime-local" v-model="customTaskData.appointmentTime" />
        </div>
      </div>
      
      <div v-else-if="selectedTaskId">
      <div class="info-row">
        <span class="info-label">任务编号</span>
        <span class="info-value">{{ taskData.taskId }}</span>
      </div>
      
      <div class="info-row">
        <span class="info-label">客户名称</span>
        <span class="info-value">{{ taskData.customerName }}</span>
      </div>
      
      <div class="info-row">
        <span class="info-label">目标地址</span>
        <span class="info-value">{{ taskData.targetAddress }}</span>
      </div>
      
      <div class="info-row">
        <span class="info-label">预约时间</span>
        <span class="info-value">{{ taskData.appointmentTime }}</span>
      </div>
      
      <div class="distance-info" v-if="locationData.distanceToTarget">
        <i class="icon-route"></i>
        <span>距离目标地点: {{ locationData.distanceToTarget }}</span>
        </div>
      </div>
      
      <div v-else class="empty-task-prompt">
        请选择一个任务或输入其他任务信息
      </div>
    </div>

    <!-- 交通工具选择 -->
    <div class="transport-info">
      <h2 class="section-title">交通工具</h2>
      
      <div v-if="transportType === 'company' || transportType === 'private'" class="vehicle-selector">
        <select v-model="selectedVehicleId" class="vehicle-select">
          <option value="" disabled>请选择车辆</option>
          <option v-for="vehicle in filteredVehicles" :key="vehicle.id" :value="vehicle.id">
            {{ vehicle.plateNumber }} ({{ vehicle.typeName }})
          </option>
        </select>
      </div>
      
      <div class="transport-options">
        <div 
          class="transport-option" 
          :class="{ active: transportType === 'company' }"
          @click="selectTransport('company')"
        >
          <i class="icon-company-car"></i>
          <span>公车</span>
        </div>
        <div 
          class="transport-option" 
          :class="{ active: transportType === 'private' }"
          @click="selectTransport('private')"
        >
          <i class="icon-private-car"></i>
          <span>私车</span>
        </div>
        <div 
          class="transport-option" 
          :class="{ active: transportType === 'public' }"
          @click="selectTransport('public')"
        >
          <i class="icon-public-transport"></i>
          <span>公共交通</span>
        </div>
      </div>
    </div>
    
    <!-- 打卡类型选择 -->
    <div class="checkin-type-info">
      <h2 class="section-title">打卡类型</h2>
      
      <div class="checkin-type-options">
        <div 
          class="checkin-type-option" 
          :class="{ active: checkinType === 1 }"
          @click="selectCheckinType(1)"
        >
          <i class="icon-departure"></i>
          <span>出发打卡</span>
        </div>
        <div 
          class="checkin-type-option" 
          :class="{ active: checkinType === 2 }"
          @click="selectCheckinType(2)"
        >
          <i class="icon-arrive"></i>
          <span>上门打卡</span>
        </div>
        <div 
          class="checkin-type-option" 
          :class="{ active: checkinType === 3 }"
          @click="selectCheckinType(3)"
        >
          <i class="icon-return"></i>
          <span>回程打卡</span>
        </div>
      </div>
    </div>

    <!-- 打卡记录 -->
    <div class="check-in-records">
      <h2 class="section-title">打卡记录</h2>
      
      <div class="record-list">
        <div 
          v-for="(record, index) in checkInRecords" 
          :key="index"
          class="record-item"
        >
          <div class="record-icon">
            <i :class="record.icon"></i>
          </div>
          <div class="record-content">
            <div class="record-title">{{ record.title }}</div>
            <div class="record-time">{{ record.time }}</div>
            <div class="record-address">{{ record.address }}</div>
          </div>
        </div>
        
        <div v-if="checkInRecords.length === 0" class="empty-records">
          暂无打卡记录
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-actions">
      <button 
        class="check-in-btn" 
        @click="checkIn"
        :disabled="loading || !isReadyForCheckIn"
      >
        <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        <i v-else class="icon-check-in"></i>
        {{ loading ? '打卡中...' : '打卡' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useVehicleRecordStore } from '../stores/vehicleRecord'

    const router = useRouter()
const vehicleRecordStore = useVehicleRecordStore()
const loading = ref(false)
const transportType = ref('') // 交通工具类型：company-公车, private-私车, public-公共交通
const selectedTaskId = ref('')
const selectedVehicleId = ref('')
const checkinType = ref(0) // 打卡类型：1-出发打卡，2-上门打卡，3-回程打卡

const locationData = reactive({
  address: '正在获取位置...',
  longitude: '',
  latitude: '',
  accuracy: 0,
  distanceToTarget: ''
})

const taskData = reactive({
  taskId: '',
  customerName: '',
  targetAddress: '',
  appointmentTime: ''
})

const customTaskData = reactive({
  taskId: '',
  customerName: '',
  targetAddress: '',
  appointmentTime: ''
})

const checkInRecords = ref([])

// 模拟的可用任务列表
const availableTasks = ref([
  {
        taskId: 'GC-2024-001',
        customerName: '上海某研究所',
        targetAddress: '上海市浦东新区张江高科技园区科苑路88号',
        appointmentTime: '2024-03-15 14:00'
      },
  {
    taskId: 'GC-2024-002',
    customerName: '南京电子设备有限公司',
    targetAddress: '南京市江宁区科学园科创路1号',
    appointmentTime: '2024-03-16 10:30'
  },
  {
    taskId: 'GC-2024-003',
    customerName: '杭州智能科技有限公司',
    targetAddress: '杭州市滨江区网商路699号',
    appointmentTime: '2024-03-17 09:00'
  }
])

// 模拟的用户车辆列表
const userVehicles = ref([
  {
    id: '1',
    plateNumber: '沪A12345',
    type: 'company',
    typeName: '公车'
  },
  {
    id: '2',
    plateNumber: '沪B67890',
    type: 'private',
    typeName: '私车'
  },
  {
    id: '3',
    plateNumber: '苏A54321',
    type: 'company',
    typeName: '公车'
  }
])

// 根据选择的交通工具类型过滤车辆列表
const filteredVehicles = computed(() => {
  if (!transportType.value) return []
  return userVehicles.value.filter(v => v.type === transportType.value)
})

// 判断是否可以打卡
const isReadyForCheckIn = computed(() => {
  // 必须选择交通工具
  if (!transportType.value) return false
  
  // 如果选择了公车或私车，必须选择具体车辆
  if ((transportType.value === 'company' || transportType.value === 'private') && !selectedVehicleId.value) {
    return false
  }
  
  // 必须有任务信息（选择已有任务或填写自定义任务）
  if (selectedTaskId.value === '') return false
  if (selectedTaskId.value === 'other') {
    if (!customTaskData.taskId || !customTaskData.customerName || !customTaskData.targetAddress) {
      return false
    }
  }
  
  // 必须选择打卡类型
  if (!checkinType.value) return false
  
  return true
})

// 从路由参数或本地存储中获取任务信息
onMounted(() => {
  // 获取路由参数中的任务ID
  const route = useRoute();
  const taskId = route.query.taskId;
  
  if (taskId) {
    // 如果有任务ID参数，自动选择对应的任务
    const task = availableTasks.value.find(t => t.taskId === taskId);
    if (task) {
      selectedTaskId.value = task.taskId;
      onTaskChange();
    }
  }
  
  // 模拟获取位置
      setTimeout(() => {
    locationData.address = '上海市浦东新区张江高科技园区科苑路88号'
    locationData.longitude = '121.5968'
    locationData.latitude = '31.2052'
    locationData.accuracy = 15
    locationData.distanceToTarget = '50米'
      }, 1000)
  
  // 获取用户车辆信息
  fetchUserVehicles()
})

// 获取用户车辆信息
async function fetchUserVehicles() {
  // 实际项目中应从API获取用户车辆信息
  // 这里使用模拟数据
}

// 任务选择变化处理
function onTaskChange() {
  if (selectedTaskId.value === 'other') {
    // 清空自定义任务数据，准备用户输入
    Object.assign(customTaskData, {
      taskId: '',
      customerName: '',
      targetAddress: '',
      appointmentTime: ''
    })
  } else if (selectedTaskId.value) {
    // 查找并填充选择的任务数据
    const selectedTask = availableTasks.value.find(task => task.taskId === selectedTaskId.value)
    if (selectedTask) {
      Object.assign(taskData, selectedTask)
      // 更新距离信息
      locationData.distanceToTarget = '40米' // 模拟数据，实际应根据地图API计算
    }
    
    // 获取该任务的打卡记录
    fetchTaskRecords()
  }
}

// 选择交通工具
function selectTransport(type) {
  transportType.value = type
  // 如果切换交通工具类型，清空已选车辆
  if (type === 'public') {
    selectedVehicleId.value = ''
  } else {
    // 如果有对应类型的车辆，默认选择第一个
    const matchingVehicles = userVehicles.value.filter(v => v.type === type)
    if (matchingVehicles.length > 0) {
      selectedVehicleId.value = matchingVehicles[0].id
    } else {
      selectedVehicleId.value = ''
    }
  }
}

// 获取任务的打卡记录
async function fetchTaskRecords() {
  if (!taskData.taskId) return
  
  try {
    const result = await vehicleRecordStore.fetchRecordsByTaskId(taskData.taskId)
    if (result.success) {
      // 转换API返回的记录为视图所需格式
      checkInRecords.value = result.records.map(record => ({
        title: record.typeName,
        time: formatDateTime(record.checkInTime),
        address: record.location,
        icon: getIconByType(record.type)
      }))
    }
  } catch (error) {
    console.error('获取打卡记录失败:', error)
  }
}

// 格式化日期时间
function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 根据类型获取图标
function getIconByType(type) {
  switch (type) {
    case 1: return 'icon-departure'
    case 2: return 'icon-arrive'
    case 3: return 'icon-return'
    default: return 'icon-check'
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 刷新位置
function refreshLocation() {
  // 实际项目中应调用地图API刷新位置
  locationData.accuracy = Math.max(5, Math.floor(locationData.accuracy * 0.7))
  locationData.distanceToTarget = `${Math.max(10, parseInt(locationData.distanceToTarget) * 0.8)}米`
  toast('位置已更新')
}

// 选择打卡类型
function selectCheckinType(type) {
  checkinType.value = type
}

// 执行打卡
async function checkIn() {
  if (loading.value || !isReadyForCheckIn.value) return
  
  loading.value = true
  
  try {
    // 获取当前任务数据（可能是预设任务或自定义任务）
    const currentTaskData = selectedTaskId.value === 'other' ? customTaskData : taskData
    
    // 创建打卡记录
    const recordData = {
      taskId: currentTaskData.taskId,
      taskName: `${currentTaskData.customerName}任务`,
      type: checkinType.value, // 使用用户选择的打卡类型
      location: locationData.address,
      longitude: locationData.longitude,
      latitude: locationData.latitude,
      distance: checkinType.value === 1 ? 0 : parseFloat(locationData.distanceToTarget) / 1000, // 转换为公里
      transportType: transportType.value,
      vehicleId: selectedVehicleId.value || null // 如果选择了车辆，添加车辆ID
    }
    
    const result = await vehicleRecordStore.addVehicleRecord(recordData)
    
    if (result.success) {
      // 添加到本地记录
      const now = new Date()
      const timeString = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }).replace(/\//g, '-')
      
      checkInRecords.value.push({
        title: getTypeNameByCode(checkinType.value),
        time: timeString,
        address: locationData.address,
        icon: getIconByType(checkinType.value)
      })
      
      toast('打卡成功')
      
      // 如果是返程打卡，提示任务完成
      if (checkinType.value === 3) {
        setTimeout(() => {
          toast('任务已完成，即将返回')
          setTimeout(() => router.push('/home'), 1500)
        }, 1000)
      }
    } else {
      toast(`打卡失败: ${result.error}`)
    }
  } catch (error) {
    console.error('打卡失败:', error)
    toast('打卡失败，请重试')
  } finally {
    loading.value = false
  }
}

// 根据类型代码获取类型名称
function getTypeNameByCode(typeCode) {
  switch (typeCode) {
    case 1: return '出发打卡'
    case 2: return '到达打卡'
    case 3: return '返程打卡'
    default: return '其他'
  }
}

// 简单提示
function toast(message) {
  // 实际项目中应使用UI组件库的Toast
  alert(message)
}
</script>

<style scoped>
.location-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 100px; /* 增加底部padding，确保内容不被底部导航栏遮挡 */
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
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.map-container {
  height: 200px;
  background-color: #e5e7eb;
  position: relative;
}

.map-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-content {
  text-align: center;
  color: #6b7280;
}

.icon-map::before {
  content: '\f279';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 48px;
  margin-bottom: 8px;
  display: block;
}

.location-info, .task-info, .check-in-records {
  background-color: #fff;
  margin: 12px 16px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.location-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.location-header .section-title {
  margin-bottom: 0;
}

.refresh-btn {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #3b82f6;
  background: none;
  border: none;
}

.icon-refresh::before {
  content: '\f2f1';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.icon-marker::before {
  content: '\f3c5';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
  margin-right: 8px;
  flex-shrink: 0;
}

.coordinates {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.coordinate-item {
  flex: 1;
}

.coordinate-label {
  font-size: 14px;
  color: #6b7280;
  display: block;
  margin-bottom: 4px;
}

.coordinate-value {
  font-weight: 500;
}

.accuracy-info {
  display: flex;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
}

.icon-signal::before {
  content: '\f012';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.info-label {
  width: 80px;
  color: #6b7280;
  font-size: 14px;
}

.info-value {
  flex: 1;
  font-weight: 500;
}

.distance-info {
  display: flex;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
  color: #3b82f6;
  font-weight: 500;
}

.icon-route::before {
  content: '\f4d7';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-item {
  display: flex;
  align-items: flex-start;
}

.record-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #dbeafe;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.icon-arrive::before {
  content: '\f5a0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
}

.icon-complete::before {
  content: '\f00c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #10b981;
}

.record-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.record-time {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.record-address {
  font-size: 14px;
  color: #6b7280;
}

.empty-records {
  text-align: center;
  padding: 24px 0;
  color: #9ca3af;
}

.transport-info {
  background-color: #fff;
  margin: 12px 16px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.transport-options {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.transport-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.transport-option.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.transport-option i {
  font-size: 24px;
  margin-bottom: 8px;
  color: #6b7280;
}

.transport-option.active i {
  color: #3b82f6;
}

.icon-company-car::before {
  content: '\f5e4';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-private-car::before {
  content: '\f1b9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-public-transport::before {
  content: '\f207';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.bottom-actions {
  position: fixed;
  bottom: 50px; /* 上移底部按钮，避免被导航栏遮挡 */
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  z-index: 100; /* 确保在最上层 */
}

.check-in-btn {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-check-in::before {
  content: '\f274';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

/* 新增样式 */
.task-selector {
  margin-bottom: 16px;
}

.task-select, .vehicle-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background-color: #fff;
  font-size: 14px;
}

.other-task-info {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
  margin-top: 16px;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
}

.empty-task-prompt {
  color: #9ca3af;
  text-align: center;
  padding: 16px 0;
}

.vehicle-selector {
  margin-bottom: 16px;
}

.checkin-type-info {
  background-color: #fff;
  margin: 12px 16px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.checkin-type-options {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.checkin-type-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.checkin-type-option.active {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.checkin-type-option i {
  font-size: 24px;
  margin-bottom: 8px;
  color: #6b7280;
}

.checkin-type-option.active i {
  color: #3b82f6;
}

.icon-departure::before {
  content: '\f55e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-return::before {
  content: '\f064';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}
</style> 