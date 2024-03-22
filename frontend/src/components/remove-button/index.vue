<script setup lang="ts">
import { NPopconfirm } from 'naive-ui'
import { ref } from 'vue'
const props = withDefaults(
  defineProps<{
    title: string
    loading?: boolean
    removeText?: string
    disabled?: boolean
  }>(),
  {
    removeText: 'common.remove',
    disabled: false,
  },
)
const emit = defineEmits<{
  (e: 'ok'): void
}>()
const currentVisible = ref(false)
const handlePositiveClick = () => {
  emit('ok')
}
const handleNegativeClick = () => {
  currentVisible.value = false
}
const showPopover = () => {
  currentVisible.value = true
}
</script>
<template>
  <n-popconfirm
    :show="currentVisible"
    @positive-click="handlePositiveClick"
    @negative-click="handleNegativeClick"
  >
    <template #trigger>
      <n-button :disabled="props.disabled" size="tiny" @click="showPopover">
        {{ $t(props.removeText) }}
      </n-button>
    </template>
    {{ `${props.title} ` }}
  </n-popconfirm>
</template>

<style scoped></style>
