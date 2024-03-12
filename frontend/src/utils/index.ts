import { TreeOption } from 'naive-ui'

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
 * 当前日期是本年第几周
 * @returns
 */
export function getCurrentWeek() {
    const currentDate = new Date()
    const firstDayOfYear = new Date(currentDate.getFullYear(), 0, 1)
    const daysBetween = (currentDate.valueOf() - firstDayOfYear.valueOf()) / 86400000
    return Math.ceil(daysBetween / 7)
}

export const mapTree = (
    tree: TreeOption | TreeOption[],
    customNodeFn: (node: TreeOption, path: string) => TreeOption | null = (node) => node,
    customChildrenKey: string = 'children',
    parentPath = '',
) => {
    if (!Array.isArray(tree)) {
        tree = [tree]
    }
    return tree
        .map((node: TreeOption) => {
            const fullPath = node.path ? `${parentPath}/${node.path}`.replace(/\/+/g, '/') : ''
            const newNode = typeof customNodeFn === 'function' ? customNodeFn(node, fullPath) : node
            if (newNode && newNode[customChildrenKey] && newNode[customChildrenKey].length > 0) {
                newNode[customChildrenKey] = mapTree(
                    newNode[customChildrenKey],
                    customNodeFn,
                    customChildrenKey,
                )
            }

            return newNode
        })
        .filter(Boolean)
}
/**
 * 生成 id 序列号
 * @returns
 */
export const getGenerateId = () => {
    const timestamp = new Date().getTime().toString()
    const randomDigits = Math.floor(Math.random() * 10000)
        .toString()
        .padStart(4, '0')
    const generateId = timestamp + randomDigits
    return generateId.substring(0, 16)
}
