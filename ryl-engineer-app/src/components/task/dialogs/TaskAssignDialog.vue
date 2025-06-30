<template>
  <div class="assign-overlay" @click="closeDialog"></div>
  <div class="assign-dialog">
    <div class="dialog-header">
      <h3 class="dialog-title">分配任务</h3>
      <button class="close-btn" @click="closeDialog">
        <i class="icon-times"></i>
      </button>
    </div>
    
    <div class="dialog-content">
      <!-- 搜索框 -->
      <div class="search-section">
        <div class="search-input">
          <i class="icon-search"></i>
          <input 
            type="text"
            v-model="searchQuery"
            placeholder="搜索工程师姓名或ID..."
            @input="searchEngineers"
          />
          <button class="clear-btn" v-if="searchQuery" @click="clearSearch">
            <i class="icon-times-circle"></i>
          </button>
        </div>
      </div>
      
      <!-- 加载中 -->
      <div class="loading-state" v-if="loading">
        <i class="icon-spinner icon-spin"></i>
        <span>正在加载工程师数据...</span>
      </div>
      
      <!-- 无结果 -->
      <div class="empty-state" v-else-if="filteredEngineers.length === 0">
        <i class="icon-search"></i>
        <h4>未找到匹配的工程师</h4>
        <p>请尝试使用其他搜索条件</p>
      </div>
      
      <!-- 工程师列表 -->
      <div class="engineers-list" v-else>
        <div 
          v-for="engineer in filteredEngineers" 
          :key="engineer.id"
          class="engineer-item"
          :class="{ 'is-selected': selectedEngineerId === engineer.id }"
          @click="selectEngineer(engineer.id)"
        >
          <div class="engineer-avatar">
            {{ getInitials(engineer.name) }}
          </div>
          <div class="engineer-info">
            <h4 class="engineer-name">{{ engineer.name }}</h4>
            <div class="engineer-details">
              <span class="engineer-id">ID: {{ engineer.id }}</span>
              <span class="engineer-department" v-if="engineer.department">
                {{ engineer.department }}
              </span>
            </div>
            <div class="engineer-stats" v-if="engineer.stats">
              <div class="stat-item">
                <i class="icon-tasks"></i>
                <span>进行中: {{ engineer.stats.activeTasks || 0 }}</span>
              </div>
              <div class="stat-item">
                <i class="icon-check-circle"></i>
                <span>已完成: {{ engineer.stats.completedTasks || 0 }}</span>
              </div>
            </div>
          </div>
          <div class="selection-indicator">
            <div v-if="selectedEngineerId === engineer.id" class="selected">
              <i class="icon-check"></i>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 备注 -->
      <div class="note-section" v-if="selectedEngineerId">
        <label for="assign-note" class="note-label">分配备注 (可选):</label>
        <textarea
          id="assign-note"
          v-model="assignNote"
          class="note-input"
          placeholder="添加关于此次任务分配的备注信息..."
          rows="3"
        ></textarea>
      </div>
    </div>
    
    <div class="dialog-actions">
      <button class="cancel-btn" @click="closeDialog">取消</button>
      <button 
        class="confirm-btn" 
        :disabled="!selectedEngineerId || isSubmitting" 
        @click="confirmAssign"
      >
        <i v-if="isSubmitting" class="icon-spinner icon-spin"></i>
        {{ isSubmitting ? '分配中...' : '确认分配' }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'TaskAssignDialog',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    },
    currentEngineerId: {
      type: [String, Number],
      default: null
    }
  },
  emits: ['close', 'assign'],
  setup(props, { emit }) {
    const engineers = ref([])
    const selectedEngineerId = ref(props.currentEngineerId || null)
    const assignNote = ref('')
    const searchQuery = ref('')
    const loading = ref(false)
    const isSubmitting = ref(false)
    
    // 根据搜索查询过滤工程师列表
    const filteredEngineers = computed(() => {
      if (!searchQuery.value) return engineers.value
      
      const query = searchQuery.value.toLowerCase()
      return engineers.value.filter(engineer => {
        return (
          engineer.name.toLowerCase().includes(query) ||
          engineer.id.toString().includes(query) ||
          (engineer.department && engineer.department.toLowerCase().includes(query))
        )
      })
    })
    
    // 获取用户首字母
    const getInitials = (name) => {
      if (!name) return '?'
      
      const names = name.split(' ')
      if (names.length >= 2) {
        return `${names[0].charAt(0)}${names[1].charAt(0)}`.toUpperCase()
      }
      
      return name.charAt(0).toUpperCase()
    }
    
    // 选择工程师
    const selectEngineer = (engineerId) => {
      selectedEngineerId.value = engineerId
    }
    
    // 清除搜索
    const clearSearch = () => {
      searchQuery.value = ''
    }
    
    // 搜索工程师
    const searchEngineers = () => {
      // 可以添加实时搜索逻辑，例如防抖动等
    }
    
    // 关闭对话框
    const closeDialog = (e) => {
      // 如果点击的是遮罩层，则关闭对话框
      if (e && e.target && e.target.classList.contains('assign-overlay')) {
        emit('close')
      } else if (!e || !e.target) {
        emit('close')
      }
    }
    
    // 确认分配
    const confirmAssign = async () => {
      if (!selectedEngineerId.value) return
      
      isSubmitting.value = true
      
      try {
        // 获取选定的工程师详细信息
        const selectedEngineer = engineers.value.find(
          engineer => engineer.id === selectedEngineerId.value
        )
        
        // 触发分配事件
        emit('assign', {
          taskId: props.taskId,
          engineerId: selectedEngineerId.value,
          engineerName: selectedEngineer ? selectedEngineer.name : '',
          note: assignNote.value.trim() || null,
          timestamp: new Date().toISOString()
        })
        
        // 关闭对话框
        closeDialog()
      } catch (error) {
        console.error('分配任务时出错:', error)
      } finally {
        isSubmitting.value = false
      }
    }
    
    // 加载工程师数据
    const loadEngineers = async () => {
      loading.value = true
      
      try {
        // 这里应该是从API获取工程师列表的逻辑
        // 为了演示，我们使用模拟数据
        const mockEngineers = [
          { 
            id: 1, 
            name: '张工程', 
            department: '维修部', 
            stats: { activeTasks: 3, completedTasks: 42 }
          },
          { 
            id: 2, 
            name: '李工程', 
            department: '调试部', 
            stats: { activeTasks: 1, completedTasks: 28 }
          },
          { 
            id: 3, 
            name: '王工程', 
            department: '安装部', 
            stats: { activeTasks: 2, completedTasks: 35 }
          },
          { 
            id: 4, 
            name: '赵工程', 
            department: '维修部', 
            stats: { activeTasks: 5, completedTasks: 50 }
          },
          { 
            id: 5, 
            name: '钱工程', 
            department: '调试部', 
            stats: { activeTasks: 2, completedTasks: 22 }
          }
        ]
        
        engineers.value = mockEngineers
      } catch (error) {
        console.error('加载工程师数据时出错:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 组件挂载时加载数据
    onMounted(() => {
      loadEngineers()
    })
    
    return {
      engineers,
      selectedEngineerId,
      assignNote,
      searchQuery,
      loading,
      isSubmitting,
      filteredEngineers,
      getInitials,
      selectEngineer,
      clearSearch,
      searchEngineers,
      closeDialog,
      confirmAssign
    }
  }
}
</script>

<style scoped>
.assign-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
  animation: fadeIn 0.2s ease;
}

.assign-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 90%;
  max-width: 500px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
  z-index: 101;
  overflow: hidden;
  animation: slideIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { opacity: 0; transform: translate(-50%, -45%); }
  to { opacity: 1; transform: translate(-50%, -50%); }
}

