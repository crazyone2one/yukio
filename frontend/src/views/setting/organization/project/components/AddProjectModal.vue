<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { computed, ref, watchEffect } from 'vue'
import {
    createOrUpdateProjectByOrg,
    getSystemOrgOption,
} from '/@/api/modules/setting/OrganizationAndProject'
import UserSelect from '/@/components/user-selector/index.vue'
import { UserRequestTypeEnum } from '/@/components/user-selector/utils'
import { useI18n } from '/@/hooks/use-i18n'
import { CreateOrUpdateSystemProjectParams } from '/@/models/setting/system/orgAndProject'
import { useAppStore, useUserStore } from '/@/store'

const props = defineProps<{
    currentProject?: CreateOrUpdateSystemProjectParams
}>()
const userStore = useUserStore()
const appStore = useAppStore()
const { t } = useI18n()
const currentVisible = defineModel<boolean>('visible', {
    default: false,
})
const formRef = ref<FormInst | null>(null)
const affiliatedOrgOption = ref<SelectOption[]>([])
const rules: FormRules = {
    name: [
        { required: true, message: t('system.project.projectNameRequired') },
        { max: 255, message: t('common.nameIsTooLang') },
    ],
    userIds: { required: true, message: t('system.project.projectAdminIsNotNull') },
}

const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void }>()
const allModuleIds = ['bugManagement', 'caseManagement', 'apiTest']
const currentOrgId = computed(() => appStore.currentOrgId)
const isEdit = computed(() => !!(props.currentProject && props.currentProject.id))
const moduleOption = [
    // { label: 'menu.workbench', value: 'workstation' },
    // { label: 'menu.testPlan', value: 'testPlan' },
    { label: 'menu.bugManagement', value: 'bugManagement' },
    { label: 'menu.caseManagement', value: 'caseManagement' },
    { label: 'menu.apiTest', value: 'apiTest' },
    // { label: 'menu.uiTest', value: 'uiTest' },
    // { label: 'menu.performanceTest', value: 'loadTest' },
]
const { send: initAffiliatedOrgOption } = useRequest(() => getSystemOrgOption(), {
    immediate: false,
})
const {
    loading: submiting,
    form: model,
    send: submit,
    onSuccess,
    onError,
    reset: resetForm,
} = useForm(
    (formData) => {
        return createOrUpdateProjectByOrg({ ...formData })
    },
    {
        // 初始化表单数据
        initialForm: {
            id: '',
            name: '',
            userIds: userStore.id ? [userStore.id] : [],
            organizationId: currentOrgId.value,
            description: '',
            enable: true,
            moduleIds: allModuleIds,
            resourcePoolIds: [],
        },
    },
)

const handleCancel = (shouldSearch = false): void => {
    emit('cancel', shouldSearch)
    formRef.value?.restoreValidation()
    resetForm()
}
onSuccess(() => {
    window.$message.success(
        isEdit.value
            ? t('system.project.updateProjectSuccess')
            : t('system.project.createProjectSuccess'),
    )
    handleCancel(true)
})
onError((error) => {
    console.error(error)
})
const handleBeforeOk = (e: MouseEvent) => {
    e.preventDefault()
    formRef.value?.validate((errors) => {
        if (errors) {
            return false
        }
        submit()
        console.log(`output->modal.val`, model.value)
    })
}

watchEffect(() => {
    initAffiliatedOrgOption().then((res) => {
        affiliatedOrgOption.value = res
    })
    if (props.currentProject?.id) {
        if (props.currentProject) {
            model.value = Object.assign({}, props.currentProject)
        } else {
            resetForm()
        }
    }
})
</script>

<template>
    <n-modal
        v-model:show="currentVisible"
        preset="dialog"
        transform-origin="center"
        :mask-closable="false"
        :on-close="handleCancel"
    >
        <template #header>
            <span v-if="isEdit">
                {{ t('system.project.updateProject') }}
                <span class="text-[var(--color-text-4)]">({{ props.currentProject?.name }})</span>
            </span>
            <span v-else>
                {{ t('system.project.createProject') }}
            </span>
        </template>
        <div>
            <n-form
                ref="formRef"
                :model="model"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                class="rounded-[4px]"
            >
                <n-form-item :label="t('system.project.name')" path="name">
                    <n-input
                        v-model:value="model.name"
                        clearable
                        :placeholder="t('system.project.projectNamePlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.project.affiliatedOrg')" path="organizationId">
                    <n-select
                        v-model:value="model.organizationId"
                        :options="affiliatedOrgOption"
                        :placeholder="t('system.project.affiliatedOrgPlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.project.projectAdmin')" path="userIds">
                    <user-select
                        v-model="model.userIds"
                        :placeholder="t('system.project.pleaseSelectAdmin')"
                        :type="UserRequestTypeEnum.ORGANIZATION_PROJECT_ADMIN"
                    />
                </n-form-item>
                <n-form-item :label="t('system.project.moduleSetting')" path="module">
                    <n-checkbox-group v-model:value="model.moduleIds">
                        <n-space item-style="display: flex;">
                            <n-checkbox
                                v-for="item in moduleOption"
                                :key="item.value"
                                :value="item.value"
                                :label="t(item.label)"
                            />
                        </n-space>
                    </n-checkbox-group>
                </n-form-item>
                <n-form-item :label="t('system.organization.description')" path="description">
                    <n-input
                        v-model:value="model.description"
                        type="textarea"
                        clearable
                        :maxlength="1000"
                        :placeholder="t('system.project.descriptionPlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.organization.status')">
                    <div class="flex flex-row items-center gap-[4px]">
                        <n-switch v-model:value="model.enable" size="small" />
                        <n-tooltip trigger="hover">
                            <template #trigger>
                                <n-icon :size="20">
                                    <span
                                        class="i-tabler:question-circle text-stone-500 hover:text-sky-500"
                                    />
                                </n-icon>
                            </template>
                            {{ t('system.project.createTip') }}
                        </n-tooltip>
                    </div>
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <n-button secondary size="small" :loading="submiting" @click="handleCancel(false)">
                {{ t('common.cancel') }}
            </n-button>
            <n-button type="primary" size="small" :loading="submiting" @click="handleBeforeOk">
                {{ isEdit ? t('common.confirm') : t('common.create') }}
            </n-button>
        </template>
    </n-modal>
</template>

<style scoped></style>
