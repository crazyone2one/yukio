import {
  CreateOrUpdateModule,
  UpdateModule,
} from '../../interface/case-management/feature-case'
import { ModuleTreeNode, TableQueryParams } from '../../interface/common'
import {
  CreateCaseModuleTreeUrl,
  DeleteCaseModuleTreeUrl,
  GetCaseModuleTreeUrl,
  UpdateCaseModuleTreeUrl,
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
/**
 * 更新模块树
 * @param params
 */
export const updateCaseModuleTree = (params: UpdateModule) =>
  alovaInst.Post(`${UpdateCaseModuleTreeUrl}`, params)
/**
 * 删除模块
 * @param id
 */
export const deleteCaseModuleTree = (id: string) =>
  alovaInst.Get(`${DeleteCaseModuleTreeUrl}/${id}`)
