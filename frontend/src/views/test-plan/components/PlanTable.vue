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
                <div>
                    {{ moduleNamePath }}
                    <span> ({{ props.modulesCount[props.activeFolder] || 0 }})</span>
                </div>
                <n-radio-group
                    v-model:value="showType"
                    type="button"
                    class="grid grid-cols-2 mb-[8px] mr-2"
                    size="small"
                >
                    <n-radio-button value="all" class="flex p-[4px] p-[2px] leading-5">
                        {{ t('testPlan.testPlanIndex.all') }}
                    </n-radio-button>
                    <n-radio-button value="testPlan" class="flex p-[4px] p-[2px] leading-5">
                        {{ t('testPlan.testPlanIndex.testPlan') }}
                    </n-radio-button>
                    <n-radio-button value="testPlanGroup" class="flex p-[4px] p-[2px] leading-5">
                        {{ t('testPlan.testPlanIndex.testPlanGroup') }}
                    </n-radio-button>
                </n-radio-group>
            </div>
        </template>
    </advance-filter>
    <all-table v-if="showType === 'all'" />
</template>

<style scoped></style>
