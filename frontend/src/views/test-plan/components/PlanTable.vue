<script setup lang="ts">
import { ref } from 'vue'
import AllTable from './AllTable.vue'
import AdvanceFilter from '/@/components/advance-filter/index.vue'
import { useI18n } from '/@/hooks/use-i18n'

const { t } = useI18n()
const props = defineProps<{
    activeFolder: string
    activeFolderType: 'folder' | 'module'
    offspringIds: string[] // 当前选中文件夹的所有子孙节点id
    modulesCount: Record<string, number> // 模块数量
}>()
const moduleNamePath = ref<string>('全部测试计划')

const showType = ref<string>('all')
</script>
<template>
    <advance-filter>
        <template #left>
            <div class="flex w-full justify-between">
                <div class="text-[var(--color-text-1)]">
                    {{ moduleNamePath }}
                    <span class="text-[var(--color-text-4)]">
                        ({{ props.modulesCount[props.activeFolder] || 0 }})</span
                    >
                </div>
                <n-radio-group v-model:value="showType" type="button" class="file-show-type mr-2">
                    <n-radio-button value="all" class="show-type-icon p-[2px]">
                        {{ t('testPlan.testPlanIndex.all') }}
                    </n-radio-button>
                    <n-radio-button value="testPlan" class="show-type-icon p-[2px]">
                        {{ t('testPlan.testPlanIndex.testPlan') }}
                    </n-radio-button>
                    <n-radio-button value="testPlanGroup" class="show-type-icon p-[2px]">
                        {{ t('testPlan.testPlanIndex.testPlanGroup') }}
                    </n-radio-button>
                </n-radio-group>
            </div>
        </template>
    </advance-filter>
    <all-table v-if="showType === 'all'" />
</template>

<style scoped></style>
