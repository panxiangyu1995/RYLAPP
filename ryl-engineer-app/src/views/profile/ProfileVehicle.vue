<template>
  <div class="vehicle-record-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>上门定位打卡记录</h1>
      <div class="header-right">
        <i class="icon-filter"></i>
      </div>
    </header>

    <!-- 打卡记录统计 -->
    <div class="stats-card">
      <div class="stats-header">
        <h3>本月打卡统计</h3>
        <span>{{ currentMonth }}</span>
      </div>
      <div class="stats-grid">
        <div class="stat-item">
          <p class="stat-value">{{ stats.checkInCount }}</p>
          <p class="stat-label">打卡次数</p>
        </div>
        <div class="stat-item">
          <p class="stat-value">{{ stats.taskCount }}</p>
          <p class="stat-label">任务数</p>
        </div>
        <div class="stat-item">
          <p class="stat-value">{{ stats.totalDistance }}</p>
          <p class="stat-label">总里程(公里)</p>
        </div>
      </div>
    </div>

    <!-- 打卡记录列表 -->
    <div class="record-list">
      <div class="list-header">
        <h3>打卡记录</h3>
      </div>

      <div v-for="(group, index) in recordGroups" :key="index">
        <!-- 月份分隔符 -->
        <div v-if="group.monthHeader" class="month-divider">
          <h4>{{ group.monthHeader }}</h4>
        </div>

        <!-- 任务记录 -->
        <div v-for="record in group.records" :key="record.id" class="record-card">
          <div class="record-header">
            <div>
              <h4>任务：{{ record.taskName }}</h4>
              <p>任务编号：{{ record.taskId }}</p>
            </div>
            <span :class="['status-badge', record.status === '已完成' ? 'status-completed' : 'status-progress']">
              {{ record.status }}
            </span>
          </div>
          
          <!-- 打卡详情 -->
          <div v-for="(checkIn, checkInIndex) in record.checkIns" :key="checkInIndex" class="check-in-item">
            <div class="check-in-header">
              <div class="check-in-type">
                <i :class="getCheckInIcon(checkIn.type)"></i>
                <span>{{ checkIn.type }}</span>
              </div>
              <span class="check-in-time">{{ checkIn.time }}</span>
            </div>
            <div class="check-in-content">
              <p>{{ checkIn.location }}</p>
              <div class="check-in-photos" v-if="checkIn.photos">
                <i class="icon-image"></i>
                <span>{{ checkIn.photos }}</span>
              </div>
            </div>
          </div>
          
          <!-- 任务总结 -->
          <div class="record-summary">
            <div class="summary-text">
              <span>总里程：{{ record.totalDistance }}公里</span>
            </div>
            <button class="detail-btn" @click="viewDetail(record.id)">
              <i class="icon-eye"></i>详情
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-actions">
      <button class="add-btn" @click="addNewRecord">
        <i class="icon-plus"></i>新建打卡记录
      </button>
    </div>

    <!-- 新建打卡记录模态框 -->
    <div v-if="isModalVisible" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>新建打卡记录</h3>
          <button class="close-btn" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <label for="taskId">任务ID</label>
              <input type="text" id="taskId" v-model="newRecordForm.taskId" required>
            </div>
             <div class="form-group">
              <label for="taskName">任务名称</label>
              <input type="text" id="taskName" v-model="newRecordForm.taskName" required>
            </div>
            <div class="form-group">
              <label>打卡类型</label>
              <div class="radio-group">
                <label><input type="radio" v-model="newRecordForm.type" :value="1"> 出发</label>
                <label><input type="radio" v-model="newRecordForm.type" :value="2"> 到达</label>
                <label><input type="radio" v-model="newRecordForm.type" :value="3"> 返程</label>
              </div>
            </div>
            <div class="form-group">
              <label>交通方式</label>
              <div class="radio-group">
                <label><input type="radio" v-model="newRecordForm.transportType" value="company"> 公车</label>
                <label><input type="radio" v-model="newRecordForm.transportType" value="private"> 私车</label>
                <label><input type="radio" v-model="newRecordForm.transportType" value="public"> 公共交通</label>
              </div>
            </div>
            <div class="form-group">
              <label>定位</label>
              <div class="location-actions">
                <button type="button" @click="getGaodeLocation">获取高德定位</button>
                <button type="button" @click="getBaiduLocation">获取百度定位</button>
              </div>
              <p v-if="newRecordForm.location" class="location-result">
                {{ newRecordForm.location }}
              </p>
            </div>
            <div class="form-group">
              <label for="remark">备注</label>
              <textarea id="remark" v-model="newRecordForm.remark"></textarea>
            </div>
            <div class="modal-footer">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="!newRecordForm.location">确认打卡</button>
            </div>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useVehicleRecordStore } from '../../stores/vehicleRecord'
