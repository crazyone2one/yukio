import { Router } from 'vue-router'
import setupUserLoginInfoGuard from './user-login-info'
import useAppStore from '/@/store/modules/app'
import { setRouteEmitter } from '/@/utils/route-listener'

function setupPageGuard(router: Router) {
  router.beforeEach((to, from, next) => {
    // 监听路由变化
    setRouteEmitter(to)
    const appStore = useAppStore()
    const urlOrgId = to.query.orgId
    const urlProjectId = to.query.pId
    // 如果访问页面的时候携带了项目 ID 或组织 ID，则将页面上的组织 ID和项目 ID设置为当前选中的组织和项目
    if (urlOrgId) {
      appStore.setCurrentOrgId(urlOrgId as string)
    }
    if (urlProjectId) {
      appStore.setCurrentProjectId(urlProjectId as string)
    }
    if (urlOrgId === undefined && urlProjectId === undefined) {
      to.query = {
        ...to.query,
        orgId: appStore.currentOrgId,
        pId: appStore.currentProjectId,
      }

      next(to)
    }
    next()
  })
}
export default function createRouteGuard(router: Router) {
  // 设置路由监听守卫
  setupPageGuard(router)
  // 设置用户登录校验守卫
  // setupUserLoginInfoGuard(router)
  // 设置菜单权限守卫
  // setupPermissionGuard(router);
}
