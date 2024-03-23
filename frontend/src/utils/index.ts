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
