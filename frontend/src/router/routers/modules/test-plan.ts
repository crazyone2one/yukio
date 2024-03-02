import { TestPlanRouteEnum } from '/@/enums/route-enum'

import { DEFAULT_LAYOUT } from '../base'
import type { AppRouteRecordRaw } from '../types'

const TestPlan: AppRouteRecordRaw = {
    path: '/test-plan/index',
    name: TestPlanRouteEnum.TEST_PLAN_INDEX,
    component: DEFAULT_LAYOUT,
    meta: {
        locale: 'menu.testPlan',
        roles: ['TEST_PLAN:READ'],
        isTopMenu: true,
    },
}

export default TestPlan
