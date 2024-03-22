<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, NText } from 'naive-ui'
import { computed, h, inject, ref, watchEffect } from 'vue'
import { TableQueryParams } from '/@/api/interface/common'
import {
  CurrentUserGroupItem,
  UserTableItem,
} from '/@/api/interface/setting/usergroup'
import {
  deleteOrgUserFromUserGroup,
  deleteUserFromUserGroup,
  postOrgUserByUserGroup,
  postUserByUserGroup,
} from '/@/api/modules/setting/user-group'
import BasePagination from '/@/components/base-pagination/index.vue'
import RemoveButton from '/@/components/remove-button/index.vue'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import { characterLimit } from '/@/utils'

const props = defineProps<{
  keyword: string
  current: CurrentUserGroupItem
  deletePermission?: string[]
  readPermission?: string[]
  updatePermission?: string[]
}>()

const systemType = inject<AuthScopeEnum>('systemType')
const { t } = useI18n()
const keyword = ref(props.keyword)
const appStore = useAppStore()

const currentOrgId = computed(() => appStore.currentOrgId)
const columns: DataTableColumns<UserTableItem> = [
  {
    title: t('system.userGroup.name'),
    key: 'name',
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: t('system.userGroup.email'),
    key: 'email',
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: t('system.userGroup.phone'),
    key: 'phone',
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: t('system.userGroup.operation'),
    key: 'operation',
    fixed: 'right',
    width: 120,
    render(record) {
      return h(RemoveButton, {
        title: `${t('system.userGroup.removeName', {
          name: characterLimit(record.name),
        })}`,
        disabled: record.userId === 'admin',
        onOk: () => handleRemove(record),
      })
    },
  },
]
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
    if (systemType === AuthScopeEnum.SYSTEM) {
      param.roleId = props.current.id
    } else if (systemType === AuthScopeEnum.ORGANIZATION) {
      param.userRoleId = props.current.id
      param.organizationId = currentOrgId.value
    }
    return systemType === AuthScopeEnum.SYSTEM
      ? postUserByUserGroup(param)
      : postOrgUserByUserGroup(param)
  },
  {
    immediate: false,
    watchingStates: [keyword],
    initialData: {
      total: 0,
      data: [],
    },
    data: (response) => response.records,
  },
)
// 移除用户
const { send: removeSystemUser } = useRequest(
  (param) => deleteUserFromUserGroup(param.id),
  { immediate: false },
)
const { send: removeOrgtemUser } = useRequest(
  (param) =>
    deleteOrgUserFromUserGroup({
      organizationId: currentOrgId.value,
      userRoleId: props.current.id,
      userIds: [param.id],
    }),
  { immediate: false },
)
const handleRemove = (record: UserTableItem) => {
  if (systemType === AuthScopeEnum.SYSTEM) {
    removeSystemUser(record).then(() => {
      fetchData()
    })
  } else if (systemType === AuthScopeEnum.ORGANIZATION) {
    removeOrgtemUser(record).then(() => {
      fetchData()
    })
  }
}
const renderCell = (value: string | number) => {
  if (!value) {
    return h(NText, { depth: 3 }, { default: () => '-' })
  }
  return value
}
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handlePage = (val: number) => {
  page.value = val
}
watchEffect(() => {
  if (props.current.id && currentOrgId.value) {
    // TODO 项目-用户组
    fetchData()
  }
})
defineExpose({
  fetchData,
})
</script>
<template>
  <n-spin :show="loading">
    <n-data-table :columns="columns" :data="data" :render-cell="renderCell" />
    <base-pagination
      :total="total"
      :page-size="pageSize"
      :page="page"
      @update:page-size="handlePrevPage"
      @update:page="handlePage"
    />
  </n-spin>
</template>

<style scoped></style>
