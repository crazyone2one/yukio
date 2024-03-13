<script setup lang="ts">
import { FormInst, FormRules, FormValidationError, TreeSelectOption } from 'naive-ui'
import { computed, onBeforeMount, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from '/@/hooks/use-i18n'
import { CreateOrUpdateCase, DetailCase, StepList } from '/@/models/case-management/feature-case'
import { useAppStore } from '/@/store'
import useFeatureCaseStore from '/@/store/modules/case/feature-case'
import AddStep from '/@/views/case-management/case-management-feature/components/AddStep.vue'
import { getGenerateId } from '/@/utils'
import RichText from '/@/components/rich-text/index.vue'
import { useRequest } from 'alova'
import {
    getCaseDefaultFields,
    getCaseModuleTree,
} from '/@/api/modules/case-management/feature-case.ts'
import TagsInput from '/@/components/tags-input/index.vue'
import FileList from '/@/components/base-upload/FileList.vue'
import { MsFileItem } from '/@/components/base-upload/types.ts'
import { ModuleTreeNode } from '/@/models/common.ts'

const { t } = useI18n()
const route = useRoute()
const appStore = useAppStore()
const currentProjectId = computed(() => appStore.currentProjectId)
const props = defineProps<{
    formModeValue: Record<string, any> // 表单值
    caseId: string // 用例id
}>()

const emit = defineEmits(['update:formModeValue', 'changeFile'])
const formRef = ref<FormInst | null>(null)
const caseFormRef = ref<FormInst | null>(null)
const caseId = ref(props.caseId)
const featureCaseStore = useFeatureCaseStore()
const modelId = computed(() => featureCaseStore.moduleId[0])
const formRules = ref<FormRules[]>([])
const rowLength = ref<number>(0)
const fileList = ref<MsFileItem[]>([])
const rules: FormRules = {
    name: [{ required: true, message: t('system.orgTemplate.caseNamePlaceholder') }],
    moduleId: [{ required: true, message: t('system.orgTemplate.moduleRuleTip') }],
}
const initForm: DetailCase = {
    id: '',
    projectId: currentProjectId.value,
    templateId: '',
    name: '',
    prerequisite: '',
    caseEditType: 'STEP',
    steps: [],
    textDescription: '',
    expectedResult: '',
    description: '',
    publicCase: false,
    moduleId: modelId.value,
    versionId: '',
    tags: [],
    customFields: [],
    relateFileMetaIds: [],
    functionalPriority: '',
    reviewStatus: 'UN_REVIEWED',
}
const model = ref<DetailCase | CreateOrUpdateCase>({ ...initForm })
const caseTree = ref<TreeSelectOption[]>([])
// 步骤描述
const stepData = ref<StepList[]>([
    {
        id: getGenerateId(),
        step: '',
        expected: '',
        showStep: false,
        showExpected: false,
    },
])
const handleSelectType = (key: string) => (model.value.caseEditType = key)
// 总参数
const params = ref<Record<string, any>>({
    request: {},
    files: [], // 总文件列表
})
const { loading, send: getDefaultFields } = useRequest(
    () => getCaseDefaultFields(currentProjectId.value),
    {
        immediate: false,
    },
)
const { send: initSelectTree } = useRequest(
    () => getCaseModuleTree({ projectId: currentProjectId.value }),
    {
        immediate: false,
    },
)
const initDefaultFields = () => {
    formRules.value = []
    getDefaultFields().then((res) => {
        const { customFields, id } = res
        model.value.templateId = id
        formRules.value = customFields.map((item: any) => {
            return {
                type: item.type,
                name: item.fieldId,
                label: item.fieldName,
                value: item.defaultValue,
                required: item.required,
                options: item.options || [],
                props: {
                    modelValue: item.defaultValue,
                    options: item.options || [],
                },
            }
        })
    })
}
const resetForm = () => {
    model.value = { ...initForm, templateId: model.value.templateId }
    initDefaultFields()
    // model.value.customFields = formItem.value.map((item: any) => {
    //     return {
    //         fieldId: item.field,
    //         value: Array.isArray(item.value) ? JSON.stringify(item.value) : item.value,
    //     }
    // })
    fileList.value = []
    caseFormRef.value?.restoreValidation()
}
const isPass = () => {
    caseFormRef.value?.validate((e) => {
        if (!e) {
            formRef.value?.validate((err) => {
                return !err
            })
        } else {
            return false
        }
    })
}
onBeforeMount(async () => {
    caseId.value = ''
    caseId.value = props.caseId
    initSelectTree().then(
        (res) =>
            (caseTree.value = res.map((item: ModuleTreeNode) => {
                return {
                    key: item.id,
                    label: item.name,
                }
            })),
    )
    if (caseId.value) {
        // getCaseInfo()
    } else {
        initDefaultFields()
    }
})
// 监视表单变化
watch(
    () => model.value,
    (val) => {
        if (val) {
            if (val) {
                params.value.request = { ...model.value }
                emit('update:formModeValue', params.value)
            }
        }
    },
    { deep: true },
)
// 监视父组件传递过来的参数处理
watch(
    () => props.formModeValue,
    () => {
        // 这里要处理预览的参数格式给到params
        params.value = {
            ...props.formModeValue,
        }
    },
    { deep: true },
)
watch(
    () => stepData.value,
    () => {
        model.value.steps = stepData.value.map((item, index) => {
            return {
                num: index,
                desc: item.step,
                result: item.expected,
            }
        })
    },
    { deep: true },
)
watch(
    () => formRules.value,
    () => {
        rowLength.value = formRules.value.length + 2
    },
    { deep: true },
)
onBeforeUnmount(() => {
    formRules.value = []
})
defineExpose({
    isPass,
    resetForm,
})
</script>

<template>
    <div class="wrapper-preview">
        <div class="preview-left pr-4">
            <n-form
                ref="caseFormRef"
                :model="model"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging"
                class="rounded-[4px]"
            >
                <n-form-item :label="t('system.orgTemplate.caseName')" path="name">
                    <n-input
                        v-model:value="model.name"
                        :placeholder="t('system.orgTemplate.caseNamePlaceholder')"
                        :maxlength="255"
                    />
                </n-form-item>
                <n-form-item :label="t('system.orgTemplate.precondition')" path="precondition">
                    <n-input v-model:value="model.prerequisite" />
                </n-form-item>
                <n-form-item
                    :label="
                        model.caseEditType === 'STEP'
                            ? t('system.orgTemplate.stepDescription')
                            : t('system.orgTemplate.textDescription')
                    "
                    path="step"
                    class="relative"
                >
                    <div class="left-16 top-0">
                        <n-divider vertical />
                        <n-dropdown
                            trigger="hover"
                            :options="[
                                { label: t('system.orgTemplate.stepDescription'), key: 'STEP' },
                                { label: t('system.orgTemplate.textDescription'), key: 'TEXT' },
                            ]"
                            @select="handleSelectType"
                        >
                            <span class="changeType">
                                {{ t('system.orgTemplate.changeType') }}
                                <n-icon>
                                    <span class="i-tabler:square-rounded-arrow-down" />
                                </n-icon>
                            </span>
                        </n-dropdown>
                    </div>
                    <!-- 步骤描述 -->
                    <div v-if="model.caseEditType === 'STEP'">
                        <add-step v-model:step-list="stepData" :is-disabled="false" />
                    </div>
                    <rich-text v-else v-model:raw="model.textDescription" />
                </n-form-item>
                <n-form-item
                    v-if="model.caseEditType === 'TEXT'"
                    :label="t('caseManagement.featureCase.expectedResult')"
                    path="remark"
                >
                    <rich-text v-model:raw="model.expectedResult" />
                </n-form-item>
                <n-form-item :label="t('caseManagement.featureCase.remark')" path="description">
                    <rich-text v-model:raw="model.description" />
                </n-form-item>
            </n-form>
            <!-- 文件列表开始 -->
            <div class="w-[90%]">
                <file-list
                    ref="fileListRef"
                    v-model:file-list="fileList"
                    mode="static"
                    :show-upload-type-desc="true"
                >
                </file-list>
            </div>
        </div>
        <!-- 自定义字段开始 -->
        <div class="preview-right px-4">
            <div>
                <n-space v-if="loading" vertical> <n-skeleton text :repeat="rowLength" /></n-space>

                <n-form v-else ref="formRef" :model="model" :rules="rules" class="rounded-[4px]">
                    <n-form-item
                        :label="t('caseManagement.featureCase.ModuleOwned')"
                        path="moduleId"
                    >
                        <n-tree-select v-model:value="model.moduleId" :options="caseTree" />
                    </n-form-item>
                    <n-form-item :label="t('system.orgTemplate.tags')" path="tags">
                        <tags-input v-model:model-value="model.tags" />
                    </n-form-item>
                </n-form>
            </div>
        </div>
    </div>
</template>

<style scoped></style>
