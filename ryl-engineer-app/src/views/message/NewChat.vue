<template>
  <div class="new-chat-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>新建聊天</h1>
    </header>

    <!-- 搜索框 -->
    <div class="search-container">
      <div class="search-input">
        <i class="icon-search"></i>
        <input 
          type="text" 
          v-model="searchText" 
          placeholder="搜索联系人..." 
          @input="handleSearch"
        />
        <i v-if="searchText" class="icon-x" @click="clearSearch"></i>
      </div>
    </div>

    <!-- 加载中状态 -->
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>加载联系人中...</p>
    </div>

    <!-- 错误提示 -->
    <div class="error-message" v-else-if="error">
      <i class="icon-alert-circle"></i>
      <p>{{ error }}</p>
      <button @click="fetchContacts">重试</button>
    </div>

    <!-- 联系人列表 -->
    <div class="contacts-container" v-else>
      <!-- 联系人分组 -->
      <div v-for="(group, groupName) in groupedContacts" :key="groupName" class="contact-group">
        <div class="group-header">{{ groupName }}</div>
        
        <div v-for="contact in group" :key="contact.id" class="contact-item" @click="startChat(contact)">
          <div class="contact-avatar">
            <img :src="contact.avatar || '/default-avatar.png'" alt="头像">
            <div class="status-indicator" :class="{ online: contact.status === 1 }"></div>
          </div>
          <div class="contact-info">
            <div class="contact-name">{{ contact.name }}</div>
            <div class="contact-role">{{ contact.department || contact.role || '' }}</div>
          </div>
        </div>
      </div>

      <!-- 无搜索结果 -->
      <div v-if="Object.keys(groupedContacts).length === 0" class="empty-state">
        <i class="icon-user-x"></i>
        <p>未找到匹配的联系人</p>
      </div>
    </div>
    
    <!-- 分页控件 -->
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
  </div>
</template>

<script>
import { getContactsList } from '@/api/contacts'
import { createConversation } from '@/api/chat'

export default {
  name: 'NewChatPage',
  data() {
    return {
      searchText: '',
      allContacts: [],
      filteredContacts: [],
      groupedContacts: {},
      loading: true,
      error: null,
      creatingChat: false,
      currentPage: 1,
      pageSize: 20,
      totalPages: 0,
      total: 0,
      searchTimer: null
    }
  },
  created() {
    // 检查URL查询参数中是否有预选的联系人
    const contactId = this.$route.query.contactId
    if (contactId) {
      this.preselectedContact = parseInt(contactId)
    }
    
    // 获取联系人列表
    this.fetchContacts()
  },
  methods: {
    async fetchContacts() {
      this.loading = true
      this.error = null
      
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          keyword: this.searchText
        }
        
        const response = await getContactsList(params)
        
        if (response.code === 200 && response.data) {
          this.allContacts = response.data.list || []
          this.filteredContacts = [...this.allContacts]
          this.total = response.data.total
          this.totalPages = response.data.pages || Math.ceil(this.total / this.pageSize)
          
          // 组织联系人分组
          this.organizeContactsByDepartment()
          
          // 如果有预选联系人，直接开始聊天
          if (this.preselectedContact) {
            const contact = this.allContacts.find(c => c.id === this.preselectedContact)
            if (contact) {
              this.startChat(contact)
              this.preselectedContact = null
            }
          }
        } else {
          this.error = response.message || '获取联系人失败'
        }
      } catch (error) {
        console.error('获取联系人出错', error)
        this.error = '获取联系人失败，请检查网络连接'
      } finally {
        this.loading = false
      }
    },
    
    organizeContactsByDepartment() {
      const groups = {}
      
      this.filteredContacts.forEach(contact => {
        const department = contact.department || '其他部门'
        
        if (!groups[department]) {
          groups[department] = []
        }
        
        groups[department].push(contact)
      })
      
      this.groupedContacts = groups
    },
    
    handleSearch() {
      // 使用防抖处理搜索
      clearTimeout(this.searchTimer)
      this.searchTimer = setTimeout(() => {
        this.currentPage = 1
        this.fetchContacts()
      }, 500)
    },
    
    clearSearch() {
      this.searchText = ''
      this.currentPage = 1
      this.fetchContacts()
    },
    
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
        this.fetchContacts()
      }
    },
    
    async startChat(contact) {
      if (this.creatingChat) return
      
      this.creatingChat = true
      
      try {
        // 获取当前用户信息
        const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
        const currentUserId = currentUser.id
        
        if (!currentUserId) {
          this.$router.push('/login')
        return
      }
      
        // 准备会话创建数据
        const conversationData = {
          type: 'single', // 单聊
          memberIds: [contact.id], // 只包含选中的联系人
          isTaskRelated: false
        }
        
        // 创建会话
        const response = await createConversation(conversationData)
        
        if (response.code === 200 && response.data) {
          // 跳转到聊天详情页
          this.$router.push(`/chat/${response.data.conversationId}`)
        } else {
          alert(response.message || '创建会话失败')
    }
      } catch (error) {
        console.error('创建会话出错', error)
        alert('创建会话失败，请重试')
      } finally {
        this.creatingChat = false
      }
    },
    
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.new-chat-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
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
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.search-container {
  padding: 12px 16px;
  background-color: #fff;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #f3f4f6;
  border-radius: 8px;
  padding: 8px 12px;
}

.icon-search::before {
  content: '\f002';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #6b7280;
  margin-right: 8px;
}

.search-input input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
}

.icon-x::before {
  content: '\f00d';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #6b7280;
  margin-left: 8px;
}

.contacts-container {
  padding: 0 16px;
}

.contact-group {
  margin-bottom: 16px;
}

.group-header {
  padding: 8px 0;
  font-weight: 600;
  color: #6b7280;
  font-size: 14px;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.contact-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
}

.contact-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 4px;
}

.contact-role {
  font-size: 14px;
  color: #6b7280;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #999;
}

.icon-user-x::before {
  content: '\f235';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 48px;
  margin-bottom: 16px;
}

.loading,
.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  text-align: center;
}

.spinner {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-top: 3px solid #3498db;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message button {
  margin-top: 16px;
  padding: 8px 16px;
  border: none;
  background-color: #3498db;
  color: white;
  border-radius: 4px;
}

.status-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #ccc;
  border: 2px solid white;
}

.status-indicator.online {
  background-color: #4CAF50;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: white;
  margin: 0 8px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination span {
  margin: 0 8px;
}
</style>
