import { LocationQueryValue } from 'vue-router'

export type SeneType =
  | 'FUNCTIONAL'
  | 'BUG'
  | 'API'
  | 'UI'
  | 'TEST_PLAN'
  | LocationQueryValue[]
  | LocationQueryValue
export interface FieldOptions {
  fieldId?: string
  value: string | number | boolean
  text: string
  internal?: boolean // 是否是内置模板
}
export interface OrdTemplateManagement {
  id: string
  name: string
  remark: string // 描述
  internal: boolean // 是否是系统模板
  updateTime: number
  createTime: number
  createUser: string // 创建人
  scopeType: string // 组织或项目级别字段
  scopeId: string // 组织或项目ID
  enableThirdPart: boolean // 是否开启api字段名配置
  enableDefault: boolean // 是否是默认模板
  refId: string // 项目模板所关联的组织模板ID
  scene: string // 使用场景
}
export interface CustomField {
  fieldId: string
  required?: boolean // 是否必填
  apiFieldId?: string // api字段名
  defaultValue?: string | string[] | null | number // 默认值
  [key: string]: string | boolean | string[] | null | number | undefined
}
// 新增 || 编辑参数
export interface AddOrUpdateField {
  id?: string
  name: string
  used: boolean
  scene: SeneType // 使用场景
  type: FormItemType
  remark: string // 备注
  scopeId: string // 组织或项目ID
  options?: FieldOptions[]
  enableOptionKey: boolean
  [key: string]: string | boolean | FieldOptions[] | SeneType | undefined
}
export type FormItemType =
  | 'INPUT'
  | 'TEXTAREA'
  | 'SELECT'
  | 'MULTIPLE_SELECT'
  | 'RADIO'
  | 'CHECKBOX'
  | 'MEMBER'
  | 'MULTIPLE_MEMBER'
  | 'DATE'
  | 'DATETIME'
  | 'INT'
  | 'FLOAT'
  | 'MULTIPLE_INPUT'
  | 'INT'
  | 'FLOAT'
  | 'NUMBER'
  | 'PassWord'
  | 'CASCADER'
  | undefined
// 自定义字段
export interface DefinedFieldItem {
  id: string
  name: string
  scene: SeneType // 使用场景
  type: FormItemType // 表单类型
  remark: string
  internal: boolean // 是否是内置字段
  scopeType: string // 组织或项目级别字段（PROJECT, ORGANIZATION）
  createTime: number
  updateTime: number
  createUser: string
  refId: string | null // 项目字段所关联的组织字段ID
  enableOptionKey: boolean | null // 是否需要手动输入选项key
  scopeId: string // 组织或项目ID
  options: FieldOptions[] | null
  required?: boolean | undefined
  fApi?: any // 表单值
  // formRules?: FormRuleItem[] | FormItem[] | FormRule[] // 表单列表
  [key: string]: any
}
export interface fieldIconAndNameModal {
  value: string
  iconName: string // 图标名称
  label: string // 对应标签
}
