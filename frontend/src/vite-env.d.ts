/// <reference types="vite/client" />
interface ImportMetaEnv {
  readonly VITE_APP_TITLE: string
  readonly VITE_APP_BASE_API: string
  readonly VITE_APP_PROXY_URL: string
  // 更多环境变量...
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
declare const __APP_ENV__: string
declare const __APP_VERSION__: string
declare module '*.vue' {
  import type {
    DialogProviderInst,
    MessageProviderInst,
    NotificationProviderInst,
  } from 'naive-ui'
  import type { defineComponent } from 'vue'
  // 增加全局类型
  global {
    interface Window {
      $message: MessageProviderInst
      $dialog: DialogProviderInst
      $notification: NotificationProviderInst
    }
  }
  const component: ReturnType<typeof defineComponent>
  export default component
}
