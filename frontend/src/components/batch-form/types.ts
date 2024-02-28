import { FormRules, SelectOption } from 'naive-ui'

export type FormItemType = 'input' | 'select' | 'inputNumber' | 'tagInput' | 'multiple' | 'switch'
export type FormMode = 'create' | 'edit'
export interface CustomValidator {
    notRepeat?: boolean
}
export interface FormItemModel {
    filed: string
    type: FormItemType
    rules?: (FormRules & CustomValidator)[]
    label?: string
    placeholder?: string
    min?: number
    max?: number
    maxLength?: number
    hideAsterisk?: boolean
    hideLabel?: boolean
    children?: FormItemModel[]
    options?: SelectOption[] // select option 选项
    // filedNames?: SelectFieldNames; // select option 选项字段名
    className?: string // 自定义样式
    defaultValue?: string | string[] | number | number[] | boolean // 默认值
    hasRedStar?: boolean // 是否有红星
}
