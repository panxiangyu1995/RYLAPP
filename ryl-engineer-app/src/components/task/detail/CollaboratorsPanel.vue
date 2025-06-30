<template>
  <div class="collaborators-panel">
    <div class="card-header">
      <h3 class="card-title">
        <i class="fas fa-users"></i>
        协作工程师
      </h3>
      <button v-if="canManageCollaborators" class="invite-btn" @click="showInviteDialog">
        邀请协作
      </button>
    </div>
    
    <div class="engineers-list">
      <div v-for="(engineer, index) in engineers" :key="index" class="engineer-item">
        <div class="engineer-avatar" @click="viewProfile(engineer.id)">
          <img v-if="engineer.avatar" :src="engineer.avatar" :alt="engineer.name">
          <div v-else class="default-avatar">{{ getInitials(engineer.name) }}</div>
          <span v-if="engineer.isMainEngineer" class="main-badge">主负责</span>
          <button 
            v-else-if="canManageCollaborators" 
            class="remove-btn"
            @click.stop="confirmRemove(engineer)"
          >
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="engineer-info">
          <p class="engineer-name">{{ engineer.name }}</p>
          <p class="engineer-role">{{ engineer.isMainEngineer ? '主负责人' : '协助' }}</p>
        </div>
      </div>
      
      <div v-if="engineers.length === 0" class="no-engineers">
        <p>暂无协作工程师</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CollaboratorsPanel',
  props: {
    engineers: {
      type: Array,
      default: () => []
    },
    canManageCollaborators: {
      type: Boolean,
      default: false
    }
  },
  emits: ['invite', 'view-profile', 'remove'],
  setup(props, { emit }) {
    // 获取姓名首字母
    const getInitials = (name) => {
      if (!name) return '?'
      return name.charAt(0).toUpperCase()
    }
    
    // 显示邀请对话框
    const showInviteDialog = () => {
      emit('invite')
    }
    
    // 查看工程师资料
    const viewProfile = (engineerId) => {
      emit('view-profile', engineerId)
    }
    
    // 确认移除协作工程师
    const confirmRemove = (engineer) => {
      if (confirm(`确定要移除 ${engineer.name} 的协作权限吗？`)) {
        emit('remove', engineer.id)
      }
    }
    
    return {
      getInitials,
      showInviteDialog,
      viewProfile,
      confirmRemove
    }
  }
}
</script>

<style scoped>
.collaborators-panel {
  background-color: #fff;
  margin: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.card-title i {
  margin-right: 8px;
  color: #666;
}

.invite-btn {
  background-color: #f3f4f6;
  color: #2563eb;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.invite-btn:hover {
  background-color: #e5e7eb;
}

.engineers-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px;
}

.engineer-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 70px;
}

.engineer-avatar {
  position: relative;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 8px;
  cursor: pointer;
}

.engineer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  background-color: #e5e7eb;
  color: #4b5563;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
}

.main-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: #2563eb;
  color: white;
  font-size: 8px;
  padding: 2px 4px;
  border-radius: 4px;
}

.remove-btn {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background-color: #ef4444;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  cursor: pointer;
  padding: 0;
}

.engineer-info {
  text-align: center;
}

.engineer-name {
  font-size: 13px;
  font-weight: 500;
  color: #4b5563;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.engineer-role {
  font-size: 11px;
  color: #6b7280;
  margin: 0;
}

.no-engineers {
  width: 100%;
  padding: 16px;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}
</style> 