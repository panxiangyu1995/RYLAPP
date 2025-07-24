import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    redirect: () => {
      const token = localStorage.getItem('token')
      const tokenExpire = localStorage.getItem('tokenExpire')
      
      // 检查token是否存在且未过期
      if (token && tokenExpire && new Date(tokenExpire) > new Date()) {
        return '/home'
      } else {
        return '/login'
      }
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/auth/ForgotPassword.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/profile/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/message/Messages.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/info',
    name: 'ProfileInfo',
    component: () => import('../views/profile/ProfileInfo.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/vehicle',
    redirect: '/profile/vehicle/list',
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/vehicle/list',
    name: 'VehicleList',
    component: () => import('../views/VehicleList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/vehicle/info/:id?',
    name: 'VehicleInfo',
    component: () => import('../views/VehicleInfo.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/vehicle/edit',
    name: 'VehicleEdit',
    component: () => import('../views/profile/ProfileVehicleEdit.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/vehicle/records',
    name: 'VehicleRecords',
    component: () => import('../views/profile/ProfileVehicle.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/change-password',
    name: 'ChangePassword',
    component: () => import('../views/profile/ChangePassword.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/security-settings',
    name: 'SecuritySettings',
    component: () => import('../views/profile/SecuritySettings.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/set-security-password',
    name: 'SetSecurityPassword',
    component: () => import('../views/profile/SetSecurityPassword.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile-info',
    redirect: '/profile/info'
  },
  {
    path: '/vehicle-record',
    redirect: '/profile/vehicle/records'
  },
  {
    path: '/warehouse',
    name: 'Warehouse',
    component: () => import('../views/Warehouse.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:id',
    name: 'WarehouseDetail',
    component: () => import('../views/warehouse/WarehouseDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/item/:itemId',
    name: 'WarehouseItemDetail',
    component: () => import('../views/warehouse/ItemDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/item/:itemId/edit',
    name: 'ItemEdit',
    component: () => import('../views/warehouse/ItemEditPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/item/add',
    name: 'ItemAdd',
    component: () => import('../views/warehouse/ItemEditPage.vue'),
    props: route => ({ isAddMode: true }),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/item/:itemId/records',
    name: 'StockRecords',
    component: () => import('../views/warehouse/StockRecordPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/stock-in',
    name: 'StockIn',
    component: () => import('../views/warehouse/StockInPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/stock-out',
    name: 'StockOut',
    component: () => import('../views/warehouse/StockOutPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/scanner',
    name: 'Scanner',
    component: () => import('../views/warehouse/ScannerPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/request',
    name: 'ItemRequest',
    component: () => import('../views/warehouse/ItemRequestPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/inventory-check',
    name: 'InventoryCheck',
    component: () => import('../views/warehouse/InventoryCheckPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/warehouse/:warehouseId/stats',
    name: 'WarehouseStats',
    component: () => import('../views/warehouse/StatsReportPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/task/:id',
    name: 'TaskDetail',
    component: () => import('../views/task/TaskDetail.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/task/:id/edit',
    name: 'EditTask',
    component: () => import('../views/task/EditTask.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/task/:id/flow',
    name: 'TaskFlowDetail',
    component: () => import('../views/task/TaskFlowDetail.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/task/create',
    name: 'CreateTask',
    component: () => import('../views/task/CreateTask.vue'),
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/location',
    name: 'Location',
    component: () => import('../views/Location.vue'),
    props: route => ({ taskId: route.query.taskId }),
    meta: { requiresAuth: true }
  },
  {
    path: '/message/:id',
    name: 'MessageDetail',
    component: () => import('../views/message/MessageDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/message-settings',
    name: 'MessageSettings',
    component: () => import('../views/message/MessageSettings.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/announcement/:id',
    name: 'AnnouncementDetail',
    component: () => import('../views/message/MessageDetail.vue'),
    props: route => ({ type: 'announcement' }),
    meta: { requiresAuth: true }
  },
  {
    path: '/assistance/:id',
    name: 'AssistanceDetail',
    component: () => import('../views/message/MessageDetail.vue'),
    props: route => ({ type: 'assistance' }),
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/new',
    name: 'NewChat',
    component: () => import('../views/message/NewChat.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/:id',
    name: 'ChatDetail',
    component: () => import('../views/message/ChatDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/customer/create',
    name: 'CreateCustomer',
    component: () => import('../views/customer/CreateCustomer.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/customer/:id',
    name: 'CustomerInfo',
    component: () => import('../views/customer/CustomerDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/engineer/:id',
    name: 'EngineerDetail',
    component: () => import('../views/contacts/EngineerDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contact/:id',
    name: 'ContactDetail',
    component: () => import('../views/contacts/ContactDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/contacts',
    component: () => import('../views/contacts/Contacts.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/contacts/engineers'
      },
      {
        path: 'engineers',
        name: 'EngineerStatus',
        component: () => import('../views/contacts/EngineerStatus.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'customers',
        name: 'CustomerManagement',
        component: () => import('../views/contacts/CustomerManagement.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'others',
        name: 'OtherContacts',
        component: () => import('../views/contacts/OtherContacts.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/social-feed',
    redirect: '/contacts/engineers'
  },
  {
    path: '/test-api',
    name: 'TestApi',
    component: () => import('../views/TestApi.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/request-assistance/:engineerId',
    name: 'RequestAssistance',
    component: () => import('../views/message/RequestAssistance.vue'),
    props: true,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const tokenExpire = localStorage.getItem('tokenExpire')
  
  // 检查token是否存在且未过期
  const isTokenValid = token && tokenExpire && new Date(tokenExpire) > new Date()
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要认证的路由
    if (!isTokenValid) {
      // 清除无效的token
      localStorage.removeItem('token')
      localStorage.removeItem('tokenExpire')
      localStorage.removeItem('user')
      
      next({ 
        name: 'Login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 不需要认证的路由
    if (isTokenValid && (to.name === 'Login' || to.name === 'Register' || to.name === 'ForgotPassword')) {
      next({ name: 'Home' })
    } else {
      next()
    }
  }
})

export default router 