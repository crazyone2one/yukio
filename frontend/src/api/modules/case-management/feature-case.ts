import instance from '/@/api'
import {
    CreateCaseModuleTreeUrl,
    GetCaseListUrl,
    GetCaseModuleTreeUrl,
} from '/@/api/requrls/case-management/featureCase'
import { CaseManagementTable, CreateOrUpdateModule } from '/@/models/case-management/feature-case'
import { CommonList, ModuleTreeNode, TableQueryParams } from '/@/models/common'

export const createCaseModuleTree = (data: CreateOrUpdateModule) =>
    instance.Post(`${CreateCaseModuleTreeUrl}`, data)
// 获取模块树
export const getCaseModuleTree = (params: TableQueryParams) =>
    instance.Get<ModuleTreeNode[]>(`${GetCaseModuleTreeUrl}/${params.projectId}`)
// 用例分页表
export const getCaseList = (data: TableQueryParams) =>
    instance.Post<CommonList<CaseManagementTable>>(`${GetCaseListUrl}`, data)
