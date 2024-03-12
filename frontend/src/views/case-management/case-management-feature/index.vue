<script setup lang="ts">
import { computed, ref } from 'vue'
import YPopConfirm from '/@/components/y-pop-confirm/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import useFeatureCaseStore from '/@/store/modules/case/feature-case.ts'
import CaseTree from '/@/views/case-management/case-management-feature/components/CaseTree.vue'
import { CreateOrUpdateModule } from '/@/models/case-management/feature-case.ts'
import { useAppStore } from '/@/store'
import { useRouter } from 'vue-router'
import { CaseManagementRouteEnum } from '/@/enums/route-enum.ts'

const { t } = useI18n()
const appStore = useAppStore()
const featureCaseStore = useFeatureCaseStore()
const activeFolder = ref<string>('all')
const activeCaseType = ref<'folder' | 'module'>('folder') // 激活用例类型
const isExpandAll = ref(false)
const confirmRef = ref<InstanceType<typeof YPopConfirm> | null>(null)
const addSubVisible = ref(false)
const rootModulesName = ref<string[]>([]) // 根模块名称列表
const offspringIds = ref<string[]>([])
const router = useRouter()
const currentProjectId = computed(() => appStore.currentProjectId)
// 获取激活用例类型样式
const getActiveClass = (type: string) => {
    return activeFolder.value === type ? 'folder-text case-active' : 'folder-text'
}
// 设置当前激活用例类型公共用例|全部用例|回收站
const setActiveFolder = (type: string) => {
    activeFolder.value = type
    if (['public', 'all', 'recycle'].includes(type)) {
        activeCaseType.value = 'folder'
    }
    // if (type === 'recycle') {
    //   router.push({
    //     name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE_RECYCLE,
    //   });
    // }
}
const modulesCount = computed(() => {
    return featureCaseStore.modulesCount
})
/**
 * 添加子模块
 */
const confirmHandler = () => {
    const { field } = confirmRef.value?.form
    // if (!confirmRef.value?.isPass) {
    //     return
    // }
    const params: CreateOrUpdateModule = {
        projectId: currentProjectId.value,
        name: field,
        parentId: 'NONE',
    }
    console.log(params)
}
// 处理用例树节点选中
const caseNodeSelect = (keys: string[], _offspringIds: string[]) => {
    ;[activeFolder.value] = keys
    activeCaseType.value = 'module'
    offspringIds.value = [..._offspringIds]
    featureCaseStore.setModuleId(keys)
}
//设置根模块名称列表
const setRootModules = (names: string[]) => (rootModulesName.value = names)
const caseDetail = () => {
    router.push({
        name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE_DETAIL,
    })
}
</script>
<template>
    <n-card class="rounded-2xl bg-white" hoverable>
        <div class="p-[24px] pb-[16px]">
            <n-button size="small" type="primary" @click="caseDetail">
                {{ t('caseManagement.featureCase.creatingCase') }}
            </n-button>
            <n-button size="small" class="mx-3">
                {{ t('caseManagement.featureCase.importExcel') }}
            </n-button>
        </div>
        <n-divider class="!my-0" />
        <div class="page-wrap">
            <n-split :default-size="0.2">
                <template #1>
                    <div class="p-[24px] pb-0">
                        <div class="feature-case h-[100%]">
                            <div class="case h-[38px]">
                                <div
                                    class="flex items-center"
                                    :class="getActiveClass('all')"
                                    @click="setActiveFolder('all')"
                                >
                                    <n-icon>
                                        <span class="i-tabler:folder-filled" />
                                    </n-icon>
                                    <div class="folder-name mx-[4px]">
                                        {{ t('caseManagement.featureCase.allCase') }}
                                    </div>
                                    <div class="folder-count">({{ modulesCount.all || 0 }})</div>
                                </div>
                                <div class="ml-auto flex items-center">
                                    <n-tooltip trigger="hover">
                                        <template #trigger>
                                            <n-button text class="!mr-0 p-[4px]">
                                                <template #icon>
                                                    <n-icon>
                                                        <span
                                                            :class="
                                                                isExpandAll
                                                                    ? 'i-tabler:folder-off'
                                                                    : 'i-tabler:folder-open'
                                                            "
                                                        />
                                                    </n-icon>
                                                </template>
                                            </n-button>
                                        </template>
                                        {{
                                            isExpandAll
                                                ? t('project.fileManagement.collapseAll')
                                                : t('project.fileManagement.expandAll')
                                        }}
                                    </n-tooltip>
                                    <y-pop-confirm
                                        ref="confirmRef"
                                        v-model:visible="addSubVisible"
                                        :title="t('caseManagement.featureCase.addSubModule')"
                                        :ok-text="t('common.confirm')"
                                        :is-delete="false"
                                        :all-names="rootModulesName"
                                        :field-config="{
                                            placeholder: t(
                                                'caseManagement.featureCase.addGroupTip',
                                            ),
                                        }"
                                        @confirm="confirmHandler"
                                    />
                                </div>
                            </div>
                            <n-divider class="my-[8px]" />
                            <case-tree @case-node-select="caseNodeSelect" @init="setRootModules" />
                        </div>
                    </div>
                </template>
                <template #2> Pane 2 </template>
            </n-split>
        </div>
    </n-card>
</template>

<style scoped></style>
