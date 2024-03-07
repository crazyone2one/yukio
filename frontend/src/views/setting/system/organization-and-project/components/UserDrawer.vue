<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, NButton } from 'naive-ui'
import { h, ref, watch } from 'vue'
import AddUserModal from './AddUserModal.vue'
import {
    deleteUserFromOrgOrProject,
    postUserTableByOrgIdOrProjectId,
} from '/@/api/modules/setting/OrganizationAndProject.ts'
import BaseDrawer from '/@/components/base-drawer/index.vue'
import BasePagination from '/@/components/base-pagination/index.vue'
import RemoveButton from '/@/components/remove-button/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import { UserListItem } from '/@/models/setting/user.ts'
import { characterLimit } from '/@/utils'

export interface projectDrawerProps {
    visible: boolean
    organizationId?: string
    projectId?: string
    currentName: string
}
const { t } = useI18n()
const props = defineProps<projectDrawerProps>()
const emit = defineEmits<{
    (e: 'cancel'): void
    (e: 'requestFetchData'): void
}>()
const currentVisible = ref(props.visible)
const keyword = ref('')
const AddUserModalRef = ref<InstanceType<typeof AddUserModal> | null>(null)
const userVisible = ref(false)
const columns: DataTableColumns<UserListItem> = [
    {
        title: t('system.organization.userName'),
        key: 'name',
        ellipsis: {
            tooltip: true,
        },
        width: 200,
    },
    {
        title: t('system.organization.email'),
        key: 'email',
        ellipsis: {
            tooltip: true,
        },
        width: 200,
    },
    {
        title: t('system.organization.phone'),
        key: 'phone',
    },

    {
        title: t('system.user.tableColumnActions'),
        key: 'operation',
        width: 110,
        fixed: 'right',
        render(record) {
            return h(RemoveButton, {
                title: t('system.organization.removeName', { name: characterLimit(record.name) }),
                subTitleTip: props.organizationId
                    ? t('system.organization.removeTip')
                    : t('system.project.removeTip'),
                onOk: () => handleRemove(record),
            })
        },
    },
]
const handleCancel = () => {
    keyword.value = ''
    emit('cancel')
}
const handleAddMember = () => {
    userVisible.value = true
}
const handleHideUserModal = () => {
    userVisible.value = false
}
const handleAddMembeSubmit = () => {
    fetchData()
    emit('requestFetchData')
}
const {
    send: fetchData,
    data,
    page,
    // 每页数据条数
    pageSize,
    total,
} = usePagination(
    // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
    (page, pageSize) => {
        const data = {
            current: page,
            pageSize: pageSize,
            keyword: keyword.value,
            organizationId: '',
            projectId: '',
        }
        if (props.organizationId) {
            data.organizationId = props.organizationId
        }
        if (props.projectId) {
            data.projectId = props.projectId
        }
        return postUserTableByOrgIdOrProjectId(data)
    },
    {
        // 请求前的初始数据（接口返回的数据格式）
        initialData: {
            total: 0,
            data: [],
        },
        initialPage: 1, // 初始页码，默认为1
        initialPageSize: 10, // 初始每页数据条数，默认为10
        immediate: false,
        data: (response) => response.records,
    },
)
const { send: deleteUser } = useRequest(
    (record) => {
        if (props.organizationId) {
            return deleteUserFromOrgOrProject(props.organizationId, record.id)
        }
        return deleteUserFromOrgOrProject(props.projectId as string, record.id, false)
    },
    { immediate: false },
)
/**
 * 移除成员
 */
const handleRemove = (record: UserListItem) => {
    deleteUser(record).then(() => {
        window.$message.success(t('common.removeSuccess'))
        fetchData()
        emit('requestFetchData')
    })
}
watch(
    () => props.visible,
    (val) => {
        currentVisible.value = val
        if (val) {
            fetchData()
        }
    },
)
watch(
    () => props.organizationId,
    () => {
        fetchData()
    },
)
watch(
    () => props.projectId,
    () => {
        fetchData()
    },
)
</script>
<template>
    <base-drawer
        :visible="currentVisible"
        :width="680"
        :title="t('system.organization.addMemberTitle', { name: props.currentName })"
        :mask="false"
        @cancel="handleCancel"
    >
        <div>
            <div class="flex flex-row justify-between">
                <n-button size="small" @click="handleAddMember">
                    {{ t('system.organization.addMember') }}
                </n-button>
                <n-input
                    v-model:value="keyword"
                    size="small"
                    :placeholder="t('system.organization.searchUserPlaceholder')"
                    class="w-[230px]"
                />
            </div>
            <n-data-table :columns="columns" :data="data" class="mt-[16px]" />
            <base-pagination :page="page" :page-size="pageSize" :total="total" />
        </div>
    </base-drawer>
    <add-user-modal
        ref="AddUserModalRef"
        :project-id="props.projectId"
        :organization-id="props.organizationId"
        :visible="userVisible"
        @cancel="handleHideUserModal"
        @submit="handleAddMembeSubmit"
    />
</template>

<style scoped></style>
