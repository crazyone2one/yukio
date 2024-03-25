import { alovaInst } from '/@/api/index.ts'
import { ProjectListItem } from '/@/api/interface/setting/user.ts'
import { ProjectListUrl, ProjectSwitchUrl } from '/@/api/requrls/project-management/project.ts'

export const getProjectList = (organizationId: string) =>
  alovaInst.Get<ProjectListItem[]>(ProjectListUrl + `/${organizationId}`)

export const switchProject = (data: { projectId: string; userId: string }) =>
  alovaInst.Post(ProjectSwitchUrl, data)
