import instance from '/@/api'
import {
    GetTestPlanModuleUrl,
    addTestPlanModuleUrl,
    updateTestPlanModuleUrl,
} from '/@/api/requrls/test-plan/testPlan'
import { CreateOrUpdateModule, UpdateModule } from '/@/models/case-management/feature-case'
import { ModuleTreeNode, TableQueryParams } from '/@/models/common'

/**
 * 获取模块树
 * @param params
 * @returns
 */
export const getTestPlanModule = (params: TableQueryParams) =>
    instance.Get<ModuleTreeNode[]>(`${GetTestPlanModuleUrl}/${params.projectId}`)
/**
 * 创建模块树
 * @param params
 * @returns
 */
export const createPlanModuleTree = (params: CreateOrUpdateModule) =>
    instance.Post(`${addTestPlanModuleUrl}`, params)
/**
 * 更新模块树
 * @param params
 * @returns
 */
export const updatePlanModuleTree = (params: UpdateModule) =>
    instance.Post(`${updateTestPlanModuleUrl}`, params)
