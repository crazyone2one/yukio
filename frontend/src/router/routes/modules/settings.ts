import { RouteRecordRaw } from 'vue-router'
import { SettingRouteEnum } from '/@/enums/route-enum'
const Setting: RouteRecordRaw = {
  path: '/setting',
  name: SettingRouteEnum.SETTING,
  component: () => import(`/@/layout/DefaultLayout.vue`),
  meta: {
    locale: 'menu.settings',
    roles: [],
    order: 8,
  },
  children: [
    {
      path: 'system',
      name: SettingRouteEnum.SETTING_SYSTEM,
      redirect: '',
      meta: {
        locale: 'menu.settings.system',
        roles: [],
        hideChildrenInMenu: true,
      },
      children: [
        {
          path: 'user',
          name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
          component: () => import(`/@/views/setting/system/user/index.vue`),
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
            import(`/@/views/setting/system/user-group/SystemUserGroup.vue`),
          meta: {
            locale: 'menu.settings.system.usergroup',
            roles: [],
            isTopMenu: true,
          },
        },
        {
          path: 'organization-and-project',
          name: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION,
          component: () =>
            import(`/@/views/setting/system/org-project/index.vue`),
          meta: {
            locale: 'menu.settings.system.organizationAndProject',
            roles: [],
            isTopMenu: true,
          },
        },
      ],
    },
    {
      path: 'organization',
      name: SettingRouteEnum.SETTING_ORGANIZATION,
      redirect: '',
      meta: {
        locale: 'menu.settings.organization',
        roles: [
          'ORGANIZATION_MEMBER:READ',
          'ORGANIZATION_USER_ROLE:READ',
          'ORGANIZATION_PROJECT:READ',
          'SYSTEM_SERVICE_INTEGRATION:READ',
          'ORGANIZATION_TEMPLATE:READ',
          'ORGANIZATION_LOG:READ',
          'ORGANIZATION_TASK_CENTER:READ',
          'ORGANIZATION_TASK_CENTER:READ',
        ],
        hideChildrenInMenu: true,
      },
      children: [
        {
          path: 'project',
          name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
          component: () =>
            import('/@/views/setting/organization/project/OrgProject.vue'),
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
            import('/@/views/setting/organization/template/index.vue'),
          meta: {
            locale: 'menu.settings.organization.template',
            roles: ['ORGANIZATION_TEMPLATE:READ'],
            isTopMenu: true,
          },
        },
        // 模板列表-模板字段设置
        {
          path: 'templateFiledSetting',
          name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
          component: () =>
            import(
              '/@/views/setting/organization/template/components/OrgFieldSetting.vue'
            ),
          meta: {
            locale: 'menu.settings.organization.templateFieldSetting',
            roles: ['ORGANIZATION_TEMPLATE:READ'],
            breadcrumbs: [
              {
                name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE,
                locale: 'menu.settings.organization.template',
              },
              {
                name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
                locale: 'menu.settings.organization.templateFieldSetting',
                editLocale: 'menu.settings.organization.templateFieldSetting',
                query: ['type'],
              },
            ],
          },
        },
      ],
    },
  ],
}
export default Setting
