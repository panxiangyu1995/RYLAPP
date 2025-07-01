<template>
  <div>
    <div class="mb-2">
      <slot name="label">
        <label class="form-label">上传附件</label>
      </slot>
    </div>
    
    <div class="border border-neutral-gray rounded-lg p-3">
      <!-- 已上传文件列表 -->
      <div v-for="(file, index) in files" :key="index" class="flex items-center justify-between mb-2 last:mb-0">
        <div class="flex items-center">
          <div class="w-8 h-8 bg-primary-light rounded-lg flex items-center justify-center text-white mr-2">
            {{ getFileIcon(file.name) }}
          </div>
          <div class="flex-1 overflow-hidden">
            <p class="text-sm truncate">{{ file.name }}</p>
            <p class="text-xs text-gray-500">{{ formatFileSize(file.size) }}</p>
          </div>
        </div>
        <button 
          type="button"
          @click="removeFile(index)" 
          class="text-red-500 ml-2"
        >
          删除
        </button>
      </div>
      
      <!-- 上传按钮 -->
      <div 
        v-if="files.length === 0"
        class="flex flex-col items-center justify-center py-6 cursor-pointer border-2 border-dashed border-neutral-gray rounded-lg hover:bg-neutral-light"
        @click="triggerFileInput"
      >
        <span class="text-primary-medium mb-1 text-3xl">+</span>
        <p class="text-gray-500">点击上传附件</p>
        <p class="text-xs text-gray-400">支持PDF、Word、Excel格式，大小不超过10MB</p>
      </div>
      
      <!-- 已有文件但可继续上传时的按钮 -->
      <button 
        v-else-if="!hasMaxFiles"
        type="button" 
        @click="triggerFileInput" 
        class="mt-3 btn btn-outline w-full"
      >
        添加更多附件
      </button>
      
      <!-- 隐藏的文件输入 -->
      <input 
        ref="fileInput" 
        type="file" 
        class="hidden" 
        accept=".pdf,.doc,.docx,.xls,.xlsx"
        @change="onFileChange" 
      />
    </div>
    
    <p v-if="error" class="text-red-500 text-xs mt-1">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  maxSize: {
    type: Number,
    default: 10 * 1024 * 1024 // 10MB
  },
  maxFiles: {
    type: Number,
    default: 5
  }
});

const emit = defineEmits(['update:files', 'upload-error']);

const fileInput = ref(null);
const files = ref([]);
const error = ref('');

const hasMaxFiles = computed(() => {
  return files.value.length >= props.maxFiles;
});

// 触发文件选择
const triggerFileInput = () => {
  if (hasMaxFiles.value) {
    error.value = `最多只能上传${props.maxFiles}个文件`;
    emit('upload-error', error.value);
    return;
  }
  fileInput.value.click();
};

// 文件选择回调
const onFileChange = (e) => {
  const selectedFiles = Array.from(e.target.files);
  
  if (files.value.length + selectedFiles.length > props.maxFiles) {
    error.value = `最多只能上传${props.maxFiles}个文件`;
    emit('upload-error', error.value);
    return;
  }
  
  error.value = '';
  
  // 检查文件类型和大小
  for (const file of selectedFiles) {
    const extension = file.name.split('.').pop().toLowerCase();
    const allowedTypes = ['pdf', 'doc', 'docx', 'xls', 'xlsx'];
    
    if (!allowedTypes.includes(extension)) {
      error.value = '仅支持PDF、Word和Excel格式的文件';
      emit('upload-error', error.value);
      continue;
    }
    
    if (file.size > props.maxSize) {
      error.value = '文件大小不能超过10MB';
      emit('upload-error', error.value);
      continue;
    }
    
    files.value.push(file);
  }
  
  // 清空文件输入框，以便可以再次选择同一文件
  e.target.value = '';
  
  // 更新父组件
  emit('update:files', files.value);
};

// 移除文件
const removeFile = (index) => {
  files.value.splice(index, 1);
  emit('update:files', files.value);
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
};

// 获取文件图标
const getFileIcon = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  
  if (extension === 'pdf') return 'PDF';
  if (extension === 'doc' || extension === 'docx') return 'DOC';
  if (extension === 'xls' || extension === 'xlsx') return 'XLS';
  
  return '文件';
};
</script> 