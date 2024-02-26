import type { Router } from 'vue-router'
import { useAppStore } from '/@/store'
import { setRouteEmitter } from '/@/utils/route-listener'
import setupUserLoginInfoGuard from './use-login-info'
function setupPageGuard(router: Router) {
    router.beforeEach((to, from, next) => {
        setRouteEmitter(to)
        const urlOrgId = to.query.organizationId
        const urlProjectId = to.query.projectId
        const appStore = useAppStore()
        // 如果访问页面的时候携带了项目 ID 或组织 ID，则将页面上的组织 ID和项目 ID设置为当前选中的组织和项目
        if (urlOrgId) {
            appStore.setCurrentOrgId(urlOrgId as string)
        }
        if (urlProjectId) {
            appStore.setCurrentProjectId(urlProjectId as string)
        }
        next();
    })
}
export default function createRouteGuard(router: Router) {
    // 设置路由监听守卫
    setupPageGuard(router);
    // 设置用户登录校验守卫
    setupUserLoginInfoGuard(router);
    // 设置菜单权限守卫
    // setupPermissionGuard(router);
  }