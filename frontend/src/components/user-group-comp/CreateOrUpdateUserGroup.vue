<script setup lang="ts">
import { FormInst, FormItemRule, FormRules, NPopover } from 'naive-ui'
import { inject, reactive, ref, watchEffect } from 'vue'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import { UserGroupItem } from '/@/models/setting/usergroup'
const systemType = inject<AuthScopeEnum>('systemType')
const props = defineProps<{
    id?: string
    list: UserGroupItem[]
    visible: boolean
    defaultName?: string
    // 权限范围
    authScope: AuthScopeEnum
}>()
const emit = defineEmits<{
    (e: 'cancel', value: boolean): void
    (e: 'submit', currentId: string): void
}>()
const { t } = useI18n()
const currentVisible = ref(props.visible)
const formRef = ref<FormInst | null>(null)
const model = reactive({
    name: '',
})
const loading = ref(false)
const handleCancel = () => {
    model.name = ''
    loading.value = false
    emit('cancel', false)
}
const handleOutsideClick = () => {
    if (currentVisible.value) {
        handleCancel()
    }
}
const rules: FormRules = {
    name: [
        {
            required: true,
            validator: (rule: FormItemRule, value: string) => {
                console.log(`output->rule`, rule)
                return new Promise<void>((resolve, reject) => {
                    if (value === undefined || value === '') {
                        reject(Error(t('system.userGroup.userGroupNameIsNotNone'))) // reject with error message
                    } else {
                        if (value === props.defaultName) {
                            resolve()
                        } else {
                            const isExist = props.list.some((item) => item.name === value)
                            if (isExist) {
                                reject(
                                    Error(
                                        t('system.userGroup.userGroupNameIsExist', { name: value }),
                                    ),
                                )
                            }
                        }
                        resolve()
                    }
                })
            },
        },
    ],
}
const handleBeforeOk = () => {
    formRef.value?.validate((err) => {
        if (err) {
            return false
        }
        console.log(`output->`, { id: props.id, name: model.name, type: props.authScope })
    })
}
watchEffect(() => {
    currentVisible.value = props.visible
    model.name = props.defaultName || ''
})
</script>
<template>
    <n-popover
        trigger="click"
        class="w-[277px]"
        placement="bottom-start"
        :on-clickoutside="handleOutsideClick"
        :show="currentVisible"
    >
        <template #trigger>
            <slot></slot>
        </template>
        <span>
            <n-form
                ref="formRef"
                :model="model"
                :rules="rules"
                label-placement="left"
                label-width="auto"
                class="rounded-[4px]"
            >
                <div class="mb-[8px] text-[14px] font-medium text-[var(--color-text-1)]">
                    {{
                        props.id
                            ? t('system.userGroup.rename')
                            : t('system.userGroup.createUserGroup')
                    }}
                </div>
                <n-form-item path="name">
                    <n-input
                        v-model:value="model.name"
                        clearable
                        class="w-[243px]"
                        :placeholder="t('system.userGroup.pleaseInputUserGroupName')"
                    />
                </n-form-item>
            </n-form>
        </span>
        <div class="flex flex-row flex-nowrap justify-end gap-2">
            <n-button size="tiny" @click="handleCancel">{{ t('common.cancel') }}</n-button>
            <n-button size="tiny" type="primary" @click="handleBeforeOk">
                {{ props.id ? t('common.rename') : t('common.create') }}
            </n-button>
        </div>
    </n-popover>
</template>

<style scoped></style>
