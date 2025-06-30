<template>
  <div class="conversation-list">
    <header class="header">
      <div class="left">
        <router-link to="/home">
          <i class="material-icons">arrow_back</i>
        </router-link>
        <h1>聊天</h1>
      </div>
      <div class="right">
        <i class="material-icons" @click="goToNewChat">add</i>
        <i class="material-icons">more_vert</i>
      </div>
    </header>
    
    <div class="search-bar">
      <div class="search-input">
        <i class="material-icons">search</i>
        <input 
          type="text" 
          placeholder="搜索会话" 
          v-model="searchKeyword"
          @input="handleSearch"
        />
        <i class="material-icons" v-if="searchKeyword" @click="clearSearch">close</i>
      </div>
    </div>
    
    <div class="filters">
      <div class="filter" :class="{ active: filter === 'all' }" @click="setFilter('all')">全部</div>
      <div class="filter" :class="{ active: filter === 'single' }" @click="setFilter('single')">单聊</div>
      <div class="filter" :class="{ active: filter === 'group' }" @click="setFilter('group')">群聊</div>
      <div class="filter" :class="{ active: filter === 'task' }" @click="setFilter('task')">任务</div>
    </div>
    
    <div class="tab-bar">
      <div 
        class="tab" 
        :class="{ active: activeTab === 'chats' }"
        @click="setActiveTab('chats')"
      >
        会话
      </div>
      <div 
        class="tab" 
        :class="{ active: activeTab === 'assistance' }"
        @click="setActiveTab('assistance')"
      >
        协助请求
      </div>
    </div>
    
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <div class="error-message" v-else-if="error">
      <i class="material-icons">error</i>
      <p>{{ error }}</p>
      <button class="retry-button" @click="handleRetry">重试</button>
    </div>
    
    <div class="empty-state" v-else-if="activeTab === 'chats' && conversations.length === 0 || activeTab === 'assistance' && assistanceRequests.length === 0">
      <i class="material-icons" v-if="activeTab === 'chats'">chat</i>
      <i class="material-icons" v-else>support_agent</i>
      <p v-if="activeTab === 'chats'">没有任何会话</p>
      <p v-else>没有协助请求</p>
      <button v-if="activeTab === 'chats'" @click="goToNewChat">开始新对话</button>
      <button v-else-if="userRole === 'sales' || userRole === 'engineer'" @click="$router.push('/assistance/request')">发起协助请求</button>
    </div>
    
    <div v-else>
      <!-- 会话列表 -->
      <div class="conversations" v-if="activeTab === 'chats'">
        <div class="sticky-conversations" v-if="stickyConversations.length > 0">
          <div 
            class="conversation-item sticky" 
            v-for="conversation in stickyConversations" 
            :key="conversation.conversationId"
            @click="openConversation(conversation)"
          >
            <div class="pin-indicator">
              <i class="material-icons">push_pin</i>
            </div>
            <div class="avatar">
              <img :src="conversation.avatar || '/default-avatar.png'" :alt="conversation.name" />
              <div class="task-badge" v-if="conversation.isTaskRelated"></div>
            </div>
            <div class="conversation-info">
              <div class="conversation-header">
                <h3>{{ conversation.name }}</h3>
                <span class="time">{{ formatTime(conversation.lastMessage.time) }}</span>
              </div>
              <div class="last-message">
                <span class="sender" v-if="conversation.type === 'group'">
                  {{ conversation.lastMessage.senderName }}:
                </span>
                <span class="message" :class="{ 'message-type': conversation.lastMessage.type !== 'chat' }">
                  {{ formatLastMessage(conversation.lastMessage) }}
                </span>
              </div>
            </div>
            <div class="conversation-status">
              <div class="unread-badge" v-if="conversation.unreadCount > 0">
                {{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}
              </div>
              <i class="material-icons mute-icon" v-if="conversation.isMute">volume_off</i>
            </div>
          </div>
        </div>
        
        <div class="regular-conversations">
          <div 
            class="conversation-item" 
            v-for="conversation in normalConversations" 
            :key="conversation.conversationId"
            @click="openConversation(conversation)"
          >
            <div class="avatar">
              <img :src="conversation.avatar || '/default-avatar.png'" :alt="conversation.name" />
              <div class="task-badge" v-if="conversation.isTaskRelated"></div>
            </div>
            <div class="conversation-info">
              <div class="conversation-header">
                <h3>{{ conversation.name }}</h3>
                <span class="time">{{ formatTime(conversation.lastMessage.time) }}</span>
              </div>
              <div class="last-message">
                <span class="sender" v-if="conversation.type === 'group'">
                  {{ conversation.lastMessage.senderName }}:
                </span>
                <span class="message" :class="{ 'message-type': conversation.lastMessage.type !== 'chat' }">
                  {{ formatLastMessage(conversation.lastMessage) }}
                </span>
              </div>
            </div>
            <div class="conversation-status">
              <div class="unread-badge" v-if="conversation.unreadCount > 0">
                {{ conversation.unreadCount > 99 ? '99+' : conversation.unreadCount }}
              </div>
              <i class="material-icons mute-icon" v-if="conversation.isMute">volume_off</i>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 协助请求列表 -->
      <div class="assistance-requests" v-else>
        <div 
          class="request-item" 
          v-for="request in assistanceRequests" 
          :key="request.requestId"
          @click="viewAssistanceRequest(request)"
        >
          <div class="avatar">
            <img 
              :src="userRole === 'engineer' ? request.requester.avatar : request.engineer.avatar || '/default-avatar.png'" 
              :alt="userRole === 'engineer' ? request.requester.name : request.engineer.name" 
            />
          </div>
          <div class="request-info">
            <div class="request-header">
              <h3>
                {{ userRole === 'engineer' ? request.requester.name : request.engineer.name }}
              </h3>
              <span class="time">{{ formatTime(request.createTime) }}</span>
            </div>
            <div class="task-info" v-if="request.taskId">
              {{ request.taskId }} - {{ request.taskTitle }}
            </div>
            <div class="description">
              {{ truncateText(request.description, 50) }}
            </div>
          </div>
          <div class="request-status" :class="getStatusClass(request.status)">
            {{ getStatusText(request.status) }}
          </div>
        </div>
      </div>
    </div>
    
    <div class="pagination" v-if="totalPages > 1">
      <button 
        :disabled="currentPage === 1" 
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage === totalPages" 
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>
    
    <div class="fab" @click="goToNewChat">
      <i class="material-icons">add</i>
    </div>
  </div>
</template>

<script>
import { getConversationList } from '@/api/chat'
import { getAssistanceList } from '@/api/assistance'

export default {
  name: 'ConversationList',
  data() {
    return {
      conversations: [],
      assistanceRequests: [],
      loading: true,
      error: null,
      currentPage: 1,
      pageSize: 20,
      total: 0,
      totalPages: 0,
      searchKeyword: '',
      filter: 'all',
      activeTab: 'chats',
      userRole: this.getUserRole()
    }
  },
  computed: {
    stickyConversations() {
      return this.conversations.filter(conv => conv.isSticky)
    },
    normalConversations() {
      return this.conversations.filter(conv => !conv.isSticky)
    }
  },
  created() {
    this.fetchConversations()
  },
  methods: {
    getUserRole() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      return user.role || ''
    },
    async fetchConversations() {
      this.loading = true
      this.error = null
      
      try {
        if (this.activeTab === 'chats') {
          await this.fetchChats()
        } else {
          await this.fetchAssistanceRequests()
        }
      } catch (error) {
        console.error(`获取${this.activeTab === 'chats' ? '会话' : '协助请求'}列表出错:`, error)
        this.error = `获取${this.activeTab === 'chats' ? '会话' : '协助请求'}列表失败，请检查网络连接`
      } finally {
        this.loading = false
      }
    },
    async fetchChats() {
      const params = {
        page: this.currentPage,
        size: this.pageSize,
        keyword: this.searchKeyword
      }
      
      if (this.filter === 'single' || this.filter === 'group') {
        params.type = this.filter
      } else if (this.filter === 'task') {
        params.isTaskRelated = true
      }
      
      const response = await getConversationList(params)
      
      if (response.code === 200) {
        this.conversations = response.data.list || []
        this.total = response.data.total
        this.totalPages = response.data.pages
        this.currentPage = response.data.current
      } else {
        this.error = response.message || '获取会话列表失败'
      }
    },
    async fetchAssistanceRequests() {
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchKeyword
        }
        
        // 根据用户角色设置查询参数
        if (this.userRole) {
          if (this.userRole === 'engineer') {
            params.engineerId = this.getCurrentUserId()
          } else if (this.userRole === 'sales') {
            params.requesterId = this.getCurrentUserId()
          }
          // 管理员可以查看所有协助请求，不需要特殊参数
        }
        
        const response = await getAssistanceList(params)
        
        if (response.code === 200) {
          // 处理协助请求数据，确保结构完整
          this.assistanceRequests = (response.data?.list || []).map(request => {
            return {
              ...request,
              requestId: request.requestId || request.id,
              taskTitle: request.taskTitle || request.title || '未命名任务',
              description: request.description || request.content || '',
              createTime: request.createTime || request.createDate || new Date().toISOString(),
              requester: request.requester || {
                id: request.requesterId || '',
                name: request.requesterName || '请求人',
                avatar: request.requesterAvatar || '/default-avatar.png'
              },
              engineer: request.engineer || {
                id: request.engineerId || '',
                name: request.engineerName || '工程师',
                avatar: request.engineerAvatar || '/default-avatar.png'
              },
              status: request.status || 'pending'
            }
          })
          
          this.total = response.data?.total || this.assistanceRequests.length
          this.totalPages = response.data?.pages || Math.ceil(this.total / this.pageSize)
          this.currentPage = response.data?.current || this.currentPage
        } else {
          this.error = response.message || '获取协助请求列表失败'
          this.assistanceRequests = []
        }
      } catch (error) {
        console.error('获取协助请求列表出错:', error)
        this.error = '获取协助请求列表失败，请检查网络连接'
        this.assistanceRequests = []
      }
    },
    formatLastMessage(lastMessage) {
      if (!lastMessage) return ''
      
      const prefixes = {
        task: '[任务] ',
        system: '[系统] '
      }
      
      const prefix = prefixes[lastMessage.type] || ''
      return prefix + lastMessage.content
    },
    formatTime(timeString) {
      if (!timeString) return ''
      
      const date = new Date(timeString)
      const now = new Date()
      const diff = Math.floor((now - date) / 1000)
      
      // 今天的消息只显示时间
      if (diff < 86400 && date.getDate() === now.getDate()) {
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
      }
      
      // 一周内的消息显示星期
      if (diff < 7 * 86400) {
        const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
        return days[date.getDay()]
      }
      
      // 更早的消息显示日期
      return `${date.getMonth() + 1}/${date.getDate()}`
    },
    truncateText(text, maxLength) {
      if (!text) return ''
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
    },
    getStatusClass(status) {
      switch (status) {
        case 'pending':
          return 'status-pending'
        case 'accepted':
          return 'status-accepted'
        case 'rejected':
          return 'status-rejected'
        case 'cancelled':
          return 'status-cancelled'
        case 'completed':
          return 'status-completed'
        default:
          return ''
      }
    },
    getStatusText(status) {
      switch (status) {
        case 'pending':
          return '待处理'
        case 'accepted':
          return '已接受'
        case 'rejected':
          return '已拒绝'
        case 'cancelled':
          return '已取消'
        case 'completed':
          return '已完成'
        default:
          return status
      }
    },
    handleSearch() {
      clearTimeout(this.searchTimer)
      this.searchTimer = setTimeout(() => {
        this.currentPage = 1
        this.fetchConversations()
      }, 500)
    },
    clearSearch() {
      this.searchKeyword = ''
      this.currentPage = 1
      this.fetchConversations()
    },
    setFilter(filter) {
      this.filter = filter
      this.currentPage = 1
      this.fetchConversations()
    },
    setActiveTab(tab) {
      this.activeTab = tab
      this.currentPage = 1
      this.filter = 'all'
      this.fetchConversations()
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
        this.fetchConversations()
      }
    },
    openConversation(conversation) {
      this.$router.push(`/chat/${conversation.conversationId}`)
    },
    viewAssistanceRequest(request) {
      this.$router.push(`/assistance/${request.requestId}`)
    },
    goToNewChat() {
      this.$router.push('/new-chat')
    },
    
    getCurrentUserId() {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      return user.workerId || user.userId || user.id
    },
    
    handleRetry() {
      this.error = null
      this.fetchConversations()
    }
  }
}
</script>

