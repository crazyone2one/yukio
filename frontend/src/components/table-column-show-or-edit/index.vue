<script setup lang="ts">
import { NInput } from 'naive-ui'
import { nextTick, ref } from 'vue'

const props = defineProps<{
  value: string
}>()
const emit = defineEmits(['update-value'])
const isEdit = ref(false)
const inputRef = ref<InstanceType<typeof NInput> | null>(null)
const inputValue = ref(props.value)
const handleChange = () => {
  emit('update-value', inputValue.value)
  isEdit.value = false
}
const handleOnClick = () => {
  isEdit.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}
</script>
<template>
  <div style="min-height: 22px" @click="handleOnClick">
    <n-input
      v-if="isEdit"
      ref="inputRef"
      v-model:value="inputValue"
      @blur="handleChange"
    />
    <span v-else>{{ props.value }}</span>
  </div>
</template>

<style scoped></style>
