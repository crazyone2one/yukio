import { CommonPage, TableQueryParams } from '../../interface/common'
import { alovaInst } from '/@/api/index.ts'
import {
  CreateOrUpdateSystemOrgParams,
  CreateOrUpdateSystemProjectParams,
  OrganizationListItem,
} from '/@/api/interface/orgnization'
import {
  OrgProjectTableItem,
  SystemOrgOption,
} from '/@/api/interface/setting/orgAndProject.ts'
import { UserTableItem } from '/@/api/interface/setting/usergroup.ts'
import * as orgUrl from '/@/api/requrls/setting/organizationAndProject'
import { MsUserSelectorOption } from '/@/components/user-selector/types.ts'

/**
 * 创建或修改组织
 * @param data
 * @returns
 */
export const createOrUpdateOrg = (
  data: CreateOrUpdateSystemOrgParams | CreateOrUpdateSystemProjectParams,
) =>
  alovaInst.Post(
    `${data.id ? orgUrl.postModifyOrgUrl : orgUrl.postAddOrgUrl}`,
    data,
  )
/**
 * 修改组织名称
 * @param data
 * @returns
 */
export const modifyOrgName = (data: { id: string; name: string }) =>
  alovaInst.Post(`${orgUrl.postModifyOrgNameUrl}`, data)

/**
 * 获取项目和组织的总数
 * @returns
 */
export const getOrgAndProjectCount = () =>
  alovaInst.Get<{ organizationTotal: number; projectTotal: number }>(
    orgUrl.getOrgAndProjectCountUrl,
  )

export const postOrgTable = (data: TableQueryParams) =>
  alovaInst.Post<CommonPage<OrganizationListItem>>(
    `${orgUrl.postOrgTableUrl}`,
    data,
  )
/**
 * 启用或禁用组织
 * @param id
 * @param isEnable
 * @returns
 */
export const enableOrDisableOrg = (id: string, isEnable = true) =>
  alovaInst.Get(
    `${isEnable ? orgUrl.getEnableOrgUrl : orgUrl.getDisableOrgUrl}${id}`,
  )
/**
 * 系统-获取项目列表
 * @param data
 */
export const postProjectTable = (data: TableQueryParams) =>
  alovaInst.Post<CommonPage<OrgProjectTableItem>>(
    `${orgUrl.postProjectTableUrl}`,
    data,
  )
export const createOrUpdateProject = (data: Partial<OrgProjectTableItem>) =>
  alovaInst.Post(
    `${data.id ? orgUrl.postModifyProjectUrl : orgUrl.postAddProjectUrl}`,
    data,
  )

export const getSystemOrgOption = () =>
  alovaInst.Post<Array<SystemOrgOption>>(orgUrl.postOrgOptionsUrl)

/**
 * 系统-获取管理员下拉选项
 * @param keyword
 */
export const getAdminByOrganizationOrProject = (keyword?: string) =>
  alovaInst.Get<UserTableItem[]>(`${orgUrl.getAdminByOrgOrProjectUrl}`, {
    params: { keyword },
  })
/**
 * 获取用户下拉选项
 * @param sourceId
 * @param keyword
 */
export const getUserByOrganizationOrProject = (
  sourceId: string,
  keyword: string,
) =>
  alovaInst.Get(`${orgUrl.getUserByOrgOrProjectUrl}${sourceId}`, {
    params: { keyword },
  })
/**
 * 组织-获取成员下的成员选项
 * @param organizationId
 * @param projectId
 * @param keyword
 */
export const getUserByProjectByOrg = (
  organizationId: string,
  projectId: string,
  keyword: string,
) =>
  alovaInst.Get<UserTableItem[]>(
    `${orgUrl.getUserByOrganizationOrProjectUrl}${organizationId}/${projectId}`,
    {
      params: {
        keyword,
      },
    },
  )
export const getAdminByProjectByOrg = (
  organizationId: string,
  keyword: string,
) =>
  alovaInst.Get<MsUserSelectorOption[]>(
    `${orgUrl.getAdminByOrganizationOrProjectUrl}${organizationId}`,
    {
      params: {
        keyword,
      },
    },
  )
