<script setup lang="ts">
import { NTag } from 'naive-ui'
import { useAttrs } from 'vue'
export type TagType =
  | 'default'
  | 'primary'
  | 'info'
  | 'success'
  | 'warning'
  | 'error'
export type Size = 'small' | 'medium' | 'large'
const attrs = useAttrs()
const props = withDefaults(
  defineProps<{
    type?: TagType // tag类型
    size?: Size // tag尺寸
    bordered?: boolean // 是否显示边框
    disabled?: boolean // 是否禁用
    noMargin?: boolean // tag之间是否有间距
    closable?: boolean // 是否可关闭
  }>(),
  {
    type: 'default',
    size: 'medium',
    noMargin: false,
    bordered: false,
    disabled: false,
  },
)
</script>
<template>
  <n-tag
    v-bind="attrs"
    :type="props.type"
    :size="props.size"
    :bordered="props.bordered"
    :closable="props.closable"
    :disabled="props.disabled"
  >
    <template #icon>
      <slot name="icon"></slot>
    </template>

    <div class="one-line-text">
      <slot></slot>
    </div>
  </n-tag>
</template>

<style scoped></style>
