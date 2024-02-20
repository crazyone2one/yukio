import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
    // 根据当前工作目录中的 `mode` 加载 .env 文件
    // 设置第三个参数为 '' 来加载所有环境变量，而不管是否有 `VITE_` 前缀。
    const env = loadEnv(mode, process.cwd(), '')
    return {
        // vite 配置
        plugins: [vue()],
        server: {
            host: true,
        },
        define: {
            __APP_ENV__: JSON.stringify(env.APP_ENV),
            __APP_VERSION__: JSON.stringify('v1.x'),
        },
    }
})
