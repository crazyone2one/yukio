import { CreateOrUpdateModule } from '../../interface/case-management/feature-case'
import { ModuleTreeNode, TableQueryParams } from '../../interface/common'
import {
  CreateCaseModuleTreeUrl,
  GetCaseModuleTreeUrl,
} from '../../requrls/case-management/featureCase'
import { alovaInst } from '/@/api/index.ts'

/**
 * 获取模块树
 * @param params
 * @returns
 */
export const getCaseModuleTree = (params: TableQueryParams) =>
  alovaInst.Get<ModuleTreeNode[]>(`${GetCaseModuleTreeUrl}/${params.projectId}`)
/**
 * 创建模块树
 * @param params
 * @returns
 */
export const createCaseModuleTree = (params: CreateOrUpdateModule) =>
  alovaInst.Post(`${CreateCaseModuleTreeUrl}`, params)
