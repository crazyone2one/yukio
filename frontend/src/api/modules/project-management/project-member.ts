import { alovaInst } from '/@/api/index.ts'
import { ProjectMemberOptions } from '/@/api/requrls/project-management/projectMember.ts'

export const getProjectMemberOptions = (projectId: string, keyword?: string) =>
  alovaInst.Get(`${ProjectMemberOptions}/${projectId}`, { params: { keyword } })
