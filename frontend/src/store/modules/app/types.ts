import { ProjectListItem } from '/@/models/setting/project'

export interface AppState {
    innerHeight: number
    menuCollapse: boolean
    menuWidth: number
    currentOrgId: string
    currentProjectId: string
    device: string
    projectList: ProjectListItem[]
    ordList: { id: string; name: string }[]
}
