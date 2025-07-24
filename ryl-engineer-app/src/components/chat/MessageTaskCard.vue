<template>
  <div class="message-task-card" @click="navigateToTask">
    <div class="card-header">
      <div class="card-icon">
        <i class="icon-file-text"></i>
      </div>
      <h4 class="card-title">{{ content.title }}</h4>
    </div>
    <div class="card-body">
      <div class="info-row">
        <span class="info-label">任务标题:</span>
        <span class="info-value">{{ content.taskTitle }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">客户名称:</span>
        <span class="info-value">{{ content.customerName }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">设备名称:</span>
        <span class="info-value">{{ content.deviceName }}</span>
      </div>
      <div class="info-row description">
        <span class="info-label">故障描述:</span>
        <p class="info-value-p">{{ content.description }}</p>
      </div>
      <div v-if="content.previewImages && content.previewImages.length > 0" class="image-preview-gallery">
        <div v-for="(imgUrl, index) in content.previewImages" :key="index" class="preview-image-container">
          <img :src="imgUrl" alt="任务图片预览" class="preview-image" @click.stop="previewImage(imgUrl)"/>
        </div>
      </div>
    </div>
    <div class="card-footer">
      <span>点击查看任务详情</span>
      <i class="icon-arrow-right"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MessageTaskCard',
  props: {
    content: {
      type: Object,
      required: true
    }
  },
  methods: {
    navigateToTask() {
      if (this.content && this.content.taskId) {
        this.$router.push(`/task/${this.content.taskId}`);
      }
    },
    previewImage(url) {
      // 可以 emit 一个事件到父组件来处理图片预览
      this.$emit('preview-image', url);
    }
  }
}
</script>

<style scoped>
.message-task-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
}
.message-task-card:hover {
  transform: translateY(-2px);
}
.card-header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
  margin-bottom: 12px;
}
.card-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}
.icon-file-text::before {
  content: '\\f15c';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #1890ff;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.card-body .info-row {
  display: flex;
  font-size: 14px;
  margin-bottom: 8px;
}
.info-row.description {
  flex-direction: column;
}
.info-label {
  color: #888;
  margin-right: 8px;
  flex-shrink: 0;
}
.info-value {
  color: #333;
  font-weight: 500;
}
.info-value-p {
  color: #555;
  line-height: 1.5;
  margin-top: 4px;
}
.image-preview-gallery {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
.preview-image-container {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
}
.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-footer {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #1890ff;
}
.icon-arrow-right::before {
    content: '\\f061';
    font-family: 'Font Awesome 6 Free';
    font-weight: 900;
}
</style> 