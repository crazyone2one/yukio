import type { LocationQueryRaw, Router } from 'vue-router'
import { hasToken } from '/@/utils/auth'

export default function setupUserLoginInfoGuard(router: Router) {
    router.beforeEach(async (to, from, next) => {
        if (to.name !== 'login' && hasToken(to.name as string)) {
            next()
        } else {
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
