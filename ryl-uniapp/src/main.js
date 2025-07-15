import "@/styles/tailwind.css";
import { createSSRApp } from "vue";
import App from "./App.vue";
import { createPinia } from 'pinia';
import { setupInterceptors } from '@/utils/interceptor';

export function createApp() {
  const app = createSSRApp(App);
  app.use(createPinia());

  // 在应用初始化时设置拦截器
  setupInterceptors();

  return {
    app,
  };
}