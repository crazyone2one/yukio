import { defineStore } from 'pinia'
import { AppState } from './types'
import { OrganizationListItem } from '/@/api/interface/setting/user'
import { getProjectList } from '/@/api/modules/project-management/project.ts'
import { useI18n } from '/@/hooks/use-i18n'

const useAppStore = defineStore('app', {
  persist: {
    paths: ['currentOrgId', 'currentProjectId', 'pageConfig'],
  },
  state: (): AppState => ({
    menuCollapse: false,
    currentOrgId: '',
    currentProjectId: '',
    footer: false,
    menuWidth: 240,
    collapsedWidth: 64,
    collapsedIconSize: 24,
    device: 'desktop',
    navbar: true,
    projectList: [],
    ordList: [],
    loading: false,
    loadingTip: '',
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
     * 更新设置
     * @param partial 设置
     */
    updateSettings(partial: Partial<AppState>) {
      this.$patch(partial)
    },
    /**
     * 设置当前组织列表
     */
    setOrdList(ordList: OrganizationListItem[]) {
      this.ordList = ordList
    },
    async initProjectList() {
      this.projectList = this.currentOrgId
        ? await getProjectList(this.currentOrgId)
        : []
    },
    /**
     * 显示全局 loading
     */
    showLoading(tip = '') {
      const { t } = useI18n()
      this.loading = true
      this.loadingTip = tip || t('message.loadingDefaultTip')
    },
    /**
     * 隐藏全局 loading
     */
    hideLoading() {
      const { t } = useI18n()
      this.loading = false
      this.loadingTip = t('message.loadingDefaultTip')
    },
  },
})
export default useAppStore
