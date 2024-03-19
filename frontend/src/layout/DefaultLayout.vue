<script setup lang="ts">
import { useWindowSize } from '@vueuse/core'
import { NLayout, NLayoutFooter } from 'naive-ui'
import { computed } from 'vue'
import Menus from '/@/components/base-menu/index.vue'
import BaseNavBar from '/@/components/base-navbar/index.vue'
import useAppStore from '/@/store/modules/app'

const appStore = useAppStore()
const { height } = useWindowSize()
const navbar = computed(() => appStore.navbar)
</script>
<template>
  <n-layout class="layout" :style="{ height: height + 'px' }">
    <div v-if="navbar" class="layout-navbar z-[100]">
      <BaseNavBar />
    </div>
    <n-layout position="absolute" style="top: 64px; bottom: 64px" has-sider>
      <Menus />
      <n-layout content-style="padding: 24px;" :native-scrollbar="false">
        <router-view />
      </n-layout>
    </n-layout>
    <n-layout-footer
      v-if="appStore.footer"
      position="absolute"
      style="height: 64px; padding: 24px"
      bordered
    >
      城府路
    </n-layout-footer>
  </n-layout>
</template>

<style scoped>
@nav-size-height: 56px;
@layout-max-width: 1100px;
.layout {
  @apply h-full w-full;
}
.layout-navbar {
  @apply fixed left-0 top-0 w-full;

  height: 56px;
}
</style>