<style scoped>
.conversation-list {
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
}

.header {
  background-color: #2196f3;
  color: white;
  padding: 10px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.left {
  display: flex;
  align-items: center;
}

.left i {
  margin-right: 16px;
}

.right {
  display: flex;
}

.right i {
  margin-left: 16px;
}

.search-bar {
  padding: 10px;
  background-color: #fff;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  padding: 8px 12px;
  border-radius: 4px;
}

.search-input input {
  flex: 1;
  border: none;
  outline: none;
  background-color: transparent;
  margin: 0 10px;
  font-size: 16px;
}

.filters {
  display: flex;
  background-color: white;
  padding: 0 10px;
  border-bottom: 1px solid #e0e0e0;
}

.filter {
  padding: 10px;
  margin: 0 5px;
  cursor: pointer;
}

.filter.active {
  color: #2196f3;
  border-bottom: 2px solid #2196f3;
}

.tab-bar {
  display: flex;
  background-color: white;
  padding: 0 10px;
  border-bottom: 1px solid #e0e0e0;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  cursor: pointer;
}

.tab.active {
  color: #2196f3;
  border-bottom: 2px solid #2196f3;
}

.conversations, .assistance-requests {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f5f5;
}

.sticky-conversations {
  background-color: #f5f5f5;
  padding-bottom: 5px;
}

.conversation-item, .request-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: white;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
}

