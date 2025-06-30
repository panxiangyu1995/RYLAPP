<template>
  <div class="contacts-layout">
    <header class="header">
      <div class="left">
        <router-link to="/home">
          <i class="material-icons">arrow_back</i>
        </router-link>
        <h1>联系人</h1>
      </div>
      <div class="right">
        <i class="material-icons">search</i>
        <i class="material-icons">more_vert</i>
      </div>
    </header>
    
    <div class="tabs">
      <router-link to="/contacts/list" class="tab" active-class="active-tab">全部</router-link>
      <router-link to="/contacts/groups" class="tab" active-class="active-tab">分组</router-link>
      <router-link to="/contacts/favorites" class="tab" active-class="active-tab">收藏</router-link>
    </div>
    
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: 'ContactsLayout'
}
</script>

<style scoped>
.contacts-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.header {
  background-color: #2196f3;
  color: white;
  padding: 10px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.left {
  display: flex;
  align-items: center;
}

.left i {
  margin-right: 16px;
}

.right {
  display: flex;
}

.right i {
  margin-left: 16px;
}

.tabs {
  display: flex;
  background-color: #2196f3;
  color: white;
  padding: 0 15px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
}

.active-tab {
  color: white;
  border-bottom: 3px solid white;
  font-weight: bold;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background-color: #f5f5f5;
}
</style> 