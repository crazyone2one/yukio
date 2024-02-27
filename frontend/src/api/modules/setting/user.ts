import instance from '/@/api'
import { GetUserListUrl } from '/@/api/requrls/setting/user'
import { CommonList, TableQueryParams } from '/@/models/common'
import { UserListItem } from '/@/models/setting/user'

// 获取用户列表
export function getUserList(page: number, pageSize: number, data: TableQueryParams) {
    data.current = page
    data.pageSize = pageSize
    return instance.Post<CommonList<UserListItem>>(GetUserListUrl, data)
}
