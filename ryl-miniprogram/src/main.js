import { createApp } from 'vue';
import { createPinia } from 'pinia';
import router from './router';
import App from './App.vue';
import './styles/main.css';

const app = createApp(App);

app.use(createPinia());
app.use(router);

// 挂载应用
app.mount('#app');