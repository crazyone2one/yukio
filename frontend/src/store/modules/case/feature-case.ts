/* eslint-disable @typescript-eslint/no-explicit-any */
import { defineStore } from 'pinia'
import { CustomAttributes, TabItemType } from '/@/models/case-management/feature-case'
import { ModuleTreeNode } from '/@/models/common'

const useFeatureCaseStore = defineStore('featureCase', {
    state: (): {
        moduleId: string[] // 当前选中模块
        caseTree: ModuleTreeNode[] // 用例树
        modulesCount: Record<string, any> // 用例树模块数量
        recycleModulesCount: Record<string, any> // 回收站模块数量
        operatingState: boolean // 操作状态
        tabSettingList: TabItemType[] // 详情tab
        activeTab: string // 激活tab
        defaultFields: CustomAttributes[]
        defaultCount: Record<string, any>
    } => ({
        moduleId: [],
        caseTree: [],
        modulesCount: {},
        recycleModulesCount: {},
        operatingState: false,
        tabSettingList: [],
        activeTab: 'detail',
        defaultFields: [],
        defaultCount: {},
    }),
    actions: {
        setModuleId(currentModuleId: string[]) {
            this.moduleId = ['all', 'recycle'].includes(currentModuleId[0])
                ? ['root']
                : currentModuleId
        },
        // 设置用例树
        setModulesTree(tree: ModuleTreeNode[]) {
            this.caseTree = tree
        },
    },
    persist: true,
})
export default useFeatureCaseStore
