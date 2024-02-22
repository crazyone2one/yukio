import { createRouter, createWebHashHistory } from 'vue-router'
const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            redirect: '/hello',
            component: () => import('/@/layout/DefaultLayout.vue'),
            children: [
                {
                    path: '/hello',
                    name: 'hello',
                    component: () => import('/@/components/HelloWorld.vue'),
                },
            ],
        },
    ],
    scrollBehavior() {
        return { top: 0 }
    },
})
export default router
