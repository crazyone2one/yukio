<script setup lang="ts">
import { useAttrs } from 'vue'
import BaseTag, { Size } from '/@/components/base-tag/index.vue'
const props = withDefaults(
  defineProps<{
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    tagList: Array<any>
    nameKey?: string
    isStringTag?: boolean // 是否是字符串数组的标签
    size?: Size
  }>(),
  {
    nameKey: 'name',
    size: 'medium',
  },
)
const attrs = useAttrs()
</script>
<template>
  <n-space>
    <base-tag
      v-for="tag of tagList"
      :key="tag.id"
      :size="props.size"
      v-bind="attrs"
    >
      {{ props.isStringTag ? tag : tag[props.nameKey] }}
    </base-tag>
  </n-space>
</template>

<style scoped></style>
