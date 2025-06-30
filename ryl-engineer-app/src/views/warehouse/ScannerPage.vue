<template>
  <div class="scanner-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>扫码查询</h1>
      <div class="header-right">
        <i class="icon-lightbulb" @click="toggleFlash"></i>
      </div>
    </header>

    <!-- 扫码区域 -->
    <div class="scanner-container">
      <div class="scanner-frame">
        <div class="scanner-line"></div>
        <div class="scanner-corners">
          <div class="corner top-left"></div>
          <div class="corner top-right"></div>
          <div class="corner bottom-left"></div>
          <div class="corner bottom-right"></div>
        </div>
      </div>
      <p class="scanner-tip">将二维码/条形码放入框内，即可自动扫描</p>
    </div>

    <!-- 手动输入区域 -->
    <div class="manual-input">
      <div class="divider">
        <span>或</span>
      </div>
      <div class="input-container">
        <input 
          type="text" 
          v-model="manualCode" 
          placeholder="手动输入物品编码"
          class="code-input"
        >
        <button class="search-btn" @click="searchByCode">
          <i class="icon-search"></i>
        </button>
      </div>
    </div>

    <!-- 最近扫描记录 -->
    <div class="recent-scans">
      <h2 class="section-title">最近扫描</h2>
      <div v-if="recentScans.length > 0" class="scan-list">
        <div 
          v-for="(scan, index) in recentScans" 
          :key="index"
          class="scan-item"
          @click="viewScanResult(scan)"
        >
          <div class="scan-icon">
            <i class="icon-barcode"></i>
          </div>
          <div class="scan-info">
            <h3 class="scan-title">{{ scan.name }}</h3>
            <p class="scan-code">{{ scan.code }}</p>
          </div>
          <div class="scan-time">{{ scan.time }}</div>
        </div>
      </div>
      <div v-else class="empty-scans">
        <i class="icon-history"></i>
        <p>暂无扫描记录</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'ScannerPage',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const warehouseId = ref(route.query.warehouseId)
    const manualCode = ref('')
    const flashOn = ref(false)
    
    // 模拟最近扫描记录
    const recentScans = ref([
      {
        id: 1,
        code: 'HP-5MS-12345',
        name: '气相色谱柱',
        time: '10分钟前'
      },
      {
        id: 2,
        code: 'LC-1260-67890',
        name: '液相色谱泵',
        time: '2小时前'
      }
    ])
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    // 切换闪光灯
    const toggleFlash = () => {
      flashOn.value = !flashOn.value
      console.log('闪光灯状态:', flashOn.value ? '开启' : '关闭')
      // 实际项目中应调用摄像头API控制闪光灯
    }
    
    // 手动搜索物品
    const searchByCode = () => {
      if (!manualCode.value) {
        alert('请输入物品编码')
        return
      }
      
      console.log('搜索物品编码:', manualCode.value)
      // 模拟搜索结果
      const mockResult = {
        id: 100,
        code: manualCode.value,
        name: '手动输入物品',
        time: '刚刚'
      }
      
      // 添加到最近扫描记录
      recentScans.value.unshift(mockResult)
      
      // 跳转到物品详情页
      router.push(`/warehouse/${warehouseId.value}/item/100`)
    }
    
    // 查看扫描结果
    const viewScanResult = (scan) => {
      console.log('查看扫描结果:', scan)
      router.push(`/warehouse/${warehouseId.value}/item/${scan.id}`)
    }
    
    // 初始化扫码功能
    const initScanner = () => {
      console.log('初始化扫码功能')
      // 实际项目中应初始化摄像头和二维码扫描库
      // 例如使用quagga.js、jsQR或html5-qrcode等库
      
      // 模拟扫码成功
      setTimeout(() => {
        const mockScanResult = {
          id: 3,
          code: 'MS-5977-24680',
          name: '质谱检测器',
          time: '刚刚'
        }
        
        // 添加到最近扫描记录
        recentScans.value.unshift(mockScanResult)
        
        // 提示扫码成功
        alert(`扫码成功: ${mockScanResult.name}`)
        
        // 跳转到物品详情页
        router.push(`/warehouse/${warehouseId.value}/item/3`)
      }, 5000)
    }
    
    // 清理扫码资源
    const cleanupScanner = () => {
      console.log('清理扫码资源')
      // 实际项目中应释放摄像头资源
    }
    
    onMounted(() => {
      initScanner()
    })
    
    onUnmounted(() => {
      cleanupScanner()
    })
    
    return {
      warehouseId,
      manualCode,
      recentScans,
      goBack,
      toggleFlash,
      searchByCode,
      viewScanResult
    }
  }
}
</script>

<style scoped>
.scanner-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 16px;
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

.icon-lightbulb::before {
  content: '\f0eb';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.scanner-container {
  position: relative;
  height: 300px;
  background-color: #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.scanner-frame {
  position: relative;
  width: 240px;
  height: 240px;
  border-radius: 8px;
  overflow: hidden;
}

.scanner-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #3b82f6;
  box-shadow: 0 0 8px #3b82f6;
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% { top: 0; }
  50% { top: 100%; }
  50.1% { top: 0; }
  100% { top: 100%; }
}

.scanner-corners {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.corner {
  position: absolute;
  width: 20px;
  height: 20px;
  border-color: #fff;
  border-style: solid;
  border-width: 0;
}

.top-left {
  top: 0;
  left: 0;
  border-top-width: 2px;
  border-left-width: 2px;
}

.top-right {
  top: 0;
  right: 0;
  border-top-width: 2px;
  border-right-width: 2px;
}

.bottom-left {
  bottom: 0;
  left: 0;
  border-bottom-width: 2px;
  border-left-width: 2px;
}

.bottom-right {
  bottom: 0;
  right: 0;
  border-bottom-width: 2px;
  border-right-width: 2px;
}

.scanner-tip {
  margin-top: 16px;
  color: #fff;
  font-size: 14px;
  text-align: center;
}

.manual-input {
  padding: 16px;
  background-color: #fff;
  margin-top: 16px;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 16px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #e5e7eb;
}

.divider::before {
  margin-right: 8px;
}

.divider::after {
  margin-left: 8px;
}

.input-container {
  display: flex;
  align-items: center;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.code-input {
  flex: 1;
  border: none;
  padding: 12px 16px;
  font-size: 14px;
  outline: none;
}

.search-btn {
  background-color: #3b82f6;
  color: #fff;
  border: none;
  padding: 12px 16px;
  cursor: pointer;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.recent-scans {
  padding: 16px;
  background-color: #fff;
  margin-top: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.scan-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.scan-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;
  cursor: pointer;
}

.scan-icon {
  width: 40px;
  height: 40px;
  background-color: #e0f2fe;
  color: #0369a1;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.icon-barcode::before {
  content: '\f02a';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 18px;
}

.scan-info {
  flex: 1;
}

.scan-title {
  font-size: 14px;
  font-weight: 500;
  margin: 0;
}

.scan-code {
  font-size: 12px;
  color: #6b7280;
  margin: 4px 0 0 0;
}

.scan-time {
  font-size: 12px;
  color: #9ca3af;
}

.empty-scans {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 0;
  color: #9ca3af;
}

.icon-history::before {
  content: '\f1da';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 32px;
  margin-bottom: 8px;
}
</style> 