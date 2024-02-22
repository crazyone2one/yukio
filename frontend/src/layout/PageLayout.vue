<template>
    <router-view v-slot="{ Component, route }">
        <transition name="fade" mode="out-in" appear>
            <!-- transition内必须有且只有一个根元素，不然会导致二级路由的组件无法渲染 -->
            <div v-show="true" class="page-content">
                <keep-alive>
                    <component :is="Component" :key="route.fullPath" />
                </keep-alive>
            </div>
        </transition>
    </router-view>
</template>

<script setup lang="ts">
import { useDialog, useLoadingBar, useMessage, useNotification } from 'naive-ui'
import router from '/@/router'
// todo: route transtion
const loadingBar = useLoadingBar()
router.beforeEach(() => loadingBar?.start())
router.afterEach(() => loadingBar?.finish())

window.$message = useMessage()
window.$dialog = useDialog()
window.$notification = useNotification()
</script>

<style scoped></style>
