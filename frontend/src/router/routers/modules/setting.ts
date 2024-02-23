import { RouteRecordRaw } from 'vue-router'
import { DEFAULT_LAYOUT } from '../base'
import { SettingRouteEnum } from '/@/enums/route-enum'

const Setting: RouteRecordRaw = {
    path: '/setting',
    name: SettingRouteEnum.SETTING,
    component: DEFAULT_LAYOUT,
    meta: {
        locale: 'menu.settings',
        order: 8,
        icon: 'i-tabler:settings-cog',
    },
    children: [
        {
            path: 'system',
            name: SettingRouteEnum.SETTING_SYSTEM,
            component: null,
            meta: { locale: 'menu.settings.system', hideChildrenInMenu: false },
            children: [
                {
                    path: 'user',
                    name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
                    component: () =>
                        import(`/@/views/setting/system/user/index.vue`),
                    meta: {
                        locale: 'menu.settings.system.user',
                        roles: ['SYSTEM_USER:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'usergroup',
                    name: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP,
                    component: () =>
                        import(
                            `/@/views/setting/system/user-group/SystemUserGroup.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.system.usergroup',
                        roles: ['SYSTEM_USER_ROLE:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'organization-and-project',
                    name: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION,
                    component: () =>
                        import(
                            `/@/views/setting//system/organization-and-project/index.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.system.organizationAndProject',
                        roles: ['SYSTEM_ORGANIZATION_PROJECT:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'log',
                    name: SettingRouteEnum.SETTING_SYSTEM_LOG,
                    component: () =>
                        import(`/@/views/setting/system/log/index.vue`),
                    meta: {
                        locale: 'menu.settings.system.log',
                        roles: ['SYSTEM_LOG:READ'],
                        isTopMenu: true,
                    },
                },
            ],
        },
        {
            path: 'organization',
            name: SettingRouteEnum.SETTING_ORGANIZATION,
            redirect: '',
            component: null,
            meta: {
                locale: 'menu.settings.organization',
                roles: [
                    'ORGANIZATION_MEMBER:READ',
                    'ORGANIZATION_USER_ROLE:READ',
                    'ORGANIZATION_PROJECT:READ',
                    'SYSTEM_SERVICE_INTEGRATION:READ',
                    'ORGANIZATION_TEMPLATE:READ',
                    'ORGANIZATION_LOG:READ',
                ],
                hideChildrenInMenu: false,
            },
            children: [
                {
                    path: 'member',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
                    component: () =>
                        import(
                            `/@/views/setting/organization/member/index.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.organization.member',
                        roles: ['ORGANIZATION_MEMBER:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'usergroup',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_USER_GROUP,
                    component: () =>
                        import(
                            `/@/views/setting/organization/user-group/OrgUserGroup.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.organization.userGroup',
                        roles: ['ORGANIZATION_USER_ROLE:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'project',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
                    component: () =>
                        import(
                            `/@/views/setting/organization/project/OrgProject.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.organization.project',
                        roles: ['ORGANIZATION_PROJECT:READ'],
                        isTopMenu: true,
                    },
                },
                {
                    path: 'template',
                    name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE,
                    component: () =>
                        import(
                            `/@/views/setting/organization/template/index.vue`
                        ),
                    meta: {
                        locale: 'menu.settings.organization.template',
                        roles: ['ORGANIZATION_TEMPLATE:READ'],
                        isTopMenu: true,
                    },
                },
            ],
        },
    ],
}
export default Setting
