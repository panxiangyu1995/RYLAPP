import { createApp } from 'vue';
import { createPinia } from 'pinia';
import router from './router';
import App from './App.vue';
import './styles/main.css';

// 导入测试函数（仅在开发环境使用）
import testGenerateTaskId from './utils/taskIdGenerator.test';

const app = createApp(App);

app.use(createPinia());
app.use(router);

// 挂载应用
app.mount('#app');

// 在开发环境中运行测试（可以在控制台查看结果）
if (import.meta.env.DEV) {
  console.log('开发环境中运行任务ID生成器测试');
  testGenerateTaskId();
} 