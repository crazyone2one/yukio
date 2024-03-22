<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, onMounted, reactive, ref } from 'vue'
import EditUser from './components/EditUser.vue'
import UserTableOpration from './components/UserTableOpration.vue'
import { TableQueryParams } from '/@/api/interface/common'
import { UserListItem } from '/@/api/interface/setting/user'
import { getUserList } from '/@/api/modules/setting/user'
import BaseCard from '/@/components/base-card/index.vue'
import BasePagination from '/@/components/base-pagination/index.vue'
import TableMoreAction from '/@/components/base-table/TableMoreAction.vue'
import { ActionsItem } from '/@/components/base-table/types'
import EnAble from '/@/components/enable-status/index.vue'
import TagGroup from '/@/components/tag-group/index.vue'
import { useI18n } from '/@/hooks/use-i18n'

type UserModalMode = 'create' | 'edit'
const { t } = useI18n()
const checkedRowKeys = ref<DataTableRowKey[]>([])
const visible = ref(false)
const keyword = ref('')
const userFormMode = ref<UserModalMode>('create')
const editUserRef = ref()
const currentUser = ref<UserListItem>()
const tableActions: ActionsItem[] = [
  {
    label: 'system.user.resetPassword',
    eventTag: 'resetPassword',
  },
  {
    label: 'system.user.disable',
    eventTag: 'disable',
  },
  {
    isDivider: true,
  },
  {
    label: 'system.user.delete',
    eventTag: 'delete',
  },
]
const columns = reactive<DataTableColumns<UserListItem>>([
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
    render(rowData) {
      return h(TagGroup, { tagList: rowData.organizationList, type: 'primary' })
    },
  },

  {
    title: t('system.user.tableColumnUserGroup'),
    key: 'userRoleList',
    width: 300,
    render(rowData) {
      return h(TagGroup, { tagList: rowData.userRoleList, type: 'primary' })
    },
  },
  {
    title: t('system.user.tableColumnStatus'),
    key: 'enable',
    render(row) {
      return h(EnAble, { record: row })
    },
  },

  {
    title: t('system.organization.operation'),
    key: 'operation',
    fixed: 'right',
    // width: 230,
    render(rowData) {
      return h(
        UserTableOpration,
        { record: rowData },
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
const handleMoreAction = (event: ActionsItem, record: UserListItem) => {
  console.log(`output->event`, event)
  console.log(`output->record`, record)
}
const {
  send: fetchData,
  page,
  pageSize,
  data,
  loading,
  total,
} = usePagination(
  (page, pageSize) => {
    const param: TableQueryParams = {
      keyword: keyword.value,
      current: page,
      pageSize,
    }
    return getUserList(param)
  },
  {
    initialData: {
      total: 0,
      data: [],
    },
    data: (response) => response.records,
    total: (response) => response.totalRow,
    watchingStates: [keyword],
    immediate: false,
  },
)

const handleCheck = (rowKeys: DataTableRowKey[]) =>
  (checkedRowKeys.value = rowKeys)
const showUserModal = (mode: UserModalMode, record?: UserListItem) => {
  visible.value = true
  userFormMode.value = mode
  if (mode === 'edit' && record) {
    currentUser.value = record
  }
}
const handleCancle = () => (visible.value = false)
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handlePage = (val: number) => {
  page.value = val
}
onMounted(() => {
  fetchData()
})
</script>
<template>
  <base-card :loading="loading" hide-back hide-footer>
    <template #headerLeft>
      <n-button type="primary" @click="showUserModal('create')">
        {{ t('system.user.createUser') }}
      </n-button>
      <n-button type="info">
        {{ t('system.user.importUser') }}
      </n-button>
    </template>
    <template #headerRight>
      <n-input
        v-model:value="keyword"
        :placeholder="t('system.user.searchUser')"
        class="w-[240px]"
        clearable
      />
    </template>
    <div>
      <n-data-table
        :columns="columns"
        :data="data"
        @update:checked-row-keys="handleCheck"
      />
      <base-pagination
        :total="total"
        :page-size="pageSize"
        :page="page"
        @update:page-size="handlePrevPage"
        @update:page="handlePage"
      />
    </div>
  </base-card>
  <edit-user
    ref="editUserRef"
    :visible="visible"
    :user-form-mode="userFormMode"
    :current-user="currentUser"
    @cancel="handleCancle"
    @load-list="fetchData"
  />
</template>

<style scoped></style>
