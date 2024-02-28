<script setup lang="ts">
import HeaderWorkspace from '../components/header/HeaderWorkspace.vue'
import PersonalMenus from '/@/components/personal-menus/index.vue'
import YMenu from '/@/components/y-menu/index.vue'
import { useAppStore } from '/@/store'

const appStore = useAppStore()
</script>
<template>
    <n-layout :style="{ height: appStore.innerHeight + 'px' }">
        <n-layout-header style="height: 64px; padding: 24px" bordered class="nav">
            <n-flex justify="end">
                <!-- <div>颐和园路</div> -->
                <header-workspace />
                <personal-menus />
            </n-flex>
        </n-layout-header>
        <n-layout position="absolute" style="top: 64px; bottom: 64px" has-sider>
            <y-menu />
            <n-layout content-style="padding: 24px;" :native-scrollbar="false">
                <!-- <page-layout></page-layout> -->
                <router-view />
            </n-layout>
        </n-layout>
        <n-layout-footer position="absolute" style="height: 64px; padding: 24px" bordered>
            城府路
        </n-layout-footer>
    </n-layout>
</template>

<style scoped>
.nav {
    align-items: center;
}
</style>
