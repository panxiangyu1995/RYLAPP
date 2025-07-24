<template>
  <div class="security-settings-page">
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="fas fa-arrow-left"></i>
      </div>
      <h1>安全设置</h1>
      <div class="header-right"></div>
    </header>

    <div class="settings-list">
      <div class="setting-item" @click="goToSetSecurityPassword">
        <div class="setting-text">
          <div class="title">安全密码</div>
          <div class="subtitle">{{ hasSecurityPassword ? '已设置' : '未设置，点击设置' }}</div>
        </div>
        <i class="fas fa-chevron-right"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

const hasSecurityPassword = computed(() => authStore.user?.hasSecurityPassword || false);

const goBack = () => {
  router.back();
};

const goToSetSecurityPassword = () => {
  router.push('/profile/set-security-password');
};
</script>

<style scoped>
.security-settings-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}
.header-left, .header-right {
  width: 24px;
}
h1 {
  font-size: 18px;
  font-weight: 600;
}
.settings-list {
  margin-top: 16px;
  background-color: #fff;
}
.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
}
.setting-text .title {
  font-size: 16px;
}
.setting-text .subtitle {
  font-size: 14px;
  color: #888;
}
</style> 