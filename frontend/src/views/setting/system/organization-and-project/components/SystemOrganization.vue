<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, NButton, NSpace } from 'naive-ui'
import { h, ref, toRef } from 'vue'
import AddOrganizationModal from './AddOrganizationModal.vue'
import AddUserModal from './AddUserModal.vue'
import { postOrgTable } from '/@/api/modules/setting/OrganizationAndProject'
import TableMoreAction from '/@/components/table-more-action/index.vue'
import { ActionsItem } from '/@/components/table-more-action/types'
import { useI18n } from '/@/hooks/use-i18n'
import { CommonList } from '/@/models/common'
import { OrganizationListItem } from '/@/models/setting/orgnization'
import {
    CreateOrUpdateSystemOrgParams,
    OrgProjectTableItem,
} from '/@/models/setting/system/orgAndProject'

export interface SystemOrganizationProps {
    keyword: string
}
const props = defineProps<SystemOrganizationProps>()
const { t } = useI18n()
const orgVisible = ref(false)
const currentOrganizationId = ref('')
const userVisible = ref(false)
const AddUserModalRef = ref<InstanceType<typeof AddUserModal> | null>(null)
const currentUpdateOrganization = ref<CreateOrUpdateSystemOrgParams>()
const tableActions: ActionsItem[] = [
    {
        label: 'system.user.delete',
        eventTag: 'delete',
        danger: true,
    },
]
const columns: DataTableColumns<OrganizationListItem> = [
    {
        title: t('system.organization.ID'),
        key: 'num',
        width: 100,
    },
    {
        title: t('system.organization.name'),
        key: 'name',
        width: 300,
    },
    {
        title: t('system.organization.member'),
        key: 'memberCount',
    },
    {
        title: t('system.organization.project'),
        key: 'projectCount',
    },
    {
        title: t('system.organization.status'),
        key: 'enable',
    },
    {
        title: t('system.organization.description'),
        key: 'description',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.organization.creator'),
        key: 'createUser',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.organization.createTime'),
        key: 'createTime',
        width: 100,
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.organization.operation'),
        key: 'operation',
        width: 230,
        fixed: 'right',
        render(row) {
            if (row.deleted) {
                return h(
                    NButton,
                    {
                        text: true,
                        type: 'primary',
                        onClick: () => showOrganizationModal(row),
                    },
                    { default: () => t('common.revokeDelete') },
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
                                    { default: () => t('common.enable') },
                                ),
                                h(
                                    NButton,
                                    { text: true, type: 'primary' },
                                    {
                                        default: () => t('common.delete'),
                                    },
                                ),
                            ]
                        },
                    },
                )
            } else {
                return h(
                    NSpace,
                    {},
                    {
                        default: () => {
                            return [
                                h(
                                    NButton,
                                    {
                                        text: true,
                                        type: 'primary',
                                        onClick: () => showOrganizationModal(row),
                                    },
                                    { default: () => t('common.edit') },
                                ),
                                h(
                                    NButton,
                                    {
                                        text: true,
                                        type: 'primary',
                                        onClick: () => showAddUserModal(row),
                                    },
                                    {
                                        default: () => t('system.organization.addMember'),
                                    },
                                ),
                                h(
                                    NButton,
                                    { text: true, type: 'primary' },
                                    { default: () => t('common.end') },
                                ),
                                h(TableMoreAction, { list: tableActions }),
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
    (page, pageSize) => postOrgTable(page, pageSize, props.keyword),
    {
        watchingStates: [toRef(props.keyword)],
        // 请求前的初始数据（接口返回的数据格式）
        initialData: {
            total: 0,
            data: [],
        },
        initialPage: 1, // 初始页码，默认为1
        initialPageSize: 10, // 初始每页数据条数，默认为10
        immediate: false,
        // total: (response) => response.totalRow,
        data: (response: CommonList<OrgProjectTableItem>) => response.records,
    },
)
const pagination = { pageSize: 10 }
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const showOrganizationModal = (record: any) => {
    currentOrganizationId.value = record.id as string
    orgVisible.value = true
    currentUpdateOrganization.value = {
        id: record.id,
        name: record.name,
        description: record.description,
        userIds: record.userIds || [],
    }
}
const handleAddOrgModalCancel = (shouldSearch: boolean) => {
    orgVisible.value = false
    if (shouldSearch) {
        fetchData()
    }
}
const showAddUserModal = (record: OrganizationListItem) => {
    currentOrganizationId.value = record.id
    userVisible.value = true
}
const handleAddUserModalCancel = () => {
    userVisible.value = false
}
defineExpose({ fetchData })
</script>
<template>
    <n-data-table :columns="columns" :data="data" :pagination="pagination" />
    <add-organization-modal
        :visible="orgVisible"
        :current-organization="currentUpdateOrganization"
        @cancel="handleAddOrgModalCancel"
    />
    <add-user-modal
        ref="AddUserModalRef"
        :organization-id="currentOrganizationId"
        :visible="userVisible"
        @cancel="handleAddUserModalCancel"
        @submit="fetchData"
    />
</template>

<style scoped></style>
