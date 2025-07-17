<template>
    <view class="p-4 min-h-screen bg-ui-bg-white">
      <view class="mb-4 flex items-center">
        <button @click="goBack" class="mr-2">
          <text class="text-xl">&larr;</text>
        </button>
        <view class="text-xl font-bold text-ui-text-black">注册账号</view>
      </view>
      
      <!-- 错误提示 -->
      <view v-if="errorMessage" class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative">
        <view class="block sm:inline">{{ errorMessage }}</view>
        <text class="absolute top-0 bottom-0 right-0 px-4 py-3" @click="errorMessage = ''">
           <XIcon :size="20" color="#000000" />
        </text>
      </view>
      
      <view class="bg-white rounded-lg shadow-md p-6">
        <form @submit="handleRegister">
          <!-- 账号信息 -->
          <view class="mb-6">
            <view class="text-lg font-bold mb-4 text-ui-text-black">账号信息</view>
            
            <!-- 用户名 - 简化版 -->
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="contact">
                登录用户名 <text class="text-red-500">*</text>
              </view>
              <ContactInput 
                :value="form.contact"
                @update="handleContactUpdate"
              />
              <view class="text-gray-500 text-xs mt-1">此用户名将用于登录系统</view>
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="password">
                密码 <text class="text-red-500">*</text>
              </view>
              <view class="relative">
                <input 
                  id="password" 
                  v-model="form.password" 
                  :type="showPassword ? 'text' : 'password'" 
                  class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                  placeholder="请输入密码"
                />
                <button 
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute right-3 top-2.5 text-gray-500"
                >
                  {{ showPassword ? '隐藏' : '显示' }}
                </button>
              </view>
              <view v-if="form.password && form.password.length < 6" class="text-red-500 text-xs mt-1">密码至少需要6个字符</view>
              <view v-else class="text-gray-500 text-xs mt-1">密码长度至少6位，建议包含字母和数字</view>
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="confirmPassword">
                确认密码 <text class="text-red-500">*</text>
              </view>
              <input 
                id="confirmPassword" 
                v-model="form.confirmPassword" 
                type="password" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请再次输入密码"
              />
              <view v-if="passwordMismatch" class="text-red-500 text-xs mt-1">两次输入的密码不一致</view>
            </view>
          </view>
          
          <!-- 客户信息 -->
          <view class="mb-6">
            <view class="text-lg font-bold mb-4 text-ui-text-black">客户信息</view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="customerName">
                客户姓名 <text class="text-red-500">*</text>
              </view>
              <input 
                id="customerName" 
                v-model="form.realName" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入姓名"
              />
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="companyName">
                公司名 <text class="text-red-500">*</text>
              </view>
              <input 
                id="companyName" 
                v-model="form.name" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入公司名称"
              />
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="department">
                部门
              </view>
              <input 
                id="department" 
                v-model="form.department" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入部门名称"
              />
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="position">
                职位
              </view>
              <input 
                id="position" 
                v-model="form.position" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入职位"
              />
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="phone">
                联系电话 <text class="text-red-500">*</text>
              </view>
              <input 
                id="phone" 
                v-model="form.phone" 
                type="tel" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入手机号码"
              />
              <view v-if="form.phone && !isValidPhone(form.phone)" class="text-red-500 text-xs mt-1">请输入有效的手机号码</view>
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="address">
                联系地址
              </view>
              <input 
                id="address" 
                v-model="form.address" 
                type="text" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入联系地址"
              />
            </view>
            
            <view class="mb-4">
              <view class="block text-ui-text-black text-sm font-bold mb-2" for="email">
                联系邮箱
              </view>
              <input 
                id="email" 
                v-model="form.email" 
                type="email" 
                class="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-ui-blue-end"
                placeholder="请输入电子邮箱"
              />
              <view v-if="form.email && !isValidEmail(form.email)" class="text-red-500 text-xs mt-1">请输入有效的邮箱地址</view>
            </view>
          </view>
          
          <!-- 用户协议 -->
          <view class="mb-6">
            <view class="flex items-center">
              <checkbox-group @change="form.agreement = !!$event.detail.value.length">
                  <checkbox :checked="form.agreement" />
              </checkbox-group>
              <view class="text-sm ml-2">
                我已阅读并同意
                <navigator url="/pages/agreement/agreement" class="text-ui-blue-start inline">《用户协议》</navigator>
                和
                <navigator url="/pages/privacy/privacy" class="text-ui-blue-start inline">《隐私政策》</navigator>
              </view>
            </view>
          </view>
          
          <button 
            form-type="submit" 
            :disabled="loading || !isFormValid"
            :class="['w-full py-3 rounded-lg text-white', isFormValid ? 'bg-ui-vibrant-gradient' : 'bg-gray-400']"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </button>
        </form>
      </view>
      
      <!-- 悬浮错误提示 -->
      <view 
        v-if="floatingError" 
        class="fixed bottom-0 left-0 right-0 bg-red-500 text-white py-3 px-4 flex justify-between items-center"
        style="z-index: 9999;"
      >
        <view>{{ floatingError }}</view>
        <view @click="floatingError = ''" class="ml-2 px-2">✕</view>
      </view>
    </view>
