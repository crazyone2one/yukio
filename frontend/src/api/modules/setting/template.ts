import { CommonPage, TableQueryParams } from '../../interface/common'
import { OrdTemplateManagement } from '../../interface/setting/template'
import {
  GetDefinedFieldListUrl,
  getOrdTemplateStateUrl,
  getProjectTemplateStateUrl,
} from '../../requrls/setting/template'
import { alovaInst } from '/@/api/index.ts'

export const getFieldList = (params: TableQueryParams) =>
  alovaInst.Post<CommonPage<OrdTemplateManagement>>(
    `${GetDefinedFieldListUrl}/${params.scopedId}/${params.scene}`,
    params,
  )
/**
 * 获取模板列表的状态(组织)
 * @param scopedId
 * @returns
 */
export const getOrdTemplate = (scopedId: string) =>
  alovaInst.Get<Record<string, boolean>>(
    `${getOrdTemplateStateUrl}/${scopedId}`,
  )
/**
 * 获取模板列表的状态(项目)
 * @param scopedId
 * @returns
 */
export const getProTemplate = (scopedId: string) =>
  alovaInst.Get<Record<string, boolean>>(
    `${getProjectTemplateStateUrl}/${scopedId}`,
  )
