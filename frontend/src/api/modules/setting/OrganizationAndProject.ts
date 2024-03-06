import { SelectOption } from 'naive-ui'
import instance from '/@/api'
import * as orgUrl from '/@/api/requrls/setting/organizationAndProject'
import { getOrgList } from '/@/api/requrls/setting/organizationAndProject'
import { UserSelectorOption } from '/@/components/user-selector/index.vue'
import { CommonList, TableQueryParams } from '/@/models/common'
import {
    CreateOrUpdateOrgProjectParams,
    CreateOrUpdateSystemOrgParams,
    CreateOrUpdateSystemProjectParams,
    OrgProjectTableItem,
} from '/@/models/setting/system/orgAndProject'
import { AddUserToOrgOrProjectParams } from '/@/models/setting/systemOrg'

/**
 * 获取组织列表
 * @param page
 * @param pageSize
 * @param data
 * @returns
 */
export const postOrgTable = (page: number, pageSize: number, keyword?: string) => {
    const data: TableQueryParams = {}
    data.current = page
    data.pageSize = pageSize
    data.keyword = keyword
    const method = instance.Post<CommonList<OrgProjectTableItem>>(orgUrl.postOrgTableUrl, data)
    method.meta = {
        authRole: 'token',
    }
    return method
}
/**
 * 获取项目和组织的总数
 * @returns
 */
export const getOrgAndProjectCount = () =>
    instance.Get<{ organizationTotal: number; projectTotal: number }>(
        orgUrl.getOrgAndProjectCountUrl,
    )

export const getAdminByOrganizationOrProject = (keyword?: string) =>
    instance.Get<Array<UserSelectorOption>>(`${orgUrl.getAdminByOrgOrProjectUrl}`, {
        params: { keyword },
    })

/**
 * 创建或修改组织
 * @param data
 * @returns
 */
export const createOrUpdateOrg = (
    data: CreateOrUpdateSystemOrgParams | CreateOrUpdateSystemProjectParams,
) => instance.Post(data.id ? orgUrl.postModifyOrgUrl : orgUrl.postAddOrgUrl, data)

/**
 * 系统-获取项目列表
 * @param page
 * @param pageSize
 * @param data
 * @returns
 */
export const postProjectTable = (page: number, pageSize: number, data: TableQueryParams) => {
    data.current = page
    data.pageSize = pageSize
    const method = instance.Post<CommonList<CreateOrUpdateSystemProjectParams>>(
        orgUrl.postProjectTableUrl,
        data,
    )
    method.meta = {
        authRole: 'token',
    }
    return method
}

export const postOrgList = () => instance.Get<Array<OrgProjectTableItem>>(`${getOrgList}`)
// 组织-项目
// 组织-获取项目列表
export const postProjectTableByOrg = (page: number, pageSize: number, data: TableQueryParams) => {
    data.current = page
    data.pageSize = pageSize
    const method = instance.Post<CommonList<OrgProjectTableItem>>(
        orgUrl.postProjectTableByOrgIdUrl,
        data,
    )
    method.meta = {
        authRole: 'token',
    }
    return method
}
/**
 * 组织-创建或更新项目
 * @param data
 * @returns
 */
export const createOrUpdateProjectByOrg = (data: CreateOrUpdateOrgProjectParams) =>
    instance.Post(data.id ? orgUrl.postModifyProjectByOrgUrl : orgUrl.postAddProjectByOrgUrl, data)

/**
 * 获取组织下拉选项
 * @returns // 获取组织下拉选项
 */
export const getSystemOrgOption = () => instance.Post<Array<SelectOption>>(orgUrl.postOrgOptionsUrl)

/**
 * 给组织或项目添加成员
 * @param data
 * @returns
 */
export const addUserToOrgOrProject = (data: AddUserToOrgOrProjectParams) => {
    return instance.Post(
        `${data.projectId ? orgUrl.postAddProjectMemberUrl : orgUrl.postAddOrgMemberUrl}`,
        data,
    )
}
