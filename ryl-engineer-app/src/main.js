import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// 导入Bootstrap样式
import 'bootstrap/dist/css/bootstrap.min.css'
// 导入Bootstrap JS
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// 导入自定义样式
import './style.css'

// 导入Vant相关组件和样式
import { PullRefresh } from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)
// 使用Vant组件
app.use(PullRefresh)

app.mount('#app')
