import instance from '/@/api'
import {
    CreateCaseModuleTreeUrl,
    CreateCaseUrl,
    GetCaseListUrl,
    GetCaseModuleTreeUrl,
    GetDefaultTemplateFieldsUrl,
    UpdateCaseUrl,
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
// 创建用例
export const createCaseRequest = (data: Record<string, any>) =>
    instance.Post(`${CreateCaseUrl}`, data)
// 编辑用例
export const updateCaseRequest = (data: Record<string, any>) =>
    instance.Post(`${UpdateCaseUrl}`, data)
// 获取默认模板自定义字段
export const getCaseDefaultFields = (projectId: string) =>
    instance.Get<Record<string, any>>(`${GetDefaultTemplateFieldsUrl}/${projectId}`)
