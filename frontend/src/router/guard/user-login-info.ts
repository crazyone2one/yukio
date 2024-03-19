import type { LocationQueryRaw, Router } from 'vue-router'
import { hasToken } from '/@/utils/auth'
export default function setupUserLoginInfoGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    // if (isLoginExpires()) {
    //   clearToken()
    // }
    if (to.name !== 'login' && hasToken()) {
      next()
    } else {
      // 未登录的都直接跳转至登录页，访问的页面地址缓存到 query 上
      if (to.name === 'login') {
        next()
        return
      }

      next({
        name: 'login',
        query: {
          redirect: to.name,
          ...to.query,
        } as LocationQueryRaw,
      })
    }
  })
}
