<template>
  <div 
    class="task-card"
    @click="handleClick"
  >
    <!-- 调试信息 -->
    <div v-if="showDebug" class="debug-button-container">
      <button class="debug-button" @click="toggleDebugInfo">
        {{ showDebugInfo ? '隐藏调试信息' : '显示调试信息' }}
      </button>
      <div v-if="showDebugInfo" class="debug-info">
        <pre>{{ JSON.stringify(task, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="task-header">
      <div>
        <h2 class="task-title">{{ task.title || '无标题任务' }}</h2>
        <p class="task-id">任务编号：{{ task.taskId || '未分配' }}</p>
      </div>
      <span :class="['task-status', getStatusClass(task.status)]" v-if="showStatus">
        {{ getStatusText(task.status) }}
      </span>
    </div>
    
    <div class="task-details">
      <div class="detail-item">
        <i class="icon-building"></i>
        <span>客户：{{ task.customerName || task.customer || '未指定' }}</span>
        <span v-if="task.customerLevel" :class="['customer-level', `level-${task.customerLevel}`]">{{ task.customerLevel }}级</span>
      </div>
      <div class="detail-item">
        <i class="icon-tools"></i>
        <span>设备：{{ getDeviceInfo() }}</span>
      </div>
      <div class="detail-item">
        <i class="icon-calendar"></i>
        <span>创建时间：{{ formatDate(task.createTime) }}</span>
      </div>
    </div>
    
    <div class="task-footer">
      <div class="engineer-list">
        <div 
          v-for="(engineer, index) in getEngineers()" 
          :key="index"
          class="engineer-avatar"
        >
          <span>{{ engineer.engineerName ? engineer.engineerName.substring(0, 1) : '工' }}</span>
        </div>
        <span v-if="getEngineers().length > 3" class="more-engineers">
          +{{ getEngineers().length - 3 }}
        </span>
        <span v-if="getEngineers().length === 0" class="no-engineer">
          未分配工程师
        </span>
      </div>
      <div class="task-actions">
        <button class="action-btn" @click.stop="$emit('call', task)" v-if="hasEngineer">
          <i class="icon-phone"></i>
        </button>
        <button class="action-btn" @click.stop="$emit('message', task)" v-if="hasEngineer">
          <i class="icon-message"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TaskCard',
  props: {
    task: {
      type: Object,
      required: true,
      default: () => ({})  // 添加默认值，防止空对象导致错误
    },
    showActions: {
      type: Boolean,
      default: true
    },
    showStatus: {
      type: Boolean,
      default: true
    },
    showDebug: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      showDebugInfo: false
    };
  },
  computed: {
    hasEngineer() {
      return Array.isArray(this.task.engineers) && this.task.engineers.length > 0;
    }
  },
  methods: {
    getStatusClass(status) {
      if (!status) return 'status-pending';
      
      switch(status) {
        case 'pending': return 'status-pending'
        case '待确认': return 'status-confirming'
        case 'in-progress': return 'status-progress'
        case 'completed': return 'status-completed'
        default: return 'status-pending'
      }
    },
    getStatusText(status) {
      if (!status) return '待处理';
      
      switch(status) {
        case 'pending': return '待处理'
        case '待确认': return '待确认'
        case 'in-progress': return '进行中'
        case 'completed': return '已完成'
        default: return status || '待处理'
      }
    },
    formatDate(date) {
      if (!date) return '无日期';
      
      try {
        // 处理字符串日期
        if (typeof date === 'string') {
          // 如果包含时间，只显示日期部分
          if (date.includes(' ')) {
            return date.split(' ')[0];
          }
          if (date.includes('T')) {
            return date.split('T')[0];
          }
          return date;
        }
        
        // 处理Date对象
        if (date instanceof Date) {
          return date.toISOString().split('T')[0];
        }
        
        return '无效日期';
      } catch (err) {
        console.error('日期格式化错误:', err);
        return '无效日期';
      }
    },
    getDeviceInfo() {
      try {
        // 如果是选型任务，可能没有设备信息
        if (this.task.taskType === 'selection') {
          return '待选型';
        }
        
        // 先尝试直接获取deviceName
        if (this.task.deviceName) {
          return this.task.deviceName;
        }
        
        // 尝试从deviceInfo对象获取
        if (this.task.deviceInfo && this.task.deviceInfo.name) {
          return this.task.deviceInfo.name;
        }
        
        return '未指定';
      } catch (err) {
        console.error('获取设备信息错误:', err);
        return '未指定';
      }
    },
    getEngineers() {
      try {
        // 打印任务对象以便调试
        if (this.showDebug) {
          console.log('Task object for engineers check:', this.task);
          if (this.task.engineers) {
            console.log('Engineers array:', this.task.engineers);
          }
          if (this.task.owner) {
            console.log('Task owner:', this.task.owner);
          }
        }
        
        // 检查engineers字段是否存在并且是数组
        if (this.task.engineers && Array.isArray(this.task.engineers) && this.task.engineers.length > 0) {
          return this.task.engineers.slice(0, 3);
        }
        
        // 如果有owner字段，优先使用owner
        if (this.task.owner && typeof this.task.owner === 'object') {
          return [this.task.owner];
        }

        // 如果有assignedEngineer或assignedEngineers字段，使用它们
        if (this.task.assignedEngineer && typeof this.task.assignedEngineer === 'object') {
          return [this.task.assignedEngineer];
        }
        
        if (this.task.assignedEngineers && Array.isArray(this.task.assignedEngineers) && this.task.assignedEngineers.length > 0) {
          return this.task.assignedEngineers.slice(0, 3);
        }

        // 如果有taskOwner字段
        if (this.task.taskOwner && typeof this.task.taskOwner === 'object') {
          return [this.task.taskOwner];
        }
        
        // 如果engineers不是数组或不存在，返回空数组
        return [];
      } catch (err) {
        console.error('获取工程师信息错误:', err);
        return [];
      }
    },
    handleClick() {
      console.log('TaskCard点击，任务ID:', this.task.taskId);
      console.log('任务对象:', this.task);
      
      if (this.task && this.task.taskId) {
        this.$emit('click', this.task.taskId);
      } else {
        console.error('任务ID不存在，无法跳转');
      }
    },
    toggleDebugInfo() {
      this.showDebugInfo = !this.showDebugInfo;
    }
  }
}
</script>

<style scoped>
.task-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 12px;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
}

