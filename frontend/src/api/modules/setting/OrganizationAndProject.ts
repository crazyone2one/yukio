import instance from '/@/api'
import * as orgUrl from '/@/api/requrls/setting/organizationAndProject'
import { CommonList, TableQueryParams } from '/@/models/common'
import { OrgProjectTableItem } from '/@/models/setting/system/orgAndProject'

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
