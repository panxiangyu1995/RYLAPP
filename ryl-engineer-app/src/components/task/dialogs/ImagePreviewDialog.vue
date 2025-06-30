<template>
  <div class="preview-overlay" @click="closePreview"></div>
  <div class="preview-dialog">
    <button class="close-btn" @click="$emit('close')">
      <i class="icon-times"></i>
    </button>
    
    <div class="preview-content">
      <div class="preview-navigation" v-if="images.length > 1">
        <button 
          class="nav-btn prev-btn" 
          @click="prevImage" 
          :disabled="currentIndex === 0"
        >
          <i class="icon-chevron-left"></i>
        </button>
        <div class="image-counter">{{ currentIndex + 1 }} / {{ images.length }}</div>
        <button 
          class="nav-btn next-btn" 
          @click="nextImage" 
          :disabled="currentIndex === images.length - 1"
        >
          <i class="icon-chevron-right"></i>
        </button>
      </div>
      
      <div class="image-container">
        <div 
          class="image-wrapper" 
          :style="{ transform: `scale(${scale})` }"
          @wheel="handleZoom"
        >
          <img 
            :src="currentImage.url" 
            :alt="currentImage.title || '图片预览'" 
            class="preview-image"
            @load="onImageLoad"
          />
        </div>
        
        <div class="loading-indicator" v-if="isLoading">
          <i class="icon-spinner icon-spin"></i>
        </div>
      </div>
      
      <div class="preview-info" v-if="showInfo && (currentImage.title || currentImage.description)">
        <h3 v-if="currentImage.title" class="preview-title">
          {{ currentImage.title }}
        </h3>
        <p v-if="currentImage.description" class="preview-description">
          {{ currentImage.description }}
        </p>
      </div>
      
      <div class="preview-actions">
        <button class="action-btn zoom-btn" @click="zoomIn" :disabled="scale >= 3">
          <i class="icon-search-plus"></i>
        </button>
        <button class="action-btn zoom-btn" @click="resetZoom">
          <i class="icon-expand"></i>
        </button>
        <button class="action-btn zoom-btn" @click="zoomOut" :disabled="scale <= 0.5">
          <i class="icon-search-minus"></i>
        </button>
        <button class="action-btn" @click="toggleInfo" v-if="hasInfo">
          <i :class="showInfo ? 'icon-info-circle' : 'icon-info'"></i>
        </button>
        <a 
          v-if="currentImage.downloadUrl" 
          :href="currentImage.downloadUrl" 
          download 
          class="action-btn download-btn"
        >
          <i class="icon-download"></i>
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'ImagePreviewDialog',
  props: {
    images: {
      type: Array,
      required: true,
      validator: (value) => {
        return value.length > 0 && value.every(img => img.url)
      }
    },
    initialIndex: {
      type: Number,
      default: 0
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const currentIndex = ref(props.initialIndex || 0)
    const isLoading = ref(true)
    const scale = ref(1)
    const showInfo = ref(true)
    
    // 当前图片
    const currentImage = computed(() => {
      return props.images[currentIndex.value] || props.images[0]
    })
    
    // 是否有图片信息
    const hasInfo = computed(() => {
      return currentImage.value.title || currentImage.value.description
    })
    
    // 图片加载完成
    const onImageLoad = () => {
      isLoading.value = false
    }
    
    // 下一张图片
    const nextImage = () => {
      if (currentIndex.value < props.images.length - 1) {
        currentIndex.value++
        isLoading.value = true
        resetZoom()
      }
    }
    
    // 上一张图片
    const prevImage = () => {
      if (currentIndex.value > 0) {
        currentIndex.value--
        isLoading.value = true
        resetZoom()
      }
    }
    
    // 放大
    const zoomIn = () => {
      if (scale.value < 3) {
        scale.value += 0.25
      }
    }
    
    // 缩小
    const zoomOut = () => {
      if (scale.value > 0.5) {
        scale.value -= 0.25
      }
    }
    
    // 重置缩放
    const resetZoom = () => {
      scale.value = 1
    }
    
    // 切换信息显示
    const toggleInfo = () => {
      showInfo.value = !showInfo.value
    }
    
    // 处理滚轮缩放
    const handleZoom = (e) => {
      if (e.deltaY < 0) {
        zoomIn()
      } else {
        zoomOut()
      }
      
      e.preventDefault()
    }
    
    // 关闭预览
    const closePreview = (e) => {
      if (e.target.classList.contains('preview-overlay')) {
        emit('close')
      }
    }
    
    // 键盘导航
    const handleKeyDown = (e) => {
      switch (e.key) {
        case 'ArrowRight':
          nextImage()
          break
        case 'ArrowLeft':
          prevImage()
          break
        case 'Escape':
          emit('close')
          break
        case '+':
          zoomIn()
          break
        case '-':
          zoomOut()
          break
        case '0':
          resetZoom()
          break
      }
    }
    
    // 挂载时添加键盘事件监听
    onMounted(() => {
      window.addEventListener('keydown', handleKeyDown)
      
      // 卸载时移除键盘事件监听
      return () => {
        window.removeEventListener('keydown', handleKeyDown)
      }
    })
    
    return {
      currentIndex,
      currentImage,
      isLoading,
      scale,
      showInfo,
      hasInfo,
      nextImage,
      prevImage,
      zoomIn,
      zoomOut,
      resetZoom,
      toggleInfo,
      handleZoom,
      closePreview,
      onImageLoad
    }
  }
}
</script>

<style scoped>
.preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

.preview-dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  border: none;
  color: #fff;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1002;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.preview-content {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.preview-navigation {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 20px;
  padding: 8px 16px;
  z-index: 1002;
}

.nav-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: transparent;
  border: none;
  color: #fff;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-btn:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.2);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.image-counter {
  margin: 0 16px;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-wrapper {
  transition: transform 0.3s ease;
  cursor: move;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  user-select: none;
  transition: opacity 0.3s ease;
}

.loading-indicator {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #fff;
  font-size: 40px;
}

.preview-info {
  position: absolute;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  max-width: 800px;
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  animation: slideUp 0.3s ease;
}

.preview-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
}

.preview-description {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  opacity: 0.8;
}

.preview-actions {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 20px;
  padding: 8px;
  z-index: 1002;
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: transparent;
  border: none;
  color: #fff;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  margin: 0 4px;
  transition: all 0.2s ease;
  text-decoration: none;
}

.action-btn:hover:not(:disabled) {
  background-color: rgba(255, 255, 255, 0.2);
}

.action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translate(-50%, 20px); }
  to { opacity: 1; transform: translate(-50%, 0); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-spin {
  animation: spin 1s linear infinite;
}
</style>
