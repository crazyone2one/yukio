<script setup lang="ts">
interface IProps {
    maskClosable?: boolean
    title?: string
    loading?: boolean
}
const props = withDefaults(defineProps<IProps>(), {
    maskClosable: false,
})
const currentVisible = defineModel<boolean>('visible', {
    default: false,
})
const emit = defineEmits<{ (e: 'cancel', shouldSearch: boolean): void }>()
const handleCancel = (shouldSearch = false) => {
    emit('cancel', shouldSearch)
}
</script>

<template>
    <n-modal
        v-model:show="currentVisible"
        preset="dialog"
        transform-origin="center"
        :mask-closable="props.maskClosable"
        :style="{ width: '750px' }"
        :title="title"
        :on-close="handleCancel"
        :loading="loading"
    >
        <template #header>
            <slot name="header" />
        </template>
        <slot></slot>
        <template #action>
            <slot name="action" />
        </template>
    </n-modal>
</template>

<style scoped></style>
