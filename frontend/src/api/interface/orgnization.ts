export interface OrgAdmin {
  id: string
  name: string
  email: string
  password: string
  enable: boolean
  createTime: number
  updateTime: number
  language: string
  lastOrganizationId: string // 当前组织ID
  phone: string
  source: string // 来源：LOCAL OIDC CAS OAUTH2
  lastProjectId: string
  createUser: string
  updateUser: string
  deleted: boolean
}

export interface OrganizationListItem {
  id: string
  num: number // 组织编号
  name: string
  description: string
  createTime: number
  updateTime: number
  createUser: string
  updateUser: string
  deleted: boolean
  deleteUser: string
  deleteTime: number
  enable: boolean
  memberCount: number
  projectCount: number
  orgAdmins: OrgAdmin[] // 列表组织管理员集合
  memberIds: string[] // 组织管理员ID集合
}
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
  resourcePoolIds: string[]
  // 列表里的
}
