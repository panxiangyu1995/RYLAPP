<template>
  <view>
    <view class="mb-2">
      <slot name="label">
        <view class="text-sm font-medium mb-1 text-ui-blue-start">上传附件</view>
      </slot>
    </view>
    
    <view class="border border-neutral-gray rounded-lg p-3">
      <!-- 已上传文件列表 -->
      <view v-for="file in fileList" :key="file.id" class="relative flex items-center justify-between mb-2 last:mb-0 p-2 rounded-md" :class="{'bg-green-50': file.status === 'success', 'bg-red-50': file.status === 'error'}">
        <view class="flex items-center flex-1 overflow-hidden">
          <view class="w-8 h-8 bg-primary-light rounded-lg flex items-center justify-center text-white mr-2">
            <text>{{ getFileIcon(file.extension) }}</text>
          </view>
          <view class="flex-1 overflow-hidden">
            <view class="text-sm truncate">{{ file.name }}</view>
            <view class="text-xs text-ui-text-black">{{ formatFileSize(file.size) }}</view>
          </view>
        </view>
        <button 
          type="button"
          @click="removeFile(file.id)" 
          class="text-red-500 ml-2"
        >
          删除
        </button>
        <view v-if="file.status === 'uploading'" class="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center text-white text-xs rounded-md">
            上传中...
        </view>
      </view>
      
      <!-- 上传按钮 -->
      <view 
        v-if="fileList.length < maxCount"
        class="flex flex-col items-center justify-center py-6 cursor-pointer border-2 border-dashed border-neutral-gray rounded-lg hover:bg-neutral-light mt-2"
        @click="handleChooseFile"
      >
        <view class="text-primary-medium mb-1 text-3xl">+</view>
        <view class="text-ui-text-black">点击上传附件</view>
        <view class="text-xs text-ui-text-black opacity-75">支持PDF、Word、Excel等，不超过{{ maxSize }}MB</view>
      </view>
    </view>
    
    <view v-if="errorMessage" class="text-red-500 text-xs mt-1">{{ errorMessage }}</view>
  </view>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request'; // 引入请求工具

const userStore = useUserStore();

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxSize: {
    type: Number,
    default: 10 // 默认10MB
  },
  maxCount: {
    type: Number,
    default: 5
  },
  // uni.chooseFile (H5) uses accept string, wx.chooseMessageFile uses type
  accept: {
    type: String,
    default: 'application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  }
});

const emit = defineEmits(['update:modelValue', 'upload-error']);

const fileList = ref([]);
const errorMessage = ref('');

watch(() => props.modelValue, (newFiles) => {
  if (Array.isArray(newFiles) && newFiles.length === 0) {
    fileList.value = [];
  }
}, { immediate: true });

const showError = (message) => {
  errorMessage.value = message;
  emit('upload-error', message);
  setTimeout(() => { errorMessage.value = ''; }, 3000);
};

const handleChooseFile = () => {
  const remainingCount = props.maxCount - fileList.value.length;
  if (remainingCount <= 0) {
    showError(`最多只能上传${props.maxCount}个文件`);
    return;
  }

  // #ifdef MP-WEIXIN
  wx.chooseMessageFile({
    count: remainingCount,
    type: 'file',
    success: (res) => {
      processFiles(res.tempFiles);
    },
    fail: (err) => {
      if (err.errMsg.indexOf('cancel') === -1) showError('选择文件失败');
    }
  });
  // #endif

  // #ifdef H5
  uni.chooseFile({
    count: remainingCount,
    accept: props.accept,
    success: (res) => {
      processFiles(res.tempFiles);
    },
    fail: (err) => {
       if (err.errMsg.indexOf('cancel') === -1) showError('选择文件失败');
    }
  });
  // #endif
};

const processFiles = (files) => {
  const maxSizeBytes = props.maxSize * 1024 * 1024;

  for (const file of files) {
    if (file.size > maxSizeBytes) {
      showError(`文件 "${file.name}" 过大，超过${props.maxSize}MB`);
      continue;
    }

    const fileItem = {
      id: file.path || file.name,
      file: file,
      name: file.name,
      size: file.size,
      extension: file.name.split('.').pop().toLowerCase(),
      url: file.path,
      status: 'uploading',
      serverUrl: ''
    };
    fileList.value.push(fileItem);
    uploadFile(fileItem);
  }
}

const uploadFile = (fileItem) => {
  const token = uni.getStorageSync('token');
  if (!token) {
    showError('用户未登录，无法上传');
    fileItem.status = 'error';
    return;
  }

  uni.uploadFile({
    url: `${request.baseURL}/upload/file`, // 修正：使用 request.baseURL
    filePath: fileItem.url,
    name: 'file',
    header: {
      'Authorization': `Bearer ${token}`
    },
    success: (uploadRes) => {
      try {
        const data = JSON.parse(uploadRes.data);
        if (data.code === 200) {
          fileItem.status = 'success';
          fileItem.serverUrl = data.data.url;
          updateModelValue();
        } else {
          fileItem.status = 'error';
          showError(data.message || '上传失败');
        }
      } catch (e) {
        fileItem.status = 'error';
        showError('服务器响应格式错误');
      }
    },
    fail: (err) => {
      fileItem.status = 'error';
      showError('上传接口请求失败');
    }
  });
};

const updateModelValue = () => {
  const successfulUploads = fileList.value
    .filter(f => f.status === 'success')
    .map(f => ({ fileUrl: f.serverUrl, fileName: f.name }));
  emit('update:modelValue', successfulUploads);
};

const removeFile = async (id) => {
  const index = fileList.value.findIndex(item => item.id === id);
  if (index !== -1) {
    const fileToRemove = fileList.value[index];
    fileList.value.splice(index, 1);
    updateModelValue();

    if (fileToRemove.serverUrl) {
      try {
        await uni.request({
          url: `${request.baseURL}/upload/delete`, // 修正：使用 request.baseURL
          method: 'POST',
          header: {
            'Authorization': `Bearer ${uni.getStorageSync('token')}`,
            'Content-Type': 'application/json'
          },
          data: {
            fileUrl: fileToRemove.serverUrl
          }
        });
      } catch (error) {
        console.error('删除服务器文件失败:', error);
      }
    }
  }
};

const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
};

const getFileIcon = (extension) => {
  if (['pdf'].includes(extension)) return 'PDF';
  if (['doc', 'docx'].includes(extension)) return 'DOC';
  if (['xls', 'xlsx'].includes(extension)) return 'XLS';
  return '文件';
};
</script> 