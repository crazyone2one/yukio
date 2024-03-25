<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, onMounted, reactive, ref } from 'vue'
import { TableQueryParams } from '/@/api/interface/common.ts'
import { UserItem } from '/@/api/interface/setting/log.ts'
import {
  CreateOrUpdateSystemProjectParams,
  OrgProjectTableItem,
} from '/@/api/interface/setting/orgAndProject.ts'
import { postProjectTable } from '/@/api/modules/setting/org-and-project.ts'
import BasePagination from '/@/components/base-pagination/index.vue'
import TableMoreAction from '/@/components/base-table/TableMoreAction.vue'
import { ActionsItem } from '/@/components/base-table/types.ts'
import ShowOrEdit from '/@/components/table-column-show-or-edit/index.vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import AddProject from '/@/views/setting/system/org-project/components/AddProject.vue'
import TableOperation from '/src/views/setting/system/org-project/components/OrgTableOperation.vue'

export interface SystemOrganizationProps {
  keyword: string
}

const props = defineProps<SystemOrganizationProps>()
const { t } = useI18n()
const checkedRowKeys = ref<DataTableRowKey[]>([])
const currentKeyword = ref(props.keyword)
const currentUpdateProject = ref<CreateOrUpdateSystemProjectParams>()
const addProjectVisible = ref(false)
const columns = reactive<DataTableColumns<OrgProjectTableItem>>([
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
    width: 300,
    ellipsis: {
      tooltip: true,
    },
    render(row) {
      return h(ShowOrEdit, {
        value: row.name,
        'onUpdate-value': (v: string) => {
          row.name = v
          handleNameChange(row)
        },
      })
    },
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
    title: t('system.organization.subordinateOrg'),
    key: 'organizationName',
  },
  {
    title: t('system.organization.creator'),
    key: 'createUser',
    width: 200,
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: t('system.organization.createTime'),
    key: 'createTime',
    width: 180,
  },
  {
    title: t('system.organization.operation'),
    key: 'operation',
    fixed: 'right',
    width: 230,
    render(rowData) {
      return h(
        TableOperation,
        {
          record: rowData,
          moreAction: tableActions,
          onHandleEdit: () => showAddProjectModal(rowData),
          onHandleEnd: () => handleEnableOrDisableProject(rowData, false),
          onHandleEnable: () => handleEnableOrDisableProject(rowData),
        },
        {
          more: () => {
            return h(TableMoreAction, {
              list: tableActions,
              onSelect: (event) => handleMoreAction(event, rowData),
            })
          },
        },
      )
    },
  },
])
const tableActions: ActionsItem[] = [
  {
    label: 'system.user.delete',
    eventTag: 'delete',
    danger: true,
  },
]
const {
  send: fetchData,
  page,
  pageSize,
  data,
} = usePagination(
  (page, pageSize) => {
    const param: TableQueryParams = {
      keyword: currentKeyword.value,
      current: page,
      pageSize,
    }
    return postProjectTable(param)
  },
  {
    initialData: {
      total: 0,
      data: [],
    },
    data: (response) => response.records,
    total: (response) => response.totalRow,
    watchingStates: [currentKeyword],
    immediate: false,
  },
)
const handleCheck = (rowKeys: DataTableRowKey[]) =>
  (checkedRowKeys.value = rowKeys)
const handleNameChange = (record: OrgProjectTableItem) => {
  console.log(`output->r`, record)
}
const showAddProjectModal = (record: OrgProjectTableItem) => {
  const {
    id,
    name,
    description,
    enable,
    adminList,
    organizationId,
    moduleIds,
  } = record
  addProjectVisible.value = true
  currentUpdateProject.value = {
    id,
    name,
    description,
    enable,
    userIds: adminList.map((item: UserItem) => item.id),
    organizationId,
    moduleIds,
  }
  console.log(`output->currentUpdateProject.value`, currentUpdateProject.value)
}
const handleAddProjectModalCancel = (shouldSearch: boolean) => {
  if (shouldSearch) {
    fetchData()
  }
  addProjectVisible.value = false
}
defineExpose({
  fetchData,
})
onMounted(() => {
  fetchData()
})
</script>
<template>
  <n-data-table
    :columns="columns"
    :data="data"
    @update:checked-row-keys="handleCheck"
  />
  <base-pagination :page="page" :page-size="pageSize" />
  <add-project
    :visible="addProjectVisible"
    :current-project="currentUpdateProject"
    @cancel="handleAddProjectModalCancel"
  />
</template>

<style scoped></style>
