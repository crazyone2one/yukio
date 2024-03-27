<script setup lang="ts">
import { useRequest } from 'alova'
import { NButton, NTree, TreeOption } from 'naive-ui'
import { computed, h, onBeforeMount, ref, watch } from 'vue'
import { ModuleTreeNode } from '/@/api/interface/common'
import { getCaseModuleTree } from '/@/api/modules/case-management/feature-case'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import useFeatureCaseStore from '/@/store/modules/case/feature-case'
import { mapTree } from '/@/utils'

const { t } = useI18n()
const props = defineProps<{
  isModal?: boolean // 是否是弹窗模式
  activeFolder?: string // 当前选中的文件夹，弹窗模式下需要使用
  selectedKeys?: Array<string | number> // 选中的节点 key
  isExpandAll: boolean // 是否展开用例节点
  allNames?: string[] // 所有的模块name列表
  modulesCount?: Record<string, number> // 模块数量统计对象
}>()
const appStore = useAppStore()
const featureCaseStore = useFeatureCaseStore()
const pattern = ref('')
const currentProjectId = computed(() => appStore.currentProjectId)
const caseTree = ref<ModuleTreeNode[]>([])
const selectedNodeKeys = ref(props.selectedKeys || [])
const emits = defineEmits([
  'update:selectedKeys',
  'caseNodeSelect',
  'init',
  'dragUpdate',
])
const { send, loading } = useRequest((p) => getCaseModuleTree(p), {
  immediate: false,
})
/**
 * 初始化模块树
 * @param isSetDefaultKey 是否设置第一个节点为选中节点
 */
const initModules = (isSetDefaultKey = false) => {
  send({ projectId: currentProjectId.value }).then((res) => {
    caseTree.value = mapTree<ModuleTreeNode>(res, (e) => {
      return {
        ...e,
        hideMoreAction: e.id === 'root',
        draggable: e.id !== 'root' && !props.isModal,
        disabled: e.id === props.activeFolder && props.isModal,
        count: props.modulesCount?.[e.id] || 0,
      }
    })
    featureCaseStore.setModulesTree(caseTree.value)
    featureCaseStore.setModuleId(['all'])
    if (isSetDefaultKey) {
      selectedNodeKeys.value = [caseTree.value[0].id]
    }
  })
}
const handleNodeMoreSelect = (type: string, node: TreeOption) => {
  console.log(`output->type`, type)
  console.log(`output->type`, node)
}
const renderSuffix = ({ option }: { option: TreeOption }) => {
  return [
    h(
      NButton,
      {
        text: true,
        type: 'warning',
        onClick: () => handleNodeMoreSelect('rename', option),
      },
      { default: () => `${t('caseManagement.featureCase.rename')}` },
    ),
    h(
      NButton,
      {
        text: true,
        type: 'error',
        onClick: () => handleNodeMoreSelect('delete', option),
      },
      { default: () => `${t('caseManagement.featureCase.delete')}` },
    ),
  ]
}
watch(
  () => props.selectedKeys,
  (val) => {
    selectedNodeKeys.value = val || []
    featureCaseStore.setModuleId(val as string[])
  },
)
watch(
  () => selectedNodeKeys.value,
  (val) => {
    emits('update:selectedKeys', val)
  },
)
onBeforeMount(() => {
  initModules()
})

defineExpose({
  initModules,
})
</script>
<template>
  <n-space vertical :size="12">
    <n-input v-model:value="pattern" size="small" placeholder="搜索" />
    <n-spin :show="loading">
      <n-tree
        :show-irrelevant-nodes="false"
        :pattern="pattern"
        :data="caseTree"
        block-line
        :default-expand-all="props.isExpandAll"
        :default-selected-keys="props.selectedKeys"
        :render-suffix="renderSuffix"
      />
    </n-spin>
  </n-space>
</template>

<style scoped></style>
