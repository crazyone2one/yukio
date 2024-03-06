<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormItemRule, FormRules, NButton, SelectOption } from 'naive-ui'
import { onBeforeMount, ref } from 'vue'
import { batchCreateUser, getSystemRoles } from '/@/api/modules/setting/user.ts'
import BaseModal from '/@/components/base-modal/index.vue'
import { FormMode } from '/@/components/batch-form/types.ts'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { UserListItem } from '/@/models/setting/user.ts'
import { validateEmail, validatePhone } from '/@/utils/validate.ts'

const currentVisible = defineModel<boolean>('visible', {
    default: false,
})
const props = withDefaults(
    defineProps<{
        formMode: FormMode
    }>(),
    {},
)
const { t } = useI18n()

const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
    name: [
        {
            required: true,
            validator(rule: FormItemRule, value: string) {
                if (!value) {
                    return new Error(t('system.user.createUserNameNotNull'))
                } else if (value.length > 255) {
                    return new Error(t('system.user.createUserNameOverLength'))
                }
                return true
            },
            trigger: ['input', 'blur'],
        },
    ],
    email: {
        required: true,
        validator(rule: FormItemRule, value: string) {
            if (!value) {
                return new Error(t('system.user.createUserEmailNotNull'))
            } else if (!validateEmail(value)) {
                return new Error(t('system.user.createUserEmailErr'))
            }
            return true
        },
        trigger: ['input', 'blur'],
    },
    phone: {
        validator(rule: FormItemRule, value: string) {
            if (value && !validatePhone(value)) {
                return new Error(t('system.user.createUserPhoneErr'))
            }
            return true
        },
        trigger: ['input', 'blur'],
    },
    userGroup: { required: true, message: t('system.user.createUserUserGroupNotNull') },
}
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void }>()
const { send: loadRole } = useRequest(() => getSystemRoles(), { immediate: false })
const userGroupOptions = ref<Array<SelectOption>>([])

const {
    // 提交状态
    loading: submitting,
    form: userForm,
    // 提交数据函数
    send: submit,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        return batchCreateUser(formData)
    },
    {
        // 初始化表单数据
        initialForm: {
            name: '',
            email: '',
            phone: '',
            enabled: true,
            userGroup: userGroupOptions.value
                .filter((e: SelectOption) => e.selected === true)
                .map((item) => item.value) as string[],
        },
    },
)
const createUser = (isContinue?: boolean) => {
    formRef.value?.validate((errors) => {
        if (errors) {
            return
        }
        submit(userForm).then((res) => {
            if (res.errorEmails) {
                window.$message.error(t('system.user.createUserEmailExist'))
            } else {
                window.$message.success(t('system.user.addUserSuccess'))
                if (!isContinue) {
                    handleCancel(true)
                }
            }
        })
    })
}
const handleCancel = (shouldSearch = false) => {
    emit('cancel', shouldSearch)
    formRef.value?.restoreValidation()
}
const editUser = (record: UserListItem) => {
    userForm.value = Object.assign({}, record)
    userForm.value.userGroup = record.userRoleList
}
onBeforeMount(() => {
    loadRole().then((res) => {
        userGroupOptions.value = res
        if (userGroupOptions.value.length) {
            userForm.value.userGroup = userGroupOptions.value
                .filter((e: SelectOption) => e.selected === true)
                .map((item) => item.value) as string[]
        }
    })
})
defineExpose({ editUser })
</script>

<template>
    <base-modal
        :visible="currentVisible"
        :title="
            props.formMode === 'create'
                ? t('system.user.createUserModalTitle')
                : t('system.user.editUserModalTitle')
        "
        :loading="submitting"
        @cancel="handleCancel"
    >
        <div>
            <n-alert class="mb-[16px]" :bordered="false" type="info">
                {{ t('system.user.createUserTip') }}
            </n-alert>
            <n-form
                v-if="visible"
                ref="formRef"
                :model="userForm"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                class="rounded-[4px]"
            >
                <n-form-item :label="t('system.user.createUserName')" path="name">
                    <n-input
                        v-model:value="userForm.name"
                        :placeholder="t('system.user.createUserNamePlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.user.createUserEmail')" path="email">
                    <n-input
                        v-model:value="userForm.email"
                        :placeholder="t('system.user.createUserEmailPlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.user.createUserPhone')" path="phone">
                    <n-input
                        v-model:value="userForm.phone"
                        :placeholder="t('system.user.createUserPhonePlaceholder')"
                    />
                </n-form-item>
                <n-form-item :label="t('system.user.createUserUserGroup')" path="userGroup">
                    <n-select
                        v-model:value="userForm.userGroup"
                        :options="userGroupOptions"
                        multiple
                    />
                </n-form-item>
                <n-form-item :label="t('common.enable')">
                    <n-switch v-model:value="userForm.enable" size="small" />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <n-button size="small">
                {{ t('system.user.editUserModalCancelCreate') }}
            </n-button>
            <n-button v-if="props.formMode === 'create'" size="small" disabled>
                {{ t('system.user.editUserModalSaveAndContinue') }}
            </n-button>
            <n-button type="primary" size="small" @click="createUser()">
                {{
                    t(
                        props.formMode === 'create'
                            ? 'system.user.editUserModalCreateUser'
                            : 'system.user.editUserModalEditUser',
                    )
                }}
            </n-button>
        </template>
    </base-modal>
</template>

<style scoped></style>
