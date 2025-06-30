<template>
  <div class="messages-page">
    <!-- 顶部导航 -->
    <header class="header">
      <h1>消息</h1>
      <div class="header-icons">
        <i class="icon-plus-circle" @click="createNewChat"></i>
        <i class="icon-settings" @click="goToSettings"></i>
      </div>
    </header>

    <!-- 消息分类 -->
    <div class="message-filter">
      <div class="filter-buttons">
        <button 
          v-for="(type, index) in messageTypes" 
          :key="index"
          :class="['filter-btn', { active: selectedMessageType === type.value }]"
          @click="selectedMessageType = type.value"
        >
          {{ type.label }}
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="message-list">
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>加载消息中...</p>
      </div>
      <div v-else-if="error" class="error-state">
        <i class="icon-alert-circle"></i>
        <p>{{ error }}</p>
        <button class="retry-button" @click="retryFetch">重新加载</button>
      </div>
      <div v-else-if="messages.length === 0" class="empty-state">
        <i class="icon-inbox"></i>
        <p>暂无消息</p>
        <button v-if="selectedMessageType === 'chat' || selectedMessageType === 'all'" 
          @click="createNewChat" class="action-button">
          开始新对话
        </button>
      </div>
      <template v-else>
        <div 
          v-for="message in filteredMessages" 
          :key="message.id" 
          class="message-card"
          @click="viewMessageDetail(message.id, message.type)"
        >
          <div class="message-content">
            <div :class="['message-icon', message.iconClass]">
              <i :class="message.icon"></i>
            </div>
            <div class="message-details">
              <div class="message-header">
                <div>
                  <h3 class="message-title">{{ message.title }}</h3>
                  <p class="message-text">{{ message.content }}</p>
                </div>
                <span class="message-time">{{ formatTime(message.time) }}</span>
              </div>
              <div class="message-actions" v-if="message.actions">
                <button 
                  v-for="(action, i) in message.actions" 
                  :key="i" 
                  class="action-btn"
                  :class="{ primary: action.primary }"
                  @click.stop="handleAction(action.type, message.id)"
                >
                  {{ action.label }}
                </button>
              </div>
              <div class="message-link" v-else>
                查看详情
              </div>
            </div>
          </div>
          <div class="unread-indicator" v-if="!message.isRead"></div>
        </div>
      </template>
    </div>
    
    <!-- 分页控制 -->
    <div class="pagination" v-if="total > 0 && Math.ceil(total / size) > 1">
      <button 
        :disabled="page === 1" 
        @click="prevPage" 
        class="page-btn"
      >
        上一页
      </button>
      <span class="page-info">{{ page }} / {{ Math.ceil(total / size) }}</span>
      <button 
        :disabled="page >= Math.ceil(total / size)" 
        @click="nextPage" 
        class="page-btn"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
import { getConversationList, getMessageList } from '@/api/chat'
import { getAnnouncementList } from '@/api/announcement'
import { getAssistanceList } from '@/api/assistance'
// import { UnifiedMessage } from '@/types/message' // 如需类型提示可在TS文件中使用

