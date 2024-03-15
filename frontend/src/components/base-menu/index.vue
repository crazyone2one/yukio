<script setup lang="ts">
import { MenuOption, NLayoutSider, NMenu } from 'naive-ui'
import { computed } from 'vue'
import useAppStore from '/@/store/modules/app'
import { renderIcon } from '/@/utils'
const appStore = useAppStore()
const collapsed = computed({
  get() {
    if (appStore.device === 'desktop') return appStore.menuCollapse
    return false
  },
  set(value: boolean) {
    appStore.updateSettings({ menuCollapse: value })
  },
})
// const renderIcon = (icon: string) => {
//   return () => h(NIcon, {}, { default: () => h('span', { class: icon }) })
// }
const menuOptions: MenuOption[] = [
  {
    label: '1973年的弹珠玩具',
    key: 'pinball-1973',
    disabled: true,
    icon: renderIcon('i-solar:alarm-add-bold'),
    children: [
      {
        label: '鼠',
        key: 'rat',
      },
    ],
  },
  {
    label: '舞，舞，舞',
    key: 'dance-dance-dance',
    icon: renderIcon('i-solar:alarm-turn-off-line-duotone'),
    children: [
      {
        type: 'group',
        label: '人物',
        key: 'people',
        children: [
          {
            label: '叙事者',
            key: 'narrator',
            icon: renderIcon('i-solar:alarm-turn-off-line-duotone'),
          },
          {
            label: '羊男',
            key: 'sheep-man',
            icon: renderIcon('i-solar:alarm-turn-off-line-duotone'),
          },
        ],
      },
      {
        label: '饮品',
        key: 'beverage',
        icon: renderIcon('i-solar:alarm-turn-off-line-duotone'),
        children: [
          {
            label: '威士忌',
            key: 'whisky',
          },
        ],
      },
      {
        label: '食物',
        key: 'food',
        children: [
          {
            label: '三明治',
            key: 'sandwich',
          },
        ],
      },
      {
        label: '过去增多，未来减少',
        key: 'the-past-increases-the-future-recedes',
      },
    ],
  },
]
</script>
<template>
  <n-layout-sider
    collapse-mode="width"
    :native-scrollbar="false"
    bordered
    :collapsed-width="appStore.collapsedWidth"
    :width="appStore.menuWidth"
    :collapsed="collapsed"
    show-trigger
    @collapse="collapsed = true"
    @expand="collapsed = false"
  >
    <n-menu
      :collapsed="collapsed"
      :collapsed-width="appStore.collapsedWidth"
      :collapsed-icon-size="appStore.collapsedIconSize"
      :options="menuOptions"
      accordion
    />
  </n-layout-sider>
</template>

<style scoped></style>
