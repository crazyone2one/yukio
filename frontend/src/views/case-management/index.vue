<script setup lang="ts">
import { useRequest } from 'alova'
import { computed, ref } from 'vue'
import CaseTree from './components/CaseTree.vue'
import { CreateOrUpdateModule } from '/@/api/interface/case-management/feature-case'
import { createCaseModuleTree } from '/@/api/modules/case-management/feature-case'
import PopConfirm from '/@/components/pop-confirm/index.vue'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import useFeatureCaseStore from '/@/store/modules/case/feature-case'

const { t } = useI18n()
const appStore = useAppStore()
const featureCaseStore = useFeatureCaseStore()
const isExpandAll = ref(false)
const currentProjectId = computed(() => appStore.currentProjectId)
const activeFolder = ref<string>('all')
const rootModulesName = ref<string[]>([]) // 根模块名称列表
const offspringIds = ref<string[]>([])
const activeCaseType = ref<'folder' | 'module'>('folder') // 激活用例类型
// 选中节点
const selectedKeys = computed({
  get: () => [activeFolder.value],
  set: (val) => val,
})
// 全部展开或折叠
const expandHandler = () => {
  isExpandAll.value = !isExpandAll.value
}
const modulesCount = computed(() => {
  return featureCaseStore.modulesCount
})
const addSubVisible = ref(false)
const confirmRef = ref()
const caseTreeRef = ref()
const { send: create } = useRequest((p) => createCaseModuleTree(p), {
  immediate: false,
})
const confirmHandler = () => {
  const { field } = confirmRef.value.form
  if (!confirmRef.value.isPass) {
    return
  }
  const params: CreateOrUpdateModule = {
    projectId: currentProjectId.value,
    name: field,
    parentId: 'NONE',
  }
  create(params).then(() => {
    window.$message.success(t('caseManagement.featureCase.addSuccess'))
    caseTreeRef.value.initModules()
    addSubVisible.value = false
  })
}
const setRootModules = (names: string[]) => (rootModulesName.value = names)
const caseNodeSelect = (keys: string[], _offspringIds: string[]) => {
  ;[activeFolder.value] = keys
  activeCaseType.value = 'module'
  offspringIds.value = [..._offspringIds]
  featureCaseStore.setModuleId(keys)
}
</script>
<template>
  <div class="rounded-2xl bg-white">
    <div class="p-[24px] pb-[16px]">
      <n-button type="primary">
        {{ $t('caseManagement.featureCase.creatingCase') }}
      </n-button>
      <n-button class="mx-3">
        {{ $t('caseManagement.featureCase.importExcel') }}
      </n-button>
    </div>
    <n-divider class="!my-0" />
    <div>
      <n-split
        direction="horizontal"
        :max="0.75"
        :min="0.2"
        :default-size="0.2"
      >
        <template #1>
          <div class="p-[24px] pb-0">
            <div class="feature-case h-[100%]">
              <div class="case h-[38px]">
                <div class="flex items-center">
                  <div class="folder-name mx-[4px]">
                    {{ $t('caseManagement.featureCase.allCase') }}
                  </div>
                  <div class="folder-count">({{ modulesCount.all || 0 }})</div>
                </div>
                <div class="ml-auto flex items-center">
                  <n-tooltip trigger="hover">
                    <template #trigger>
                      <n-button
                        class="!mr-0 p-[4px]"
                        text
                        @click="expandHandler"
                      >
                        <template #icon>
                          <n-icon>
                            <div
                              :class="
                                isExpandAll
                                  ? 'i-ion:folder-open-outline'
                                  : 'i-ion:folder'
                              "
                            />
                          </n-icon>
                        </template>
                      </n-button>
                    </template>
                    {{
                      isExpandAll
                        ? $t('project.fileManagement.collapseAll')
                        : $t('project.fileManagement.expandAll')
                    }}
                  </n-tooltip>
                  <pop-confirm
                    ref="confirmRef"
                    v-model:visible="addSubVisible"
                    :title="t('caseManagement.featureCase.addSubModule')"
                    :ok-text="'common.confirm'"
                    :field-config="{
                      placeholder: $t('caseManagement.featureCase.addGroupTip'),
                    }"
                    :all-names="rootModulesName"
                    @confirm="confirmHandler"
                  >
                    <template #trigger>
                      <n-button class="!mr-0 p-[2px]" text>
                        <template #icon>
                          <n-icon>
                            <div class="i-ion:add-circle-outline" />
                          </n-icon>
                        </template>
                      </n-button>
                    </template>
                  </pop-confirm>
                </div>
              </div>
              <n-divider class="my-[8px]" />
              <case-tree
                ref="caseTreeRef"
                :is-expand-all="isExpandAll"
                :active-folder="activeFolder"
                :selected-keys="selectedKeys"
                :modules-count="modulesCount"
                @init="setRootModules"
                @case-node-select="caseNodeSelect"
              />
              <!-- <div class="b-0 absolute w-[88%]">
                <n-divider class="!my-0 !mb-2" />
                <div class="case h-[38px]">
                  <div class="flex items-center">
                    <div class="folder-name mx-[4px]">
                      {{ $t('caseManagement.featureCase.recycle') }}
                    </div>
                  </div>
                </div>
              </div> -->
            </div>
          </div>
        </template>
        <template #2> Pane 2 </template>
      </n-split>
    </div>
  </div>
</template>

<style scoped></style>
