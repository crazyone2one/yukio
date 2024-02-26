<script setup lang="ts">
import { FormInst } from 'naive-ui'
import { computed, reactive, ref, watchEffect } from 'vue'
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
const model = reactive<{
    name: string
    userIds: string[]
    description: string
}>({
    name: '',
    userIds: userStore.id ? [userStore.id] : [],
    description: '',
})
const isEdit = computed(() => !!props.currentOrganization?.id)
watchEffect(() => {
    if (props.currentOrganization) {
        model.name = props.currentOrganization.name
        model.userIds = props.currentOrganization.userIds
        model.description = props.currentOrganization.description
    }
})
const handleCancel = (shouldSearch = false) => {
    emit('cancel', shouldSearch)
    formRef.value?.restoreValidation()
}
const handleBeforeOk = (e: MouseEvent) => {
    e.preventDefault()
    formRef.value?.validate((errors) => {
        if (errors) {
            return false
        }
        window.$message.success(
            props.currentOrganization?.id
                ? t('system.organization.updateOrganizationSuccess')
                : t('system.organization.createOrganizationSuccess'),
        )
        handleCancel(true)
    })
}
</script>
<template>
    <n-modal v-model:show="currentVisible" preset="dialog" title="Dialog" :mask-closable="false">
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
                <n-button type="tertiary" @click="handleCancel(false)">
                    {{ t('common.cancel') }}
                </n-button>
                <n-button type="primary" @click="handleBeforeOk">
                    {{ isEdit ? t('common.confirm') : t('common.create') }}
                </n-button>
            </div>
        </template>
    </n-modal>
</template>

<style scoped></style>
