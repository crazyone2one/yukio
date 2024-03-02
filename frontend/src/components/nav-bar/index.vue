<script setup lang="ts">
import { useRequest } from 'alova'
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { switchProject } from '/@/api/modules/project-management/project'
import logo from '/@/assets/vue.svg'
import HeaderWorkspace from '/@/components/header/HeaderWorkspace.vue'
import PersonalMenus from '/@/components/personal-menus/index.vue'
import { useAppStore, useUserStore } from '/@/store'

const appStore = useAppStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const handleUpdateValue = (value: string) => {
    window.$message.info(value)
    appStore.setCurrentProjectId(value)
    send(value)
        .then((res) => {
            console.log(`output->res`, res)
        })
        .finally(() => {
            router.replace({
                path: route.path,
                query: {
                    ...route.query,
                    organizationId: appStore.currentOrgId,
                    projectId: appStore.currentProjectId,
                },
            })
        })
}
const { send } = useRequest(
    (value) =>
        switchProject({
            projectId: value,
            userId: userStore.id || '',
        }),
    { immediate: false },
)
watch(
    () => appStore.currentOrgId,
    async () => {
        appStore.initProjectList()
    },
    {
        immediate: true,
    },
)
</script>
<template>
    <div class="flex h-full justify-between bg-transparent">
        <div class="flex items-center pl-[24px]">
            <n-space>
                <div class="one-line-text flex max-w-[145px] items-center">
                    <img :src="logo" class="mr-[4px] h-[34px] w-[32px]" />
                    <div
                        class="font-['Helvetica_Neue'] text-[16px] font-bold text-[rgb(var(--primary-5))]"
                    >
                        Yukio
                    </div>
                </div>
            </n-space>
        </div>
        <div class="flex flex-1 items-center">
            <n-divider vertical class="ml-0" />
            <n-select
                v-model:value="appStore.currentProjectId"
                :options="appStore.projectList"
                filterable
                class="w-[200px] focus-within:!bg-[var(--color-text-n8)] hover:!bg-[var(--color-text-n8)]"
                @update:value="handleUpdateValue"
            />
            <n-divider vertical class="mr-0" />
        </div>
        <ul class="right-side">
            <li>
                <header-workspace />
                <n-divider vertical class="mr-0" />
            </li>
            <li><personal-menus /></li>
        </ul>
    </div>
</template>

<style scoped>
.right-side {
    display: flex;
    list-style-type: none;
    padding-right: 20px;
    li {
        display: flex;
        align-items: center;
        padding-left: 10px;
    }
    .nav-btn {
        font-size: 16px;
        border-color: rgb(var(--gray-2));
        color: rgb(var(--gray-8));
        line-height: 24px;
    }
}
</style>
