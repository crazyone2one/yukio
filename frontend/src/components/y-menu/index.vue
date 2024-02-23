<script setup lang="ts">
import type { MenuOption } from 'naive-ui'
import { NIcon, NMenu } from 'naive-ui'
import { computed, h, onMounted, ref, watchEffect } from 'vue'
import { RouteRecordRaw, RouterLink, useRoute } from 'vue-router'
import useMenuTree from './use-menu-tree'
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

const { menuTree } = useMenuTree()
const menuOptions = ref<MenuOption[]>([])
/**
 * 菜单label
 * @param item 路由信息
 */
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
/**
 * 菜单图标
 */
const renderIcon = (icon: string) => {
    return () => h(NIcon, {}, { default: () => h('span', { class: icon }) })
}
const generateMenuOptions = (menu: RouteRecordRaw[]) => {
    return menu.map((item) => {
        let menuOption: MenuOption = {}
        menuOption.label = generateMenuLabel(item)
        menuOption.key = item.name as string
        menuOption.icon = renderIcon(item.meta?.icon as string)
        // if (item.component === null) {
        //     menuOption.type = 'group'
        // }
        if (item.children && item.children.length > 0) {
            menuOption.children = generateMenuOptions(item.children)
        }
        return menuOption
    })
}
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
watchEffect(() => {
    if (menuTree.value) {
        menuOptions.value = generateMenuOptions(
            menuTree.value as RouteRecordRaw[],
        )
    }
})
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
