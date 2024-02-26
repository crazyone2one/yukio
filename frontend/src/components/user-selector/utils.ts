import { getAdminByOrganizationOrProject } from '/@/api/modules/setting/OrganizationAndProject'

/* eslint-disable @typescript-eslint/no-explicit-any */
export enum UserRequestTypeEnum {
    SYSTEM_USER_GROUP = 'SYSTEM_USER_GROUP',
    SYSTEM_ORGANIZATION = 'SYSTEM_ORGANIZATION',
    SYSTEM_ORGANIZATION_ADMIN = 'SYSTEM_ORGANIZATION_ADMIN',
    SYSTEM_PROJECT = 'SYSTEM_PROJECT',
    SYSTEM_PROJECT_ADMIN = 'SYSTEM_PROJECT_ADMIN',
    ORGANIZATION_USER_GROUP = 'ORGANIZATION_USER_GROUP',
    ORGANIZATION_USER_GROUP_ADMIN = 'ORGANIZATION_USER_GROUP_ADMIN',
    ORGANIZATION_PROJECT = 'ORGANIZATION_PROJECT',
    ORGANIZATION_PROJECT_ADMIN = 'ORGANIZATION_PROJECT_ADMIN',
    SYSTEM_ORGANIZATION_PROJECT = 'SYSTEM_ORGANIZATION_PROJECT',
    SYSTEM_ORGANIZATION_MEMBER = 'SYSTEM_ORGANIZATION_MEMBER',
    PROJECT_PERMISSION_MEMBER = 'PROJECT_PERMISSION_MEMBER',
    PROJECT_USER_GROUP = 'PROJECT_USER_GROUP',
}
export default function initOptionsFunc(type: string, params: Record<string, any>) {
    if (
        type === UserRequestTypeEnum.SYSTEM_ORGANIZATION_ADMIN ||
        type === UserRequestTypeEnum.SYSTEM_PROJECT_ADMIN
    ) {
        // 系统 - 【组织 或 项目】-添加管理员-下拉选项
        return getAdminByOrganizationOrProject(params.keyword)
    }
}
