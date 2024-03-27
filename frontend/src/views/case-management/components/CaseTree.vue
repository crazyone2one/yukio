<script setup lang="ts">
import { useRequest } from 'alova'
import { NButton, NTree, TreeOption, NIcon } from 'naive-ui'
import { computed, h, onBeforeMount, ref, watch } from 'vue'
import { ModuleTreeNode } from '/@/api/interface/common'
import {
  createCaseModuleTree,
  deleteCaseModuleTree,
  getCaseModuleTree,
} from '/@/api/modules/case-management/feature-case'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import useFeatureCaseStore from '/@/store/modules/case/feature-case'
import { mapTree } from '/@/utils'
import PopConfirm from '/@/components/pop-confirm/index.vue'
import {
  CreateOrUpdateModule,
  UpdateModule,
} from '/@/api/interface/case-management/feature-case.ts'

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
const focusNodeKey = ref<string>('')
const addSubVisible = ref(false)
const renamePopVisible = ref(false)
const renameCaseName = ref('')
const { send, loading } = useRequest((p) => getCaseModuleTree(p), {
  immediate: false,
  force: true,
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
    emits(
      'init',
      caseTree.value.map((e) => e.name),
    )
  })
}
const handleNodeMoreSelect = (type: string, node: TreeOption) => {
  focusNodeKey.value = (node.key as string) || ''
  switch (type) {
    case 'rename':
      renameCaseName.value = (node.name as string) || ''
      renamePopVisible.value = true
      document
        .querySelector(`#renameSpan${node.id}`)
        ?.dispatchEvent(new Event('click'))
      break
    case 'delete':
      deleteHandler(node)
      resetFocusNodeKey()
      break
    case 'add':
      break
    default:
      break
  }
}
const deleteHandler = (node: TreeOption) => {
  window.$dialog.error({
    title: t('caseManagement.featureCase.moduleDeleteTipTitle', {
      name: node.name,
    }),
    content() {
      return t('caseManagement.featureCase.deleteCaseTipContent')
    },
    positiveText: t('caseManagement.featureCase.deleteConfirm'),
    negativeText: t('common.cancel'),
    maskClosable: false,
    onPositiveClick(e) {
      e.preventDefault
      deleteModule(node.id).then(() => {
        window.$message.success(t('caseManagement.featureCase.deleteSuccess'))
        initModules(selectedNodeKeys.value[0] === node.id)
      })
    },
  })
}
const { send: create } = useRequest((p) => createCaseModuleTree(p), {
  immediate: false,
})
const { send: deleteModule } = useRequest((p) => deleteCaseModuleTree(p), {
  immediate: false,
})
const addSubModule = (formValue?: { field: string }, cancel?: () => void) => {
  const params: CreateOrUpdateModule = {
    projectId: currentProjectId.value,
    name: formValue?.field as string,
    parentId: focusNodeKey.value,
  }
  create(params).then(() => {
    window.$message.success(t('common.addSuccess'))
    if (cancel) {
      cancel()
    }
    initModules()
  })
}
const updateNameModule = (
  formValue?: { field: string },
  cancel?: () => void,
) => {
  const params: UpdateModule = {
    id: focusNodeKey.value,
    name: formValue?.field as string,
  }
  console.log(params)
}
const resetFocusNodeKey = () => {
  focusNodeKey.value = ''
  renamePopVisible.value = false
  renameCaseName.value = ''
}
const renderSuffix = ({ option }: { option: TreeOption }) => {
  return [
    h(
      PopConfirm,
      {
        visible: addSubVisible.value,
        title: t('caseManagement.featureCase.addSubModule'),
        okText: 'common.confirm',
        isDelete: false,
        allNames: [],
        fieldConfig: {
          placeholder: t('caseManagement.featureCase.addGroupTip'),
        },
        onConfirm: addSubModule,
        onCancel: resetFocusNodeKey,
      },
      {
        trigger: () => {
          return h(
            NButton,
            {
              text: true,
              size: 'tiny',
              type: 'primary',
              disabled: option.id === 'root',
              onClick: () => handleNodeMoreSelect('add', option),
            },
            {
              icon: () => {
                return h(
                  NIcon,
                  {},
                  {
                    default: () =>
                      h('div', { class: 'i-ion:add-circle-outline' }),
                  },
                )
              },
            },
          )
        },
      },
    ),
    h(
      PopConfirm,
      {
        visible: renamePopVisible.value,
        title: t('caseManagement.featureCase.rename'),
        okText: 'common.confirm',
        isDelete: false,
        allNames: [],
        fieldConfig: {
          field: renameCaseName.value,
        },
        onConfirm: updateNameModule,
        onCancel: resetFocusNodeKey,
      },
      {
        default: () => {
          return h('span', { id: `renameSpan${option.id}`, class: 'relative' })
        },
        trigger: () => {
          return h(
            NButton,
            {
              text: true,
              size: 'tiny',
              type: 'warning',
              disabled: true,
              class: 'ml-1',
              onClick: () => handleNodeMoreSelect('rename', option),
            },
            {
              icon: () => {
                return h(
                  NIcon,
                  {},
                  {
                    default: () => h('div', { class: 'i-carbon:edit' }),
                  },
                )
              },
            },
          )
        },
      },
    ),
    h(
      NButton,
      {
        text: true,
        size: 'tiny',
        type: 'error',
        disabled: option.id === 'root',
        class: 'ml-1',
        onClick: () => handleNodeMoreSelect('delete', option),
      },
      {
        icon: () => {
          return h(
            NIcon,
            {},
            {
              default: () => h('div', { class: 'i-carbon:delete' }),
            },
          )
        },
      },
    ),
  ]
}
const renderLabel = ({ option }: { option: TreeOption }) => {
  if (!props.isModal) {
    return `${option.label} ` + `(${option.count || 0})`
  }
  return `${option.label} `
}
const nodeProps = ({ option }: { option: TreeOption }) => {
  return {
    onClick() {
      const offspringIds: string[] = []
      mapTree(option.children || [], (e) => {
        offspringIds.push(e.key)
        return e
      })
      emits('caseNodeSelect', option.key, offspringIds)
    },
  }
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
        :render-label="renderLabel"
        :node-props="nodeProps"
      />
    </n-spin>
  </n-space>
</template>

<style scoped></style>
