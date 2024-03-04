<script setup lang="ts">
import { useRequest } from 'alova'
import { computed, ref } from 'vue'
import PlanTable from './components/PlanTable.vue'
import TestPlanTree from './components/TestPlanTree.vue'
import { createPlanModuleTree } from '/@/api/modules/test-plan'
import YPopConfirm from '/@/components/y-pop-confirm/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import { CreateOrUpdateModule } from '/@/models/case-management/feature-case'
import { useAppStore } from '/@/store'

const { t } = useI18n()
const appStore = useAppStore()
const currentProjectId = computed(() => appStore.currentProjectId)
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const modulesCount = ref<Record<string, any>>({})
const isExpandAll = ref(false)
// 全部展开或折叠
const expandHandler = () => {
    isExpandAll.value = !isExpandAll.value
}
const addSubVisible = ref(false)
const rootModulesName = ref<string[]>([])
const planTreeRef = ref<InstanceType<typeof TestPlanTree> | null>(null)
const activeFolder = ref<string>('all')
const offspringIds = ref<string[]>([])
const activeCaseType = ref<'folder' | 'module'>('folder') // 激活计划树类型
const setRootModules = (names: string[]) => (rootModulesName.value = names)
const split = ref<number>(0.2)
const confirmRef = ref<InstanceType<typeof YPopConfirm> | null>(null)
const { send: save } = useRequest((param) => createPlanModuleTree(param), { immediate: false })
const confirmHandler = () => {
    const { field } = confirmRef.value?.form || {}
    if (field) {
        const params: CreateOrUpdateModule = {
            projectId: currentProjectId.value,
            name: field,
            parentId: 'NONE',
        }
        save(params).then(() => {
            window.$message.success(t('caseManagement.featureCase.addSuccess'))
            planTreeRef.value?.initModules()
            addSubVisible.value = false
        })
    }
}
const planNodeSelect = (keys: string, _offspringIds: string[]) => {
    activeFolder.value = keys
    activeCaseType.value = 'module'
    offspringIds.value = [..._offspringIds]
}
const setActiveFolder = (type: string) => {
    activeFolder.value = type
}
</script>
<template>
    <div class="rounded-2xl bg-white">
        <div class="p-[24px] pb-[16px]">
            <n-button type="primary">{{ t('testPlan.testPlanIndex.createTestPlan') }}</n-button>
        </div>
        <n-divider class="!my-0" />
        <div class="pageWrap">
            <n-split v-model:size="split" direction="horizontal" :min="0.2" class="h-full">
                <template #1>
                    <div class="p-[24px] pb-0">
                        <div class="test-plan h-[100%]">
                            <div class="case h-[38px]">
                                <div class="flex items-center" @click="setActiveFolder('all')">
                                    <n-icon size="20" color="#333">
                                        <span class="i-tabler:folder-filled" />
                                    </n-icon>
                                    <div class="folder-name mx-[4px]">
                                        {{ t('testPlan.testPlanIndex.allTestPlan') }}
                                    </div>
                                    <div class="folder-count">({{ modulesCount.all || 0 }})</div>
                                </div>
                                <div class="ml-auto flex items-center">
                                    <n-tooltip trigger="hover">
                                        <template #trigger>
                                            <n-button
                                                text
                                                class="!mr-0 p-[4px]"
                                                @click="expandHandler"
                                            >
                                                <template #icon>
                                                    <n-icon size="16" color="#333">
                                                        <span
                                                            :class="
                                                                isExpandAll
                                                                    ? 'i-tabler:layout-bottombar-collapse-filled'
                                                                    : 'i-tabler:layout-bottombar-expand-filled'
                                                            "
                                                        />
                                                    </n-icon>
                                                </template>
                                            </n-button>
                                        </template>
                                        {{
                                            isExpandAll
                                                ? t('testPlan.testPlanIndex.collapseAll')
                                                : t('testPlan.testPlanIndex.expandAll')
                                        }}
                                    </n-tooltip>
                                    <y-pop-confirm
                                        ref="confirmRef"
                                        v-model:visible="addSubVisible"
                                        :title="t('testPlan.testPlanIndex.addSubModule')"
                                        :ok-text="t('common.confirm')"
                                        :is-delete="false"
                                        :all-names="rootModulesName"
                                        :field-config="{
                                            placeholder: t('testPlan.testPlanIndex.addGroupTip'),
                                        }"
                                        @confirm="confirmHandler"
                                    />
                                </div>
                            </div>
                            <n-divider class="my-[8px]" />
                            <test-plan-tree
                                ref="planTreeRef"
                                :is-expand-all="isExpandAll"
                                :active-folder="activeFolder"
                                @init="setRootModules"
                                @plan-tree-node-select="planNodeSelect"
                            />
                        </div>
                    </div>
                </template>
                <template #2>
                    <plan-table
                        :active-folder="activeFolder"
                        :offspring-ids="offspringIds"
                        :active-folder-type="activeCaseType"
                        :modules-count="modulesCount"
                    />
                </template>
            </n-split>
        </div>
    </div>
</template>

<style scoped></style>
