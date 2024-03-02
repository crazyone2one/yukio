import { SelectOption } from 'naive-ui'

export interface AppState {
    innerHeight: number
    menuCollapse: boolean
    menuWidth: number
    currentOrgId: string
    currentProjectId: string
    device: string
    projectList: SelectOption[]
    // projectListOptions: SelectOption[]
    ordList: { id: string; name: string }[]
}
