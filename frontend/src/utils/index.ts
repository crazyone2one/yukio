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
