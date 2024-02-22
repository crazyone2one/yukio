import 'virtual:uno.css'
import { createApp } from 'vue'
import App from './App.vue'
import { setupI18n } from './locale'
import router from './router'
import pinia from './store'
// import './style.css'
import naive from './utils/naive'

async function bootstrap() {
    const app = createApp(App)
    await setupI18n(app)
    app.use(pinia).use(naive).use(router)
    app.mount('#app')
}
bootstrap()
