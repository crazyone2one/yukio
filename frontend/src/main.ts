import 'virtual:uno.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'
import './style.css'
import naive from './utils/naive'

const app = createApp(App)
app.use(pinia).use(router).use(naive)
app.mount('#app')
