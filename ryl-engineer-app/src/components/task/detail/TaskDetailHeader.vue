<template>
  <header class="header">
    <div class="header-left" @click="goBack">
      <i class="icon-arrow-left"></i>
    </div>
    <h1>任务详情</h1>
    <div class="header-right">
      <div class="action-icons">
        <router-link 
          v-if="canViewLocation" 
          :to="`/location?taskId=${task?.taskId}&from=taskDetail`" 
          class="action-btn"
        >
          <i class="icon-location-pin"></i>
        </router-link>
        <button 
          v-if="canEditTask" 
          class="action-btn" 
          @click="showTaskOptions"
        >
          <i class="icon-ellipsis"></i>
        </button>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'TaskDetailHeader',
  props: {
    task: {
      type: Object,
      required: true
    },
    canViewLocation: {
      type: Boolean,
      default: true
    },
    canEditTask: {
      type: Boolean,
      default: false
    },
    showOptions: {
      type: Boolean,
      default: false
    }
  },
  emits: ['go-back', 'edit-task', 'show-task-options', 'hide-task-options'],
  setup(props, { emit }) {
    // 导航返回
    const goBack = () => {
      emit('go-back')
    }
    
    // 编辑任务
    const editTask = () => {
      emit('edit-task')
    }
    
    // 显示任务选项菜单
    const showTaskOptions = () => {
      emit('show-task-options')
    }
    
    // 隐藏任务选项菜单
    const hideTaskOptions = () => {
      emit('hide-task-options')
    }
    
    return {
      goBack,
      editTask,
      showTaskOptions,
      hideTaskOptions
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
}

.header-left {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f3f4f6;
  transition: all 0.2s ease;
}

.header-left:hover {
  background-color: #e5e7eb;
}

.header-left:active {
  transform: scale(0.95);
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #4b5563;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  color: #111827;
  flex: 1;
  text-align: center;
}

.header-right {
  margin-left: auto;
}

.action-icons {
  display: flex;
  gap: 12px;
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f3f4f6;
  color: #4b5563;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.action-btn:hover {
  background-color: #e5e7eb;
}

.action-btn:active {
  transform: scale(0.95);
}

.icon-location-pin::before {
  content: '\f3c5';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 14px;
}

.icon-ellipsis::before {
  content: '\f142';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 14px;
}
</style> 