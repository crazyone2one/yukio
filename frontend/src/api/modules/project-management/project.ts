import { alovaInst } from '/@/api/index.ts'
import { ProjectListUrl } from '/@/api/requrls/project-management/project.ts'
import { ProjectListItem } from '/@/api/interface/setting/user.ts'

export const getProjectList = (organizationId: string) =>
  alovaInst.Get<ProjectListItem[]>(ProjectListUrl + `/${organizationId}`)
