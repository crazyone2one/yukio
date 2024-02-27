<script setup lang="ts">
import { NTag } from 'naive-ui'
import { useAttrs } from 'vue'

export type TagType = 'default' | 'primary' | 'error' | 'warning' | 'success' | 'info'
export type Size = 'small' | 'medium' | 'large'
const attrs = useAttrs()
console.log(`output->attrs`, attrs)
const props = withDefaults(
    defineProps<{
        type?: TagType // tag类型
        size?: Size // tag尺寸
        noMargin?: boolean // tag之间是否有间距
        closable?: boolean // 是否可关闭
    }>(),
    {
        type: 'default',
        size: 'small',
        noMargin: false,
    },
)
const emit = defineEmits<{
    (e: 'close'): void
}>()
</script>
<template>
    <n-tag v-bind="attrs" :bordered="false" :size="props.size" @close="emit('close')">
        <slot name="icon"></slot>
        <div class="one-line-text">
            <slot></slot>
        </div>
    </n-tag>
</template>

<style scoped></style>