export default {
  name: 'MessagesPage',
  data() {
    return {
      selectedMessageType: 'all',
      messageTypes: [
        { label: '全部', value: 'all' },
        { label: '系统通知', value: 'system' },
        { label: '任务消息', value: 'task' },
        { label: '聊天消息', value: 'chat' },
        { label: '公告', value: 'announcement' },
        { label: '协作', value: 'assistance' }
      ],
      messages: [], // 用于聚合后的消息
      loading: false,
      page: 1,
      size: 20,
      total: 0,
      error: null
    }
  },
  computed: {
    filteredMessages() {
      if (this.selectedMessageType === 'all') {
        return this.messages;
      }
      return this.messages.filter(message => message.type === this.selectedMessageType);
    }
  },
  methods: {
    async fetchAllMessages() {
      this.loading = true;
      this.error = null;
      try {
        // 1. 拉取chat相关消息（只取每个会话的最后一条消息）
        const chatPromise = getConversationList({ page: 1, size: 20 });
        // 2. 拉取公告
        const announcementPromise = getAnnouncementList({ page: 1, size: 20 });
        // 3. 拉取协助请求 - 注意:目前该API返回500，临时处理
        const assistancePromise = getAssistanceList({ page: 1, size: 20 })
          .catch(err => {
            console.warn('协助请求API暂时不可用:', err);
            return { code: 200, data: { list: [] } }; // 返回空数组，不影响其他功能
          });
        
        // 并行请求所有数据
        const [chatRes, announcementRes, assistanceRes] = await Promise.all([
          chatPromise, 
          announcementPromise,
          assistancePromise
        ]);
        
        // 处理各类消息数据
        const chatMessages = this.processChatMessages(chatRes);
        const announcementMessages = this.processAnnouncementMessages(announcementRes);
        const assistanceMessages = this.processAssistanceMessages(assistanceRes);
        
        // 合并并按时间倒序排列所有消息
        const all = [...chatMessages, ...announcementMessages, ...assistanceMessages]
          .sort((a, b) => new Date(b.time || 0).getTime() - new Date(a.time || 0).getTime());
        
        this.messages = all;
        this.total = all.length;
      } catch (e) {
        console.error('消息聚合失败', e);
        this.error = '获取消息失败，请检查网络连接并重试';
      } finally {
        this.loading = false;
      }
    },
    
    // 处理聊天消息数据
    processChatMessages(chatRes) {
      if (!chatRes || !chatRes.code || chatRes.code !== 200 || !chatRes.data) {
        console.error('会话数据响应无效');
        return [];
      }
      
      return (chatRes.data?.list || []).map(conv => {
        const msg = conv.lastMessage || {};
        return {
          id: msg.messageId || conv.conversationId,
          type: msg.type || 'chat',
          title: conv.name || '会话',
          content: msg.content || '',
          time: msg.sendTime || conv.updateTime || new Date().toISOString(),
          icon: 'icon-user-circle',
          iconClass: conv.isTaskRelated ? 'green' : 'teal',
          isRead: (conv.unreadCount === 0),
          sourceType: 'chat',
          extraData: { conversationId: conv.conversationId }
        }
      });
    },
    
    // 处理公告消息数据
    processAnnouncementMessages(announcementRes) {
      if (!announcementRes || !announcementRes.code || announcementRes.code !== 200 || !announcementRes.data) {
        console.error('公告数据响应无效');
        return [];
      }
      
      return (announcementRes.data?.list || []).map(a => ({
        id: String(a.id),
        type: 'announcement',
        title: a.title || '系统公告',
        content: a.content || '',
        time: a.publishTime || a.createTime || new Date().toISOString(),
        icon: 'icon-bell',
        iconClass: 'blue',
        isRead: !!a.isRead,
        sourceType: 'announcement',
        extraData: { announcementId: a.id }
      }));
    },
    
    // 处理协助请求数据
    processAssistanceMessages(assistanceRes) {
      if (!assistanceRes || !assistanceRes.code || assistanceRes.code !== 200 || !assistanceRes.data) {
        console.error('协助请求数据响应无效');
        return [];
      }
      
      return (assistanceRes.data?.list || []).map(a => ({
        id: a.requestId || String(a.id),
        type: 'assistance',
        title: a.title || a.taskTitle || '协助请求',
        content: a.content || a.description || '',
        time: a.createTime || new Date().toISOString(),
        icon: 'icon-users',
        iconClass: 'purple',
        isRead: a.status === 'completed',
        sourceType: 'assistance',
        extraData: { requestId: a.requestId || String(a.id) }
      }));
    },
    
    // 重试加载
    retryFetch() {
      this.fetchAllMessages();
    },
    
    // 格式化时间显示
    formatTime(timestamp) {
      if (!timestamp) return '';
      
      const date = new Date(timestamp);
      if (isNaN(date.getTime())) return '';
      
      const now = new Date();
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      const yesterday = new Date(today);
      yesterday.setDate(yesterday.getDate() - 1);
      
      if (date >= today) {
        // 今天，显示时间
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
      } else if (date >= yesterday) {
        // 昨天
        return '昨天';
      } else {
        // 一周以内显示星期几，否则显示日期
        const daysDiff = Math.floor((today - date) / (1000 * 60 * 60 * 24));
        if (daysDiff < 7) {
          const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
          return weekdays[date.getDay()];
        } else {
          return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' });
        }
      }
    },
    
    // 下一页
    nextPage() {
      if (this.page < Math.ceil(this.total / this.size)) {
        this.page++;
        this.fetchAllMessages();
      }
    },
    
    // 上一页
    prevPage() {
      if (this.page > 1) {
        this.page--;
        this.fetchAllMessages();
      }
    },
    
    viewMessageDetail(messageId, messageType) {
      // 根据消息类型跳转到不同页面
      switch (messageType) {
        case 'chat':
          this.$router.push(`/chat/${messageId}`);
          break;
        case 'announcement':
          this.$router.push(`/announcement/${messageId}`);
          break;
        case 'assistance':
          this.$router.push(`/assistance/${messageId}`);
          break;
        default:
          // 默认行为
          this.$router.push({
            path: `/message/${messageId}`,
            query: { type: messageType }
          });
      }
    },
    handleAction(actionType, messageId) {
      console.log('处理操作:', actionType, messageId);
      // 后续实现处理操作功能
    },
    goToSettings() {
      this.$router.push('/settings');
    },
    createNewChat() {
      this.$router.push('/new-chat');
    }
  },
  created() {
    this.fetchAllMessages();
  },
  watch: {
    selectedMessageType() {
      // 可选：如需分页可在此触发fetchAllMessages
    }
  }
}
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
}

