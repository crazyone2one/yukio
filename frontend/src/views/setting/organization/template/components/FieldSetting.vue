<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import EditField from './EditField.vue'
import { TableQueryParams } from '/@/api/interface/common'
import {
  OrdTemplateManagement,
  SeneType,
} from '/@/api/interface/setting/template'
import { getFieldList } from '/@/api/modules/setting/template'
import BasePagination from '/@/components/base-pagination/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'

const props = defineProps<{
  mode: 'organization' | 'project'
}>()
const { t } = useI18n()
const keyword = ref('')
const fieldDrawerRef = ref()
const appStore = useAppStore()
const route = useRoute()
const showDrawer = ref<boolean>(false)
const currentOrd = computed(() => appStore.currentOrgId)
const currentProjectId = computed(() => appStore.currentProjectId)
const templateType = computed(() => {
  switch (route.query.type) {
    case 'API':
      return t('system.orgTemplate.templateApi')
    case 'BUG':
      return t('system.orgTemplate.templateBug')
    default:
      return t('system.orgTemplate.templateCase')
  }
})
const scene = ref<SeneType>(route.query.type)
const getParams = () => {
  scene.value = route.query.type
  return {
    scene: scene.value,
    scopedId:
      props.mode === 'organization' ? currentOrd.value : currentProjectId.value,
  }
}
const checkedRowKeys = ref<DataTableRowKey[]>([])
const columns = reactive<DataTableColumns<OrdTemplateManagement>>([
  {
    title: t('system.orgTemplate.name'),
    key: 'name',
    width: 300,
  },
  {
    title: t('system.orgTemplate.columnFieldType'),
    key: 'type',
  },
  {
    title: t('system.orgTemplate.columnFieldDescription'),
    key: 'remark',
    width: 300,
  },
  {
    title: t('system.orgTemplate.columnFieldUpdatedTime'),
    key: 'updateTime',
  },
  {
    title: t('system.orgTemplate.operation'),
    key: 'operation',
  },
])
const handleCheck = (rowKeys: DataTableRowKey[]) =>
  (checkedRowKeys.value = rowKeys)
const fieldHandler = () => {
  showDrawer.value = true
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
      ...getParams(),
    }
    return getFieldList(param)
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
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handlePage = (val: number) => {
  page.value = val
}
onMounted(() => {
  // doCheckIsTip();
  // isEnableOperation();
  fetchData()
})
</script>
<template>
  <n-spin :show="loading">
    <div class="mb-4 flex items-center justify-between">
      <div class="flex items-center">
        <!-- <span class="font-medium">{{ t('system.orgTemplate.fieldList') }}</span> -->
        <n-button type="primary" @click="fieldHandler">
          {{ t('system.orgTemplate.addField') }}
        </n-button>
      </div>
      <n-input
        v-model:value="keyword"
        :placeholder="t('system.orgTemplate.searchTip')"
        class="!w-[230px]"
      />
    </div>
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
  </n-spin>
  <edit-field
    ref="fieldDrawerRef"
    v-model:visible="showDrawer"
    :mode="props.mode"
    :data="[]"
  />
</template>

<style scoped></style>
