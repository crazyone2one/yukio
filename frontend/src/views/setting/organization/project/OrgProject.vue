<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import { TableQueryParams } from '/@/api/interface/common'
import {
  CreateOrUpdateSystemProjectParams,
  OrgProjectTableItem,
} from '/@/api/interface/setting/orgAndProject'
import { postProjectTableByOrg } from '/@/api/modules/setting/org-and-project'
import BaseCard from '/@/components/base-card/index.vue'
import BasePagination from '/@/components/base-pagination/index.vue'
import { ActionsItem } from '/@/components/base-table/types'
import ShowOrEdit from '/@/components/table-column-show-or-edit/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'

const { t } = useI18n()
const checkedRowKeys = ref<DataTableRowKey[]>([])
const keyword = ref('')
const userVisible = ref(false)
const addProjectVisible = ref(false)
const currentProjectId = ref('')
const currentUpdateProject = ref<CreateOrUpdateSystemProjectParams>()
const appStore = useAppStore()
const currentOrgId = computed(() => appStore.currentOrgId)
const tableActions: ActionsItem[] = [
  {
    label: 'system.user.delete',
    eventTag: 'delete',
  },
]
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
  },
])
const handleNameChange = (record: OrgProjectTableItem) => {
  console.log(`output->r`, record)
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
      organizationId: currentOrgId.value,
    }
    return postProjectTableByOrg(param)
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

const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handlePage = (val: number) => {
  page.value = val
}
const showAddProject = () => {
  addProjectVisible.value = true
  currentUpdateProject.value = undefined
}
onMounted(() => {
  fetchData()
})
</script>
<template>
  <base-card :loading="loading" hide-back hide-footer>
    <template #headerLeft>
      <n-button type="primary" @click="showAddProject">
        {{ t('system.organization.createProject') }}
      </n-button>
    </template>
    <template #headerRight>
      <n-input
        v-model:value="keyword"
        :placeholder="t('system.organization.searchIndexPlaceholder')"
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
</template>

<style scoped></style>
