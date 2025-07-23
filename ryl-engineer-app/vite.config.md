import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
  // 获取项目根目录的绝对路径
  const root = path.resolve(__dirname)
  
  // 从项目根目录加载环境变量
  const env = loadEnv(mode, root, '')
  
  console.log(`当前环境: ${mode}, API地址: ${env.VITE_API_BASE_URL || '未设置'}`)
  
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8089',
          changeOrigin: true,
          rewrite: (path) => path
        },
        '/uploads': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8089',
          changeOrigin: true
        }
      }
    }
  }
})
