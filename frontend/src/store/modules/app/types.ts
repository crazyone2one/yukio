import {
  OrganizationListItem,
  ProjectListItem,
} from '/@/api/interface/setting/user'

export interface AppState {
  menuCollapse: boolean
  footer: boolean
  menuWidth: number
  collapsedWidth: number
  collapsedIconSize: number
  currentOrgId: string
  currentProjectId: string
  device: string
  navbar: boolean
  projectList: ProjectListItem[]
  ordList: OrganizationListItem[]
  loading: boolean;
  loadingTip: string;
}