</template>

<script setup>
  import { ref, computed, watch } from 'vue';
  import { useUserStore } from '@/stores/user';
  import ContactInput from '@/components/ContactInput.vue';
  import XIcon from '@/components/icons/XIcon.vue';
  
  const userStore = useUserStore();
  const loading = ref(false);
  const showPassword = ref(false);
  const errorMessage = ref('');
  const floatingError = ref(''); // 悬浮错误提示
  
  const passwordMismatch = computed(() => {
    return form.value.confirmPassword && form.value.password !== form.value.confirmPassword;
  });
  
  // 注册表单
  const form = ref({
    contact: '', // 登录用户名
    password: '',
    confirmPassword: '',
    realName: '', // 客户真实姓名
    name: '',
    department: '',
    position: '',
    phone: '',
    address: '',
    email: '',
    agreement: false
  });
  
  // 监听用户名变化，清除错误提示
  watch(() => form.value.contact, (newVal, oldVal) => {
    if (newVal !== oldVal && (errorMessage.value || floatingError.value)) {
      // 如果用户名发生变化，清除所有与用户名相关的错误提示
      if (errorMessage.value && errorMessage.value.includes('用户名')) {
        errorMessage.value = '';
      }
      if (floatingError.value && floatingError.value.includes('用户名')) {
        floatingError.value = '';
      }
    }
  });
  
  // 处理用户名更新
  const handleContactUpdate = (value) => {
    form.value.contact = value;
    // 清除与用户名相关的错误提示
    if (errorMessage.value && errorMessage.value.includes('用户名')) {
      errorMessage.value = '';
    }
    if (floatingError.value && floatingError.value.includes('用户名')) {
      floatingError.value = '';
    }
  };
  
  // 表单验证函数
  const isValidEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };
  
  const isValidPhone = (phone) => {
    const re = /^1[3-9]\d{9}$/;
    return re.test(phone);
  };
  
  // 表单验证计算属性
  const isFormValid = computed(() => {
    const basicValidation = 
      form.value.contact.length >= 4 && 
      form.value.password.length >= 6 && 
      !passwordMismatch.value &&
      form.value.realName &&
      form.value.name &&
      form.value.phone && isValidPhone(form.value.phone) &&
      form.value.agreement;
    return basicValidation;
  });
  
  // 处理注册
  const handleRegister = async () => {
    if (!isFormValid.value) {
      errorMessage.value = '请检查表单信息是否完整且正确';
      floatingError.value = '请检查表单信息是否完整且正确';
      return;
    }
    
    loading.value = true;
    errorMessage.value = '';
    floatingError.value = '';
    
    try {
      // 先检查用户名是否可用 - 每次提交都重新检查，不依赖缓存
      const isContactAvailable = await userStore.checkContact(form.value.contact);
      
      if (!isContactAvailable) {
        const errorMsg = '该用户名已被使用，请更换';
        errorMessage.value = errorMsg;
        floatingError.value = errorMsg;
        loading.value = false;
        return;
      }
      
      // 用户名可用，继续注册流程
      const registrationData = {
        contact: form.value.contact, // 登录用户名
        password: form.value.password,
        realName: form.value.realName, // 客户真实姓名
        name: form.value.name,
        department: form.value.department,
        position: form.value.position,
        phone: form.value.phone,
        address: form.value.address,
        email: form.value.email
      };
      
      const res = await userStore.register(registrationData);
      
      uni.showToast({
        title: '注册成功！',
        icon: 'success',
        duration: 2000,
        complete: () => {
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/login' });
          }, 2000);
        }
      });
      
    } catch (error) {
      console.error('注册失败:', error);
      const errorMsg = error.message || '注册失败，请稍后重试';
      errorMessage.value = errorMsg;
      floatingError.value = errorMsg;
    } finally {
      loading.value = false;
    }
  };

  const goBack = () => {
    uni.navigateBack();
  };

  const goToLogin = () => {
    uni.navigateTo({ url: '/pages/login/login' });
  };
</script>

<style scoped>
/* 样式 */
</style>