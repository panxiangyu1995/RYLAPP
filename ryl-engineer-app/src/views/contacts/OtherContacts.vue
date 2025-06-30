<template>
  <div class="other-contacts-container">
    <!-- 搜索框 -->
    <div class="search-box">
      <input 
        type="text" 
        v-model="searchText" 
        placeholder="搜索联系人..." 
        @input="handleSearch"
      >
      <i class="search-icon" @click="loadContacts"></i>
    </div>
    
    <!-- 联系人列表 -->
    <div class="contacts-list">
      <div v-if="loading" class="loading-spinner">加载中...</div>
      <div v-else-if="filteredContacts.length === 0" class="no-data">
        没有找到联系人
      </div>
      <template v-else>
        <!-- 联系人卡片 -->
        <div 
          v-for="contact in filteredContacts" 
          :key="contact.id" 
          class="contact-card"
          @click="viewContactDetail(contact)"
        >
          <div class="avatar">
            <img :src="contact.avatar || defaultAvatar" alt="头像">
            <span :class="['status-dot', getStatusClass(contact.status)]"></span>
          </div>
          <div class="contact-info">
            <div class="name-role">
              <h3>{{ contact.name }}</h3>
              <span class="role-tag">{{ contact.role }}</span>
            </div>
            <p class="contact-meta">
              <span>{{ contact.department || '未设置部门' }}</span>
            </p>
          </div>
          <div class="contact-action">
            <i class="action-icon"></i>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOtherContacts } from '@/api/contacts'
import defaultAvatar from '@/assets/images/company-logo.png'

export default {
  name: 'OtherContacts',
  setup() {
    const router = useRouter()
    const contacts = ref([])
    const loading = ref(true)
    const searchText = ref('')
    
    // 过滤联系人
    const filteredContacts = computed(() => {
      if (!searchText.value) return contacts.value
      const keyword = searchText.value.toLowerCase()
      return contacts.value.filter(contact => 
        contact.name.toLowerCase().includes(keyword) || 
        (contact.department && contact.department.toLowerCase().includes(keyword))
      )
    })
    
    // 加载非工程师联系人列表
    const loadContacts = async () => {
      loading.value = true
      try {
        const params = {
          page: 1,
          size: 100, // 较大的值，确保加载全部联系人
          keyword: searchText.value
        }
        console.log('请求其它联系人列表，参数:', params)
        const res = await getOtherContacts(params)
        
        if (res.code === 200) {
          // 处理分页数据结构
          if (res.data && res.data.list && Array.isArray(res.data.list)) {
            contacts.value = res.data.list
            console.log(`成功加载${contacts.value.length}个其它联系人`)
          } else if (Array.isArray(res.data)) {
            // 兼容直接返回数组的情况
            contacts.value = res.data
            console.log(`成功加载${contacts.value.length}个其它联系人`)
          } else {
            contacts.value = []
            console.warn('其它联系人数据为空或格式不符合预期:', res.data)
          }
        } else {
          contacts.value = []
          console.error('获取其它联系人失败:', res.message || '未知错误')
        }
      } catch (error) {
        contacts.value = []
        console.error('获取其它联系人请求异常:', error)
        
        // 详细错误信息记录
        if (error.status) {
          console.error(`HTTP错误: ${error.status}`, error.message)
        } else if (error.isNetworkError) {
          console.error('网络连接错误，请检查网络')
        } else if (error.isTimeout) {
          console.error('请求超时，服务器响应时间过长')
        }
      } finally {
        loading.value = false
      }
    }
    
    // 查看联系人详情
    const viewContactDetail = (contact) => {
      router.push({
        name: 'ContactDetail',
        params: { id: contact.id },
        query: { type: 'other' }
      })
    }
    
    // 搜索处理
    const handleSearch = () => {
      if (searchText.value.length > 0) {
        // 本地过滤已加载的联系人
        // 如果需要从服务器搜索，可以调用 loadContacts()
      }
    }
    
    // 获取状态样式
    const getStatusClass = (status) => {
      switch (status) {
        case 1: return 'online'
        case 2: return 'busy'
        default: return 'offline'
      }
    }
    
    onMounted(() => {
      loadContacts()
    })
    
    return {
      contacts,
      loading,
      searchText,
      filteredContacts,
      handleSearch,
      viewContactDetail,
      getStatusClass,
      defaultAvatar
    }
  }
}
</script>

<style scoped>
.other-contacts-container {
  padding: 16px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.search-box {
  position: relative;
  margin-bottom: 16px;
}

.search-box input {
  width: 100%;
  padding: 10px 15px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  background-color: #fff;
  font-size: 14px;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  cursor: pointer;
  width: 20px;
  height: 20px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23999"><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>');
  background-repeat: no-repeat;
  background-position: center;
}

.contacts-list {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.contact-card {
  display: flex;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
}

.avatar {
  position: relative;
  width: 50px;
  height: 50px;
  border-radius: 25px;
  overflow: hidden;
  margin-right: 12px;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #fff;
}

.status-dot.online {
  background-color: #4CAF50;
}

.status-dot.busy {
  background-color: #FF9800;
}

.status-dot.offline {
  background-color: #9E9E9E;
}

.contact-info {
  flex: 1;
}

.name-role {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.name-role h3 {
  font-size: 16px;
  font-weight: 500;
  margin: 0;
  margin-right: 8px;
}

.role-tag {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: #E3F2FD;
  color: #1976D2;
}

.contact-meta {
  font-size: 13px;
  color: #757575;
  margin: 0;
}

.contact-action {
  margin-left: 16px;
}

.loading-spinner, .no-data {
  text-align: center;
  padding: 30px 0;
  color: #757575;
}
</style> 