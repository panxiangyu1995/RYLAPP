<template>
  <div class="message-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>消息详情</h1>
    </header>

    <div class="message-container">
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!message" class="error">未找到消息详情</div>
      <template v-else>
        <!-- 消息头部 -->
        <div class="message-header">
          <div :class="['message-icon', message.iconClass]">
            <i :class="message.icon"></i>
          </div>
          <div class="message-title-container">
            <h2 class="message-title">{{ message.title }}</h2>
            <div class="message-meta">
              <span class="message-time">{{ formatTime(message.time) }}</span>
              <span class="message-type">{{ getMessageTypeLabel(message.type) }}</span>
            </div>
          </div>
        </div>

        <!-- 消息内容 -->
        <div class="message-content">
          <p class="message-text">{{ message.content }}</p>
          
          <div v-if="message.details" class="message-details">
            <div v-for="(detail, index) in message.details" :key="index" class="detail-item">
              <span class="detail-label">{{ detail.label }}:</span>
              <span class="detail-value">{{ detail.value }}</span>
            </div>
          </div>
        </div>

        <!-- 相关任务 -->
        <div v-if="message.relatedTask" class="related-task">
          <h3 class="section-title">相关任务</h3>
          <div class="task-card" @click="viewTask(message.relatedTask.id)">
            <div class="task-header">
              <span class="task-id">{{ message.relatedTask.id }}</span>
              <span :class="['task-status', message.relatedTask.statusClass]">
                {{ message.relatedTask.status }}
              </span>
            </div>
            <div class="task-info">
              <div class="task-name">{{ message.relatedTask.name }}</div>
              <div class="task-customer">{{ message.relatedTask.customer }}</div>
            </div>
            <div class="task-footer">
              <div class="task-date">
                <i class="icon-calendar"></i>
                <span>{{ message.relatedTask.date }}</span>
              </div>
              <div class="task-link">查看任务 <i class="icon-arrow-right"></i></div>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div v-if="message.actions" class="message-actions">
          <button 
            v-for="(action, i) in message.actions" 
            :key="i" 
            class="action-btn"
            :class="{ primary: action.primary }"
            @click="handleAction(action.type)"
          >
            {{ action.label }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { getMessageList } from '@/api/chat'
import { getAnnouncementDetail, markAnnouncementRead } from '@/api/announcement'
import { getAssistanceDetail } from '@/api/assistance'

export default {
  name: 'MessageDetailPage',
  props: {
    type: {
      type: String,
      default: null
    }
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const goBack = () => { router.back() }
    return { goBack, route }
  },
  data() {
    return {
      message: null,
      loading: false
    }
  },
  methods: {
    getMessageTypeLabel(type) {
      const map = {
        system: '系统通知',
        task: '任务消息',
        chat: '聊天消息',
        announcement: '公告',
        assistance: '协助请求',
        collaboration: '协作邀请',
        warehouse: '仓库通知'
      }
      return map[type] || '其他消息'
    },
    formatTime(timeString) {
      if (!timeString) return '';
      
      try {
        const date = new Date(timeString);
        return date.toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
      } catch (e) {
        console.error('时间格式化错误', e);
        return timeString;
      }
    },
    async fetchMessageDetail() {
      this.loading = true;
      try {
        // 获取消息ID和类型
        const id = this.$route.params.id;
        // 使用props传入的类型，或者使用查询参数中的类型，或者默认为普通消息
        const messageType = this.type || this.$route.query.type;
        
        console.log('获取消息详情', { id, messageType });
        
        if (messageType === 'announcement') {
          await this.fetchAnnouncementDetail(id);
        } else if (messageType === 'assistance') {
          await this.fetchAssistanceDetail(id);
        } else {
          // 默认为普通消息
          await this.fetchGeneralMessageDetail(id);
        }
      } catch (e) {
        console.error('获取消息详情失败', e);
        this.message = null;
        this.$toast && this.$toast.error('获取消息详情失败');
      } finally {
        this.loading = false;
      }
    },
    async fetchAnnouncementDetail(id) {
      try {
        const res = await getAnnouncementDetail(id);
        if (res.code === 200) {
          const a = res.data;
          this.message = {
            id: a.id,
            type: 'announcement',
            title: a.title,
            content: a.content,
            time: a.startTime,
            icon: 'icon-bell',
            iconClass: 'blue',
            details: [
              { label: '发布时间', value: this.formatTime(a.startTime) },
              { label: '发布人', value: a.sender?.name || a.senderName },
              { label: '状态', value: a.status }
            ],
            actions: [
              { label: a.isRead ? '已读' : '标记为已读', type: 'mark-read', primary: !a.isRead }
            ]
          };
        } else {
          this.message = null;
          console.error('获取公告详情失败', res.message);
        }
      } catch (e) {
        console.error('获取公告详情出错', e);
        throw e;
      }
    },
    async fetchAssistanceDetail(id) {
      try {
        const res = await getAssistanceDetail(id);
        if (res.code === 200) {
          const a = res.data;
          this.message = {
            id: a.requestId || a.id,
            type: 'assistance',
            title: a.taskTitle || '协助请求',
            content: a.description || a.content,
            time: a.createTime,
            icon: 'icon-users',
            iconClass: 'purple',
            details: [
              { label: '请求人', value: a.requester?.name || a.requesterName },
              { label: '工程师', value: a.engineer?.name || a.engineerName },
              { label: '状态', value: a.status },
              { label: '紧急程度', value: a.urgency }
            ],
            actions: [
              { label: '查看协作会话', type: 'view-conv', primary: true }
            ]
          };
        } else {
          this.message = null;
          console.error('获取协助请求详情失败', res.message);
        }
      } catch (e) {
        console.error('获取协助请求详情出错', e);
        throw e;
      }
    },
    async fetchGeneralMessageDetail(id) {
      try {
        // 普通消息，假设id为conversationId，取最新一条
        const res = await getMessageList(id, { page: 1, size: 1 });
        if (res.code === 200 && res.data.list.length > 0) {
          const m = res.data.list[0];
          this.message = {
            id: m.messageId,
            type: m.messageType || 'chat',
            title: m.sender?.name || '消息',
            content: m.content,
            time: m.sendTime,
            icon: 'icon-user-circle',
            iconClass: m.messageType === 'task' ? 'green' : (m.messageType === 'system' ? 'blue' : 'teal'),
            details: [
              { label: '发送人', value: m.sender?.name },
              { label: '时间', value: this.formatTime(m.sendTime) },
              { label: '状态', value: m.status }
            ],
            actions: [
              { label: '标记为已读', type: 'mark-read', primary: true }
            ]
          };
        } else {
          this.message = null;
          console.error('获取消息详情失败', res.message);
        }
      } catch (e) {
        console.error('获取普通消息详情出错', e);
        throw e;
      }
    },
    async handleAction(actionType) {
      if (!this.message) return;
      if (actionType === 'mark-read') {
        if (this.message.type === 'announcement') {
          try {
            const res = await markAnnouncementRead(this.message.id);
            if (res.code === 200) {
              this.$toast && this.$toast.success('已标记为已读');
              this.message.actions[0].label = '已读';
              this.message.actions[0].primary = false;
            } else {
              this.$toast && this.$toast.error(res.message || '标记失败');
            }
          } catch (e) {
            console.error('标记公告已读出错', e);
            this.$toast && this.$toast.error('标记失败，请重试');
          }
        }
        // 其他类型可补充
      } else if (actionType === 'view-conv') {
        // 跳转到协作会话
        if (this.message.type === 'assistance') {
          this.$router.push(`/chat/${this.message.id}`);
        }
      }
    },
    viewTask(taskId) {
      this.$router.push(`/task/${taskId}`);
    }
  },
  created() {
    this.fetchMessageDetail();
  }
}
</script>

<style scoped>
.message-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
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

.message-container {
  padding: 16px;
}

.loading, .error {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  width: 100%;
  color: #666;
  font-size: 16px;
}

.error {
  color: #e53e3e;
}

.message-header {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}

.message-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
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

.message-icon.teal {
  background-color: #ccfbf1;
  color: #14b8a6;
}

.message-icon.yellow {
  background-color: #fef3c7;
  color: #d97706;
}

.message-title-container {
  flex: 1;
}

.message-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 4px;
}

