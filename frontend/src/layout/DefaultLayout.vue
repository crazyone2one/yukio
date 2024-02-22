<script setup lang="ts">
import PageLayout from './PageLayout.vue'
import { useAppStore } from '/@/store'
const appStore = useAppStore()
</script>
<template>
    <n-layout :style="{ height: appStore.innerHeight + 'px' }">
        <n-layout-header style="height: 64px; padding: 24px" bordered>
            颐和园路
        </n-layout-header>
        <n-layout position="absolute" style="top: 64px; bottom: 64px" has-sider>
            <n-layout-sider
                content-style="padding: 24px;"
                :native-scrollbar="false"
                bordered
            >
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
                <n-h2>海淀桥</n-h2>
            </n-layout-sider>
            <n-layout content-style="padding: 24px;" :native-scrollbar="false">
                <page-layout></page-layout>
            </n-layout>
        </n-layout>
        <n-layout-footer
            position="absolute"
            style="height: 64px; padding: 24px"
            bordered
        >
            城府路
        </n-layout-footer>
    </n-layout>
</template>

<style scoped></style>
