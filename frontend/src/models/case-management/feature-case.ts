/* eslint-disable @typescript-eslint/no-explicit-any */
export interface ModulesTreeType {
    id: string
    name: string
    type: string
    parentId: string
    children: ModulesTreeType[]
    attachInfo: Record<string, any> // 附加信息
    count: number // 节点资源数量（多数情况下不会随着节点信息返回，视接口而定）
}
// 创建模块
export interface CreateOrUpdateModule {
    projectId: string
    name: string
    parentId: string
}
// 更新模块
export interface UpdateModule {
    id: string
    name: string
}
export interface customFieldsItem {
    caseId?: string; // 用例id
    fieldId: string;
    value: string;
    defaultValue?: string;
    [key: string]: any;
  }