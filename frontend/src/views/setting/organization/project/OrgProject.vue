<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, NButton, NSpace } from 'naive-ui'
import { computed, h, onMounted, ref } from 'vue'
import AddProjectModal from './components/AddProjectModal.vue'
import { postProjectTableByOrg } from '/@/api/modules/setting/OrganizationAndProject'
import YCard from '/@/components/y-card/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import { CommonList } from '/@/models/common'
import {
    CreateOrUpdateSystemProjectParams,
    OrgProjectTableItem,
} from '/@/models/setting/system/orgAndProject'
import { useAppStore } from '/@/store'

const { t } = useI18n()
const keyword = ref('')
const appStore = useAppStore()
const currentOrgId = computed(() => appStore.currentOrgId)
const currentUpdateProject = ref<CreateOrUpdateSystemProjectParams>()
const addProjectVisible = ref(false)
const addProjectModalRef = ref<InstanceType<typeof AddProjectModal> | null>(null)
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const columns: DataTableColumns<{ [key: string]: any }> = [
    {
        title: t('system.organization.ID'),
        key: 'num',
        width: 100,
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
        width: 180,
    },
    {
        title: t('system.organization.createTime'),
        key: 'createTime',
        width: 180,
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
                                    { text: true, type: 'primary' },
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
const { data, send: fetchData } = usePagination(
    // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
    (page, pageSize) =>
        postProjectTableByOrg(page, pageSize, { keyword: '', organizationId: currentOrgId.value }),
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
        data: (response: CommonList<OrgProjectTableItem>) => response.records,
    },
)
const showAddProject = () => {
    addProjectVisible.value = true
    currentUpdateProject.value = undefined
}
const handleAddProjectModalCancel = (shouldSearch: boolean) => {
    addProjectVisible.value = false
    currentUpdateProject.value = undefined
    if (shouldSearch) {
        fetchData()
    }
}
onMounted(() => {
    fetchData()
})
</script>
<template>
    <y-card simple>
        <div class="mb-[16px] flex items-center justify-end">
            <n-button type="primary" @click="showAddProject">
                {{ t('system.organization.createProject') }}
            </n-button>
            <n-input
                v-model:value="keyword"
                :placeholder="t('system.organization.searchIndexPlaceholder')"
                clearable
            />
        </div>
        <n-data-table :columns="columns" :data="data" />
    </y-card>
    <add-project-modal
        ref="addProjectModalRef"
        :visible="addProjectVisible"
        :current-project="currentUpdateProject"
        @cancel="handleAddProjectModalCancel"
    />
</template>

<style scoped></style>
