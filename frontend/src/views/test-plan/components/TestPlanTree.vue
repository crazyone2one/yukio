<script setup lang="ts">
import { useRequest } from 'alova'
import { NTree, TreeOption } from 'naive-ui'
import { computed, onBeforeMount, ref, watch } from 'vue'
import { getTestPlanModule } from '/@/api/modules/test-plan'
import { useI18n } from '/@/hooks/use-i18n'
import { ModuleTreeNode } from '/@/models/common'
import { useAppStore } from '/@/store'
import { mapTree } from '/@/utils'

const props = defineProps<{
    isModal?: boolean // 是否是弹窗模式
    activeFolder?: string // 当前选中的文件夹，弹窗模式下需要使用
    selectedKeys?: Array<string | number> // 选中的节点 key
    isExpandAll: boolean // 是否展开用例节点
    allNames?: string[] // 所有的模块name列表
    modulesCount?: Record<string, number> // 模块数量统计对象
}>()

const { t } = useI18n()
const pattern = ref<string>('')
const appStore = useAppStore()
const currentProjectId = computed(() => appStore.currentProjectId)
const data = ref<Array<TreeOption>>([])
const emits = defineEmits(['update:selectedKeys', 'planTreeNodeSelect', 'init'])
const {
    send: initModules,
    loading,
    onSuccess,
} = useRequest(() => getTestPlanModule({ projectId: currentProjectId.value }), {
    // 当immediate为false时，默认不发出
    immediate: false,
    force: true,
})
const buildTreeData = (data: ModuleTreeNode[]) => {
    const treeData: TreeOption[] = []
    if (data.length > 0) {
        data.forEach((e) => {
            const option: TreeOption = {
                label: e.name,
                key: e.id,
            }
            if (e.children && e.children.length > 0) {
                option.children = buildTreeData(e.children)
            }
            treeData.push(option)
        })
    }
    return treeData
}
onSuccess((res) => {
    data.value = buildTreeData(res.data)
    emits(
        'init',
        data.value.map((e) => e.name),
    )
})
const nodeProps = ({ option }: { option: TreeOption }) => {
    return {
        onclick: () => {
            const offspringIds: string[] = []
            mapTree(option.children || [], (e) => {
                offspringIds.push(e.key as string)
                return e
            })
            emits('planTreeNodeSelect', option.key, offspringIds)
        },
    }
}
watch(
    () => props.activeFolder,
    (val) => {
        if (val === 'all') {
            initModules()
        }
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
    <n-space vertical>
        <n-input
            v-model:value="pattern"
            size="small"
            :placeholder="t('caseManagement.featureCase.searchTip')"
            clearable
        />
        <n-spin :show="loading">
            <n-tree
                :pattern="pattern"
                :data="data"
                block-line
                :node-props="nodeProps"
                :default-expand-all="props.isExpandAll"
            />
        </n-spin>
    </n-space>
</template>

<style scoped></style>
