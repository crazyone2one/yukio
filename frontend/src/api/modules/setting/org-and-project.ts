import { CommonPage, TableQueryParams } from '../../interface/common'
import { alovaInst } from '/@/api/index.ts'
import {
  CreateOrUpdateSystemOrgParams,
  CreateOrUpdateSystemProjectParams,
  OrganizationListItem,
} from '/@/api/interface/orgnization'
import * as orgUrl from '/@/api/requrls/setting/organizationAndProject'

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
