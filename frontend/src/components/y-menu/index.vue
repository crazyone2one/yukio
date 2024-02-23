<script setup lang="ts">
import type { MenuOption } from 'naive-ui'
import { NMenu } from 'naive-ui'
import { computed, h, ref, watchEffect } from 'vue'
import { RouteRecordRaw, RouterLink } from 'vue-router'
import useMenuTree from './use-menu-tree'
import { useI18n } from '/@/hooks/use-i18n'
import { useAppStore } from '/@/store'

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
const activeKey = ref<string | null>(null)

const { menuTree } = useMenuTree()
const menuOptions = ref<MenuOption[]>([])
const generateMenuLabel = (item: RouteRecordRaw) => {
    if (item.meta?.isTopMenu) {
        return () =>
            h(
                RouterLink,
                { to: { name: item.name } },
                {
                    default: () => t(item.meta?.locale as string),
                },
            )
    } else {
        return () => t(item.meta?.locale as string)
    }
}
const generateMenuOptions = (menu: RouteRecordRaw[]) => {
    return menu.map((item) => {
        let menuOption: MenuOption = {}
        menuOption.label = generateMenuLabel(item)
        menuOption.key = item.name as string
        if (item.children && item.children.length > 0)
            menuOption.children = generateMenuOptions(item.children)
        return menuOption
    })
}
watchEffect(() => {
    if (menuTree.value) {
        menuOptions.value = generateMenuOptions(
            menuTree.value as RouteRecordRaw[],
        )
    }
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
        content-style="padding: 24px;"
        :native-scrollbar="false"
        @collapse="collapsed = true"
        @expand="collapsed = false"
    >
        <n-menu
            v-model:value="activeKey"
            :collapsed="collapsed"
            :collapsed-width="64"
            :collapsed-icon-size="22"
            :options="menuOptions"
            accordion
        />
    </n-layout-sider>
</template>

<style scoped></style>
