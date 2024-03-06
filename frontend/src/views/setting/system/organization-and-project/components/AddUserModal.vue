<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules } from 'naive-ui'
import { onUnmounted, reactive, ref } from 'vue'
import { addUserToOrgOrProject } from '/@/api/modules/setting/OrganizationAndProject'
import UserSelect from '/@/components/user-selector/index.vue'
import { UserRequestTypeEnum } from '/@/components/user-selector/utils.ts'
import { useI18n } from '/@/hooks/use-i18n.ts'

const { t } = useI18n()
const props = defineProps<{
    organizationId?: string
    projectId?: string
}>()

const emit = defineEmits<{
    (e: 'cancel'): void
    (e: 'submit'): void
}>()
const formRef = ref<FormInst | null>(null)
const currentVisible = defineModel<boolean>('visible', {
    default: false,
})
const rules: FormRules = {
    name: [{ required: true, message: t('system.organization.addMemberRequired') }],
}
const form = reactive({
    name: [],
})
const handleCancel = () => {
    form.name = []
    emit('cancel')
}
const {
    // 提交状态
    loading: submitting,

    send: submit,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        formData.userIds = form.name
        formData.organizationId = props.organizationId as string
        formData.projectId = props.projectId as string
        return addUserToOrgOrProject(formData)
    },
    {
        // 初始化表单数据
        initialForm: {
            userIds: [],
            organizationId: '',
            projectId: '',
        },
    },
)
const handleAddMember = () => {
    formRef.value?.validate((errors) => {
        if (errors) {
            return false
        }
        // const { organizationId, projectId } = props
        // 提交表单
        submit().then(() => {
            window.$message.success(t('system.organization.addSuccess'))
            handleCancel()
            emit('submit')
        })
    })
}
onUnmounted(() => {
    form.name = []
})
</script>

<template>
    <n-modal
        v-model:show="currentVisible"
        preset="dialog"
        title="Dialog"
        :mask-closable="false"
        :on-close="handleCancel"
    >
        <template #header>
            {{ t('system.organization.addMember') }}
        </template>
        <div class="form">
            <n-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                class="rounded-[4px]"
            >
                <n-form-item :label="t('system.organization.organizationAdmin')" path="name">
                    <user-select
                        v-model="form.name"
                        :placeholder="t('system.organization.organizationAdminPlaceholder')"
                        :type="UserRequestTypeEnum.SYSTEM_ORGANIZATION"
                    />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <div>
                <n-button
                    type="tertiary"
                    :loading="submitting"
                    size="small"
                    class="mr-[5px]"
                    @click="handleCancel"
                >
                    {{ t('common.cancel') }}
                </n-button>
                <n-button
                    type="primary"
                    :loading="submitting"
                    :disabled="!form.name"
                    size="small"
                    @click="handleAddMember"
                >
                    {{ t('common.add') }}
                </n-button>
            </div>
        </template>
    </n-modal>
</template>

<style scoped></style>
