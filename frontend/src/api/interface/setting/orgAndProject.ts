import { TableQueryParams } from '/@/api/interface/common.ts'
import { UserItem } from '/@/api/interface/setting/log.ts'

export interface CreateOrUpdateSystemOrgParams {
  id?: string
  name: string
  description: string
  userIds: string[]
}
export interface CreateOrUpdateSystemProjectParams {
  id?: string
  // 项目名称
  name: string
  // 项目描述
  description: string
  // 启用或禁用
  enable: boolean
  // 项目成员
  userIds: string[]
  // 模块配置
  moduleIds?: string[]
  // 所属组织
  organizationId?: string
  // 资源池
  // resourcePoolIds: string[]
  // 列表里的
}

export interface CreateOrUpdateOrgProjectParams {
  id?: string
  name: string
  description?: string
  enable?: boolean
  userIds?: string[]
  organizationId?: string
  resourcePoolIds?: string[]
}

export interface SystemOrgOption {
  label: string
  value: string
}
export interface SystemGetUserByOrgOrProjectIdParams extends TableQueryParams {
  projectId?: string
  organizationId?: string
}
export interface OrgProjectTableItem {
  id: string
  name: string
  description: string
  enable: boolean
  adminList: UserItem[]
  organizationId: string
  organizationName: string
  num: number
  updateTime: number
  createTime: number
  memberCount: number
  userIds: string[]
  resourcePoolIds: string[]
  deleted: boolean
  moduleIds: string[]
  [key: string]: string | boolean | string[] | number | number[] | UserItem[]
}
