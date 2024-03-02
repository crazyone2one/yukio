<script setup lang="ts">
import type { MenuOption } from 'naive-ui'
import { NIcon, NMenu } from 'naive-ui'
import { computed, h, onMounted, ref } from 'vue'
import { RouteRecordRaw, RouterLink, useRoute } from 'vue-router'
import useMenuTree from './use-menu-tree'
import { CaseManagementRouteEnum, SettingRouteEnum, TestPlanRouteEnum } from '/@/enums/route-enum'
import { useI18n } from '/@/hooks/use-i18n'
import { useAppStore } from '/@/store'

const { t } = useI18n()
const appStore = useAppStore()
const route = useRoute()
const collapsed = computed({
    get() {
        if (appStore.device === 'desktop') return appStore.menuCollapse
        return false
    },
    set(value: boolean) {
        appStore.updateSettings({ menuCollapse: value })
    },
})
const activeKey = ref<string | null>(null)
/**
 * 菜单图标
 */
const renderIcon = (icon: string) => {
    return () => h(NIcon, {}, { default: () => h('span', { class: icon }) })
}
const { menuTree } = useMenuTree()
const menuOptions: MenuOption[] = [
    {
        label: t('menu.caseManagement'),
        key: CaseManagementRouteEnum.CASE_MANAGEMENT,
        children: [
            {
                // label: t('menu.caseManagement.featureCase'),
                label: () =>
                    h(
                        RouterLink,
                        {
                            to: {
                                name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
                            },
                        },
                        { default: () => t('menu.caseManagement.featureCase') },
                    ),
                key: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
            },
            {
                // label: t('menu.caseManagement.caseManagementReview'),
                label: () =>
                    h(
                        RouterLink,
                        {
                            to: {
                                name: CaseManagementRouteEnum.CASE_MANAGEMENT_REVIEW,
                            },
                        },
                        { default: () => t('menu.caseManagement.featureCase') },
                    ),
                key: CaseManagementRouteEnum.CASE_MANAGEMENT_REVIEW,
            },
        ],
    },
    {
        // label: t('menu.testPlan'),
        label: () =>
            h(
                RouterLink,
                {
                    to: {
                        name: TestPlanRouteEnum.TEST_PLAN_INDEX,
                    },
                },
                { default: () => t('menu.testPlan') },
            ),
        key: TestPlanRouteEnum.TEST_PLAN_INDEX,
        icon: renderIcon('i-icon-report'),
    },
    {
        label: t('menu.settings'),
        key: SettingRouteEnum.SETTING,
        icon: renderIcon('i-tabler:settings'),
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
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
                                    },
                                },
                                { default: () => t('menu.settings.system.user') },
                            ),
                        key: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
                    },
                    {
                        // label: t('menu.settings.system.usergroup'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP,
                                    },
                                },
                                { default: () => t('menu.settings.system.usergroup') },
                            ),
                        key: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP,
                    },
                    {
                        // label: t('menu.settings.system.organizationAndProject'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION,
                                    },
                                },
                                { default: () => t('menu.settings.system.organizationAndProject') },
                            ),
                        key: SettingRouteEnum.SETTING_SYSTEM_ORGANIZATION,
                    },
                    {
                        // label: t('menu.settings.system.log'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_SYSTEM_LOG,
                                    },
                                },
                                { default: () => t('menu.settings.system.log') },
                            ),
                        key: SettingRouteEnum.SETTING_SYSTEM_LOG,
                    },
                ],
            },
            {
                label: t('menu.settings.organization'),
                key: SettingRouteEnum.SETTING_ORGANIZATION,
                children: [
                    {
                        // label: t('menu.settings.organization.member'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
                                    },
                                },
                                { default: () => t('menu.settings.organization.member') },
                            ),
                        key: SettingRouteEnum.SETTING_ORGANIZATION_MEMBER,
                    },
                    {
                        // label: t('menu.settings.organization.userGroup'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_ORGANIZATION_USER_GROUP,
                                    },
                                },
                                { default: () => t('menu.settings.organization.userGroup') },
                            ),
                        key: SettingRouteEnum.SETTING_ORGANIZATION_USER_GROUP,
                    },
                    {
                        // label: t('menu.settings.organization.project'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
                                    },
                                },
                                { default: () => t('menu.settings.organization.project') },
                            ),
                        key: SettingRouteEnum.SETTING_ORGANIZATION_PROJECT,
                    },
                    {
                        // label: t('menu.settings.organization.template'),
                        label: () =>
                            h(
                                RouterLink,
                                {
                                    to: {
                                        name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE,
                                    },
                                },
                                { default: () => t('menu.settings.organization.template') },
                            ),
                        key: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE,
                    },
                ],
            },
        ],
    },
]

const findMenuOpenKeys = (target: string) => {
    const result: string[] = []
    let isFind = false
    const backtrack = (item: RouteRecordRaw | null, keys: string[]) => {
        if (target.includes(item?.name as string)) {
            result.push(...keys)
            if (result.length >= 2) {
                // 由于目前存在三级子路由，所以至少会匹配到三层才算结束
                isFind = true
                return
            }
        }
        if (item?.children?.length) {
            item.children.forEach((el) => {
                backtrack(el, [...keys, el.name as string])
            })
        }
    }
    menuTree.value?.forEach((el) => {
        if (isFind) return // 节省性能
        backtrack(el, [el?.name as string])
    })
    return result
}
const selectedKey = ref<string[]>([])
onMounted(() => {
    const menuOpenKeys = findMenuOpenKeys(route.name as string)
    selectedKey.value = [menuOpenKeys[menuOpenKeys.length - 1]]
    activeKey.value = route.name as string
})
</script>
<template>
    <n-layout-sider
        bordered
        collapse-mode="width"
        :collapsed-width="64"
        :width="appStore.menuWidth"
        :collapsed="collapsed"
        show-trigger
        :native-scrollbar="false"
        @collapse="collapsed = true"
        @expand="collapsed = false"
    >
        <n-menu
            v-model:value="activeKey"
            :collapsed="collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="30"
            :options="menuOptions"
            :indent="20"
            accordion
        />
    </n-layout-sider>
</template>

<style scoped></style>
