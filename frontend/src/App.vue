<script setup lang="ts">
import { useEventListener, useWindowSize } from '@vueuse/core'
import {
    NConfigProvider,
    NDialogProvider,
    NGlobalStyle,
    NLoadingBarProvider,
    NMessageProvider,
    NNotificationProvider,
    darkTheme,
    dateEnUS,
    dateZhCN,
    enUS,
    useOsTheme,
    zhCN,
} from 'naive-ui'
import { computed, onMounted } from 'vue'
import useLocale from './locale/use-locale'
import { useAppStore } from './store'

const appStore = useAppStore()
const { currentLocale } = useLocale()
const locale = computed(() => {
    switch (currentLocale.value) {
        case 'zh-CN':
            return zhCN
        case 'en-US':
            return enUS
        default:
            return zhCN
    }
})
const dateLocale = computed(() => {
    switch (currentLocale.value) {
        case 'zh-CN':
            return dateZhCN
        case 'en-US':
            return dateEnUS
        default:
            return dateZhCN
    }
})
const osTheme = useOsTheme()
const theme = computed(() => (osTheme.value === 'dark' ? darkTheme : null))
onMounted(() => {
    const { height } = useWindowSize()
    appStore.innerHeight = height.value
})
/** 屏幕大小改变时重新赋值innerHeight */
useEventListener(window, 'resize', () => {
    const { height } = useWindowSize()
    appStore.innerHeight = height.value
})
</script>

<template>
    <n-config-provider
        :theme="theme"
        :locale="locale"
        :date-locale="dateLocale"
    >
        <n-global-style />
        <n-loading-bar-provider>
            <n-dialog-provider>
                <n-notification-provider>
                    <n-message-provider>
                        <router-view />
                        <slot />
                    </n-message-provider>
                </n-notification-provider>
            </n-dialog-provider>
        </n-loading-bar-provider>
    </n-config-provider>
</template>

<style scoped>
.logo {
    height: 6em;
    padding: 1.5em;
    will-change: filter;
    transition: filter 300ms;
}
.logo:hover {
    filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
    filter: drop-shadow(0 0 2em #42b883aa);
}
</style>
