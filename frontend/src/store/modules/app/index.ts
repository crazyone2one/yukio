import { SelectOption } from 'naive-ui'
import { defineStore } from 'pinia'
import type { AppState } from './types'
import { getProjectList } from '/@/api/modules/project-management/project'
const useAppStore = defineStore('app', {
    state: (): AppState => ({
        innerHeight: 0,
        currentOrgId: '',
        currentProjectId: '',
        menuCollapse: false,
        menuWidth: 240,
        device: 'desktop',
        projectList: [],
        // projectListOptions:[],
        ordList: [],
    }),
    getters: {
        getCurrentOrgId(state: AppState): string {
            return state.currentOrgId
        },
        getCurrentProjectId(state: AppState): string {
            return state.currentProjectId
        },
    },
    actions: {
        /**
         * 更新设置
         * @param partial 设置
         */
        updateSettings(partial: Partial<AppState>) {
            this.$patch(partial)
        },
        /**
         * 设置当前组织 ID
         */
        setCurrentOrgId(id: string) {
            this.currentOrgId = id
        },
        /**
         * 设置当前项目 ID
         */
        setCurrentProjectId(id: string) {
            this.currentProjectId = id
        },
        /**
         * 设置当前组织列表
         */
        setOrdList(ordList: { id: string; name: string }[]) {
            this.ordList = ordList
        },
        async initProjectList() {
            try {
                if (this.currentOrgId) {
                    const res = await getProjectList(this.getCurrentOrgId)
                    this.projectList = []
                    res.forEach((item) => {
                        const option: SelectOption = {}
                        option.label = item.name
                        option.value = item.id
                        this.projectList.push(option)
                    })
                } else {
                    this.projectList = []
                }
            } catch (error) {
                // eslint-disable-next-line no-console
                console.log(error)
            }
        },
    },
    persist: {
        paths: ['currentOrgId', 'currentProjectId', 'pageConfig'],
    },
})
export default useAppStore
