import { alovaInst } from '/@/api/index.ts'
import { UserTableItem } from '/@/api/interface/setting/usergroup.ts'
import * as ugUrl from '/@/api/interface/setting/project-management/usergroup.ts'
/**
 * 项目-获取需要关联的用户选项
 * @param projectId
 * @param userRoleId
 * @param keyword
 */
export const getProjectUserGroupOptions = (
  projectId: string,
  userRoleId: string,
  keyword: string,
) =>
  alovaInst.Get<UserTableItem[]>(
    `${ugUrl.getMemberOptionsUrl}${projectId}/${userRoleId}`,
    {
      params: {
        keyword,
      },
    },
  )
