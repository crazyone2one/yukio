<script setup lang="ts">
import { DropdownOption } from 'naive-ui'
import { ref, watch } from 'vue'
import { ActionsItem } from './types'
import { useI18n } from '/@/hooks/use-i18n'

const props = defineProps<{
  list: ActionsItem[]
}>()
const { t } = useI18n()
const emit = defineEmits(['select', 'close'])
const options = ref<Array<DropdownOption>>([])
const handleSelect = (value: string | number) => {
  const item = props.list.find((e: ActionsItem) => e.eventTag === value)
  emit('select', item)
}
watch(
  () => props.list,
  (actions) => {
    options.value = []
    actions.forEach((action) => {
      if (action.isDivider) {
        const item = {
          type: 'divider',
        }
        options.value.push(item)
      }
      const item = {
        label: t(action.label as string),
        key: action.eventTag,
      }
      options.value.push(item)
    })
  },
  { immediate: true },
)
</script>
<template>
  <n-dropdown
    :options="options"
    placement="bottom-start"
    trigger="click"
    @select="handleSelect"
  >
    <n-button text>
      <n-icon>
        <div class="i-local-more" />
      </n-icon>
    </n-button>
  </n-dropdown>
</template>

<style scoped></style>
