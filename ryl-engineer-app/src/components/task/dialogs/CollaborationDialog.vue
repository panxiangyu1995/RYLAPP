<template>
  <div class="collaboration-overlay" @click="closeDialog"></div>
  <div class="collaboration-dialog">
    <div class="dialog-header">
      <h3 class="dialog-title">添加协作工程师</h3>
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
          :class="{ 'is-selected': selectedEngineers.includes(engineer.id) }"
          @click="toggleEngineer(engineer)"
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
          </div>
          <div class="selection-indicator">
            <div v-if="selectedEngineers.includes(engineer.id)" class="checked">
              <i class="icon-check"></i>
            </div>
            <div v-else class="unchecked"></div>
          </div>
        </div>
      </div>
      
      <!-- 已选中工程师 -->
      <div class="selected-section" v-if="selectedEngineers.length > 0">
        <h4 class="section-title">
          已选择 ({{ selectedEngineers.length }})
        </h4>
        <div class="selected-list">
          <div 
            v-for="id in selectedEngineers" 
            :key="id"
            class="selected-item"
          >
            <div class="engineer-brief">
              <div class="brief-avatar">
                {{ getInitials(getEngineerName(id)) }}
              </div>
              <span class="brief-name">{{ getEngineerName(id) }}</span>
            </div>
            <button class="remove-btn" @click.stop="removeEngineer(id)">
              <i class="icon-times"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="dialog-actions">
      <button class="cancel-btn" @click="closeDialog">取消</button>
      <button 
        class="confirm-btn" 
        :disabled="selectedEngineers.length === 0 || isSubmitting" 
        @click="confirmSelection"
      >
        <i v-if="isSubmitting" class="icon-spinner icon-spin"></i>
        {{ isSubmitting ? '添加中...' : '确认添加' }}
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'CollaborationDialog',
  props: {
    taskId: {
      type: [String, Number],
      required: true
    },
    existingCollaborators: {
      type: Array,
      default: () => []
    }
  },
  emits: ['close', 'add-collaborators'],
  setup(props, { emit }) {
    const engineers = ref([])
    const selectedEngineers = ref([])
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
    
    // 根据ID获取工程师姓名
    const getEngineerName = (id) => {
      const engineer = engineers.value.find(eng => eng.id === id)
      return engineer ? engineer.name : 'Unknown'
    }
    
    // 选择或取消选择工程师
    const toggleEngineer = (engineer) => {
      const index = selectedEngineers.value.findIndex(id => id === engineer.id)
      
      if (index > -1) {
        // 如果已经选择，则取消选择
        selectedEngineers.value.splice(index, 1)
      } else {
        // 如果未选择，则添加到选择列表
        selectedEngineers.value.push(engineer.id)
      }
    }
    
    // 移除已选择的工程师
    const removeEngineer = (id) => {
      const index = selectedEngineers.value.findIndex(selectedId => selectedId === id)
      if (index > -1) {
        selectedEngineers.value.splice(index, 1)
      }
    }
    
    // 清除搜索
    const clearSearch = () => {
      searchQuery.value = ''
    }
    
    // 搜索工程师
    const searchEngineers = () => {
      // 这里可以添加实时搜索逻辑，例如防抖动等
    }
    
    // 关闭对话框
    const closeDialog = (e) => {
      // 如果点击的是遮罩层，则关闭对话框
      if (e && e.target && e.target.classList.contains('collaboration-overlay')) {
        emit('close')
      } else if (!e || !e.target) {
        emit('close')
      }
    }
    
    // 确认选择
    const confirmSelection = async () => {
      if (selectedEngineers.value.length === 0) return
      
      isSubmitting.value = true
      
      try {
        // 获取选定的工程师详细信息
        const selectedEngineerDetails = engineers.value.filter(
          engineer => selectedEngineers.value.includes(engineer.id)
        )
        
        // 触发添加协作者事件
        emit('add-collaborators', {
          taskId: props.taskId,
          engineers: selectedEngineerDetails
        })
        
        // 关闭对话框
        closeDialog()
      } catch (error) {
        console.error('添加协作工程师时出错:', error)
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
          { id: 1, name: '张工程', department: '维修部' },
          { id: 2, name: '李工程', department: '调试部' },
          { id: 3, name: '王工程', department: '安装部' },
          { id: 4, name: '赵工程', department: '维修部' },
          { id: 5, name: '钱工程', department: '调试部' },
          { id: 6, name: '孙工程', department: '安装部' },
          { id: 7, name: '周工程', department: '维修部' },
          { id: 8, name: '吴工程', department: '调试部' },
          { id: 9, name: '郑工程', department: '安装部' },
          { id: 10, name: '冯工程', department: '维修部' }
        ]
        
        // 过滤掉已经是协作者的工程师
        engineers.value = mockEngineers.filter(
          eng => !props.existingCollaborators.includes(eng.id)
        )
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
      selectedEngineers,
      searchQuery,
      loading,
      isSubmitting,
      filteredEngineers,
      getInitials,
      getEngineerName,
      toggleEngineer,
      removeEngineer,
      clearSearch,
      searchEngineers,
      closeDialog,
      confirmSelection
    }
  }
}
</script>

<style scoped>
.collaboration-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
  animation: fadeIn 0.2s ease;
}

.collaboration-dialog {
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
  margin-bottom: 20px;
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
  margin-bottom: 20px;
}

.engineer-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.engineer-item:hover {
  background-color: #f9fafb;
}

.engineer-item.is-selected {
  background-color: #eff6ff;
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
}

.engineer-id,
.engineer-department {
  font-size: 13px;
  color: #6b7280;
}

.selection-indicator {
  margin-left: 12px;
}

.unchecked {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #d1d5db;
}

.checked {
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

.selected-section {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
  margin-top: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 12px 0;
}

.selected-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-item {
  display: flex;
  align-items: center;
  padding: 6px 10px 6px 6px;
  background-color: #eff6ff;
  border-radius: 30px;
}

.engineer-brief {
  display: flex;
  align-items: center;
}

.brief-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #3b82f6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 500;
  margin-right: 6px;
}

.brief-name {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}

.remove-btn {
  margin-left: 6px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #e5e7eb;
  border: none;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 10px;
  transition: all 0.2s ease;
}

.remove-btn:hover {
  background-color: #fee2e2;
  color: #b91c1c;
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
