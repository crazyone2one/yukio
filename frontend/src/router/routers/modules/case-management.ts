import { CaseManagementRouteEnum } from '/@/enums/route-enum'

import { RouteRecordRaw } from 'vue-router'
import { DEFAULT_LAYOUT } from '../base'
const CaseManagement: RouteRecordRaw = {
    path: '/case-management',
    name: CaseManagementRouteEnum.CASE_MANAGEMENT,
    redirect: '/case-management/featureCase',
    component: DEFAULT_LAYOUT,
    meta: {
        locale: 'menu.caseManagement',
        icon: 'icon-icon_functional_testing',
        order: 3,
        hideChildrenInMenu: true,
        roles: ['FUNCTIONAL_CASE:READ', 'CASE_REVIEW:READ'],
    },
    children: [
        {
            path: 'featureCase',
            name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
            component: () => import('/@/views/case-management/case-management-feature/index.vue'),
            meta: {
                locale: 'menu.caseManagement.featureCase',
                roles: ['FUNCTIONAL_CASE:READ'],
                isTopMenu: true,
            },
        },
        // 用例评审
        {
            path: 'caseManagementReview',
            name: CaseManagementRouteEnum.CASE_MANAGEMENT_REVIEW,
            component: () => import('/@/views/case-management/case-review/index.vue'),
            meta: {
                locale: 'menu.caseManagement.caseManagementReview',
                roles: ['CASE_REVIEW:READ'],
                isTopMenu: true,
            },
        },
    ],
}
export default CaseManagement
