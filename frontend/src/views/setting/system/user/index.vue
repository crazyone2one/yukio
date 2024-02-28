<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { cloneDeep } from 'lodash-es'
import {
    DataTableColumns,
    FormInst,
    FormRules,
    NAlert,
    NButton,
    NIcon,
    NSpace,
    SelectOption,
} from 'naive-ui'
import { h, onMounted, ref } from 'vue'
import { getUserList } from '/@/api/modules/setting/user'
import YTagGroup from '/@/components/y-tag/YTagGroup.vue'
import { useI18n } from '/@/hooks/use-i18n'
import { CommonList, TableQueryParams } from '/@/models/common'
import { UserListItem } from '/@/models/setting/user'

type UserModalMode = 'create' | 'edit'
interface UserForm {
    id?: string
    name: string
    email: string
    phone?: string
    userGroup: string[]
}
const { t } = useI18n()

const keyword = ref('')
const param = ref<TableQueryParams>({})
const visible = ref(false)
const userFormMode = ref<UserModalMode>('create')
const defaultUserForm = {
    name: '',
    email: '',
    phone: '',
    userGroup: [],
}
const userForm = ref<UserForm>(cloneDeep(defaultUserForm))
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
    name: [{ required: true, message: t('system.user.createUserNameNotNull') }],
    email: {
        required: true,
        message: t('system.user.createUserEmailNotNull'),
    },
    phone: {
        message: t('system.user.createUserEmailNotNull'),
    },
}
const userGroupOptions = ref([])
const columns: DataTableColumns<UserListItem> = [
    {
        title: t('system.user.tableColumnEmail'),
        key: 'email',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.user.tableColumnName'),
        key: 'name',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.user.tableColumnPhone'),
        key: 'phone',
    },
    {
        title: t('system.user.tableColumnOrg'),
        key: 'organizationList',
        width: 300,
        render(row) {
            return h(YTagGroup, { tagList: row.organizationList, type: 'primary' }, {})
        },
    },
    {
        title: t('system.user.tableColumnUserGroup'),
        key: 'userRoleList',
        width: 300,
        render(row) {
            return h(YTagGroup, { tagList: row.userRoleList, type: 'primary' }, {})
        },
    },
    {
        title: t('system.user.tableColumnStatus'),
        key: 'enable',
        render(row) {
            if (row.enable) {
                return h(
                    'div',
                    { class: 'flex flex-row flex-nowrap items-center gap-[2px]' },
                    {
                        default: () => {
                            return [
                                h(
                                    NIcon,
                                    { color: '#0e7a0d' },
                                    {
                                        default: () =>
                                            h('span', { class: 'i-tabler:circle-check-filled' }),
                                    },
                                ),
                                h('div', {}, { default: () => t('msTable.enable') }),
                            ]
                        },
                    },
                )
            } else {
                return h(
                    'div',
                    { class: 'flex flex-row flex-nowrap items-center gap-[2px]' },
                    {
                        default: () => {
                            return [
                                h(
                                    NIcon,
                                    { color: '#ff1e90' },
                                    {
                                        default: () =>
                                            h('span', { class: 'i-tabler:playstation-x' }),
                                    },
                                ),
                                h('div', {}, { default: () => t('msTable.disable') }),
                            ]
                        },
                    },
                )
            }
        },
    },

    {
        title: t('system.user.tableColumnActions'),
        key: 'operation',
        width: 110,
        fixed: 'right',
        render(row) {
            if (row.enable) {
                return h(
                    NButton,
                    {
                        text: true,
                        type: 'primary',
                    },
                    { default: () => t('system.user.editUser') },
                )
            } else if (!row.enable) {
                return h(
                    NSpace,
                    {},
                    {
                        default: () => {
                            return [
                                h(
                                    NButton,
                                    { text: true, type: 'primary' },
                                    { default: () => t('system.user.enable') },
                                ),
                                h(
                                    NButton,
                                    { text: true, type: 'primary' },
                                    {
                                        default: () => t('system.user.delete'),
                                    },
                                ),
                            ]
                        },
                    },
                )
            }
        },
    },
]
const {
    // 加载状态
    // loading,

    // 列表数据
    data,

    // 当前页码，改变此页码将自动触发请求
    //  page,

    // 每页数据条数
    //  pageSize,

    // 总数据量
    //  total,
    send: fetchData,
} = usePagination(
    // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
    (page, pageSize) => getUserList(page, pageSize, param),
    {
        watchingStates: [keyword],
        // 请求前的初始数据（接口返回的数据格式）
        initialData: {
            total: 0,
            data: [],
        },
        initialPage: 1, // 初始页码，默认为1
        initialPageSize: 10, // 初始每页数据条数，默认为10
        immediate: false,
        // total: (response) => response.totalRow,
        data: (response: CommonList<UserListItem>) => response.records,
    },
)
const showUserModal = (mode: UserModalMode, record?: UserListItem) => {
    visible.value = true
    userFormMode.value = mode
    if (mode === 'edit' && record) {
        userForm.value.id = record.id
        userForm.value.name = record.name
        ;(userForm.value.phone = record.phone ? record.phone.replace(/\s/g, '') : record.phone),
            (userForm.value.email = record.email)
        userForm.value.userGroup = record.userRoleList
    }
}
const handleBeforeClose = () => {
    window.$dialog.warning({
        title: t('common.tip'),
        content: t('system.user.closeTip'),
        positiveText: t('common.close'),
        onPositiveClick: () => {
            cancelCreate()
        },
    })
}
const cancelCreate = () => {
    visible.value = false
    resetUserForm()
}
const resetUserForm = () => {
    formRef.value?.validate()
    userForm.value.email = ''
    userForm.value.name = ''
    userForm.value.phone = ''
    userForm.value.id = ''
    userForm.value.userGroup = userGroupOptions.value.filter(
        (e: SelectOption) => e.selected === true,
    )
}
onMounted(() => {
    fetchData()
})
</script>
<template>
    <n-card>
        <div class="mb-4 flex items-center justify-between">
            <div>
                <n-button type="primary" class="mr-3" @click="showUserModal('create')">
                    {{ t('system.user.createUser') }}
                </n-button>
                <!-- <n-button type="primary" class="mr-3">
                    {{ t('system.user.emailInvite') }}
                </n-button> -->
                <n-button type="primary" class="mr-3">
                    {{ t('system.user.importUser') }}
                </n-button>
            </div>
            <n-input
                v-model:value="keyword"
                :placeholder="t('system.user.searchUser')"
                class="w-[230px]"
                allow-clear
            />
        </div>
        <n-data-table :columns="columns" :data="data" />
    </n-card>
    <n-modal v-model:show="visible" preset="dialog" transform-origin="center">
        <template #header>
            <div>
                {{
                    userFormMode === 'create'
                        ? t('system.user.createUserModalTitle')
                        : t('system.user.editUserModalTitle')
                }}
            </div>
        </template>
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
                :style="{
                    maxWidth: '640px',
                }"
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
                <n-form-item :label="t('system.user.createUserUserGroup')">
                    <n-select v-model:value="userForm.userGroup" :options="userGroupOptions" />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <n-button size="small" @click="handleBeforeClose">
                {{ t('system.user.editUserModalCancelCreate') }}</n-button
            >
            <n-button v-if="userFormMode === 'create'" size="small">
                {{ t('system.user.editUserModalSaveAndContinue') }}
            </n-button>
            <n-button type="primary" size="small">
                {{
                    t(
                        userFormMode === 'create'
                            ? 'system.user.editUserModalCreateUser'
                            : 'system.user.editUserModalEditUser',
                    )
                }}</n-button
            >
        </template>
    </n-modal>
</template>

<style scoped></style>
