import { SelectOption } from 'naive-ui'
import { UpdateOrgUrl } from '../../requrls/user'
import instance from '/@/api'
import {
    CreateUserUrl,
    GetSystemRoleUrl,
    GetUserListUrl,
    ResetPasswordUrl,
} from '/@/api/requrls/setting/user'
import { CommonList, TableQueryParams } from '/@/models/common'
import { CreateUserResult, ResetUserPasswordParams, UserListItem } from '/@/models/setting/user'

// 获取用户列表
export function getUserList(page: number, pageSize: number, data: TableQueryParams) {
    data.current = page
    data.pageSize = pageSize
    return instance.Post<CommonList<UserListItem>>(GetUserListUrl, data)
}

/**
 * 更新用户org
 * @param orgId org id
 * @returns
 */
export const updateUserOrg = (orgId: string) =>
    instance.Get<UserListItem>(`${UpdateOrgUrl}${orgId}`)

/**
 * 获取系统用户组
 */
export const getSystemRoles = () => instance.Get<SelectOption[]>(`${GetSystemRoleUrl}`)

/**
 * 创建用户
 * @param userForm
 */
export const batchCreateUser = (userForm: {
    name: string
    email: string
    phone: string
    enabled: boolean
    userGroup: Array<string>
}) => {
    const params = {
        userInfoList: [
            {
                name: userForm.name,
                email: userForm.email,
                phone: userForm.phone,
                enable: userForm.enabled,
            },
        ],
        userRoleIdList: userForm.userGroup,
    }

    return instance.Post<CreateUserResult>(`${CreateUserUrl}`, params)
}

/**
 * 重置用户密码
 * @param data
 * @returns
 */
export const resetUserPassword = (data: ResetUserPasswordParams) =>
    instance.Post(`${ResetPasswordUrl}`, data)
