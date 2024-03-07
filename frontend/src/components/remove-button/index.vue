<script setup lang="ts">
import { NPopconfirm } from 'naive-ui'
import { useI18n } from '/@/hooks/use-i18n'
const props = withDefaults(
    defineProps<{
        title: string
        subTitleTip: string
        loading?: boolean
        removeText?: string
        okText?: string
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

const { t } = useI18n()
const handlePositiveClick = () => {
    emit('ok')
}
const handleNegativeClick = () => {
    window.$message.info('handleNegativeClick')
}
</script>

<template>
    <n-popconfirm @positive-click="handlePositiveClick" @negative-click="handleNegativeClick">
        <template #trigger>
            <n-button :disabled="props.disabled" size="tiny">{{ t(props.removeText) }}</n-button>
        </template>
        <div class="flex flex-row flex-nowrap items-center">
            {{ props.title }} {{ props.subTitleTip }}
        </div>
    </n-popconfirm>
</template>

<style scoped></style>
