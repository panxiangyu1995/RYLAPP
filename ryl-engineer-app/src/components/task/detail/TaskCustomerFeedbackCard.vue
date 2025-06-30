<template>
  <div class="feedback-card">
    <h3 class="section-title">
      <i class="icon-comment-dots"></i>
      客户评价
    </h3>
    
    <div class="feedback-content">
      <div v-if="!comments || comments.length === 0" class="no-feedback">
        <i class="icon-comment-slash"></i>
        <p>暂无客户评价</p>
      </div>
      
      <div v-else class="feedback-list">
        <div 
          v-for="(comment, index) in comments" 
          :key="index"
          class="feedback-item"
        >
          <div class="feedback-header">
            <div class="rating">
              <i 
                v-for="i in 5" 
                :key="i" 
                :class="[
                  'icon-star', 
                  i <= (comment.rating || 0) ? 'filled' : 'empty'
                ]"
              ></i>
              <span>{{ comment.rating || 0 }}/5</span>
            </div>
            
            <div class="feedback-date">
              {{ formatDate(comment.date) }}
            </div>
          </div>
          
          <p class="feedback-text">{{ comment.content }}</p>
          
          <div v-if="comment.photos && comment.photos.length > 0" class="feedback-photos">
            <div 
              v-for="(photo, photoIndex) in comment.photos" 
              :key="photoIndex"
              class="feedback-photo-container"
              @click="showFullImage(photo)"
            >
              <img :src="photo" :alt="`评价照片 ${photoIndex + 1}`" class="feedback-photo">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { format } from 'date-fns'

export default {
  name: 'TaskCustomerFeedbackCard',
  props: {
    comments: {
      type: Array,
      default: () => []
    }
  },
  setup() {
    // 格式化日期
    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      try {
        const date = new Date(dateStr)
        return format(date, 'yyyy-MM-dd')
      } catch (err) {
        return dateStr
      }
    }
    
    // 显示大图
    const showFullImage = (imageUrl) => {
      if (!imageUrl) return
      
      // 这里可以实现查看大图的逻辑，比如打开一个弹窗或跳转
      console.log('查看图片:', imageUrl)
    }
    
    return {
      formatDate,
      showFullImage
    }
  }
}
</script>

<style scoped>
.feedback-card {
  background-color: #fff;
  margin: 16px 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  animation: slideInUp 0.4s ease;
  animation-delay: 0.4s;
}

@keyframes slideInUp {
  from { 
    opacity: 0;
    transform: translateY(20px);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin: 0;
  padding: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.section-title i {
  font-size: 18px;
  margin-right: 8px;
  color: #8b5cf6;
}

.feedback-content {
  padding: 16px;
}

.no-feedback {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
  color: #9ca3af;
}

.no-feedback i {
  font-size: 32px;
  margin-bottom: 8px;
}

.no-feedback p {
  font-size: 14px;
  margin: 0;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  border-bottom: 1px solid #f3f4f6;
  padding-bottom: 16px;
}

.feedback-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.rating {
  display: flex;
  align-items: center;
}

.icon-star {
  color: #d1d5db;
  margin-right: 2px;
  font-size: 16px;
}

.icon-star.filled {
  color: #f59e0b;
}

.rating > span {
  margin-left: 6px;
  font-weight: 600;
  font-size: 14px;
  color: #374151;
}

.feedback-date {
  font-size: 12px;
  color: #6b7280;
}

.feedback-text {
  font-size: 14px;
  line-height: 1.5;
  color: #4b5563;
  margin: 0 0 12px 0;
  white-space: pre-line;
}

.feedback-photos {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.feedback-photo-container {
  width: calc(33.33% - 6px);
  aspect-ratio: 1/1;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e5e7eb;
}

.feedback-photo-container:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.feedback-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 480px) {
  .feedback-photo-container {
    width: calc(50% - 4px);
  }
}
</style> 