<script setup lang="ts">
import { NDynamicTags } from 'naive-ui'
import { computed, ref, watch } from 'vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
const props = withDefaults(
    defineProps<{
        modelValue: string[] | Array<{ label: string; value: string }>
        maxTagCount?: number
        maxLength?: number

        size?: 'small' | 'medium' | 'large'
    }>(),
    {
        maxLength: 64,
        size: 'medium',
    },
)
const emit = defineEmits(['update:modelValue', 'update:inputValue', 'change', 'clear'])
const tags = ref(props.modelValue)
const tagsLength = ref(0)
const { t } = useI18n()
watch(
    () => props.modelValue,
    (val) => {
        tags.value = val
        tagsLength.value = val.length
    },
)
watch(
    () => tags.value,
    (val) => {
        if (val.length < tagsLength.value) {
            // 输入框内标签长度变化且比记录的 tagsLength 小，说明是删除标签，此时需要同步 tagsLength 与标签长度
            tagsLength.value = val.length
        }
        emit('update:modelValue', val)
        emit('change', val)
    },
)
const isError = computed(() => {
    return tags.value.some((item) => item.toString().length > props.maxLength)
})
</script>

<template>
    <n-dynamic-tags v-model:value="tags" :max="props.maxTagCount" :size="props.size" />
    <span v-if="isError" class="ml-[1px] text-[12px] text-red-600">
        {{ t('common.tagInputMaxLength', { number: props.maxLength }) }}
    </span>
</template>

<style scoped></style>
