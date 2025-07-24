<template>
  <div class="page-container">
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="fas fa-arrow-left"></i>
      </div>
      <h1>{{ pageTitle }}</h1>
      <div class="header-right"></div>
    </header>

    <div class="form-container">
      <form @submit.prevent="handleSubmit">
        <div v-if="isChangeMode" class="form-group">
          <label for="oldPassword">当前安全密码</label>
          <input type="password" id="oldPassword" v-model="form.oldPassword" required />
        </div>
        <div class="form-group">
          <label for="newPassword">新安全密码</label>
          <input type="password" id="newPassword" v-model="form.newPassword" required />
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认新密码</label>
          <input type="password" id="confirmPassword" v-model="form.confirmPassword" required />
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '处理中...' : '确认' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/auth';
import { setSecurityPassword, changeSecurityPassword } from '../../api/user';

const router = useRouter();
const authStore = useAuthStore();

const isChangeMode = computed(() => authStore.user?.hasSecurityPassword || false);
const pageTitle = computed(() => isChangeMode.value ? '修改安全密码' : '设置安全密码');

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const loading = ref(false);
const error = ref('');

const goBack = () => router.back();

const handleSubmit = async () => {
  if (form.newPassword !== form.confirmPassword) {
    error.value = '两次输入的新密码不一致';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    let response;
    if (isChangeMode.value) {
      response = await changeSecurityPassword(form.oldPassword, form.newPassword);
    } else {
      response = await setSecurityPassword(form.newPassword);
    }

    if (response.code === 200) {
      // 更新store中的状态
      await authStore.fetchUserInfo();
      alert('操作成功');
      router.back();
    } else {
      throw new Error(response.message || '操作失败');
    }
  } catch (err) {
    error.value = err.message || '发生未知错误';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* 简化样式 */
.page-container {
  padding: 16px;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.header-left {
  cursor: pointer;
}
h1 {
  flex: 1;
  text-align: center;
  font-size: 18px;
}
.form-group {
  margin-bottom: 16px;
}
label {
  display: block;
  margin-bottom: 8px;
}
input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.error-message {
  color: red;
  margin-bottom: 16px;
}
.submit-btn {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.submit-btn:disabled {
  background-color: #aaa;
}
</style> 