<template>
  <view>
    <view class="mb-2">
      <slot name="label">
        <view class="text-sm font-medium mb-1 text-ui-blue-start">上传图片 (最多{{maxCount}}张)</view>
      </slot>
    </view>
    
    <view class="grid grid-cols-4 gap-2">
      <!-- 已上传图片预览 -->
      <view 
        v-for="(image, index) in images" 
        :key="index" 
        class="relative w-full aspect-square rounded-lg overflow-hidden border border-neutral-gray bg-neutral-light"
      >
        <image :src="image.serverUrl || image.url" class="w-full h-full object-cover" :alt="`图片 ${index + 1}`" />
        <button 
          type="button"
          @click="removeImage(index)" 
          class="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
        >
          ×
        </button>
        <view v-if="image.status === 'uploading'" class="uploading-overlay">上传中...</view>
        <view v-if="image.status === 'error'" class="error-badge">!</view>
      </view>
      
      <!-- 上传按钮 -->
      <view 
        v-if="images.length < maxCount" 
        @click="handleChooseImage"
        class="w-full aspect-square border-2 border-dashed border-primary-light rounded-lg flex flex-col items-center justify-center cursor-pointer hover:bg-neutral-light"
      >
        <view class="text-3xl text-primary-medium mb-1">+</view>
        <view class="text-xs text-ui-text-black">上传图片</view>
      </view>
    </view>
    
    <view v-if="error" class="text-red-500 text-xs mt-1">{{ error }}</view>
    <view class="text-xs text-ui-text-black mt-1">支持jpg、png、jpeg格式，单张图片不超过5MB</view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request'; // 引入请求工具
import { API_PATHS } from '@/constants/api';

const userStore = useUserStore();

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  taskId: {
    type: String,
    required: true
  },
  maxCount: {
    type: Number,
    default: 8
  },
  maxSize: {
    type: Number,
    default: 5 * 1024 * 1024 // 5MB
  },
});

const emit = defineEmits(['update:modelValue', 'upload-error']);

const images = ref([]);
const error = ref('');

watch(() => props.modelValue, (newVal) => {
  if (Array.isArray(newVal) && newVal.length === 0) {
      images.value = [];
  }
}, { immediate: true });


// 统一的图片选择方法
const handleChooseImage = () => {
  const remainingCount = props.maxCount - images.value.length;
  if (remainingCount <= 0) {
    showError(`最多只能上传${props.maxCount}张图片`);
    return;
  }

  error.value = '';

  uni.chooseImage({
    count: remainingCount,
    sizeType: ['original', 'compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      const tempFiles = res.tempFiles;
      
      tempFiles.forEach(file => {
        if (file.size > props.maxSize) {
          showError(`图片 "${file.name}" 大小超过5MB`);
          return;
        }

        const imageItem = {
          file: file,
          url: file.path,
          name: file.name || `image-${Date.now()}`,
          status: 'uploading', // uploading, success, error
          serverUrl: '',
          imageId: null
        };
        images.value.push(imageItem);
        uploadImage(imageItem, images.value.length - 1);
      });
    },
    fail: (err) => {
      showError('选择图片失败', err);
    }
  });
};

const uploadImage = (imageItem, index) => {
  const token = uni.getStorageSync('token');
  if (!token) {
    showError('用户未登录，无法上传');
    imageItem.status = 'error';
    return;
  }

  const url = `${request.baseURL}${API_PATHS.TASK_IMAGE_UPLOAD}?taskId=${props.taskId}&imageType=0&sort=${index}`;

  uni.uploadFile({
    url: url,
    filePath: imageItem.url,
    name: 'file',
    header: {
      'Authorization': `Bearer ${token}`
    },
    success: (uploadRes) => {
      try {
        const resData = JSON.parse(uploadRes.data);
        if (resData.code === 200) {
          const fileInfo = resData.data;
          // 创建一个新的、已更新的图片对象
          const updatedImageItem = {
            ...imageItem,
            status: 'success',
            serverUrl: fileInfo.fileUrl,
            imageId: fileInfo.id
          };
          // 通过创建一个新数组来替换旧数组中的对应项，以强制UI更新
          images.value.splice(index, 1, updatedImageItem);
          updateModelValue();
        } else {
          imageItem.status = 'error';
          showError(resData.message || '上传失败');
        }
      } catch (e) {
        imageItem.status = 'error';
        showError('服务器响应格式错误');
      }
    },
    fail: (err) => {
      imageItem.status = 'error';
      showError('上传接口请求失败', err);
    },
  });
};


const removeImage = async (index) => {
  const imageToRemove = images.value[index];
  images.value.splice(index, 1);
  updateModelValue();

  if (imageToRemove.imageId) {
    try {
      await request.delete(`/files/task/image/${imageToRemove.imageId}`);
    } catch (error) {
      console.error('删除服务器图片失败:', error);
    }
  }
};

const updateModelValue = () => {
  const successfulUploads = images.value
    .filter(img => img.status === 'success')
    .map(img => ({ url: img.serverUrl, name: img.name, imageId: img.imageId }));
  emit('update:modelValue', successfulUploads);
};

const showError = (message, details = null) => {
    error.value = message;
    emit('upload-error', message);
    if (details) {
        console.error(message, details);
    }
};
</script>
<style scoped>
.uploading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}
.error-badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background-color: #ef4444;
    color: white;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: bold;
}
</style> 