import { TestPlanRouteEnum } from '/@/enums/route-enum'

import { DEFAULT_LAYOUT } from '../base'
import type { AppRouteRecordRaw } from '../types'

const TestPlan: AppRouteRecordRaw = {
    path: '/test-plan',
    name: TestPlanRouteEnum.TEST_PLAN,
    component: DEFAULT_LAYOUT,
    redirect: '/test-plan/index',
    meta: {
        locale: 'menu.testPlan',
        roles: ['TEST_PLAN:READ'],
        isTopMenu: true,
    },
    children: [
        {
            path: '/test-plan/index',
            name: TestPlanRouteEnum.TEST_PLAN_INDEX,
            component: () => import(`/@/views/test-plan/index.vue`),
        },
    ],
}

export default TestPlan
