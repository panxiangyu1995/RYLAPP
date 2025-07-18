import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import { UnifiedViteWeappTailwindcssPlugin as uvwt } from 'weapp-tailwindcss/vite';

// 注意： Vite 中使用 PostCSS，Vite 会自动查找使用到的 PostCSS 插件
// https://vitejs.dev/guide/features.html#postcss

// 在 uni-app 使用 PostCSS
// https://uniapp.dcloud.net.cn/tutorial/migration-to-vue3.html#postcss

export default defineConfig({
  // uni 是 uni-app 官方插件， uvwt 一定要放在 uni 插件之后，对生成的文件进行处理
  plugins: [uni(), uvwt()],
  server: {
    port: 4000,
    proxy: {
      '/api': {
        target: 'http://api.jsryl.top',
        changeOrigin: true,
        rewrite: (path) => path, // 不修改路径，保留/api前缀
        secure: false, // 接受无效证书
        ws: true, // 支持WebSocket
        configure: (proxy) => {
          // 设置更长的超时时间
          proxy.on('proxyReq', function(proxyReq, req, res) {
            proxyReq.setHeader('Connection', 'keep-alive');
          });
          proxy.on('error', function(err, req, res) {
            console.error('代理错误:', err);
          });
        }
      },
      '/uploads': {
        target: 'http://api.jsryl.top',
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on('error', function(err, req, res) {
            console.error('上传文件代理错误:', err);
          });
        }
      }
    }
  },
  // 假如 postcss.config.js 不起作用，请使用内联配置
  // https://github.com/dcloudio/uni-app/issues/3223
  css: {
    postcss: {
      plugins: [require("tailwindcss"), require("autoprefixer")],
    },
  },
});
