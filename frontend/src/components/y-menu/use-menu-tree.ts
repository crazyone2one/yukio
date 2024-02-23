import { cloneDeep } from 'lodash-es'
import { computed } from 'vue'
import { RouteRecordNormalized, RouteRecordRaw } from 'vue-router'
import appClientMenus from '/@/router/app-menus'

export default function useMenuTree() {
    const menuTree = computed(() => {
        const copyRouter = cloneDeep(appClientMenus) as RouteRecordNormalized[]
        copyRouter.sort(
            (a: RouteRecordNormalized, b: RouteRecordNormalized) => {
                return (
                    ((a.meta.order as number) || 0) -
                    ((b.meta.order as number) || 0)
                )
            },
        )
        function travel(_routes: RouteRecordRaw[], layer: number) {
            if (!_routes) return null

            const collector = _routes.map((element) => {
                // 权限校验不通过
                //   if (!permission.accessRouter(element)) {
                //     return null;
                //   }

                // 叶子菜单
                if (element.meta?.hideChildrenInMenu || !element.children) {
                    element.children = []
                    return element
                }

                // 过滤隐藏的菜单
                element.children = element.children.filter(
                    (x) => x.meta?.hideInMenu !== true,
                )

                // 解析子菜单
                const subItem = travel(element.children, layer + 1)

                if (subItem && subItem.length) {
                    element.children = subItem as RouteRecordRaw[]
                    return element
                }
                // the else logic
                if (layer > 1) {
                    element.children = subItem as RouteRecordRaw[]
                    return element
                }

                if (element.meta?.hideInMenu === false) {
                    return element
                }

                return null
            })
            return collector.filter(Boolean)
        }
        return travel(copyRouter, 0)
    })
    return {
        menuTree,
    }
}
