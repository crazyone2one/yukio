import instance from '/@/api'
import { ModuleTreeNode, TableQueryParams } from '/@/models/common'
import {GetTestPlanModuleUrl} from '/@/api/requrls/test-plan/testPlan'

export const getTestPlanModule = (params: TableQueryParams) =>
    instance.Get<ModuleTreeNode[]>(`${GetTestPlanModuleUrl}/${params.projectId}`)