import { useToast } from 'vue-toastification'

const router = useRouter()
const vehicleRecordStore = useVehicleRecordStore()
const loading = ref(false)
const toast = useToast()

const isModalVisible = ref(false)
const newRecordForm = reactive({
  taskId: '',
  taskName: '',
  type: 1,
  location: '',
  longitude: '',
  latitude: '',
  photos: '',
  distance: 0,
  transportType: 'company',
  remark: ''
})

const currentMonth = ref('')
const stats = reactive({
  checkInCount: 0,
  taskCount: 0,
  totalDistance: 0
})

const recordGroups = ref([])

// 页面加载时获取数据
onMounted(async () => {
  loading.value = true
  try {
    // 获取当月统计数据
    await fetchCurrentMonthStats()
    
    // 获取打卡记录
    await fetchRecords()
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
})

// 获取当月统计数据
async function fetchCurrentMonthStats() {
  try {
    const result = await vehicleRecordStore.fetchCurrentMonthStats()
    if (result.success) {
      const statsData = result.stats
      currentMonth.value = statsData.month
      stats.checkInCount = statsData.checkInCount
      stats.taskCount = statsData.taskCount
      stats.totalDistance = Math.round(statsData.totalDistance)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取打卡记录
async function fetchRecords() {
  try {
    const result = await vehicleRecordStore.fetchMyRecords()
    if (result.success) {
      // 处理记录，按月份分组
      processRecords(result.records)
    }
  } catch (error) {
    console.error('获取打卡记录失败:', error)
  }
}

// 处理记录，按月份分组
function processRecords(records) {
  if (!records || records.length === 0) {
    recordGroups.value = []
    return
  }
  
  // 按任务ID分组
  const taskGroups = {}
  
  records.forEach(record => {
    if (!taskGroups[record.taskId]) {
      taskGroups[record.taskId] = {
        id: record.taskId,
        taskName: record.taskName,
        checkIns: [],
        totalDistance: 0,
        status: '进行中',
        date: new Date(record.checkInTime)
      }
    }
    
    // 添加打卡记录
    taskGroups[record.taskId].checkIns.push({
      type: record.typeName,
      time: formatDateTime(record.checkInTime),
      location: record.location,
      photos: record.photoCount > 0 ? `${record.photoCount}张照片` : null
    })
    
    // 累加里程
    if (record.distance) {
      taskGroups[record.taskId].totalDistance += record.distance
    }
    
    // 如果有返程打卡，标记为已完成
    if (record.type === 3) {
      taskGroups[record.taskId].status = '已完成'
    }
  })
  
  // 将对象转为数组
  const taskList = Object.values(taskGroups)
  
  // 按月份分组
  const monthGroups = {}
  
  taskList.forEach(task => {
    const monthKey = formatMonth(task.date)
    
    if (!monthGroups[monthKey]) {
      monthGroups[monthKey] = {
        monthHeader: monthKey,
        records: []
      }
    }
    
    monthGroups[monthKey].records.push({
      id: task.id,
      taskName: task.taskName,
      taskId: task.id,
      status: task.status,
      totalDistance: Math.round(task.totalDistance),
      checkIns: task.checkIns
    })
  })
  
  // 将对象转为数组并按月份排序
  recordGroups.value = Object.values(monthGroups).sort((a, b) => {
    return new Date(b.monthHeader.replace('年', '-').replace('月', '-01')) - 
           new Date(a.monthHeader.replace('年', '-').replace('月', '-01'))
  })
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

// 格式化月份
function formatMonth(date) {
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
}

// 根据打卡类型获取图标
function getCheckInIcon(type) {
  switch (type) {
    case '出发打卡': return 'icon-departure'
    case '到达打卡': return 'icon-arrival'
    case '返程打卡': return 'icon-return'
    default: return 'icon-check'
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 查看详情
function viewDetail(recordId) {
  console.log('查看详情:', recordId)
  // 后续实现查看详情功能
  // router.push(`/profile/vehicle/detail/${recordId}`)
}

function addNewRecord() {
  isModalVisible.value = true
}

function closeModal() {
  isModalVisible.value = false
  // 重置表单
  Object.assign(newRecordForm, {
    taskId: '',
    taskName: '',
    type: 1,
    location: '',
    longitude: '',
    latitude: '',
    photos: '',
    distance: 0,
    transportType: 'company',
    remark: ''
  })
}

async function handleSubmit() {
  if (!newRecordForm.taskId || !newRecordForm.taskName) {
    toast.error('任务ID和任务名称不能为空')
    return
  }
  loading.value = true
  try {
    const result = await vehicleRecordStore.addVehicleRecord(newRecordForm)
    if (result.success) {
      toast.success('打卡成功！')
      closeModal()
      await fetchRecords()
      await fetchCurrentMonthStats()
    } else {
      toast.error(result.error || '打卡失败')
    }
  } catch (error) {
    toast.error('打卡时发生错误')
  } finally {
    loading.value = false
  }
}

// --- 定位功能 ---

// 请在此处替换为您申请的高德地图JS API Key
const GAODE_MAP_KEY = "YOUR_GAODE_MAP_KEY_SHOULD_BE_HERE";
// 请在此处替换为您申请的百度地图JS API Key
const BAIDU_MAP_KEY = "YOUR_BAIDU_MAP_KEY_SHOULD_BE_HERE";

const loadScript = (url) => {
  return new Promise((resolve, reject) => {
    // 避免重复加载
    if (document.querySelector(`script[src="${url}"]`)) {
      resolve();
      return;
    }
    const script = document.createElement('script');
    script.src = url;
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  });
};

const getGaodeLocation = async () => {
  newRecordForm.location = '正在使用高德地图获取位置...';
  try {
    if (!window.AMap) {
      await loadScript(`https://webapi.amap.com/maps?v=2.0&key=${GAODE_MAP_KEY}`);
    }

    await new Promise((resolve, reject) => {
        window.AMap.plugin(['AMap.Geolocation', 'AMap.Geocoder'], function() {
            const geolocation = new window.AMap.Geolocation({
                enableHighAccuracy: true,
                timeout: 10000,
            });

            geolocation.getCurrentPosition((status, result) => {
                if (status === 'complete') {
                    const geocoder = new window.AMap.Geocoder();
                    const lnglat = [result.position.lng, result.position.lat];
                    geocoder.getAddress(lnglat, (status, geoResult) => {
                        if (status === 'complete' && geoResult.info === 'OK') {
                            newRecordForm.longitude = result.position.lng;
                            newRecordForm.latitude = result.position.lat;
                            newRecordForm.location = geoResult.regeocode.formattedAddress;
                            toast.success('高德定位成功！');
                        } else {
                            toast.error('高德地图逆地理编码失败');
                            newRecordForm.location = '高德地图逆地理编码失败';
                        }
                        resolve();
                    });
                } else {
                    const errorMsg = `高德地图定位失败: ${result.message}`;
                    toast.error(errorMsg);
                    newRecordForm.location = errorMsg;
                    reject(new Error(result.message));
                }
            });
        });
    });

  } catch (err) {
    const errorMsg = '加载高德地图SDK失败，请检查网络或API Key。';
    toast.error(errorMsg);
    newRecordForm.location = errorMsg;
    console.error(err);
  }
};

let resolveBaiduMapLoading;
const baiduPromise = new Promise(resolve => {
    resolveBaiduMapLoading = resolve;
});

const getBaiduLocation = async () => {
  newRecordForm.location = '正在使用百度地图获取位置...';
  try {
    if (!window.BMap) {
      window.onBaiduMapLoaded = () => {
        resolveBaiduMapLoading();
      };
      await loadScript(`https://api.map.baidu.com/api?v=2.0&ak=${BAIDU_MAP_KEY}&callback=onBaiduMapLoaded`);
      await baiduPromise;
    }
    
    const geolocation = new window.BMap.Geolocation();
    geolocation.getCurrentPosition((r) => {
        if (geolocation.getStatus() == window.BMAP_STATUS_SUCCESS) {
            const geocoder = new window.BMap.Geocoder();
            geocoder.getLocation(r.point, (rs) => {
                newRecordForm.longitude = r.point.lng;
                newRecordForm.latitude = r.point.lat;
                newRecordForm.location = rs.address;
                toast.success('百度定位成功！');
            });
        } else {
            let errorMsg = '百度地图定位失败';
            switch(geolocation.getStatus()){
                case window.BMAP_STATUS_PERMISSION_DENIED:
                    errorMsg += ": 用户拒绝授权";
                    break;
                case window.BMAP_STATUS_SERVICE_UNAVAILABLE:
                    errorMsg += ": 定位服务不可用";
                    break;
                case window.BMAP_STATUS_TIMEOUT:
                    errorMsg += ": 定位超时";
                    break;
            }
            toast.error(errorMsg);
            newRecordForm.location = errorMsg;
        }
    });
  } catch (err) {
      const errorMsg = '加载百度地图SDK失败，请检查网络或API Key。';
      toast.error(errorMsg);
      newRecordForm.location = errorMsg;
      console.error(err);
  }
};


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

// 格式化月份
function formatMonth(date) {
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
}

// 根据打卡类型获取图标
function getCheckInIcon(type) {
  switch (type) {
    case '出发打卡': return 'icon-departure'
    case '到达打卡': return 'icon-arrival'
    case '返程打卡': return 'icon-return'
    default: return 'icon-check'
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 查看详情
function viewDetail(recordId) {
  console.log('查看详情:', recordId)
  // 后续实现查看详情功能
  // router.push(`/profile/vehicle/detail/${recordId}`)
}

function addNewRecord() {
  isModalVisible.value = true
}

function closeModal() {
  isModalVisible.value = false
  // 重置表单
  Object.assign(newRecordForm, {
    taskId: '',
    taskName: '',
    type: 1,
    location: '',
    longitude: '',
    latitude: '',
    photos: '',
    distance: 0,
    transportType: 'company',
    remark: ''
  })
}

async function handleSubmit() {
  if (!newRecordForm.taskId || !newRecordForm.taskName) {
    toast.error('任务ID和任务名称不能为空')
    return
  }
  loading.value = true
  try {
    const result = await vehicleRecordStore.addVehicleRecord(newRecordForm)
    if (result.success) {
      toast.success('打卡成功！')
      closeModal()
      await fetchRecords()
      await fetchCurrentMonthStats()
    } else {
      toast.error(result.error || '打卡失败')
    }
  } catch (error) {
    toast.error('打卡时发生错误')
  } finally {
    loading.value = false
  }
}

// 获取高德定位
async function getGaodeLocation() {
  try {
    const result = await vehicleRecordStore.getGaodeLocation()
    if (result.success) {
      newRecordForm.location = result.location
      newRecordForm.longitude = result.longitude
      newRecordForm.latitude = result.latitude
      toast.success('高德定位成功！')
    } else {
      toast.error(result.error || '高德定位失败')
    }
  } catch (error) {
    toast.error('高德定位时发生错误')
  }
}

// 获取百度定位
async function getBaiduLocation() {
  try {
    const result = await vehicleRecordStore.getBaiduLocation()
    if (result.success) {
      newRecordForm.location = result.location
      newRecordForm.longitude = result.longitude
      newRecordForm.latitude = result.latitude
      toast.success('百度定位成功！')
    } else {
      toast.error(result.error || '百度定位失败')
    }
  } catch (error) {
    toast.error('百度定位时发生错误')
  }
}
</script>

<style scoped>
.vehicle-record-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
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

.icon-filter::before {
  content: '\f0b0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.stats-card {
  background-color: #fff;
  padding: 16px;
  margin-bottom: 16px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stats-header h3 {
  font-size: 16px;
  font-weight: 500;
}

.stats-header span {
  font-size: 14px;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #3b82f6;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.record-list {
  background-color: #fff;
}

.list-header {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.list-header h3 {
  font-size: 18px;
  font-weight: 500;
}

.month-divider {
  padding: 16px;
  background-color: #f5f5f5;
}

.month-divider h4 {
  font-size: 14px;
  color: #666;
  font-weight: 400;
}

.record-card {
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.record-header h4 {
  font-size: 16px;
  font-weight: 500;
}

.record-header p {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.status-badge {
  padding: 2px 8px;
  border-radius: 9999px;
  font-size: 12px;
}

.status-completed {
  background-color: #d1fae5;
  color: #059669;
}

.status-progress {
  background-color: #dbeafe;
  color: #2563eb;
}

.check-in-item {
  margin-top: 12px;
  background-color: #f9fafb;
  padding: 12px;
  border-radius: 8px;
}

.check-in-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.check-in-type {
  display: flex;
  align-items: center;
}

.check-in-type i {
  margin-right: 8px;
}

.check-in-type span {
  font-size: 14px;
  font-weight: 500;
}

.check-in-time {
  font-size: 12px;
  color: #666;
}

.icon-departure::before {
  content: '\f064';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
}

.icon-arrival::before {
  content: '\f3c5';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #ef4444;
}

.icon-return::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #10b981;
}

.check-in-content {
  margin-top: 8px;
  font-size: 14px;
  color: #4b5563;
}

.check-in-photos {
  display: flex;
  align-items: center;
  margin-top: 4px;
}

.icon-image::before {
  content: '\f03e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 400;
  color: #9ca3af;
  margin-right: 4px;
}

.check-in-photos span {
  font-size: 14px;
  color: #6b7280;
}

.record-summary {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background-color: #eff6ff;
  border-radius: 8px;
}

.summary-text {
  font-size: 14px;
  font-weight: 500;
  color: #2563eb;
}

.detail-btn {
  display: flex;
  align-items: center;
  color: #3b82f6;
  font-size: 14px;
  background: none;
  border: none;
}

.icon-eye::before {
  content: '\f06e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.bottom-actions {
  position: fixed;
  bottom: 10px;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 16px;
  padding-bottom: calc(16px , 50px); /* 增加更多底部安全区域，确保不被导航栏遮挡 */
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
}

.add-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  background-color: #3b82f6;
  border: none;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 40px; /* 增加底部边距，确保按钮不被遮挡 */
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.modal-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  line-height: 1;
  color: #9ca3af;
  cursor: pointer;
  padding: 0;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex-grow: 1;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-group input[type="text"],
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.875rem;
  color: #111827;
  box-sizing: border-box;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input[type="text"]:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 8px;
}

.radio-group label {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 0.875rem;
  color: #374151;
}

.radio-group input[type="radio"] {
  margin-right: 6px;
  accent-color: #3b82f6;
}

.location-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.location-actions button {
  flex: 1;
  padding: 10px 12px;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}
.location-actions button:first-child {
  background-color: #3b82f6;
}
.location-actions button:first-child:hover {
  background-color: #2563eb;
}
.location-actions button:last-child {
  background-color: #10b981;
}
.location-actions button:last-child:hover {
  background-color: #059669;
}

.location-result {
  margin-top: 12px;
  padding: 10px;
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  font-size: 0.875rem;
  color: #166534;
  word-break: break-all;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.cancel-btn, .submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.cancel-btn {
  background-color: #ffffff;
  color: #374151;
  border: 1px solid #d1d5db;
}

.cancel-btn:hover {
  background-color: #f9fafb;
}

.submit-btn {
  background-color: #3b82f6;
  color: #ffffff;
}

.submit-btn:hover {
  background-color: #2563eb;
}

.submit-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}
</style> 