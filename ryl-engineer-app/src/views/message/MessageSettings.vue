<template>
  <div class="message-settings-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-left" @click="goBack">
        <i class="icon-arrow-left"></i>
      </div>
      <h1>消息设置</h1>
    </header>

    <div class="settings-container">
      <!-- 通知开关 -->
      <div class="settings-section">
        <h2 class="section-title">通知设置</h2>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">接收消息通知</div>
            <div class="item-description">开启后将接收所有消息通知</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.enableNotifications">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 通知类型 -->
      <div class="settings-section" v-if="settings.enableNotifications">
        <h2 class="section-title">通知类型</h2>
        
        <div 
          v-for="(type, index) in notificationTypes" 
          :key="index"
          class="settings-item"
        >
          <div class="item-content">
            <div class="item-title">{{ type.label }}</div>
            <div class="item-description">{{ type.description }}</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="type.enabled">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 通知方式 -->
      <div class="settings-section" v-if="settings.enableNotifications">
        <h2 class="section-title">通知方式</h2>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">应用内通知</div>
            <div class="item-description">在应用内显示消息通知</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.inAppNotifications">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">推送通知</div>
            <div class="item-description">在手机上显示推送通知</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.pushNotifications">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">声音提醒</div>
            <div class="item-description">收到通知时播放声音</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.soundNotifications">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">振动提醒</div>
            <div class="item-description">收到通知时振动</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.vibrationNotifications">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
      </div>

      <!-- 免打扰时段 -->
      <div class="settings-section" v-if="settings.enableNotifications">
        <h2 class="section-title">免打扰时段</h2>
        
        <div class="settings-item">
          <div class="item-content">
            <div class="item-title">启用免打扰</div>
            <div class="item-description">在指定时段内不接收通知</div>
          </div>
          <div class="item-control">
            <label class="toggle-switch">
              <input type="checkbox" v-model="settings.enableDoNotDisturb">
              <span class="toggle-slider"></span>
            </label>
          </div>
        </div>
        
        <div v-if="settings.enableDoNotDisturb" class="time-settings">
          <div class="time-item">
            <label>开始时间</label>
            <input type="time" v-model="settings.doNotDisturbStart">
          </div>
          <div class="time-item">
            <label>结束时间</label>
            <input type="time" v-model="settings.doNotDisturbEnd">
          </div>
        </div>
      </div>

      <!-- 保存按钮 -->
      <div class="settings-actions">
        <button class="save-btn" @click="saveSettings">保存设置</button>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'MessageSettingsPage',
  setup() {
    const router = useRouter()

    const goBack = () => {
      router.back()
    }

    return {
      goBack
    }
  },
  data() {
    return {
      settings: {
        enableNotifications: true,
        inAppNotifications: true,
        pushNotifications: true,
        soundNotifications: true,
        vibrationNotifications: true,
        enableDoNotDisturb: false,
        doNotDisturbStart: '22:00',
        doNotDisturbEnd: '08:00'
      },
      notificationTypes: [
        {
          label: '系统通知',
          description: '系统更新、维护等通知',
          enabled: true
        },
        {
          label: '任务通知',
          description: '新任务、任务更新等通知',
          enabled: true
        },
        {
          label: '协作邀请',
          description: '其他工程师的协作邀请',
          enabled: true
        },
        {
          label: '仓库通知',
          description: '配件申请、库存更新等通知',
          enabled: true
        }
      ]
    }
  },
  methods: {
    saveSettings() {
      console.log('保存设置:', {
        settings: this.settings,
        notificationTypes: this.notificationTypes
      })
      
      // 实际项目中，这里会调用API保存设置
      
      // 显示保存成功提示
      alert('设置已保存')
    },
    loadSettings() {
      // 实际项目中，这里会从API获取设置
      console.log('加载设置')
    }
  },
  created() {
    this.loadSettings()
  }
}
</script>

<style scoped>
.message-settings-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  width: 24px;
}

.icon-arrow-left::before {
  content: '\f060';
  font-family: 'Font Awesome 6 Free';
  font-weight: 900;
  color: #666;
}

.header h1 {
  font-size: 18px;
  font-weight: 600;
  margin-left: 16px;
}

.settings-container {
  padding: 16px;
}

.settings-section {
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  overflow: hidden;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.settings-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.settings-item:last-child {
  border-bottom: none;
}

.item-content {
  flex: 1;
}

.item-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.item-description {
  font-size: 14px;
  color: #6b7280;
}

.item-control {
  display: flex;
  align-items: center;
  margin-left: 16px;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #e5e7eb;
  transition: .4s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: #3b82f6;
}

input:checked + .toggle-slider:before {
  transform: translateX(26px);
}

.time-settings {
  display: flex;
  gap: 16px;
  padding: 0 16px 16px;
}

.time-item {
  flex: 1;
}

.time-item label {
  display: block;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.time-item input {
  width: 100%;
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.settings-actions {
  margin-top: 24px;
}

.save-btn {
  width: 100%;
  padding: 12px;
  background-color: #3b82f6;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
}
</style> 