.dialog-header {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.close-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f3f4f6;
  border: none;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background-color: #e5e7eb;
  color: #374151;
}

.dialog-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

.search-section {
  margin-bottom: 16px;
}

.search-input {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input i {
  position: absolute;
  left: 12px;
  color: #6b7280;
  font-size: 16px;
}

.search-input input {
  width: 100%;
  padding: 12px 40px 12px 36px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  background-color: #f9fafb;
  transition: all 0.2s ease;
}

.search-input input:focus {
  outline: none;
  border-color: #3b82f6;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.clear-btn {
  position: absolute;
  right: 12px;
  background: transparent;
  border: none;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  font-size: 16px;
}

.clear-btn:hover {
  color: #374151;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #6b7280;
}

.loading-state i,
.empty-state i {
  font-size: 36px;
  margin-bottom: 16px;
  display: block;
}

.loading-state span {
  font-size: 16px;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #374151;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.engineers-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.engineer-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border: 1px solid transparent;
}

.engineer-item:hover {
  background-color: #f9fafb;
  border-color: #e5e7eb;
}

.engineer-item.is-selected {
  background-color: #eff6ff;
  border-color: #93c5fd;
}

.engineer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 500;
  margin-right: 12px;
  flex-shrink: 0;
}

.engineer-info {
  flex: 1;
}

.engineer-name {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 4px 0;
}

.engineer-details {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 6px;
}

.engineer-id,
.engineer-department {
  font-size: 13px;
  color: #6b7280;
}

.engineer-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #4b5563;
}

.stat-item i {
  margin-right: 4px;
  color: #6b7280;
  font-size: 12px;
}

.selection-indicator {
  margin-left: 12px;
  margin-top: 8px;
}

.selected {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.note-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.note-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.note-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  background-color: #f9fafb;
  resize: vertical;
  transition: all 0.2s ease;
}

.note-input:focus {
  outline: none;
  border-color: #3b82f6;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.dialog-actions {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background-color: #fff;
  color: #4b5563;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.confirm-btn {
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  background-color: #2563eb;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
}

.confirm-btn:hover:not(:disabled) {
  background-color: #1d4ed8;
}

.confirm-btn:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.confirm-btn i {
  margin-right: 8px;
  font-size: 14px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-spin {
  animation: spin 1s linear infinite;
}
</style>
