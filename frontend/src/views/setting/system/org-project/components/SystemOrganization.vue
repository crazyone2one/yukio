<script setup lang="ts">
import { DataTableColumn, DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, reactive, ref } from 'vue'
import AddOrganization from './AddOrganization.vue'
import {
  CreateOrUpdateSystemOrgParams,
  OrgAdmin,
  OrganizationListItem,
} from '/@/api/interface/orgnization'
import TableMoreAction from '/@/components/base-table/TableMoreAction.vue'
import TableOperation from '/@/components/base-table/TableOperation.vue'
import { ActionsItem } from '/@/components/base-table/types'
import EnAble from '/@/components/enable-status/index.vue'
import ShowOrEdit from '/@/components/table-column-show-or-edit/index.vue'
import { useI18n } from '/@/hooks/use-i18n'

const { t } = useI18n()
const checkedRowKeys = ref<DataTableRowKey[]>([])
const currentOrganizationId = ref('')
const orgVisible = ref(false)
const currentUpdateOrganization = ref<CreateOrUpdateSystemOrgParams>()
const createTimeColumn: DataTableColumn<OrganizationListItem> = {
  title: t('system.organization.createTime'),
  key: 'createTime',
  sortOrder: false,
  sorter: 'default',
}
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
    render(row) {
      return h(EnAble, { record: row })
    },
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
  },
  createTimeColumn,
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
          onHandleEdit: () => showOrganizationModal(rowData),
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
]
const data = Array.from({ length: 100 }).map((_, index) => ({
  id: `${index}o`,
  num: `${index}`,
  name: `Organization ${index}`,
  memberCount: 100 + index,
  projectCount: 200 + index,
  enable: index % 3 === 0 ? true : false,
  description: 'This is a description',
  createUser: 'John Doe',
  createTime: new Date().toLocaleString(),
  orgAdmins: [],
}))
const pagination = reactive({
  page: 1,
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 20, 30],
  onChange: (page: number) => {
    pagination.page = page
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize
    pagination.page = 1
  },
})
const handleCheck = (rowKeys: DataTableRowKey[]) =>
  (checkedRowKeys.value = rowKeys)
const handleNameChange = (record: OrganizationListItem) => {
  console.log(`output->r`, record)
}
const handleMoreAction = (event: ActionsItem, record: OrganizationListItem) => {
  console.log(`output->event`, event)
  console.log(`output->record`, record)
}
/**
 * 编辑
 */
const showOrganizationModal = (record: OrganizationListItem) => {
  currentOrganizationId.value = record.id
  orgVisible.value = true
  currentUpdateOrganization.value = {
    id: record.id,
    name: record.name,
    description: record.description,
    userIds: record.orgAdmins.map((item: OrgAdmin) => item.id) || [],
  }
}
const handleAddOrgModalCancel = (shouldSearch: boolean) => {
  orgVisible.value = false
  if (shouldSearch) {
    // fetchData()
  }
}
</script>
<template>
  <n-data-table
    :columns="columns"
    :data="data"
    :pagination="pagination"
    @update:checked-row-keys="handleCheck"
  />
  <add-organization
    :visible="orgVisible"
    :current-organization="currentUpdateOrganization"
    @cancel="handleAddOrgModalCancel"
  />
</template>

<style scoped></style>