.message-meta {
  display: flex;
  font-size: 12px;
  color: #666;
}

.message-time {
  margin-right: 16px;
}

.message-content {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}

.message-text {
  font-size: 16px;
  line-height: 1.6;
  margin: 0 0 16px;
  white-space: pre-line;
}

.message-details {
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
}

.detail-label {
  width: 80px;
  color: #666;
  flex-shrink: 0;
}

.detail-value {
  font-weight: 500;
}

.related-task {
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px;
  color: #333;
}

.task-card {
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 12px;
  cursor: pointer;
}

.task-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.task-id {
  font-weight: 500;
  color: #666;
}

.task-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 12px;
  background-color: #f3f4f6;
}

.task-status.green {
  background-color: #d1fae5;
  color: #10b981;
}

.task-status.yellow {
  background-color: #fef3c7;
  color: #d97706;
}

.task-status.red {
  background-color: #fee2e2;
  color: #ef4444;
}

.task-info {
  margin-bottom: 12px;
}

.task-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.task-customer {
  font-size: 14px;
  color: #666;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.task-date {
  color: #666;
  display: flex;
  align-items: center;
}

.task-date i {
  margin-right: 4px;
}

.task-link {
  color: #3b82f6;
  display: flex;
  align-items: center;
}

.task-link i {
  margin-left: 4px;
}

.message-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  background-color: #f3f4f6;
  border: none;
  color: #374151;
}

.action-btn.primary {
  background-color: #3b82f6;
  color: white;
}

.action-btn:hover {
  opacity: 0.9;
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 