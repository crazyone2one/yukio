import { createMemoryHistory, createRouter } from 'vue-router'
import DefaultLayout from '../layout/DefaultLayout.vue'
const routes = [{ path: '/', component: DefaultLayout }]

const router = createRouter({
  history: createMemoryHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})
export default router
