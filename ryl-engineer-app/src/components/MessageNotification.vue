<template>
  <div class="message-notification" v-if="hasUnreadMessages">
    <div class="notification-content" @click="viewMessages">
      <div class="notification-icon">
        <i class="icon-bell"></i>
        <span class="badge">{{ unreadCount }}</span>
      </div>
      <div class="notification-text">
        <div class="notification-title">{{ latestMessage.title }}</div>
        <div class="notification-preview">{{ latestMessage.content }}</div>
      </div>
      <div class="notification-time">{{ latestMessage.time }}</div>
    </div>
  </div>
</template>

<script>
import { getConversationList } from '@/api/chat'
import { getAnnouncementList } from '@/api/announcement'
import { getAssistanceList } from '@/api/assistance'
import { useRouter } from 'vue-router'

export default {
  name: 'MessageNotification',
  setup() {
    const router = useRouter()
    const viewMessages = () => {
      router.push('/messages')
    }
    return { viewMessages }
  },
  data() {
    return {
      unreadMessages: []
    }
  },
  computed: {
    hasUnreadMessages() {
      return this.unreadMessages.length > 0
    },
    unreadCount() {
      return this.unreadMessages.length
    },
    latestMessage() {
      return this.unreadMessages[0] || {}
    }
  },
  methods: {
    async fetchUnreadMessages() {
      // 聚合三类未读消息
      try {
        const [chatRes, annRes, assRes] = await Promise.all([
          getConversationList({ onlyUnread: true, page: 1, size: 5 }),
          getAnnouncementList({ onlyUnread: true, page: 1, size: 5 }),
          getAssistanceList({ page: 1, size: 5 }) // 协作未读逻辑可根据业务调整
        ])
        const chatMsgs = (chatRes.data?.list || [])
          .filter(conv => conv.unreadCount > 0)
          .map(conv => ({
            id: conv.conversationId,
            title: conv.name || '会话',
            content: conv.lastMessage?.content || '',
            time: conv.lastMessage?.time || '',
            type: conv.lastMessage?.type || 'chat',
            read: false
          }))
        const annMsgs = (annRes.data?.list || [])
          .filter(a => !a.isRead)
          .map(a => ({
            id: String(a.id),
            title: a.title,
            content: a.content,
            time: a.startTime,
            type: 'announcement',
            read: false
          }))
        // 协作请求未读逻辑可根据业务调整，这里假设status!=completed为未读
        const assMsgs = (assRes.data?.list || [])
          .filter(a => a.status !== 'completed')
          .map(a => ({
            id: a.requestId,
            title: a.taskTitle || '协助请求',
            content: a.description,
            time: a.createTime,
            type: 'assistance',
            read: false
          }))
        const all = [...chatMsgs, ...annMsgs, ...assMsgs]
          .sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime())
        this.unreadMessages = all
      } catch (e) {
        this.unreadMessages = []
      }
    },
    markAsRead(messageId) {
      // 实际项目中，这里会调用API标记消息为已读
      this.unreadMessages = this.unreadMessages.filter(msg => msg.id !== messageId)
    }
  },
  mounted() {
    this.fetchUnreadMessages()
    this.timer = setInterval(() => {
      this.fetchUnreadMessages()
    }, 60000)
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>

<style scoped>
.message-notification {
  position: fixed;
  top: 16px;
  left: 16px;
  right: 16px;
  z-index: 100;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 1px 3px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  animation: slide-in 0.3s ease-out forwards;
}

@keyframes slide-in {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.notification-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
}

.notification-icon {
  position: relative;
  margin-right: 12px;
  flex-shrink: 0;
}

.icon-bell::before {
  content: '\f0f3';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  font-size: 20px;
  color: #3b82f6;
}

.badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background-color: #ef4444;
  color: #fff;
  font-size: 10px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-text {
  flex: 1;
  overflow: hidden;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-preview {
  font-size: 14px;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
  margin-left: 12px;
}
</style> 