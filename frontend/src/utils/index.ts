/* eslint-disable @typescript-eslint/no-explicit-any */
import { cloneDeep } from 'lodash-es'
import { NIcon } from 'naive-ui'
import { h } from 'vue'

export const renderIcon = (icon: string) => {
  return () => h(NIcon, {}, { default: () => h('div', { class: icon }) })
}
/**
 * 对话框标题动态内容字符限制
 * @param str 标题的动态内容
 * @returns 转化后的字符串
 */
export function characterLimit(str?: string): string {
  if (!str) return ''
  if (str.length <= 20) {
    return str
  }
  return `${str.slice(0, 20 - 3)}...`
}
/**
 * 获取 URL 哈希参数
 * @returns 参数对象
 */
export const getHashParameters = (): Record<string, string> => {
  const query = window.location.hash.split('?')[1] // 获取 URL 哈希参数部分
  const paramsArray = query?.split('&') || [] // 将哈希参数字符串分割成数组
  const params: Record<string, string> = {}

  // 遍历数组并解析参数
  paramsArray.forEach((param) => {
    const [key, value] = param.split('=')
    if (key && value) {
      params[key] = decodeURIComponent(value) // 解码参数值
    }
  })

  return params
}
export interface TreeNode<T> {
  [key: string]: any
  children?: TreeNode<T>[]
}

export function mapTree<T>(
  tree: TreeNode<T> | TreeNode<T>[] | T | T[],
  customNodeFn: (node: TreeNode<T>, path: string) => TreeNode<T> | null = (
    node,
  ) => node,
  customChildrenKey = 'children',
  parentPath = '',
  level = 0,
  parent: TreeNode<T> | null = null,
): T[] {
  let cloneTree = cloneDeep(tree)
  if (!Array.isArray(cloneTree)) {
    cloneTree = [cloneTree]
  }
  function mapFunc(
    _tree: TreeNode<T> | TreeNode<T>[] | T | T[],
    _parentPath = '',
    _level = 0,
    _parent: TreeNode<T> | null = null,
  ): T[] {
    if (!Array.isArray(_tree)) {
      _tree = [_tree]
    }
    return _tree
      .map((node: TreeNode<T>, i: number) => {
        const fullPath = node.path
          ? `${_parentPath}/${node.path}`.replace(/\/+/g, '/')
          : ''
        node.sort = i + 1 // sort 从 1 开始
        node.parent = _parent || undefined // 没有父节点说明是树的第一层
        node.key = node.id
        node.label = node.name
        const newNode =
          typeof customNodeFn === 'function'
            ? customNodeFn(node, fullPath)
            : node
        if (newNode) {
          newNode.level = _level
          if (
            newNode[customChildrenKey] &&
            newNode[customChildrenKey].length > 0
          ) {
            newNode[customChildrenKey] = mapFunc(
              newNode[customChildrenKey],
              fullPath,
              _level + 1,
              node,
            )
          }
        }
        return newNode
      })
      .filter(Boolean)
  }
  return mapFunc(cloneTree, parentPath, level, parent)
}
