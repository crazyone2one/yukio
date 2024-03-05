<script setup lang="ts">
import { ref, watch } from 'vue'

export type Direction = 'horizontal' | 'vertical'
const props = withDefaults(
    defineProps<{
        size?: number // 左侧宽度/顶部容器高度。expandDirection为 right 时，size 也是左侧容器宽度，所以想要缩小右侧容器宽度只需要将 size 调大即可
        min?: number
        max?: number
        direction?: Direction
        disabled?: boolean // 是否禁用
    }>(),
    {
        size: 1,
        min: 0.25,
        max: 0.75,
        direction: 'horizontal',
    },
)
const emit = defineEmits(['update:size', 'expandChange'])

const innerSize = ref(props.size || '300px')

watch(
    () => props.size,
    (val) => {
        if (val !== undefined) {
            innerSize.value = val
        }
    },
)

watch(
    () => innerSize.value,
    (val) => {
        emit('update:size', val)
    },
)
</script>
<template>
    <n-split
        v-model:size="innerSize"
        :direction="props.direction"
        :min="props.min"
        :max="props.max"
        class="h-full"
        :disabled="props.disabled"
    >
        <template #1>
            <div
                v-if="props.direction === 'horizontal' && !props.disabled"
                class="absolute right-0 h-full w-[16px]"
            >
                <slot name="first"></slot>
            </div>
        </template>
        <template #2>
            <div>
                <slot name="second"></slot>
            </div>
        </template>
    </n-split>
</template>

<style scoped></style>
