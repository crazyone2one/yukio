<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, onMounted, reactive, ref, watchEffect } from 'vue'
import AddOrganization from './AddOrganization.vue'
import { TableQueryParams } from '/@/api/interface/common'
import {
  CreateOrUpdateSystemOrgParams,
  OrgAdmin,
  OrganizationListItem,
} from '/@/api/interface/orgnization'
import {
  enableOrDisableOrg,
  postOrgTable,
} from '/@/api/modules/setting/org-and-project'
import TableMoreAction from '/@/components/base-table/TableMoreAction.vue'
import { ActionsItem } from '/@/components/base-table/types'
import EnAble from '/@/components/enable-status/index.vue'
import ShowOrEdit from '/@/components/table-column-show-or-edit/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import TableOperation from '/src/views/setting/system/org-project/components/OrgTableOperation.vue'
export interface SystemOrganizationProps {
  keyword: string
}

const props = defineProps<SystemOrganizationProps>()
const { t } = useI18n()
const checkedRowKeys = ref<DataTableRowKey[]>([])
const currentOrganizationId = ref('')
const orgVisible = ref(false)
const currentUpdateOrganization = ref<CreateOrUpdateSystemOrgParams>()
const currentKeyword = ref(props.keyword)

const tableActions: ActionsItem[] = [
  {
    label: 'system.user.delete',
    eventTag: 'delete',
    danger: true,
  },
]
const columns = reactive<DataTableColumns<OrganizationListItem>>([
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
  {
    title: t('system.organization.createTime'),
    key: 'createTime',
    width: 180,
    sortOrder: false,
    sorter(rowA, rowB) {
      return rowA.createTime - rowB.createTime
    },
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
          onHandleEdit: () => showOrganizationModal(rowData),
          onHandleEnd: () => handleEnableOrDisableOrg(rowData, false),
          onHandleEnable: () => handleEnableOrDisableOrg(rowData),
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
    return postOrgTable(param)
  },
  {
    initialData: {
      total: 0,
      data: [],
    },
    data: (response) => response.records,
    watchingStates: [currentKeyword],
    immediate: false,
  },
)
const pagination = reactive({
  page: page,
  pageSize: pageSize,
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
const { send: enableOrDisableOrgApi } = useRequest(
  (id: string, enable: boolean) => enableOrDisableOrg(id, enable),
  { immediate: false },
)
const handleEnableOrDisableOrg = async (
  record: OrganizationListItem,
  isEnable = true,
) => {
  const title = isEnable
    ? t('system.organization.enableTitle')
    : t('system.organization.endTitle')
  const content = isEnable
    ? t('system.organization.enableContent')
    : t('system.organization.endContent')
  const okText = isEnable ? t('common.confirmStart') : t('common.confirmEnd')
  window.$dialog.warning({
    title,
    content,
    positiveText: okText,
    negativeText: t('common.cancel'),
    onPositiveClick: async () => {
      await enableOrDisableOrgApi(record.id, isEnable)
      window.$message.success(
        isEnable ? t('common.enableSuccess') : t('common.closeSuccess'),
      )
      fetchData()
    },
  })
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
    fetchData()
  }
}

watchEffect(() => {
  currentKeyword.value = props.keyword
})
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