.sticky-conversations .conversation-item {
  margin-bottom: 2px;
  background-color: #e3f2fd;
}

.pin-indicator {
  position: absolute;
  top: 8px;
  right: 8px;
  font-size: 12px;
  color: #757575;
}

.avatar {
  position: relative;
  margin-right: 15px;
}

.avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.task-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #ff9800;
  border: 2px solid white;
}

.conversation-info, .request-info {
  flex: 1;
  overflow: hidden;
}

.conversation-header, .request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conversation-header h3, .request-header h3 {
  margin: 0;
  font-size: 16px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time {
  font-size: 12px;
  color: #9e9e9e;
  white-space: nowrap;
  margin-left: 5px;
}

.last-message, .description {
  font-size: 14px;
  color: #757575;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-info {
  font-size: 12px;
  color: #ff9800;
  margin-bottom: 4px;
}

.sender {
  font-weight: 500;
}

.message-type {
  color: #2196f3;
}

.conversation-status {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.unread-badge {
  background-color: #f44336;
  color: white;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  margin-bottom: 5px;
}

.mute-icon {
  font-size: 16px;
  color: #9e9e9e;
}

.request-status {
  padding: 4px 8px;
  border-radius: 10px;
  font-size: 12px;
  white-space: nowrap;
}

.status-pending {
  background-color: #ffecb3;
  color: #ff6f00;
}

.status-accepted {
  background-color: #e3f2fd;
  color: #1976d2;
}

.status-rejected {
  background-color: #ffebee;
  color: #d32f2f;
}

.status-cancelled {
  background-color: #f5f5f5;
  color: #757575;
}

.status-completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.loading,
.error-message,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  text-align: center;
  flex: 1;
}

.loading .spinner {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #2196f3;
  animation: spin 1.5s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message i,
.empty-state i {
  font-size: 48px;
  color: #9e9e9e;
  margin-bottom: 10px;
}

.error-message p {
  color: #f44336;
  margin-bottom: 16px;
}

.retry-button,
.empty-state button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.retry-button:hover,
.empty-state button:hover {
  background-color: #1976d2;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: white;
  border-top: 1px solid #e0e0e0;
}

.pagination button {
  padding: 8px 12px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #e0e0e0;
  color: #9e9e9e;
  cursor: not-allowed;
}

.fab {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #2196f3;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.3);
}
</style> 