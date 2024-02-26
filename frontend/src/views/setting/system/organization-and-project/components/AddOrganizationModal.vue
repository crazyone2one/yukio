<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules } from 'naive-ui'
import { computed, ref, watchEffect } from 'vue'
import { createOrUpdateOrg } from '/@/api/modules/setting/OrganizationAndProject'
import UserSelect from '/@/components/user-selector/index.vue'
import { UserRequestTypeEnum } from '/@/components/user-selector/utils'
import { useI18n } from '/@/hooks/use-i18n'
import type { CreateOrUpdateSystemOrgParams } from '/@/models/setting/system/orgAndProject'
import { useUserStore } from '/@/store'
import { characterLimit } from '/@/utils'

const currentVisible = defineModel<boolean>('visible', { default: false })
const { t } = useI18n()
const userStore = useUserStore()
const props = defineProps<{ currentOrganization?: CreateOrUpdateSystemOrgParams }>()
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void }>()
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
    name: [
        { required: true, message: t('system.organization.organizationNameRequired') },
        { max: 255, message: t('common.nameIsTooLang') },
    ],
    userIds: {
        type: 'array',
        required: true,
        message: t('system.organization.organizationAdminRequired'),
    },
}
const isEdit = computed(() => !!props.currentOrganization?.id)

const handleCancel = (shouldSearch = false) => {
    emit('cancel', shouldSearch)
    formRef.value?.restoreValidation()
}
const {
    // 提交状态
    loading: submiting,

    // 响应式的表单数据，内容由initialForm决定
    form: model,

    // 提交数据函数
    send: submit,

    // 提交成功回调绑定
    onSuccess,

    // 提交失败回调绑定
    onError,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        return createOrUpdateOrg({ ...formData })
    },
    {
        // 初始化表单数据
        initialForm: {
            id: '',
            name: '',
            description: '',
            userIds: userStore.id ? [userStore.id] : [],
        },
    },
)
const handleBeforeOk = (e: MouseEvent) => {
    e.preventDefault()
    formRef.value?.validate((errors) => {
        if (errors) {
            return false
        }
        submit()
    })
}
onSuccess(() => {
    window.$message.success(
        props.currentOrganization?.id
            ? t('system.organization.updateOrganizationSuccess')
            : t('system.organization.createOrganizationSuccess'),
    )
    handleCancel(true)
})
onError((e) => {
    console.log(`output->e`, e)
})
watchEffect(() => {
    if (props.currentOrganization) {
        model.value.id = props.currentOrganization.id
        model.value.name = props.currentOrganization.name
        model.value.userIds = props.currentOrganization.userIds
        model.value.description = props.currentOrganization.description
    }
})
</script>
<template>
    <n-modal
        v-model:show="currentVisible"
        preset="dialog"
        title="Dialog"
        :mask-closable="false"
        :type="model.id ? 'warning' : 'info'"
        :on-close="handleCancel"
    >
        <template #header>
            <span v-if="isEdit">
                {{ t('system.organization.updateOrganization') }}
                <n-tooltip placement="right" :show-arrow="false">
                    <template #trigger>
                        <span class="font-normal text-[var(--color-text-4)]">
                            ({{ characterLimit(props.currentOrganization?.name) }})
                        </span>
                    </template>
                    {{ props.currentOrganization?.name }}
                </n-tooltip>
            </span>
            <span v-else>
                {{ t('system.organization.createOrganization') }}
            </span>
        </template>
        <div class="form">
            <n-form
                ref="formRef"
                :model="model"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                :style="{
                    maxWidth: '640px',
                }"
                class="rounded-[4px]"
            >
                <n-form-item :label="t('system.organization.organizationName')" path="name">
                    <n-input
                        v-model:value="model.name"
                        :placeholder="t('system.organization.organizationNamePlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.organization.organizationAdmin')" path="userIds">
                    <user-select
                        v-model="model.userIds"
                        :placeholder="t('system.organization.organizationAdminPlaceholder')"
                        :type="UserRequestTypeEnum.SYSTEM_ORGANIZATION_ADMIN"
                    />
                </n-form-item>
                <n-form-item :label="t('system.organization.description')" path="description">
                    <n-input
                        v-model:value="model.description"
                        type="textarea"
                        clearable
                        :placeholder="t('system.organization.descriptionPlaceholder')"
                    />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <div>
                <n-button type="tertiary" :disabled="submiting" @click="handleCancel(false)">
                    {{ t('common.cancel') }}
                </n-button>
                <n-button type="primary" :disabled="submiting" @click="handleBeforeOk">
                    {{ isEdit ? t('common.confirm') : t('common.create') }}
                </n-button>
            </div>
        </template>
    </n-modal>
</template>

<style scoped></style>
