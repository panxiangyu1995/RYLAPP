<template>
  <div class="bottom-nav">
    <div 
      v-for="(item, index) in navItems" 
      :key="index" 
      class="nav-item" 
      :class="{ active: currentRoute === item.route }"
      @click="navigate(item.route)"
    >
      <i :class="['nav-icon', item.icon]"></i>
      <span class="nav-text">{{ item.text }}</span>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'BottomNavigation',
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const navItems = [
      { text: '首页', icon: 'icon-home', route: '/home' },
      { text: '仓库', icon: 'icon-warehouse', route: '/warehouse' },
      { text: '联系人', icon: 'icon-address-book', route: '/contacts' },
      { text: '消息', icon: 'icon-message', route: '/messages' },
      { text: '我的', icon: 'icon-user', route: '/profile' }
    ]
    
    const currentRoute = computed(() => {
      // 检查是否是从任务详情页进入的其他页面
      const fromTaskDetail = route.query.from === 'taskDetail';
      
      // 任务详情页面及相关页面高亮首页图标
      if (route.path.includes('/task/') || 
          route.path.includes('/task-detail/') ||
          route.path.includes('/task-flow/') ||
          // 只有从任务详情页进入的客户信息和打卡页面才高亮首页
          (route.path.includes('/customer/') && fromTaskDetail) ||
          (route.path.includes('/location') && fromTaskDetail)) {
        return '/home'
      }
      
      // 联系人路由及子路由处理
      if (route.path.includes('/contacts') || route.path === '/social-feed') {
        return '/contacts'
      }
      
      // 否则返回当前路径的第一段作为当前路由
      return '/' + route.path.split('/')[1]
    })
    
    const navigate = (path) => {
      // 获取当前路由的主路径
      const currentMainPath = '/' + route.path.split('/')[1]
      
      // 如果点击的是当前激活的导航项，则刷新并回到对应的主页面
      if (path === currentMainPath) {
        // 根据不同的路径执行不同的刷新逻辑
        switch (path) {
          case '/home':
            router.replace({ path: '/home', force: true })
            break
          case '/warehouse':
            router.replace({ path: '/warehouse', force: true })
            break
          case '/contacts':
            router.replace({ path: '/contacts/engineers', force: true })
            break
          case '/messages':
            router.replace({ path: '/messages', force: true })
            break
          case '/profile':
            router.replace({ path: '/profile', force: true })
            break
          default:
            router.replace({ path, force: true })
        }
      } else {
        // 如果点击的不是当前激活的导航项，则正常导航
        router.push(path)
      }
    }
    
    return {
      navItems,
      currentRoute,
      navigate
    }
  }
}
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 56px;
  background-color: #fff;
  display: flex;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  cursor: pointer;
}

.nav-item.active {
  color: #3b82f6;
}

.nav-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.nav-icon::before {
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
}

.icon-home::before {
  content: '\f015';
}

.icon-warehouse::before {
  content: '\f494';
}

.icon-address-book::before {
  content: '\f2b9';
}

.icon-message::before {
  content: '\f0e0';
}

.icon-user::before {
  content: '\f007';
}

.nav-text {
  font-size: 12px;
}
</style> 