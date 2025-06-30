<template>
  <div class="contacts-list">
    <div class="search-bar">
      <div class="search-input">
        <i class="material-icons">search</i>
        <input 
          type="text" 
          placeholder="搜索联系人" 
          v-model="searchKeyword"
          @input="handleSearch"
        />
        <i class="material-icons" v-if="searchKeyword" @click="clearSearch">close</i>
      </div>
    </div>
    
    <div class="filters">
      <div class="filter" :class="{ active: filter === 'all' }" @click="setFilter('all')">全部</div>
      <div class="filter" :class="{ active: filter === 'online' }" @click="setFilter('online')">在线</div>
      <div class="filter" :class="{ active: filter === 'department' }" @click="toggleDepartmentDropdown">
        部门
        <i class="material-icons">{{ showDepartments ? 'keyboard_arrow_up' : 'keyboard_arrow_down' }}</i>
      </div>
    </div>
    
    <div class="departments-dropdown" v-if="showDepartments">
      <div 
        class="department-option" 
        v-for="dept in departments" 
        :key="dept"
        :class="{ active: selectedDepartment === dept }"
        @click="selectDepartment(dept)"
      >
        {{ dept }}
      </div>
    </div>
    
    <div class="loading" v-if="loading">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <div class="error-message" v-else-if="error">
      <i class="material-icons">error</i>
      <p>{{ error }}</p>
    </div>
    
    <div class="no-contacts" v-else-if="contacts.length === 0">
      <i class="material-icons">people_alt</i>
      <p>没有找到联系人</p>
    </div>
    
    <div class="contacts" v-else>
      <div 
        class="contact-item" 
        v-for="contact in contacts" 
        :key="contact.id"
        @click="viewContactDetail(contact.id)"
      >
        <div class="avatar">
          <img :src="contact.avatar || '/default-avatar.png'" :alt="contact.name" />
          <div class="status-indicator" :class="{ online: contact.status === 1 }"></div>
        </div>
        <div class="contact-info">
          <h3>{{ contact.name }}</h3>
          <p>{{ contact.department }} | {{ contact.workId }}</p>
        </div>
        <div class="actions">
          <i class="material-icons action-icon" @click.stop="callContact(contact)">phone</i>
          <i class="material-icons action-icon" @click.stop="messageContact(contact)">message</i>
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
  </div>
</template>

<script>
import { getContactsList } from '@/api/contacts'

export default {
  name: 'ContactsList',
  data() {
    return {
      contacts: [],
      loading: true,
      error: null,
      currentPage: 1,
      pageSize: 20,
      total: 0,
      totalPages: 0,
      searchKeyword: '',
      filter: 'all',
      showDepartments: false,
      selectedDepartment: '',
      departments: ['技术部', '销售部', '管理部', '仓库部']
    }
  },
  created() {
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
          keyword: this.searchKeyword
        }
        
        if (this.filter === 'online') {
          params.status = 1
        }
        
        if (this.selectedDepartment) {
          params.department = this.selectedDepartment
        }
        
        const response = await getContactsList(params)
        
        if (response.code === 200) {
          this.contacts = response.data.list || []
          this.total = response.data.total
          this.totalPages = response.data.pages
          this.currentPage = response.data.current
        } else {
          this.error = response.message || '获取联系人列表失败'
        }
      } catch (error) {
        console.error('获取联系人列表出错:', error)
        this.error = '获取联系人列表失败，请检查网络连接'
      } finally {
        this.loading = false
      }
    },
    viewContactDetail(contactId) {
      this.$router.push(`/contacts/detail/${contactId}`)
    },
    callContact(contact) {
      if (contact.mobile) {
        window.location.href = `tel:${contact.mobile}`
      } else {
        alert('没有该联系人的电话号码')
      }
    },
    messageContact(contact) {
      // 转到聊天页面
      this.$router.push(`/chat/new?contactId=${contact.id}`)
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
      this.searchKeyword = ''
      this.currentPage = 1
      this.fetchContacts()
    },
    changePage(page) {
      if (page >= 1 && page <= this.totalPages) {
        this.currentPage = page
        this.fetchContacts()
      }
    },
    setFilter(filter) {
      this.filter = filter
      this.currentPage = 1
      
      if (filter !== 'department') {
        this.showDepartments = false
      }
      
      if (filter === 'all') {
        this.selectedDepartment = ''
      }
      
      this.fetchContacts()
    },
    toggleDepartmentDropdown() {
      this.filter = 'department'
      this.showDepartments = !this.showDepartments
    },
    selectDepartment(department) {
      this.selectedDepartment = department
      this.showDepartments = false
      this.currentPage = 1
      this.fetchContacts()
    }
  }
}
</script>

<style scoped>
.contacts-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 10px;
}

.search-bar {
  margin-bottom: 10px;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: white;
  padding: 8px 12px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-input input {
  flex: 1;
  border: none;
  outline: none;
  margin: 0 10px;
  font-size: 16px;
}

.filters {
  display: flex;
  margin-bottom: 10px;
}

.filter {
  flex: 1;
  text-align: center;
  padding: 8px;
  background-color: white;
  margin-right: 5px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter:last-child {
  margin-right: 0;
}

.filter.active {
  background-color: #2196f3;
  color: white;
}

.departments-dropdown {
  background-color: white;
  border-radius: 4px;
  margin-bottom: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.department-option {
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.department-option:last-child {
  border-bottom: none;
}

.department-option.active {
  background-color: #e3f2fd;
  color: #2196f3;
}

.contacts {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: white;
  margin-bottom: 8px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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

.status-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #9e9e9e;
  border: 2px solid white;
}

.status-indicator.online {
  background-color: #4caf50;
}

.contact-info {
  flex: 1;
}

.contact-info h3 {
  margin: 0;
  font-size: 16px;
}

.contact-info p {
  margin: 5px 0 0;
  font-size: 14px;
  color: #757575;
}

.actions {
  display: flex;
}

.action-icon {
  margin-left: 15px;
  color: #2196f3;
}

.loading,
.error-message,
.no-contacts {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  text-align: center;
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
.no-contacts i {
  font-size: 48px;
  color: #9e9e9e;
  margin-bottom: 10px;
}

.error-message p {
  color: #f44336;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: white;
  margin-top: 10px;
  border-radius: 4px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
</style> 