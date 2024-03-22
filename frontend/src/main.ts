import 'virtual:uno.css'
import { createApp } from 'vue'
import App from './App.vue'
import directive from './directive'
import { setupI18n } from './locale'
import router from './router'
import pinia from './store'
import './style.css'
import naive from './utils/naive'

async function bootstrap() {
  const app = createApp(App)
  app.use(pinia).use(router).use(naive)
  await setupI18n(app)
  app.use(directive)
  app.mount('#app')
}

bootstrap()
