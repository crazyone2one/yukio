import { createRouter, createWebHashHistory } from 'vue-router'
import DefaultLayout from '../layout/DefaultLayout.vue'
import appRoutes from './routes'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', component: DefaultLayout },
    {
      path: '/login',
      name: 'login',
      component: () => import(`/@/views/login/index.vue`),
    },
    ...appRoutes,
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})
// createRouteGuard(router)
export default router
