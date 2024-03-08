<script setup lang="ts">
import { NTree, TreeOption } from 'naive-ui'
import { computed, onBeforeMount, ref } from 'vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { useForm } from '@alova/scene-vue'
import { getCaseModuleTree } from '/@/api/modules/case-management/feature-case.ts'
import { useAppStore } from '/@/store'
import useFeatureCaseStore from '/@/store/modules/case/feature-case.ts'
import { ModuleTreeNode } from '/@/models/common.ts'
import { mapTree } from '/@/utils'

const { t } = useI18n()
const pattern = ref<string>('')
const appStore = useAppStore()
const featureCaseStore = useFeatureCaseStore()
const caseTree = ref<Array<TreeOption>>([])
const emits = defineEmits(['update:selectedKeys', 'caseNodeSelect', 'init'])
const currentProjectId = computed(() => appStore.currentProjectId)
const {
    // 提交状态
    loading: submitting,
    send: initModules,
    onSuccess,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        formData.projectId = currentProjectId.value
        return getCaseModuleTree(formData)
    },
    {
        // 初始化表单数据
        initialForm: {
            projectId: '',
        },
        immediate: false,
    },
)
const buildTree = (treeData: ModuleTreeNode[]) => {
    const result: TreeOption[] = []
    if (treeData.length > 0) {
        treeData.forEach((item) => {
            const option: TreeOption = {
                key: item.id,
                label: item.name,
            }
            if (item.children) {
                buildTree(item.children)
            }
            result.push(option)
        })
    }
    return result
}
onSuccess((res) => {
    caseTree.value = buildTree(res.data)
    featureCaseStore.setModulesTree(res.data)
    featureCaseStore.setModuleId(['all'])
    emits(
        'init',
        caseTree.value.map((e) => e.name),
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
            emits('caseNodeSelect', option.key, offspringIds)
        },
    }
}
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
            :placeholder="t('caseManagement.featureCase.searchTip')"
            class="mb-[16px]"
            clearable
        />
        <n-spin :show="submitting">
            <n-tree
                :show-irrelevant-nodes="false"
                :pattern="pattern"
                :data="caseTree"
                block-line
                :node-props="nodeProps"
            />
        </n-spin>
    </n-space>
</template>

<style scoped></style>