.task-id {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.task-status {
  padding: 4px 12px;
  border-radius: 9999px;
  font-size: 14px;
}

.status-pending {
  background-color: #dbeafe;
  color: #2563eb;
}

.status-progress {
  background-color: #fef3c7;
  color: #d97706;
}

.status-completed {
  background-color: #d1fae5;
  color: #059669;
}

.task-status.status-progress {
  background-color: #dbeafe;
  color: #3b82f6;
}
.task-status.status-confirming {
  background-color: #fef3c7;
  color: #d97706;
}
.task-status.status-completed {
  background-color: #dcfce7;
  color: #22c55e;
}

.task-details {
  margin-top: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-item i {
  margin-right: 8px;
  color: #6b7280;
}

.detail-item .customer-level {
  margin-left: 8px;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.level-A {
  background-color: #fee2e2;
  color: #ef4444;
}

.level-B {
  background-color: #fef3c7;
  color: #d97706;
}

.level-C {
  background-color: #dbeafe;
  color: #2563eb;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.engineer-list {
  display: flex;
  align-items: center;
}

.engineer-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  border: 2px solid #fff;
  margin-right: -8px;
}

.more-engineers {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #e5e7eb;
  color: #6b7280;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 12px;
  margin-left: 4px;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f3f4f6;
  color: #6b7280;
  display: flex;
  justify-content: center;
  align-items: center;
  border: none;
}

.icon-building::before {
  content: '\f1ad';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-tools::before {
  content: '\f7d9';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-calendar::before {
  content: '\f133';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-phone::before {
  content: '\f095';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-message::before {
  content: '\f4ad';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.no-engineer {
  font-size: 12px;
  color: #6b7280;
}

.debug-button-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.debug-button {
  padding: 8px 16px;
  border-radius: 4px;
  background-color: #f3f4f6;
  color: #6b7280;
  border: none;
}

.debug-info {
  background-color: #f0f9ff;
  border: 1px solid #bae6fd;
  padding: 8px;
  margin-bottom: 8px;
  border-radius: 4px;
  font-size: 12px;
  overflow-x: auto;
}

.debug-info pre {
  margin: 0;
  white-space: pre-wrap;
}
</style> 