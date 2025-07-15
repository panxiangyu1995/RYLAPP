<template>
  <div class="chat-detail-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>{{ chatInfo.name }}</h1>
      <div class="header-right" @click="showOptions">
        <i class="icon-more-vertical"></i>
      </div>
    </header>

    <!-- 聊天内容区域 -->
    <div class="chat-container" ref="chatContainer">
      <!-- 加载中状态 -->
      <div v-if="loading" class="loading-messages">
        <div class="spinner"></div>
        <p>加载消息中...</p>
      </div>
      
      <!-- 加载更多 -->
      <div v-if="hasMoreMessages" class="load-more" @click="loadMoreMessages">
        加载更多消息
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-message">
        <i class="icon-alert-circle"></i>
        <p>{{ error }}</p>
        <button @click="fetchMessages">重试</button>
      </div>

      <div class="chat-date-divider">{{ formatDate(currentDate) }}</div>

      <div v-for="(message, index) in messages" :key="message.messageId" class="message-wrapper">
        <!-- 如果需要显示日期分隔线 -->
        <div v-if="shouldShowDateDivider(message, index)" class="chat-date-divider">
          {{ formatDate(message.sendTime) }}
        </div>

        <!-- 消息气泡 -->
        <div :class="['message-bubble', { 'sent': message.senderId === currentUserId, 'received': message.senderId !== currentUserId }]">
          <!-- 接收的消息显示头像 -->
          <div v-if="message.senderId !== currentUserId" class="avatar">
            <img :src="getSenderAvatar(message)" alt="头像">
          </div>

          <div class="message-content">
            <!-- 解析后的交互式消息 -->
            <div v-if="getParsedContent(message).isInteractive" class="interactive-message">
              <div class="text-message">{{ getParsedContent(message).text }}</div>
              <div class="message-actions" v-if="getParsedContent(message).actions">
                <button 
                  v-for="(action, i) in getParsedContent(message).actions" 
                  :key="i"
                  :class="['action-btn', action.primary ? 'primary' : '']"
                  @click="handleAction(action)">
                  {{ action.label }}
                </button>
              </div>
            </div>
            <!-- 文本消息 -->
            <div v-else-if="message.contentType === 'text'" class="text-message">
              {{ message.content }}
            </div>

            <!-- 图片消息 -->
            <div v-else-if="message.contentType === 'image'" class="image-message">
              <img :src="message.content" @click="previewImage(message.content)" alt="图片消息">
            </div>

            <!-- 文件消息 -->
            <div v-else-if="message.contentType === 'file'" class="file-message">
              <div class="file-icon">
                <i class="icon-file"></i>
              </div>
              <div class="file-info">
                <div class="file-name">{{ getFileName(message.content) }}</div>
                <div class="file-size">{{ message.fileSize || '未知大小' }}</div>
              </div>
              <div class="file-action">
                <i class="icon-download"></i>
              </div>
            </div>

            <div class="message-time">{{ formatTime(message.sendTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input-container">
      <div class="input-toolbar">
        <i class="icon-mic" @click="startVoiceRecord"></i>
        <div class="input-wrapper">
          <input 
            type="text" 
            v-model="messageText" 
            placeholder="输入消息..." 
            @keyup.enter="sendTextMessage"
          />
        </div>
        <i class="icon-plus" @click="showAttachmentOptions"></i>
        <i v-if="messageText.trim()" class="icon-send" @click="sendTextMessage"></i>
      </div>

      <!-- 附件选项 -->
      <div v-if="showAttachments" class="attachment-options">
        <div class="attachment-option" @click="selectImage">
          <div class="attachment-icon">
            <i class="icon-image"></i>
          </div>
          <div class="attachment-label">图片</div>
        </div>
        <div class="attachment-option" @click="selectFile">
          <div class="attachment-icon">
            <i class="icon-file"></i>
          </div>
          <div class="attachment-label">文件</div>
        </div>
      </div>
    </div>
    
    <!-- 弹窗组件 -->
    <RejectReasonDialog
      v-if="showRejectDialog"
      :task-id="currentOperatingTask?.id"
      @close="closeRejectDialog"
      @submit="handleRejectSubmit"
    />

    <TaskAssignDialog
      v-if="showAssignDialog"
      :task-id="currentOperatingTask?.id"
      @close="closeAssignDialog"
      @assign="handleAssignSubmit"
    />
  </div>
</template>

<script>
import { getMessageList, sendMessage, markMessageRead, uploadMessageImage } from '@/api/chat'
import { acceptTask, rejectTask } from '@/api/task'
import RejectReasonDialog from '@/components/task/dialogs/RejectReasonDialog.vue'
import TaskAssignDialog from '@/components/task/dialogs/TaskAssignDialog.vue'


export default {
  name: 'ChatDetailPage',
  components: {
    RejectReasonDialog,
    TaskAssignDialog
  },
  data() {
    return {
      conversationId: this.$route.params.id,
      chatInfo: {
        name: '',
        avatar: '/default-avatar.png'
      },
      currentUserId: this.getCurrentUserId(),
      messages: [],
      loading: true,
      error: null,
      messageText: '',
      showAttachments: false,
      currentDate: new Date(),
      page: 1,
      pageSize: 20,
      hasMoreMessages: false,
      sending: false,
      // 新增状态
      showRejectDialog: false,
      showAssignDialog: false,
      currentOperatingTask: null,
      rejectionData: null
    }
  },
  created() {
    this.fetchConversationInfo()
    this.fetchMessages()
    // 标记会话为已读
    this.markConversationAsRead()
  },
  mounted() {
    this.scrollToBottom()
  },
  methods: {
    // 获取当前用户ID
    getCurrentUserId() {
      const userInfo = JSON.parse(localStorage.getItem('user') || '{}')
      return userInfo.id || 0
    },
    
    // 获取会话信息
    async fetchConversationInfo() {
      try {
        // 这里应该有获取会话详情的API，暂时模拟
        const response = await fetch(`/api/v1/chat/conversation/${this.conversationId}`)
        const data = await response.json()
        if (data.code === 200) {
          this.chatInfo = data.data
        }
      } catch (error) {
        console.error('获取会话信息失败', error)
      }
    },
    
    // 获取消息列表
    async fetchMessages() {
      this.loading = true
      this.error = null
      
      try {
        const params = {
          page: this.page,
          size: this.pageSize
        }
        
        const response = await getMessageList(this.conversationId, params)
        
        if (response.data && response.code === 200) {
          // 如果是第一页，直接替换消息列表
          if (this.page === 1) {
            this.messages = response.data.list || []
          } else {
            // 否则将新消息追加到现有消息列表前面
            this.messages = [...response.data.list, ...this.messages]
          }
          
          // 检查是否还有更多消息
          this.hasMoreMessages = response.data.total > (this.page * this.pageSize)
          
          // 如果是第一次加载，滚动到底部
          if (this.page === 1) {
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          }
        } else {
          this.error = response.message || '获取消息失败'
        }
      } catch (error) {
        console.error('获取消息列表出错', error)
        this.error = '获取消息失败，请检查网络连接'
      } finally {
        this.loading = false
      }
    },
    
    // 标记会话为已读
    async markConversationAsRead() {
      try {
        await markMessageRead({
          conversationId: this.conversationId
        })
      } catch (error) {
        console.error('标记会话已读失败', error)
      }
    },
    
    // 获取发送者头像
    getSenderAvatar(message) {
      // 这里应该根据发送者ID获取头像
      // 暂时返回默认头像
      return message.senderAvatar || this.chatInfo.avatar || '/default-avatar.png'
    },
    
    // 获取文件名
    getFileName(fileUrl) {
      if (!fileUrl) return '未知文件'
      
      // 从URL中提取文件名
      const parts = fileUrl.split('/')
      return parts[parts.length - 1]
    },
    
    // 加载更多消息
    loadMoreMessages() {
      this.page += 1
      this.fetchMessages()
    },
    
    // 发送文本消息
    async sendTextMessage() {
      if (!this.messageText.trim() || this.sending) return
      
      this.sending = true
      
      try {
        const messageData = {
          conversationId: this.conversationId,
          content: this.messageText,
          messageType: 'chat',
          contentType: 'text'
        }
        
        const response = await sendMessage(messageData)
        
        if (response.code === 200) {
          // 清空输入框
          this.messageText = ''
          
          // 隐藏附件选项
          this.showAttachments = false
          
          // 添加新消息到列表
          if (response.data) {
            this.messages.push(response.data)
            this.$nextTick(() => {
              this.scrollToBottom()
            })
          } else {
            // 如果后端没有返回完整的消息对象，则重新获取最新消息
            this.page = 1
            this.fetchMessages()
          }
        } else {
          console.error('发送消息失败', response.message)
        }
      } catch (error) {
        console.error('发送消息出错', error)
        alert('发送失败，请重试')
      } finally {
        this.sending = false
      }
    },
    
    // 显示附件选项
    showAttachmentOptions() {
      this.showAttachments = !this.showAttachments
    },
    
    // 选择图片
    async selectImage() {
      // 这里应该调用文件选择API
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = 'image/*'
      
      input.onchange = async (e) => {
        const file = e.target.files[0]
        if (!file) return
        
        try {
          this.sending = true
          
          // 创建表单数据
          const formData = new FormData()
          formData.append('file', file)
          formData.append('conversationId', this.conversationId)
          
          // 上传图片
          const response = await uploadMessageImage(formData)
          
          if (response.code === 200) {
            // 发送图片消息
            await sendMessage({
              conversationId: this.conversationId,
              content: response.data.url,
              messageType: 'chat',
              contentType: 'image'
            })
            
            // 重新获取最新消息
            this.page = 1
            this.fetchMessages()
          } else {
            console.error('上传图片失败', response.message)
            alert('上传图片失败，请重试')
          }
        } catch (error) {
          console.error('上传图片出错', error)
          alert('上传失败，请重试')
        } finally {
          this.sending = false
          this.showAttachments = false
        }
      }
      
      input.click()
    },
    
    // 选择文件
    selectFile() {
      // 暂时使用与选择图片相同的逻辑
      // 实际项目中可能需要单独的文件上传API
      this.selectImage()
    },
    
    // 开始语音录制
    startVoiceRecord() {
      // 实现语音录制功能
      alert('语音录制功能暂未实现')
    },
    
    // 预览图片
    previewImage(imageUrl) {
      // 实现图片预览功能
      window.open(imageUrl, '_blank')
    },
    
    // 滚动到底部
    scrollToBottom() {
      if (this.$refs.chatContainer) {
        this.$refs.chatContainer.scrollTop = this.$refs.chatContainer.scrollHeight
      }
    },
    
    // 格式化时间
    formatTime(timestamp) {
      if (!timestamp) return ''
      
      const date = new Date(timestamp)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    },
    
    // 格式化日期
    formatDate(timestamp) {
      if (!timestamp) return ''
      
      const date = new Date(timestamp)
      const today = new Date()
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)

      if (date.toDateString() === today.toDateString()) {
        return '今天'
      } else if (date.toDateString() === yesterday.toDateString()) {
        return '昨天'
      } else {
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
      }
    },

    // 是否显示日期分隔线
    shouldShowDateDivider(message, index) {
      if (index === 0) return false
      
      const currentDate = new Date(message.sendTime).toDateString()
      const prevDate = new Date(this.messages[index - 1].sendTime).toDateString()
      
      return currentDate !== prevDate
    },
    
    // 返回上一页
    goBack() {
      this.$router.back()
    },
    
    // 显示更多选项
    showOptions() {
      // 实现显示更多选项功能
    },

    getParsedContent(message) {
      if (message.contentType === 'text') {
        try {
          const content = JSON.parse(message.content);
          if (content && typeof content === 'object' && content.actions) {
            return { ...content, isInteractive: true };
          }
        } catch (e) {
          // 不是JSON，是普通文本
        }
      }
      return { text: message.content, isInteractive: false };
    },
    async handleAction(action) {
        if (!action || !action.type) return;

        this.currentOperatingTask = { id: action.taskId };

        switch (action.type) {
            case 'accept-task':
                try {
                    await acceptTask(action.taskId);
                    // 可以在这里显示一个toast提示
                    console.log(`任务 ${action.taskId} 已接受`);
                    // 可选：刷新页面或更新此消息的状态
                } catch (error) {
                    console.error('接受任务失败', error);
                }
                break;
            case 'reject-task':
                this.showRejectDialog = true;
                break;
            default:
                console.warn('未知的操作类型:', action.type);
        }
    },
    closeRejectDialog() {
      this.showRejectDialog = false;
      this.currentOperatingTask = null;
      this.rejectionData = null;
    },
    async handleRejectSubmit(data) {
        this.showRejectDialog = false;
        this.rejectionData = { reason: data.reason }; // 暂存拒绝理由

        if (data.transferType === 'admin') {
            try {
                await rejectTask(data.taskId, { 
                    reason: data.reason, 
                    transferTarget: 'admin' 
                });
                console.log(`任务 ${data.taskId} 已拒绝并转派给管理员`);
            } catch (error) {
                console.error('拒绝任务失败', error);
            }
            this.currentOperatingTask = null;
            this.rejectionData = null;
        } else if (data.transferType === 'engineer') {
            // 打开工程师选择对话框
            this.showAssignDialog = true;
        }
    },
    closeAssignDialog() {
      this.showAssignDialog = false;
      this.currentOperatingTask = null;
      this.rejectionData = null;
    },
    async handleAssignSubmit(assignData) {
        this.showAssignDialog = false;
        if (!this.rejectionData) {
            console.error('无法转派，缺少拒绝理由');
            return;
        }

        try {
            await rejectTask(assignData.taskId, {
                reason: this.rejectionData.reason,
                transferTarget: assignData.engineerId.toString()
            });
            console.log(`任务 ${assignData.taskId} 已转派给工程师 ${assignData.engineerId}`);
        } catch (error) {
            console.error('转派任务失败', error);
        }

        this.currentOperatingTask = null;
        this.rejectionData = null;
    },
  }
}
</script>

<style scoped>
.chat-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
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
  flex: 1;
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
  text-align: center;
}

.header-right {
  width: 24px;
}

.icon-more-vertical::before {
  content: '\f142';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.chat-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.chat-date-divider {
  text-align: center;
  margin: 16px 0;
  color: #666;
  font-size: 14px;
  background-color: rgba(0, 0, 0, 0.05);
  padding: 4px 12px;
  border-radius: 16px;
  align-self: center;
}

.message-wrapper {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}

.message-bubble {
  display: flex;
  max-width: 80%;
}

.message-bubble.sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-bubble.received {
  align-self: flex-start;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 8px;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content {
  position: relative;
}

.text-message {
  background-color: #fff;
  padding: 12px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin-bottom: 4px;
  word-break: break-word;
}

.message-bubble.sent .text-message {
  background-color: #dcf8c6;
}

.image-message {
  max-width: 240px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 4px;
}

.image-message img {
  width: 100%;
  display: block;
}

.file-message {
  background-color: #fff;
  padding: 12px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin-bottom: 4px;
}

.message-bubble.sent .file-message {
  background-color: #dcf8c6;
}

.file-icon {
  width: 40px;
  height: 40px;
  background-color: #f3f4f6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.icon-file::before {
  content: '\f15b';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #6b7280;
  font-size: 20px;
}

.file-info {
  flex: 1;
}

.file-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.file-size {
  font-size: 12px;
  color: #6b7280;
}

.file-action {
  margin-left: 12px;
}

.icon-download::before {
  content: '\f019';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
}

.location-message {
  max-width: 240px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.location-preview {
  position: relative;
  height: 120px;
}

.location-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.location-info {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 8px;
}

.location-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.location-address {
  font-size: 12px;
  color: #6b7280;
}

.message-time {
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}

.load-more {
  text-align: center;
  color: #3b82f6;
  padding: 8px;
  margin-bottom: 16px;
  cursor: pointer;
}

.chat-input-container {
  background-color: #fff;
  padding: 12px;
  border-top: 1px solid #e5e7eb;
}

.input-toolbar {
  display: flex;
  align-items: center;
}

.icon-mic::before {
  content: '\f130';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #6b7280;
  font-size: 20px;
  margin-right: 12px;
}

.input-wrapper {
  flex: 1;
  background-color: #f3f4f6;
  border-radius: 24px;
  padding: 8px 16px;
  margin-right: 12px;
}

.input-wrapper input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
}

.icon-plus::before {
  content: '\f067';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #6b7280;
  font-size: 20px;
  margin-right: 12px;
}

.icon-send::before {
  content: '\f1d8';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
  font-size: 20px;
}

.attachment-options {
  display: flex;
  padding: 16px 0 8px;
  justify-content: space-around;
}

.attachment-option {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.attachment-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.icon-image::before {
  content: '\f03e';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #3b82f6;
  font-size: 24px;
}

.icon-map-pin::before {
  content: '\f3c5';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #ef4444;
  font-size: 24px;
}

.attachment-label {
  font-size: 12px;
  color: #6b7280;
}
</style>
