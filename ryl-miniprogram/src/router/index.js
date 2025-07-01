import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/task/create',
    name: 'TaskCreate',
    component: () => import('../views/TaskCreate.vue'),
    props: (route) => ({ taskType: route.query.type })
  },
  {
    path: '/task/progress',
    name: 'TaskProgress',
    component: () => import('../views/TaskProgress.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/task/detail/:id',
    name: 'TaskDetail',
    component: () => import('../views/TaskDetail.vue'),
    props: true,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('token');
  
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else {
    next();
  }
});

export default router; 