.header-icons {
  display: flex;
  gap: 16px;
}

.icon-more::before {
  content: '\f141';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.icon-settings::before {
  content: '\f013';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.icon-plus-circle::before {
  content: '\f055';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.message-filter {
  background-color: #fff;
  padding: 12px 16px;
  margin-top: 8px;
}

.filter-buttons {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 4px;
}

.filter-btn {
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #666;
  border-radius: 9999px;
  font-size: 14px;
  border: none;
  white-space: nowrap;
}

.filter-btn.active {
  background-color: #3b82f6;
  color: #fff;
}

.message-list {
  padding: 12px 16px;
}

.message-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 16px;
  margin-bottom: 12px;
  position: relative;
}

.message-content {
  display: flex;
}

.message-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.message-icon.blue {
  background-color: #dbeafe;
  color: #3b82f6;
}

.message-icon.green {
  background-color: #d1fae5;
  color: #10b981;
}

.message-icon.purple {
  background-color: #f3e8ff;
  color: #8b5cf6;
}

.message-icon.yellow {
  background-color: #fef3c7;
  color: #d97706;
}

.message-icon.teal {
  background-color: #ccfbf1;
  color: #14b8a6;
}

.icon-bell::before {
  content: '\f0f3';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-tasks::before {
  content: '\f0ae';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-users::before {
  content: '\f0c0';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-warehouse::before {
  content: '\f494';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-user-circle::before {
  content: '\f2bd';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.message-details {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
}

.message-title {
  font-weight: 600;
  font-size: 16px;
}

.message-text {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.message-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.message-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.action-btn {
  font-size: 14px;
  background: none;
  border: none;
  color: #666;
  padding: 0;
}

.action-btn.primary {
  color: #3b82f6;
}

.message-link {
  font-size: 14px;
  color: #3b82f6;
  margin-top: 8px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  text-align: center;
}

.empty-state i {
  font-size: 48px;
  color: #95a5a6;
  margin-bottom: 16px;
}

.retry-button, .action-button {
  margin-top: 16px;
  padding: 8px 16px;
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.unread-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e74c3c;
  position: absolute;
  right: 16px;
  top: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
  padding: 8px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  margin: 0 16px;
  font-size: 14px;
  color: #666;
}

/* 加载中状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  text-align: center;
}

.spinner {
  border: 3px solid rgba(0, 0, 0, 0.1);
  border-top: 3px solid #4a90e2;
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

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
  text-align: center;
}

.error-state i {
  font-size: 48px;
  color: #e74c3c;
  margin-bottom: 16px;
}
</style> 