import pagesConfig from '../pages.json';
import { useUserStore } from '@/stores/user';

// 拦截器核心逻辑
async function interceptorLogic(args) {
  const userStore = useUserStore();
  // 使用userStore.isLoggedIn判断登录状态
  const isLoggedIn = userStore.isLoggedIn;
  const url = args.url.split('?')[0];
  
  console.log('[拦截器] 当前页面:', url, '登录状态:', isLoggedIn, '用户信息:', userStore.hasUserInfo);

  // 从导入的 pages.json 中获取页面配置，替代之前失败的 __PAGES__ 全局变量方案
  const pages = pagesConfig.pages;
  const pageInfo = pages.find(p => `/${p.path}` === url);

  // 默认不需要登录
  let needLogin = false;
  if (pageInfo && pageInfo.needLogin) {
    needLogin = true;
    console.log('[拦截器] 该页面需要登录:', needLogin);
  }
  
  // 如果已登录但 Pinia 中没有用户信息，则异步获取
  if (isLoggedIn && !userStore.hasUserInfo) {
    console.log('[拦截器] 已登录但没有用户信息，尝试获取用户信息');
    try {
      await userStore.getUserInfo();
      console.log('[拦截器] 获取用户信息成功');
    } catch (error) {
      // 获取用户信息失败，可能 token 已过期，发出需要重定向的信号
      console.error('[拦截器] 获取用户信息失败，可能token已过期:', error);
      userStore.logout();
      uni.$emit('needs-login-redirect');
    }
  }

  // 如果目标页面需要登录，但用户未登录，则发出需要重定向的信号
  if (needLogin && !isLoggedIn) {
    console.log('[拦截器] 页面需要登录但用户未登录，准备重定向到登录页');
    uni.$emit('needs-login-redirect');
  }

  return args; // 总是放行，让 App.vue 中的监听器处理实际的跳转
}

export function setupInterceptors() {
  const methodsToIntercept = ['navigateTo', 'redirectTo', 'reLaunch'];

  methodsToIntercept.forEach(method => {
    uni.addInterceptor(method, {
      invoke: interceptorLogic,
      fail(err) {
        console.error(`Interceptor error on ${method}:`, err);
      }
    });
  });
} 