import { alovaInst } from '/@/api/index.ts'
import {
  getProjectListUrl,
  getUserList,
} from '/@/api/requrls/setting/member.ts'
import { LinkItem } from '/@/api/interface/setting/member.ts'

/**
 *  获取组织下边的项目
 * @param organizationId
 * @param keyword
 */
export const getProjectList = (organizationId: string, keyword?: string) =>
  alovaInst.Get<LinkItem[]>(`${getProjectListUrl}/${organizationId}`, {
    params: { keyword },
  })

/**
 * 获取系统用户下拉
 * @param organizationId
 * @param keyword
 */
export const getUser = (organizationId: string, keyword: string) =>
  alovaInst.Get<LinkItem[]>(`${getUserList}/${organizationId}`, {
    params: { keyword },
  })
