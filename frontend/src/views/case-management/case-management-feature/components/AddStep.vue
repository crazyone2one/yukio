<script setup lang="ts">
import { StepList } from '/@/models/case-management/feature-case.ts'
import { ref, watch, h } from 'vue'
import { DataTableColumns, DropdownOption, NInput } from 'naive-ui'
import { getGenerateId } from '/@/utils'
import { useI18n } from 'vue-i18n'
import { ActionsItem } from '/@/components/table-more-action/types.ts'
import TableMoreAction from '/@/components/table-more-action/index.vue'

const props = withDefaults(
    defineProps<{
        stepList: any
        isDisabled?: boolean
    }>(),
    {
        isDisabled: false,
    },
)
const { t } = useI18n()
const emit = defineEmits(['update:stepList'])
const stepData = ref<StepList[]>([
    {
        id: getGenerateId(),
        step: '',
        expected: '',
        showStep: false,
        showExpected: false,
    },
])
const templateFieldColumns: DataTableColumns<StepList> = [
    {
        title: t('system.orgTemplate.numberIndex'),
        key: 'index',
        width: 100,
        render(row, rowIndex) {
            return h('span', {}, { default: () => rowIndex + 1 })
        },
    },
    {
        title: t('system.orgTemplate.useCaseStep'),
        key: 'caseStep',
        render(rowData, index) {
            return h(NInput, {
                value: rowData.step,
                maxlength: 1000,
                placeholder: t('system.orgTemplate.stepTip'),
                onUpdateValue(v) {
                    stepData.value[index].step = v
                },
            })
        },
    },
    {
        title: t('system.orgTemplate.expectedResult'),
        key: 'expectedResult',
        render(rowData, index) {
            return h(NInput, {
                value: rowData.expected,
                maxlength: 1000,
                placeholder: t('system.orgTemplate.expectationTip'),
                onUpdateValue(v) {
                    stepData.value[index].expected = v
                },
            })
        },
    },
    {
        title: t('system.orgTemplate.operation'),
        key: 'operation',
        width: 200,
        fixed: 'right',
        render(record) {
            if (!record.internal) {
                return h(TableMoreAction, {
                    list:
                        stepData.value.length <= 1
                            ? moreActions.slice(0, moreActions.length - 2)
                            : moreActions,
                    onSelect: (item: ActionsItem) => handleMoreActionSelect(item, record),
                })
            }
        },
    },
]
const moreActions: ActionsItem[] = [
    {
        label: 'caseManagement.featureCase.copyStep',
        eventTag: 'copyStep',
    },
    {
        label: 'caseManagement.featureCase.InsertStepsBefore',
        eventTag: 'InsertStepsBefore',
    },
    {
        label: 'caseManagement.featureCase.afterInsertingSteps',
        eventTag: 'afterInsertingSteps',
    },
    {
        isDivider: true,
    },
    {
        label: 'common.delete',
        danger: true,
        eventTag: 'delete',
    },
]
const handleMoreActionSelect = (item: DropdownOption, record: StepList) => {
    switch (item.key) {
        case 'copyStep':
            copyStep(record)
            break
        case 'InsertStepsBefore':
            insertStepsBefore(record)
            break
        case 'afterInsertingSteps':
            afterInsertingSteps(record)
            break
        default:
            deleteStep(record)
            break
    }
}
const copyStep = (record: StepList) => {
    const index = stepData.value.map((item: any) => item.id).indexOf(record.id)
    const insertItem = {
        ...record,
        id: getGenerateId(),
    }
    stepData.value.splice(index + 1, 0, insertItem)
}
const deleteStep = (record: StepList) => {
    stepData.value = stepData.value.filter((item: any) => item.id !== record.id)
    // setProps({ data: stepData.value })
}
const insertStepsBefore = (record: StepList) => {
    const index = stepData.value.map((item: any) => item.id).indexOf(record.id)
    const insertItem = {
        id: getGenerateId(),
        step: '',
        expected: '',
        showStep: false,
        showExpected: false,
    }
    stepData.value.splice(index, 0, insertItem)
}
const afterInsertingSteps = (record: StepList) => {
    const index = stepData.value.map((item: any) => item.id).indexOf(record.id)
    const insertItem = {
        id: getGenerateId(),
        step: '',
        expected: '',
        showStep: false,
        showExpected: false,
    }
    stepData.value.splice(index + 1, 0, insertItem)
}
// 添加步骤
const addStep = () => {
    stepData.value.push({
        id: getGenerateId(),
        step: '',
        expected: '',
        showStep: false,
        showExpected: false,
    })
}
watch(
    () => stepData.value,
    (val) => {
        emit('update:stepList', val)
        // setProps({ data: stepData.value })
    },
    { deep: true },
)

watch(
    () => props.stepList,
    () => {
        stepData.value = props.stepList
    },
    {
        immediate: true,
    },
)
</script>

<template>
    <n-data-table :columns="templateFieldColumns" :data="stepData" />
    <n-button v-if="!props.isDisabled" class="mt-2 px-0" text @click="addStep">
        <template #icon>
            <n-icon class="text-[14px]">
                <span class="i-tabler:circle-plus" />
            </n-icon>
        </template>
        {{ t('system.orgTemplate.addStep') }}
    </n-button>
</template>

<style scoped></style>
