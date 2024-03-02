import { ProjectListUrl, ProjectSwitchUrl } from '../../requrls/project-management/project'
import instance from '/@/api'
import { ProjectListItem } from '/@/models/setting/project'

export const getProjectList = (organizationId: string) =>
    instance.Get<ProjectListItem[]>(`${ProjectListUrl}/${organizationId}`)

export const switchProject = (data: { projectId: string; userId: string }) =>
    instance.Post(`${ProjectSwitchUrl}`, data)
