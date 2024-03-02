<script setup lang="ts">
import { useRequest } from 'alova'
import { NTree, TreeOption } from 'naive-ui'
import { computed, onBeforeMount, ref } from 'vue'
import { getTestPlanModule } from '/@/api/modules/test-plan'
import { useI18n } from '/@/hooks/use-i18n'
import { useAppStore } from '/@/store'

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
})
onSuccess((res) => {
    console.log(`output->res`, res)
    emits(
        'init',
        data.value.map((e) => e.name),
    )
})
onBeforeMount(() => {
    initModules()
})
defineExpose({
    initModules,
})
</script>
<template>
    <n-space vertical :size="12">
        <n-input
            v-model:value="pattern"
            :placeholder="t('caseManagement.featureCase.searchTip')"
            clearable
        />
        <n-spin :show="loading">
            <n-tree :pattern="pattern" :data="data" block-line />
        </n-spin>
    </n-space>
</template>

<style scoped></style>
