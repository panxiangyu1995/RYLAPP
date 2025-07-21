<template>
  <div v-if="appStore.showUpdateDialog" class="update-dialog-overlay">
    <div class="update-dialog">
      <div class="dialog-header">
        <h3 class="dialog-title">发现新版本 v{{ appStore.latestVersion?.versionName }}</h3>
      </div>
      <div class="dialog-content">
        <h4>更新日志:</h4>
        <div class="update-log" v-html="formattedUpdateLog"></div>
      </div>
      <div class="dialog-footer">
        <button v-if="!appStore.latestVersion?.isForced" @click="closeDialog" class="btn-secondary">稍后提醒</button>
        <button @click="startUpdate" class="btn-primary">立即更新</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAppStore } from '../stores/app';

const appStore = useAppStore();

const formattedUpdateLog = computed(() => {
  return appStore.latestVersion?.updateLog.replace(/\\n/g, '<br />') || '';
});

const closeDialog = () => {
  appStore.closeUpdateDialog();
};

const startUpdate = () => {
  if (appStore.latestVersion?.downloadUrl) {
    // 在新标签页中打开下载链接
    window.open(appStore.latestVersion.downloadUrl, '_blank');
  }
  // 如果是强制更新，即使用户不点击，弹窗也不会关闭
  if (!appStore.latestVersion?.isForced) {
    appStore.closeUpdateDialog();
  }
};
</script>

<style scoped>
.update-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.update-dialog {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 400px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #f7f7f7;
}

.dialog-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.dialog-content {
  padding: 24px;
  line-height: 1.6;
  color: #555;
  max-height: 50vh;
  overflow-y: auto;
}

.dialog-content h4 {
  font-size: 16px;
  font-weight: 600;
  margin-top: 0;
  margin-bottom: 8px;
}

.update-log {
  font-size: 14px;
  white-space: pre-wrap; /* 保持换行符 */
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  background-color: #f7f7f7;
}

button {
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-left: 12px;
}

.btn-primary {
  background-color: #1e40af;
  color: white;
  font-weight: 500;
}

.btn-primary:hover {
  background-color: #1c388a;
}

.btn-secondary {
  background-color: #e5e7eb;
  color: #374151;
}

.btn-secondary:hover {
  background-color: #d1d5db;
}
</style> 