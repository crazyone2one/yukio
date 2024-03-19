import { NIcon } from 'naive-ui'
import { h } from 'vue'

export const renderIcon = (icon: string) => {
  return () => h(NIcon, {}, { default: () => h('div', { class: icon }) })
}
