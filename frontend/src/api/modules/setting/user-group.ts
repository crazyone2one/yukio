import { CommonPage, TableQueryParams } from '../../interface/common'
import {
  SystemUserGroupParams,
  UserGroupItem,
  UserTableItem,
} from '/@/api//interface/setting/usergroup'
import { alovaInst } from '/@/api/index.ts'
import * as ugUrl from '/@/api/requrls/setting/usergroup'

/**
 * 系统-获取用户组列表
 * @returns
 */
export const getUserGroupList = () =>
  alovaInst.Get<UserGroupItem[]>(`${ugUrl.getUserGroupU}`)

/**
 * 系统-获取用户组对应的用户列表
 * @param data
 * @returns
 */
export const postUserByUserGroup = (data: TableQueryParams) =>
  alovaInst.Post<CommonPage<UserTableItem[]>>(
    `${ugUrl.postUserByUserGroupUrl}`,
    data,
  )
/**
 * 组织-获取用户组对应的用户列表
 * @param data
 * @returns
 */
export const postOrgUserByUserGroup = (data: TableQueryParams) =>
  alovaInst.Post<CommonPage<UserTableItem[]>>(
    `${ugUrl.postOrgUserByUserGroupUrl}`,
    data,
  )
/**
 * 系统-删除用户组对应的用户
 * @param id
 * @returns
 */
export const deleteUserFromUserGroup = (id: string) =>
  alovaInst.Get<string>(`${ugUrl.deleteUserFromUserGroupUrl}${id}`)
/**
 * 组织-删除用户组对应的用户
 * @param data
 * @returns
 */
export const deleteOrgUserFromUserGroup = (data: {
  userRoleId: string
  userIds: string[]
  organizationId: string
}) =>
  alovaInst.Post<CommonPage<UserTableItem[]>>(
    `${ugUrl.deleteOrgUserFromUserGroupUrl}`,
    data,
  )
/**
 * 系统-创建或修改用户组
 * @param data
 * @returns
 */
export const updateOrAddUserGroup = (data: SystemUserGroupParams) =>
  alovaInst.Post<UserGroupItem>(
    `${data.id ? ugUrl.updateUserGroupU : ugUrl.addUserGroupU}`,
    data,
  )
/**
 * 系统-获取需要关联的用户选项
 * @param id
 * @param keyword
 */
export const getSystemUserGroupOption = (id: string, keyword: string) =>
  alovaInst.Get<UserTableItem[]>(`${ugUrl.getSystemUserGroupOptionUrl}${id}`, {
    params: {
      keyword,
    },
  })

/**
 * 组织-获取需要关联的用户选项
 * @param organizationId
 * @param roleId
 * @param keyword
 */
export const getOrgUserGroupOption = (
  organizationId: string,
  roleId: string,
  keyword: string,
) =>
  alovaInst.Get<UserTableItem[]>(
    `${ugUrl.getOrgUserGroupOptionUrl}${organizationId}/${roleId}`,
    {
      params: {
        keyword,
      },
    },
  )
