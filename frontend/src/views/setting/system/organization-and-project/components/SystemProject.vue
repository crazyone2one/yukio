<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, NButton, NSpace } from 'naive-ui'
import { h, onMounted, ref } from 'vue'
import AddUserModal from './AddUserModal.vue'
import { postProjectTable } from '/@/api/modules/setting/OrganizationAndProject'
import BasePagination from '/@/components/base-pagination/index.vue'
import UserAdminDiv from '/@/components/user-admin-div/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import { CommonList } from '/@/models/common'
import { CreateOrUpdateSystemProjectParams } from '/@/models/setting/system/orgAndProject'

export interface SystemOrganizationProps {
    keyword: string
}
const { t } = useI18n()
const props = defineProps<SystemOrganizationProps>()
const userVisible = ref(false)
const currentProjectId = ref('')
const {
    // 加载状态
    // loading,

    // 列表数据
    data,
    page,
    pageSize,

    // 总数据量
    //  total,
    send: fetchData,
} = usePagination(
    // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
    (page, pageSize) => {
        const param = {
            current: page,
            pageSize: pageSize,
            keyword: '',
        }
        return postProjectTable(param)
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
        // total: (response) => response.totalRow,
        data: (response: CommonList<CreateOrUpdateSystemProjectParams>) => response.records,
        // watchingStates: [props.keyword],
    },
)
const columns: DataTableColumns<CreateOrUpdateSystemProjectParams> = [
    {
        title: t('system.organization.ID'),
        key: 'num',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.organization.name'),
        key: 'name',
        ellipsis: {
            tooltip: true,
        },
    },
    {
        title: t('system.organization.member'),
        key: 'memberCount',
        render(record) {
            return h('span', {}, { default: () => record.memberCount || 0 })
        },
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
        title: t('system.organization.subordinateOrg'),
        key: 'organizationName',
        width: 200,
    },
    {
        title: t('system.organization.creator'),
        key: 'createUser',
        ellipsis: {
            tooltip: true,
        },
        width: 200,
        render(record) {
            return h(UserAdminDiv, {
                isAdmin: record.projectCreateUserIsAdmin,
                name: record.createUser,
            })
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
                        // onClick: () => showOrganizationModal(row),
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
                                        // onClick: () => showOrganizationModal(row),
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
                            ]
                        },
                    },
                )
            }
        },
    },
]
const showAddUserModal = (record: CreateOrUpdateSystemProjectParams) => {
    currentProjectId.value = record.id as string
    userVisible.value = true
}
const handleAddUserModalCancel = () => {
    userVisible.value = false
}
onMounted(() => {
    fetchData()
})
</script>
<template>
    <n-data-table :columns="columns" :data="data" />
    <base-pagination :page="page" :page-size="pageSize" />
    <add-user-modal
        :project-id="currentProjectId"
        :visible="userVisible"
        @submit="fetchData"
        @cancel="handleAddUserModalCancel"
    />
</template>
<style scoped></style>
