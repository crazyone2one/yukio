import { useAppStore } from '/@/store'
import { defineStore } from 'pinia'
import { computed } from 'vue'
import { getOrdTemplate, getProTemplate } from '/@/api/modules/setting/template.ts'

const appStore = useAppStore()
const useTemplateStore = defineStore('template', {
    persist: true,
    state: (): {
        ordStatus: Record<string, boolean>
        projectStatus: Record<string, boolean>
    } => ({
        ordStatus: {
            FUNCTIONAL: false,
            API: false,
            UI: false,
            TEST_PLAN: false,
            BUG: false,
        },
        projectStatus: {
            FUNCTIONAL: false,
            API: false,
            UI: false,
            TEST_PLAN: false,
            BUG: false,
        },
    }),
    actions: {
        // 模板列表的状态
        async getStatus() {
            const currentOrgId = computed(() => appStore.currentOrgId)
            const currentProjectId = computed(() => appStore.currentProjectId)
            try {
                // if (currentOrgId.value && hasAnyPermission(['ORGANIZATION_TEMPLATE:READ'])) {
                //     this.ordStatus = await getOrdTemplate(currentOrgId.value)
                // }
                if (currentOrgId.value) {
                    this.ordStatus = await getOrdTemplate(currentOrgId.value)
                }
                // if (currentProjectId.value && hasAnyPermission(['PROJECT_TEMPLATE:READ'])) {
                //     this.projectStatus = await getProTemplate(currentProjectId.value)
                // }
                if (currentProjectId.value) {
                    this.projectStatus = await getProTemplate(currentProjectId.value)
                }
            } catch (error) {
                console.log(error)
            }
        },
    },
})
export default useTemplateStore
