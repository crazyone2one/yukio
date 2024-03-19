import vue from '@vitejs/plugin-vue'
import path from 'path'
import UnoCSS from 'unocss/vite'
import { defineConfig, loadEnv } from 'vite'

// https://vitejs.dev/config/

export default defineConfig(({ mode }) => {
  // 根据当前工作目录中的 `mode` 加载 .env 文件
  // 设置第三个参数为 '' 来加载所有环境变量，而不管是否有 `VITE_` 前缀。
  const env = loadEnv(mode, process.cwd(), '')
  return {
    // vite 配置
    plugins: [vue(), UnoCSS()],
    server: {
      host: true,
      proxy: {
        [env.VITE_APP_BASE_API]: {
          target: env.VITE_APP_PROXY_URL,
          changeOrigin: true,
          rewrite: (path) =>
            path.replace(new RegExp('^' + env.VITE_APP_BASE_API), ''),
        },
      },
    },
    resolve: {
      alias: [
        // /@/xxxx => src/xxxx
        {
          find: /\/@\//,
          replacement: path.resolve(__dirname, '.', 'src') + '/',
        },
      ],
    },
    define: {
      __APP_ENV__: JSON.stringify(env.APP_ENV),
      __APP_VERSION__: JSON.stringify('v1.x'),
      __VUE_OPTIONS_API__: true,
      __VUE_PROD_DEVTOOLS__: false,
      // 设置hydration不匹配详细信息的标志
      __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: true,
    },
  }
})
