import { createRouter, createWebHashHistory } from 'vue-router'
import createRouteGuard from './guard'
import appRoutes from './routers'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            redirect: '/workbench',
            component: () => import('/@/layout/DefaultLayout.vue'),
            children: [
                {
                    path: '/workbench',
                    name: 'workbench',
                    component: () => import(`/@/views/workbench/index.vue`),
                },
            ],
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('/@/views/login/index.vue'),
        },
        ...appRoutes,
    ],
    scrollBehavior() {
        return { top: 0 }
    },
})
createRouteGuard(router)
export default router
