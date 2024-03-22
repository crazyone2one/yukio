import {
  CreateUserParams,
  CreateUserResult,
  UpdateUserInfoParams,
  UserListItem,
} from '../../interface/setting/user'
import {
  CreateUserUrl,
  GetUserListUrl,
  UpdateUserUrl,
} from '../../requrls/setting/user'
import { alovaInst } from '/@/api/index.ts'
import { CommonPage, TableQueryParams } from '/@/api/interface/common'

export const getUserList = (params: TableQueryParams) =>
  alovaInst.Post<CommonPage<UserListItem>>(`${GetUserListUrl}`, params)

/**
 * 批量创建用户
 * @param params
 * @returns
 */
export const batchCreateUser = (params: CreateUserParams) =>
  alovaInst.Post<CreateUserResult>(`${CreateUserUrl}`, params)

export const updateUserInfo = (params: UpdateUserInfoParams) =>
  alovaInst.Post(`${UpdateUserUrl}`, params)
