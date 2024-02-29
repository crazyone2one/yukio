import instance from '/@/api'
import * as ugUrl from '/@/api/requrls/setting/usergroup'
import { UserGroupAuthSetting, UserGroupItem } from '/@/models/setting/usergroup'

// 系统-获取用户组列表
export const getUserGroupList = () => instance.Get<Array<UserGroupItem>>(`${ugUrl.getUserGroupU}`)
/**
 * 系统-获取用户组对应的权限配置
 * @param id
 * @returns
 */
export const getGlobalUSetting = (id: string) =>
    instance.Get<Array<UserGroupAuthSetting>>(`${ugUrl.getGlobalUSettingUrl}${id}`)

/**
 *  组织-获取用户组对应的权限配置
 * @param id
 * @returns
 */
export const getOrgUSetting = (id: string) =>
    instance.Get<Array<UserGroupAuthSetting>>(`${ugUrl.getOrgUSettingUrl}${id}`)
