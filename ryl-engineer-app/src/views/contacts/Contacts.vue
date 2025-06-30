<template>
  <div class="contacts-page">
    <!-- 顶部导航 -->
    <header class="header">
      <h1>联系人</h1>
    </header>

    <!-- 导航标签 -->
    <div class="tab-nav">
      <router-link 
        :to="{ name: 'EngineerStatus' }" 
        class="tab-item" 
        active-class="active"
      >
        工程师状态
      </router-link>
      <router-link 
        :to="{ name: 'CustomerManagement' }" 
        class="tab-item" 
        active-class="active"
      >
        客户管理
      </router-link>
      <router-link 
        :to="{ name: 'OtherContacts' }" 
        class="tab-item" 
        active-class="active"
      >
        其它联系人
      </router-link>
    </div>

    <!-- 子路由内容 -->
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'ContactsPage'
}
</script>

<style scoped>
.contacts-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
}

.tab-nav {
  display: flex;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  color: #666;
  text-decoration: none;
  font-size: 15px;
  position: relative;
}

.tab-item.active {
  color: #3b82f6;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  width: 50%;
  height: 2px;
  background-color: #3b82f6;
}
</style> 