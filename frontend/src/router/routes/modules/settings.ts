import { RouteRecordRaw } from 'vue-router'
import { SettingRouteEnum } from '/@/enums/route-enum'
import DefaultLayout from '/@/layout/DefaultLayout.vue'
const Setting: RouteRecordRaw = {
  path: '/setting',
  name: SettingRouteEnum.SETTING,
  component: DefaultLayout,
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
  ],
}
export default Setting
