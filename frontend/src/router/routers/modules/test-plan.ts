import { TestPlanRouteEnum } from '/@/enums/route-enum'

import { DEFAULT_LAYOUT } from '../base'
import type { AppRouteRecordRaw } from '../types'

const TestPlan: AppRouteRecordRaw = {
    path: '/test-plan',
    name: TestPlanRouteEnum.TEST_PLAN,
    redirect: '/test-plan/testPlanIndex',
    component: DEFAULT_LAYOUT,
    meta: {
        locale: 'menu.testPlan',
        icon: 'icon-icon_test-tracking_filled',
        order: 1,
        hideChildrenInMenu: true,
        roles: ['TEST_PLAN:READ'],
    },
    children: [
        // 测试计划
        {
            path: 'testPlanIndex',
            name: TestPlanRouteEnum.TEST_PLAN_INDEX,
            component: () => import('/@/views/test-plan/index.vue'),
            meta: {
                locale: 'menu.testPlan',
                roles: ['TEST_PLAN:READ'],
                isTopMenu: true,
            },
        },
    ],
}

export default TestPlan
