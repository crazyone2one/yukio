<script setup lang="ts">
import BaseModal from '/@/components/base-modal/index.vue'
import { onBeforeMount, ref, watchEffect } from 'vue'
import { FormInst, FormItemRule, FormRules, NAlert, NButton, SelectOption } from 'naive-ui'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { cloneDeep } from 'lodash-es'
import { SimpleUserInfo, UserListItem } from '/@/models/setting/user.ts'
import { validateEmail, validatePhone } from '/@/utils/validate.ts'
import { VueDraggable } from 'vue-draggable-plus'
import { FormItemModel, FormMode } from '/@/components/batch-form/types.ts'
import { useRequest } from 'alova'
import { getSystemRoles } from '/@/api/modules/setting/user.ts'
import { scrollIntoView } from '/@/utils/dom.ts'

interface UserForm {
    list: SimpleUserInfo[]
    userGroup: string[]
}
const props = withDefaults(
    defineProps<{
        models: FormItemModel[]
        formMode: FormMode
        addText: string
        maxHeight?: string
        defaultVals?: any[] // 当外层是编辑状态时，可传入已填充的数据
        isShowDrag?: boolean // 是否可以拖拽
        formWidth?: string // 自定义表单区域宽度
        showEnable?: boolean // 是否显示启用禁用switch状态
        hideAdd?: boolean // 是否隐藏添加按钮
    }>(),
    {
        maxHeight: '30vh',
        isShowDrag: false,
        hideAdd: false,
    },
)
const currentVisible = defineModel<boolean>('visible', {
    default: false,
})
const { t } = useI18n()
const defaultUserForm = {
    list: [
        {
            name: '',
            email: '',
            phone: '',
        },
    ],
    userGroup: [],
}
const userForm = ref<UserForm>(cloneDeep(defaultUserForm))
const formRef = ref<FormInst | null>(null)
const defaultForm = {
    list: [] as Record<string, any>[],
}
const form = ref<Record<string, any>>({ list: [...defaultForm.list] })
const formItem: Record<string, any> = {}
const userGroupOptions = ref<Array<SelectOption>>([])
const rules: FormRules = {
    name: [
        {
            required: true,
            // message: t('system.user.createUserNameNotNull'),
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
        // message: t('system.user.createUserEmailNotNull'),
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
        // message: t('system.user.createUserPhoneErr'),
        validator(rule: FormItemRule, value: string) {
            if (value !== null && value !== '' && value !== undefined && !validatePhone(value)) {
                return new Error(t('system.user.createUserPhoneErr'))
            }
            return true
        },
        trigger: ['input', 'blur'],
    },
}
const { send: loadRole } = useRequest(() => getSystemRoles(), { immediate: false })
const createUser = (isContinue?: boolean) => {
    if (props.formMode === 'create') {
        const params = {
            userInfoList: userForm.value.list,
            userRoleIdList: userForm.value.userGroup,
        }
        console.log(`output->params`, params)
    }
}
const editUser = (record: UserListItem) => {
    userForm.value.list = [
        {
            id: record.id,
            name: record.name,
            email: record.email,
            phone: record.phone ? record.phone.replace(/\s/g, '') : record.phone,
        },
    ]
    userForm.value.userGroup = record.userRoleList
}
const addField = () => {
    const item = [{ ...formItem }]
    item[0].type = []
    formRef.value?.validate((errors) => {
        if (errors) {
            scrollIntoView(document.querySelector('.n-form-item-feedback__line'), {
                block: 'center',
            })
            return
        }
        form.value.list.push(item[0])
    })
}
watchEffect(() => {
    props.models.forEach((e) => {
        // 默认填充表单项
        let value: string | number | boolean | string[] | number[] | undefined
        if (e.type === 'inputNumber') {
            value = undefined
        } else if (e.type === 'tagInput') {
            value = []
        } else {
            value = e.defaultValue
        }
        formItem[e.filed] = value
        if (props.showEnable) {
            // 如果有开启关闭状态，将默认禁用
            formItem.enable = false
        }
        // 默认填充表单项的子项
        e.children?.forEach((child) => {
            formItem[child.filed] = child.type === 'inputNumber' ? null : child.defaultValue
        })
    })
    form.value.list = [{ ...formItem }]
    if (props.defaultVals?.length) {
        // 取出defaultVals的表单 filed
        form.value.list = props.defaultVals.map((e) => e)
    }
})
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
    >
        <div>
            <n-alert class="mb-[16px]">
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
                <div
                    class="mb-[16px] overflow-y-auto rounded-[4px] bg-[var(--color-fill-1)] p-[12px]"
                    :style="{ width: props.formWidth || '100%' }"
                >
                    <VueDraggable
                        v-model="form.list"
                        ghost-class="ghost"
                        drag-class="dragChosenClass"
                        :disabled="!props.isShowDrag"
                        :force-fallback="true"
                        class="rounded"
                    >
                        <div
                            v-for="(element, index) in form.list"
                            :key="`${element.filed}${index}`"
                            class="flex w-full items-start justify-between rounded gap-[8px] py-[6px] pr-[8px]"
                            :class="[props.isShowDrag ? 'cursor-move' : '']"
                        >
                            <div v-if="props.isShowDrag" class="ml-[8px] mr-[8px] pt-[8px]">xx</div>
                            <n-form-item
                                v-for="model of props.models"
                                :key="`${model.filed}${index}`"
                                :path="`${model.filed}`"
                            >
                                <template #label>
                                    <div class="inline-flex flex-row">
                                        <div>
                                            {{ index === 0 && model.label ? t(model.label) : '' }}
                                        </div>
                                    </div>
                                </template>
                                <n-input
                                    v-if="model.type === 'input'"
                                    v-model:value="userForm.list[index][model.filed]"
                                    :placeholder="t(model.placeholder || '')"
                                    :max-length="model.maxLength || 255"
                                    allow-clear
                                />
                            </n-form-item>
                            <div v-if="showEnable">
                                <n-switch
                                    v-model:value="element.enable"
                                    class="mt-[8px]"
                                    size="small"
                                    :style="{
                                        'margin-top':
                                            index === 0 && !props.isShowDrag ? '36px' : '',
                                    }"
                                />
                            </div>
                            <div
                                v-if="!props.hideAdd"
                                v-show="form.list.length > 1"
                                :class="[
                                    'flex',
                                    'h-full',
                                    'w-[32px]',
                                    'cursor-pointer',
                                    'items-center',
                                    'justify-center',
                                    'text-[var(--color-text-4)]',
                                    'mt-[8px]',
                                ]"
                                :style="{
                                    'margin-top': index === 0 && !props.isShowDrag ? '36px' : '',
                                }"
                            >
                                <n-icon>
                                    <span class="i-tabler:circle-minus" />
                                </n-icon>
                            </div>
                        </div>
                    </VueDraggable>
                    <div v-if="props.formMode === 'create'" class="w-full">
                        <n-button text size="small" @click="addField">
                            <template #icon>
                                <n-icon>
                                    <span class="i-tabler:circle-plus" />
                                </n-icon>
                            </template>
                            {{ t(props.addText) }}
                        </n-button>
                    </div>
                </div>

                <n-form-item :label="t('system.user.createUserUserGroup')">
                    <n-select
                        v-model:value="userForm.userGroup"
                        :options="userGroupOptions"
                        multiple
                    />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <n-button size="small">
                {{ t('system.user.editUserModalCancelCreate') }}
            </n-button>
            <n-button v-if="props.formMode === 'create'" size="small">
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
