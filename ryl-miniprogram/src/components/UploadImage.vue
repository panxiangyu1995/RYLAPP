<template>
  <div>
    <div class="mb-2">
      <slot name="label">
        <label class="form-label">上传图片 (最多{{maxCount}}张)</label>
      </slot>
    </div>
    
    <div class="grid grid-cols-4 gap-2">
      <!-- 已上传图片预览 -->
      <div 
        v-for="(image, index) in images" 
        :key="index" 
        class="relative w-full aspect-square rounded-lg overflow-hidden border border-neutral-gray bg-neutral-light"
      >
        <img :src="image.url" class="w-full h-full object-cover" :alt="`图片 ${index + 1}`" />
        <button 
          type="button"
          @click="removeImage(index)" 
          class="absolute top-1 right-1 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center"
        >
          ×
        </button>
      </div>
      
      <!-- 上传按钮 -->
      <div 
        v-if="images.length < maxCount" 
        @click="triggerFileInput"
        class="w-full aspect-square border-2 border-dashed border-primary-light rounded-lg flex flex-col items-center justify-center cursor-pointer hover:bg-neutral-light"
      >
        <span class="text-3xl text-primary-medium mb-1">+</span>
        <span class="text-xs text-gray-500">上传图片</span>
        <input 
          ref="fileInput" 
          type="file" 
          accept="image/*" 
          multiple 
          class="hidden" 
          @change="onFileChange" 
        />
      </div>
    </div>
    
    <p v-if="error" class="text-red-500 text-xs mt-1">{{ error }}</p>
    <p class="text-xs text-gray-500 mt-1">支持jpg、png、jpeg格式，单张图片不超过5MB</p>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  maxCount: {
    type: Number,
    default: 8
  },
  maxSize: {
    type: Number,
    default: 5 * 1024 * 1024 // 5MB
  }
});

const emit = defineEmits(['update:images', 'upload-error']);

const fileInput = ref(null);
const images = ref([]);
const error = ref('');

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click();
};

// 文件选择回调
const onFileChange = async (e) => {
  const files = Array.from(e.target.files);
  
  if (images.value.length + files.length > props.maxCount) {
    error.value = `最多只能上传${props.maxCount}张图片`;
    emit('upload-error', error.value);
    return;
  }
  
  error.value = '';
  
  // 处理每个文件
  for (const file of files) {
    if (!file.type.includes('image/')) {
      error.value = '只能上传图片文件';
      emit('upload-error', error.value);
      continue;
    }
    
    if (file.size > props.maxSize) {
      error.value = '图片大小不能超过5MB';
      emit('upload-error', error.value);
      continue;
    }
    
    try {
      const compressedFile = await compressImage(file);
      const url = URL.createObjectURL(compressedFile);
      images.value.push({
        file: compressedFile,
        url,
        name: file.name
      });
    } catch (err) {
      console.error('图片处理失败:', err);
      error.value = '图片处理失败';
      emit('upload-error', error.value);
    }
  }
  
  // 清空文件输入框，以便可以再次选择同一文件
  e.target.value = '';
  
  // 更新父组件
  emit('update:images', images.value.map(img => img.file));
};

// 压缩图片
const compressImage = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) => {
      const img = new Image();
      img.src = event.target.result;
      
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        
        // 等比例缩小图片尺寸
        const MAX_WIDTH = 1200;
        const MAX_HEIGHT = 1200;
        
        if (width > height && width > MAX_WIDTH) {
          height = Math.round((height * MAX_WIDTH) / width);
          width = MAX_WIDTH;
        } else if (height > MAX_HEIGHT) {
          width = Math.round((width * MAX_HEIGHT) / height);
          height = MAX_HEIGHT;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        
        // 转换为Blob对象
        canvas.toBlob((blob) => {
          // 创建一个新的File对象
          const compressedFile = new File([blob], file.name, {
            type: file.type,
            lastModified: new Date().getTime()
          });
          resolve(compressedFile);
        }, file.type, 0.8); // 使用80%的质量压缩
      };
      
      img.onerror = (error) => {
        reject(error);
      };
    };
    
    reader.onerror = (error) => {
      reject(error);
    };
  });
};

// 移除图片
const removeImage = (index) => {
  URL.revokeObjectURL(images.value[index].url); // 释放URL对象
  images.value.splice(index, 1);
  emit('update:images', images.value.map(img => img.file));
};
</script> 