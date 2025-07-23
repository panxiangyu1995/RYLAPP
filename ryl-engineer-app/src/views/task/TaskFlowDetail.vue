<template>
  <div class="task-flow-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>任务流程详情</h1>
      <div class="header-right">
        <button class="share-btn">
          <i class="icon-share"></i>分享
        </button>
      </div>
    </header>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-else-if="error" class="error-container">
      <i class="icon-error"></i>
      <p>{{ error }}</p>
      <div class="error-actions">
        <button class="retry-btn" @click="loadTaskFlow">重试</button>
        <button class="back-btn" @click="goBack">返回任务</button>
      </div>
    </div>
    
    <!-- 任务基本信息 -->
    <div v-else class="task-info">
      <div class="task-header">
        <div>
          <h2 class="task-title">{{ task.title }}</h2>
          <p class="task-id">任务编号: {{ task.taskId }}</p>
          <div class="task-meta">
            <span class="task-type">{{ taskTypeNames[task.taskType] }}</span>
            <span class="task-assignee">负责人: {{ task.assignee }}</span>
            <span :class="['task-status', getStatusClass(task.status)]">{{ task.status }}</span>
          </div>
        </div>
        <div class="task-time">
          <p class="time-label">开始时间</p>
          <p class="time-value">{{ task.startTime }}</p>
        </div>
      </div>

      <!-- 设备信息 -->
      <div class="device-info" v-if="task.deviceInfo">
        <h3 class="section-subtitle">设备信息</h3>
        <div class="device-details">
          <div class="device-item">
            <span class="device-label">名称:</span>
            <span class="device-value">{{ task.deviceInfo.name }}</span>
          </div>
          <div class="device-item">
            <span class="device-label">型号:</span>
            <span class="device-value">{{ task.deviceInfo.model }}</span>
          </div>
          <div class="device-item">
            <span class="device-label">品牌:</span>
            <span class="device-value">{{ task.deviceInfo.brand }}</span>
          </div>
          <div class="device-item">
            <span class="device-label">序列号:</span>
            <span class="device-value">{{ task.deviceInfo.serialNumber }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 任务流程时间线 -->
    <div class="flow-timeline">
      <h3 class="section-title">{{ taskTypeNames[task.taskType] }}流程</h3>
      
      <!-- 已完成步骤 -->
      <div 
        v-for="(step, index) in completedSteps" 
        :key="index"
        class="flow-step completed"
      >
        <div class="step-line"></div>
        <div class="step-content">
          <div class="step-icon">
            <i class="icon-check"></i>
          </div>
          <div class="step-details">
            <div class="step-header">
              <h4 class="step-title">{{ step.title }}</h4>
              <span class="step-time">{{ step.time }}</span>
            </div>
            <div class="step-card">
              <p class="step-description">{{ step.description }}</p>
              <div class="step-images" v-if="step.images && step.images.length > 0">
                <img 
                  v-for="(image, i) in step.images" 
                  :key="i"
                  :src="image"
                  alt="步骤图片"
                  class="step-image"
                  @click="previewImage(image)"
                >
              </div>
              <div class="step-meta">
                <span class="step-date">{{ step.date }}</span>
                <span class="step-duration">用时: {{ step.duration }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 当前步骤 -->
      <div class="flow-step current">
        <div class="step-content">
          <div class="step-icon">
            <i class="icon-pen"></i>
          </div>
          <div class="step-details">
            <div class="step-header">
              <h4 class="step-title">{{ currentStep.title }}</h4>
              <span class="step-time">{{ currentStep.time }}</span>
            </div>
            <div class="step-card current">
              <p class="step-description">{{ currentStep.description }}</p>
              <div class="step-images" v-if="currentStep.images && currentStep.images.length > 0">
                <img 
                  v-for="(image, i) in currentStep.images" 
                  :key="i"
                  :src="image"
                  alt="步骤图片"
                  class="step-image"
                  @click="previewImage(image)"
                >
              </div>
              <div class="step-meta">
                <span class="step-date">{{ currentStep.date }}</span>
                <span class="step-duration">用时: {{ currentStep.duration }}</span>
              </div>
            </div>
              </div>
        </div>
      </div>
      
      <!-- 未完成步骤 -->
      <div class="pending-steps">
        <div class="pending-label">待完成步骤</div>
        <ol class="pending-list">
          <li v-for="(step, index) in pendingSteps" :key="index">
            {{ step }}
          </li>
        </ol>
      </div>
    </div>
    
    <!-- 底部操作栏 -->
    <div class="bottom-actions" v-if="isResponsibleEngineer">
      <button class="bottom-btn primary" @click="goBack">
        <i class="icon-arrow-left"></i>返回任务详情
      </button>
    </div>
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { getTaskFlow } from '../../api/task'
import { ref, onMounted } from 'vue'

export default {
  name: 'TaskFlowDetailPage',
  props: {
    id: {
      type: String,
      required: false
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(true)
    const error = ref(null)

    const goBack = () => {
      // 返回到任务详情页面
      const taskId = props.id || route.params.id
      if (taskId) {
        router.push(`/task/${taskId}`)
      } else {
        router.back()
      }
    }

    return {
      goBack,
      loading,
      error
    }
  },
  data() {
    return {
      isResponsibleEngineer: true, // 是否为负责工程师，控制是否显示返回按钮
      task: {
        title: '某大学 - 气相色谱仪维修',
        taskId: 'T20231120-001',
        taskType: 'repair', // 添加任务类型字段
        assignee: '张工程师',
        status: '进行中',
        startTime: '2023-11-20 09:00',
        deviceInfo: { // 添加设备信息
          name: '气相色谱仪',
          model: 'GC-2000',
          brand: 'Agilent',
          serialNumber: 'SN12345678'
        }
      },
      completedSteps: [
        {
          title: '接收任务',
          time: '09:00',
          description: '接收到某大学气相色谱仪维修任务，客户反映进样后无色谱峰，怀疑进样系统故障。',
          date: '2023-11-20 09:00',
          duration: '10分钟',
          images: []
        },
        {
          title: '上门定位',
          time: '10:30',
          description: '抵达客户现场，进行设备位置定位打卡。设备位于某大学化学实验楼3楼分析实验室。',
          date: '2023-11-20 10:30',
          duration: '30分钟',
          images: [
            'https://images.unsplash.com/photo-1581093458791-9d1f9b7a7e76?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ]
        },
        {
          title: '初步检查',
          time: '11:00',
          description: '对设备进行初步检查，开机自检正常，软件连接正常。进行空白进样测试，确认无色谱峰现象属实。',
          date: '2023-11-20 11:00',
          duration: '1小时',
          images: [
            'https://images.unsplash.com/photo-1581093196277-9f608bb3b4b9?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
            'https://images.unsplash.com/photo-1581093196277-9f608bb3b4b9?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ]
        },
        {
          title: '拆卸检查',
          time: '13:10',
          description: '拆卸进样口组件，发现进样口密封圈老化严重，已经变形。同时检查温控系统，发现温度显示不稳定。',
          date: '2023-11-20 13:10',
          duration: '1小时10分钟',
          images: [
            'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
            'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
          ]
        }
      ],
      currentStep: {
        title: '检修结果填写',
        time: '14:30',
        description: '检查发现进样口密封圈老化，需要更换。同时色谱柱温度控制不稳定，怀疑是温控板故障。',
        date: '2023-11-20 14:30',
        duration: '1小时20分钟',
        images: [
          'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80',
          'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'
        ]
      },
      pendingSteps: [
        '配件申请',
        '维修方案制定',
        '维修实施',
        '设备测试',
        '客户验收'
      ],
      // 任务类型与流程步骤的映射关系
      taskTypeStepsMap: {
        repair: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '检修完成', description: '维修评估：文字＋图片' },
          { title: '维修方案和报价', description: '维修评估和方案：文字＋图片，维修报价' },
          { title: '维修中', description: '工程师维修中' },
          { title: '验证报告', description: '已完成维修验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        maintenance: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '检修完成', description: '保养评估：文字＋图片' },
          { title: '保养方案和报价', description: '保养评估和方案：文字＋图片，保养报价' },
          { title: '保养中', description: '工程师保养中' },
          { title: '验证报告', description: '已完成保养验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        recycle: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '检查完成', description: '回收评估：文字＋图片' },
          { title: '回收方案和报价', description: '回收评估和方案：文字＋图片，回收报价' },
          { title: '回收中', description: '工程师回收中' },
          { title: '验证报告', description: '已完成回收验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        leasing: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '检查完成', description: '租赁评估：文字＋图片' },
          { title: '租赁方案和报价', description: '租赁评估和方案：文字＋图片，租赁报价' },
          { title: '租赁中', description: '工程师租赁中' },
          { title: '验证报告', description: '已完成租赁验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        training: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '培训准备完成', description: '培训评估：文字＋图片' },
          { title: '培训方案和报价', description: '培训评估和方案：文字＋图片，培训报价' },
          { title: '培训中', description: '工程师培训中' },
          { title: '验证报告', description: '已完成培训验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        verification: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '验证准备完成', description: '验证评估：文字＋图片' },
          { title: '验证方案和报价', description: '验证评估和方案：文字＋图片，验证报价' },
          { title: '验证中', description: '工程师验证中' },
          { title: '验证报告', description: '已完成验证验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        selection: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '选型分析完成', description: '选型评估：文字＋图片' },
          { title: '选型方案和报价', description: '选型评估和方案：文字＋图片，选型报价' },
          { title: '选型进行中', description: '工程师选型中' },
          { title: '验证报告', description: '已完成选型验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ],
        installation: [
          { title: '已接单', description: '请保持电话畅通，我们稍后会联系您' },
          { title: '判断是否需要上门', description: '确定是否需要工程师上门服务或远程协助' },
          { title: '安装准备完成', description: '安装评估：文字＋图片' },
          { title: '安装方案和报价', description: '安装评估和方案：文字＋图片，安装报价' },
          { title: '安装中', description: '工程师安装中' },
          { title: '验证报告', description: '已完成安装验证，查看验证报告' },
          { title: '服务评价', description: '服务态度、服务质量、总体评价' },
          { title: '订单已完成', description: '祝您生活愉快！' }
        ]
      },
      // 任务类型名称映射
      taskTypeNames: {
        repair: '仪器维修',
        maintenance: '仪器保养',
        recycle: '仪器回收',
        leasing: '仪器租赁',
        training: '培训预约',
        verification: '仪器验证',
        selection: '仪器选型',
        installation: '仪器安装'
      },
      showRecordForm: false // 移除记录表单，只作为展示
    }
  },
  methods: {
    getStatusClass(status) {
      switch(status) {
        case '进行中': return 'status-in-progress';
        case '已完成': return 'status-completed';
        case '待处理': return 'status-pending';
        default: return '';
      }
    },
    // 获取任务类型对应的标题
    getTaskTypeTitle(taskType) {
      switch(taskType) {
        case 'repair': return '维修';
        case 'maintenance': return '保养';
        case 'recycle': return '回收';
        case 'leasing': return '租赁';
        case 'training': return '培训';
        case 'verification': return '验证';
        case 'selection': return '选型';
        case 'installation': return '安装';
        default: return '任务';
      }
    },
    // 预览图片
    previewImage(image) {
      console.log('预览图片:', image);
      // 这里可以实现图片预览功能，如使用第三方库或自定义弹窗
    },
    // 初始化任务步骤
    initTaskSteps() {
      // 如果后端返回的任务没有对应的步骤，则根据任务类型加载对应的步骤模板
      if (this.pendingSteps.length === 0) {
        const typeSteps = this.taskTypeStepsMap[this.task.taskType] || [];
        // 跳过已完成的步骤和当前步骤
        const skipCount = this.completedSteps.length + 1;
        // 将剩余步骤添加到待完成步骤中
        this.pendingSteps = typeSteps.slice(skipCount).map(step => step.title);
      }
    }
  },
  created() {
    // 加载任务流程数据
    this.loadTaskFlow();
    // 初始化任务步骤
    this.initTaskSteps();
  },
  
  methods: {
    getStatusClass(status) {
      switch(status) {
        case '进行中': return 'status-in-progress';
        case '已完成': return 'status-completed';
        case '待处理': return 'status-pending';
        default: return '';
      }
    },
    
    // 加载任务流程数据
    async loadTaskFlow() {
      this.loading = true;
      this.error = null;
      
      try {
        const taskId = this.$route.params.id;
        if (!taskId) {
          this.loading = false;
          return;
        }
        
        const response = await getTaskFlow(taskId);
        if (response && response.code === 200 && response.data) {
          // 更新任务流程数据
          this.task = response.data.task || this.task;
          this.completedSteps = response.data.completedSteps || [];
          this.currentStep = response.data.currentStep || {};
          this.pendingSteps = response.data.pendingSteps || [];
        } else {
          console.error('获取任务流程失败:', response?.message || '未知错误');
          this.error = response?.message || '获取任务流程失败';
        }
      } catch (err) {
        console.error('加载任务流程出错:', err);
        this.error = err.message || '加载任务流程失败';
      } finally {
        this.loading = false;
      }
    },
    
    // 获取任务类型对应的标题
    getTaskTypeTitle(taskType) {
      switch(taskType) {
        case 'repair': return '维修';
        case 'maintenance': return '保养';
        case 'recycle': return '回收';
        case 'leasing': return '租赁';
        case 'training': return '培训';
        case 'verification': return '验证';
        case 'selection': return '选型';
        case 'installation': return '安装';
        default: return '任务';
      }
    },
    
    // 预览图片
    previewImage(image) {
      console.log('预览图片:', image);
      // 这里可以实现图片预览功能，如使用第三方库或自定义弹窗
    },
    
    // 初始化任务步骤
    initTaskSteps() {
      // 如果后端返回的任务没有对应的步骤，则根据任务类型加载对应的步骤模板
      if (this.pendingSteps.length === 0) {
        const typeSteps = this.taskTypeStepsMap[this.task.taskType] || [];
        // 跳过已完成的步骤和当前步骤
        const skipCount = this.completedSteps.length + 1;
        // 将剩余步骤添加到待完成步骤中
        this.pendingSteps = typeSteps.slice(skipCount).map(step => step.title);
      }
    }
  }
}
</script>

<style scoped>
.task-flow-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: #3b82f6;
  animation: spin 1s ease-in-out infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-error {
  font-size: 48px;
  color: #ef4444;
  margin-bottom: 16px;
}

.error-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.retry-btn,
.back-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
}

.retry-btn {
  background-color: #3b82f6;
  color: white;
}

.back-btn {
  background-color: #6b7280;
  color: white;
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

.share-btn {
  display: flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #3b82f6;
  color: #fff;
  border-radius: 9999px;
  font-size: 14px;
  border: none;
}

.icon-share::before {
  content: '\f064';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 4px;
}

.task-info {
  background-color: #fff;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.task-title {
  font-size: 18px;
  font-weight: 600;
}

.task-id {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.task-meta {
  display: flex;
  align-items: center;
  margin-top: 8px;
}

.task-type {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
  background-color: #fef3c7;
  color: #d97706;
  margin-right: 16px;
}

.task-assignee {
  font-size: 14px;
  color: #6b7280;
  margin-right: 16px;
}

.task-status {
  padding: 4px 8px;
  border-radius: 9999px;
  font-size: 12px;
}

.status-in-progress {
  background-color: #fef3c7;
  color: #d97706;
}

.status-completed {
  background-color: #d1fae5;
  color: #059669;
}

.status-pending {
  background-color: #dbeafe;
  color: #3b82f6;
}

.task-time {
  text-align: right;
}

.time-label {
  font-size: 12px;
  color: #6b7280;
}

.time-value {
  font-size: 14px;
  font-weight: 500;
  margin-top: 4px;
}

.device-info {
  margin-top: 16px;
}

.section-subtitle {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.device-details {
  display: flex;
  flex-wrap: wrap;
}

.device-item {
  width: 50%;
  margin-bottom: 8px;
}

.device-label {
  font-size: 14px;
  color: #6b7280;
}

.device-value {
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
}

.flow-timeline {
  background-color: #fff;
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
}

.flow-step {
  position: relative;
  padding-bottom: 32px;
}

.flow-step.completed .step-line {
  position: absolute;
  left: 16px;
  top: 32px;
  bottom: 0;
  width: 2px;
  background-color: #e5e7eb;
}

.step-content {
  display: flex;
}

.step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #10b981;
  flex-shrink: 0;
  z-index: 1;
}

.flow-step.current .step-icon {
  background-color: #3b82f6;
}

.icon-check::before {
  content: '\f00c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #fff;
  font-size: 12px;
}

.icon-pen::before {
  content: '\f304';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #fff;
  font-size: 12px;
}

.step-details {
  margin-left: 16px;
  flex: 1;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
}

.step-time {
  font-size: 12px;
  color: #6b7280;
}

.step-card {
  background-color: #f3f4f6;
  padding: 12px;
  border-radius: 8px;
}

.step-card.current {
  background-color: #eff6ff;
  border-left: 4px solid #3b82f6;
}

.step-description {
  font-size: 14px;
}

.step-images {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 8px 0;
  margin-top: 8px;
}

.step-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.step-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #6b7280;
  margin-top: 8px;
}

.pending-steps {
  margin-left: 48px;
  margin-top: 16px;
}

.pending-label {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 8px;
}

.pending-list {
  list-style: decimal;
  padding-left: 20px;
  font-size: 14px;
  color: #6b7280;
}

.pending-list li {
  margin-bottom: 8px;
}

/* 底部操作栏样式 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e7eb;
  padding: 16px;
  padding-bottom: calc(16px + 56px); /* 增加底部padding，防止被导航栏遮挡 */
  display: flex;
  gap: 8px;
}

.bottom-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border: none;
}

.bottom-btn.primary {
  background-color: #3b82f6;
  color: #fff;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  margin-right: 8px;
}
</style> 