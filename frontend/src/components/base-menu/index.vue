<script setup lang="ts">
import { MenuOption, NLayoutSider, NMenu } from 'naive-ui'
import { computed, h } from 'vue'
import { RouterLink } from 'vue-router'
import {
  BugManagementRouteEnum,
  CaseManagementRouteEnum,
  ProjectManagementRouteEnum,
  SettingRouteEnum,
} from '/@/enums/route-enum'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import { renderIcon } from '/@/utils'

const { t } = useI18n()
const appStore = useAppStore()
const collapsed = computed({
  get() {
    if (appStore.device === 'desktop') return appStore.menuCollapse
    return false
  },
  set(value: boolean) {
    appStore.updateSettings({ menuCollapse: value })
  },
})

const menuOptions: MenuOption[] = [
  {
    label: t('menu.bugManagement'),
    key: BugManagementRouteEnum.BUG_MANAGEMENT,
    icon: renderIcon('i-carbon-debug'),
    disabled: true,
  },
  {
    label: t('menu.caseManagement'),
    key: CaseManagementRouteEnum.CASE_MANAGEMENT,
    icon: renderIcon('i-local:functional_testing'),
    disabled: true,
  },
  {
    label: t('menu.projectManagement'),
    key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT,
    icon: renderIcon('i-carbon:settings-edit'),
    disabled: true,
    children: [
      {
        label: t('menu.projectManagement.projectPermission'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION,
        children: [
          {
            label: t('project.permission.basicInfo'),
            key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO,
          },
          {
            label: t('project.permission.menuManagement'),
            key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT,
          },
          {
            label: t('project.permission.projectVersion'),
            key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_VERSION,
          },
          {
            label: t('project.permission.member'),
            key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
          },
          {
            label: t('project.permission.userGroup'),
            key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_PERMISSION_USER_GROUP,
          },
        ],
      },
      // 項目管理模板
      {
        label: t('menu.projectManagement.templateManager'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE,
      },
      // 模板列表-模板字段设置
      {
        label: t('menu.settings.organization.templateFieldSetting'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE_FIELD_SETTING,
      },
      // 模板列表-模板管理列表
      {
        label: t('menu.settings.organization.templateManagement'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
      },
      // 项目-模板-用例模板
      {
        label: t('system.orgTemplate.createCaseTemplate'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT_CASE_DETAIL,
      },
      // 项目-模板-接口模板
      {
        label: t('system.orgTemplate.createApiTemplate'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT_API_DETAIL,
      },
      // 项目-模板-缺陷模板
      {
        label: t('system.orgTemplate.createDefectTemplate'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT_BUG_DETAIL,
      },
      // 文件管理
      {
        label: t('menu.projectManagement.fileManagement'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_FILE_MANAGEMENT,
      },
      // 消息管理
      {
        label: t('menu.projectManagement.messageManagement'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT,
      },
      // 文件管理
      {
        label: t('menu.projectManagement.messageManagementEdit'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT_EDIT,
      },
      // 公共脚本
      {
        label: t('menu.projectManagement.commonScript'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_COMMON_SCRIPT,
      },
      // 项目日志
      {
        label: t('menu.projectManagement.log'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_LOG,
      },
      // 菜单管理-误报规则
      {
        label: t('project.menu.API_ERROR_REPORT_RULE'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_MENU_MANAGEMENT_ERROR_REPORT_RULE,
      },
      // 环境管理
      {
        label: t('menu.projectManagement.environmentManagement'),
        key: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_ENVIRONMENT_MANAGEMENT,
      },
    ],
  },
  {
    label: t('menu.settings'),
    key: SettingRouteEnum.SETTING,
    icon: renderIcon('i-carbon:settings-services'),
    children: [
      {
        label: t('menu.settings.system'),
        key: SettingRouteEnum.SETTING_SYSTEM,
        children: [
          {
            // label: t('menu.settings.system.user'),
            label: () =>
              h(
                RouterLink,
                { to: { name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE } },
                { default: () => t('menu.settings.system.user') },
              ),
            key: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
          },
          {
            // label: t('menu.settings.system.usergroup'),
            label: () =>
              h(
                RouterLink,
                { to: { name: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP } },
                { default: () => t('menu.settings.system.usergroup') },
              ),
            key: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP,
          },
          {
            // label: t('menu.settings.system.organizationAndProject'),
            label: () =>
              h(
                RouterLink,
                { to: { name: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION } },
                {
                  default: () =>
                    t('menu.settings.system.organizationAndProject'),
                },
              ),
            key: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION,
          },
          {
            label: t('menu.settings.system.parameter'),
            key: SettingRouteEnum.SETTING_SYSTEM_PARAMETER,
            disabled: true,
          },
          {
            label: t('menu.settings.system.resourcePool'),
            key: SettingRouteEnum.SETTING_SYSTEM_RESOURCE_POOL,
            disabled: true,
          },
          // {
          //   label: t('menu.settings.system.resourcePoolDetail'),
          //   key: SettingRouteEnum.SETTING_SYSTEM_RESOURCE_POOL_DETAIL,
          // },
          {
            label: t('menu.settings.system.authorizedManagement'),
            key: SettingRouteEnum.SETTING_SYSTEM_AUTHORIZED_MANAGEMENT,
            disabled: true,
          },
          {
            label: t('menu.settings.system.log'),
            key: SettingRouteEnum.SETTING_SYSTEM_LOG,
            disabled: true,
          },
          {
            label: t('menu.projectManagement.taskCenter'),
            key: SettingRouteEnum.SETTING_SYSTEM_TASK_CENTER,
            disabled: true,
          },
          {
            label: t('menu.settings.system.pluginManager'),
            key: SettingRouteEnum.SETTING_SYSTEM_PLUGIN_MANAGEMENT,
            disabled: true,
          },
        ],
      },
      {
        label: t('menu.settings.organization'),
        key: SettingRouteEnum.SETTING_ORGANIZATION,
        children: [
          {
            label: t('menu.settings.organization.member'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
          },
          {
            label: t('menu.settings.organization.userGroup'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_USER_GROUP,
          },
          {
            label: t('menu.settings.organization.project'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
          },
          {
            label: t('menu.settings.organization.serviceIntegration'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_SERVICE,
            disabled: true,
          },
          {
            label: t('menu.settings.organization.template'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE,
          },
          {
            label: t('menu.settings.organization.templateFieldSetting'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
          },
          {
            label: t('menu.settings.organization.templateManagementList'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
          },
          // 模板列表-创建&编辑模板
          // 用例模板
          {
            label: t('system.orgTemplate.createCaseTemplate'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_CASE_DETAIL,
          },
          // 接口模板
          {
            label: t('system.orgTemplate.createApiTemplate'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_API_DETAIL,
          },
          // 缺陷模板
          {
            label: t('system.orgTemplate.createDefectTemplate'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_BUG_DETAIL,
          },
          {
            label: t('menu.settings.organization.log'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_LOG,
          },
          {
            label: t('menu.projectManagement.taskCenter'),
            key: SettingRouteEnum.SETTING_ORGANIZATION_TASK_CENTER,
          },
        ],
      },
    ],
  },
]
</script>
<template>
  <n-layout-sider
    collapse-mode="width"
    :native-scrollbar="false"
    bordered
    :collapsed-width="appStore.collapsedWidth"
    :width="appStore.menuWidth"
    :collapsed="collapsed"
    show-trigger
    @collapse="collapsed = true"
    @expand="collapsed = false"
  >
    <n-menu
      :collapsed="collapsed"
      :collapsed-width="appStore.collapsedWidth"
      :collapsed-icon-size="appStore.collapsedIconSize"
      :options="menuOptions"
      accordion
    />
  </n-layout-sider>
</template>

<style scoped></style>
