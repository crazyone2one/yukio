import { RouteRecordRaw } from 'vue-router'
import { CaseManagementRouteEnum } from '/@/enums/route-enum'
const CaseManagement: RouteRecordRaw = {
  path: '/case-management',
  name: CaseManagementRouteEnum.CASE_MANAGEMENT,
  redirect: '/case-management/featureCase',
  component: () => import(`/@/layout/DefaultLayout.vue`),
  meta: {
    locale: 'menu.caseManagement',
    icon: 'icon-icon_functional_testing',
    order: 3,
    hideChildrenInMenu: true,
    roles: ['FUNCTIONAL_CASE:READ', 'CASE_REVIEW:READ'],
  },
  children: [
    // 功能用例
    {
      path: 'featureCase',
      name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
      component: () => import('/@/views/case-management/index.vue'),
      meta: {
        locale: 'menu.caseManagement.featureCase',
        roles: ['FUNCTIONAL_CASE:READ'],
        isTopMenu: true,
      },
    },
  ],
}
export default CaseManagement
