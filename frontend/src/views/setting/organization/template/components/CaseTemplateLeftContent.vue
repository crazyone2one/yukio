<script setup lang="ts">
import { ref } from 'vue'
import { DataTableColumns, FormInst } from 'naive-ui'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { StepList } from '/@/models/case-management/feature-case.ts'

const { t } = useI18n()
const isDisabled = ref<boolean>(true)
const viewFormRef = ref<FormInst | null>(null)
const viewForm = ref({
    name: '',
    precondition: '',
    value: '',
    remark: '',
})
const handleSelectType = () => {}
const columns: DataTableColumns<StepList> = [
    { title: t('system.orgTemplate.numberIndex'), key: 'index', width: 100 },
    { title: t('system.orgTemplate.useCaseStep'), key: 'caseStep' },
    { title: t('system.orgTemplate.expectedResult'), key: 'expectedResult' },
    { title: t('system.orgTemplate.operation'), key: 'operation', fixed: 'right', width: 200 },
]
</script>

<template>
    <n-form
        ref="viewFormRef"
        :model="viewForm"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
        class="rounded-[4px]"
    >
        <n-form-item :label="t('system.orgTemplate.caseName')" path="caseName">
            <n-input
                v-model:value="viewForm.name"
                :placeholder="t('system.orgTemplate.caseNamePlaceholder')"
                :maxlength="255"
                class="max-w-[732px]"
                :disabled="isDisabled"
            />
        </n-form-item>
        <n-form-item :label="t('system.orgTemplate.precondition')" path="precondition">
            <n-input v-model:value="viewForm.precondition" />
        </n-form-item>
        <n-form-item :label="t('system.orgTemplate.stepDescription')" path="step">
            <div class="absolute left-16 top-0">
                <n-divider direction="vertical" />
                <n-dropdown :options="[]" @select="handleSelectType">
                    <span class="text-[14px] text-[var(--color-text-4)]">
                        {{ t('system.orgTemplate.changeType') }}
                        <n-icon>
                            <span class="i-tabler:square-rounded-chevron-down" />
                        </n-icon>
                    </span>
                </n-dropdown>
            </div>
            <!-- 步骤描述 -->
            <div class="w-full">
                <n-data-table
                    ref="stepTableRef"
                    :columns="columns"
                    :data="[{ id: 1, showStep: false, showExpected: false }]"
                    :bordered="false"
                />
            </div>
            <n-button class="mt-2 px-0" text :disabled="isDisabled">
                <template #icon>
                    <n-icon class="text-[14px]">
                        <span class="i-tabler:circle-plus" />
                    </n-icon>
                </template>
                {{ t('system.orgTemplate.addStep') }}
            </n-button>
        </n-form-item>
        <n-form-item label="备注" path="remark">
            <n-input v-model:value="viewForm.remark" />
        </n-form-item>
        <n-form-item label="添加附件" path="attachment">
            <div class="flex flex-col">
                <div class="mb-1">
                    <n-button :disabled="isDisabled">
                        <template #icon>
                            <n-icon class="text-[14px]">
                                <span class="i-tabler:circle-plus" />
                            </n-icon>
                        </template>
                        {{ t('system.orgTemplate.addAttachment') }}
                    </n-button>
                </div>
                <div class="text-[var(--color-text-4)]">
                    {{ t('system.orgTemplate.addAttachmentTip') }}
                </div>
            </div>
        </n-form-item>
    </n-form>
</template>

<style scoped></style>
