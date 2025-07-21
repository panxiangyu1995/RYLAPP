import { useUserStore } from '@/stores/user';

/**
 * 一个高阶函数，用于保护需要认证的操作。
 * @param {Function} action - 需要在用户登录后执行的函数。
 * @returns {Function} - 一个新的函数，在执行前会检查用户登录状态。
 */
export function withAuth(action) {
  return function(...args) {
    const userStore = useUserStore();
    const isLoggedIn = userStore.isLoggedIn;

    if (isLoggedIn) {
      // 如果用户已登录，直接执行原始操作
      action(...args);
    } else {
      // 如果用户未登录，显示模态对话框
      uni.showModal({
        title: '登录提示',
        content: '此功能需要登录后才能使用，是否前往登录页面？',
        success: function(res) {
          if (res.confirm) {
            // 用户点击确定，跳转到登录页
            uni.navigateTo({
              url: '/pages/login/login'
            });
          }
          // 如果用户点击取消，则不执行任何操作
        }
      });
    }
  };
} 