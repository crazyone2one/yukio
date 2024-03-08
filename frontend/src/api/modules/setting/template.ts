import instance from '/@/api'
import {
    getOrdTemplateStateUrl,
    getProjectTemplateStateUrl,
} from '/@/api/requrls/setting/template.ts'

// 获取模板列表的状态(组织)
export const getOrdTemplate = (scopedId: string) =>
    instance.Get<Record<string, boolean>>(`${getOrdTemplateStateUrl}/${scopedId}`)
// 获取模板列表的状态(项目)
export const getProTemplate = (scopedId: string) =>
    instance.Get<Record<string, boolean>>(`${getProjectTemplateStateUrl}/${scopedId}`)
