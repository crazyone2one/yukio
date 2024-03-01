<script setup lang="ts">
import { FormInst } from 'naive-ui'
import { computed, ref, watch } from 'vue'
import { useI18n } from '/@/hooks/use-i18n'

interface FieldConfig {
    field?: string
    placeholder?: string
    maxLength?: number
    isTextArea?: boolean
    nameExistTipText?: string // 添加重复提示文本
}
const props = withDefaults(
    defineProps<{
        title: string // 文本提示标题
        subTitleTip?: string // 子内容提示
        isDelete?: boolean // 当前使用是否是移除
        loading?: boolean
        okText?: string // 确定按钮文本
        cancelText?: string
        visible?: boolean // 是否打开
        fieldConfig?: FieldConfig // 表单配置项
        allNames?: string[] // 添加或者重命名名称重复
    }>(),
    {
        type: 'warning',
        isDelete: true, // 默认移除pop
        okText: 'common.remove',
        cancelText: 'common.cancel',
        subTitleTip: '',
        allNames: () => [],
    },
)
const { t } = useI18n()
const emits = defineEmits<{
    (e: 'confirm', formValue?: { field: string }, cancel?: () => void): void
    (e: 'cancel'): void
    (e: 'update:visible', visible: boolean): void
}>()
const formRef = ref<FormInst | null>(null)
// 表单
const form = ref({
    field: props.fieldConfig?.field || '',
})
const currentVisible = ref(props.visible || false)
// 重置
const reset = () => {
    form.value.field = ''
    formRef.value?.restoreValidation()
}
const handleCancel = () => {
    currentVisible.value = false
    emits('cancel')
    reset()
}
// 获取当前标题的样式
const titleClass = computed(() => {
    return props.isDelete
        ? 'ml-2 font-medium text-[var(--color-text-1)] text-[14px]'
        : 'mb-[8px] font-medium text-[var(--color-text-1)] text-[14px] leading-[22px]'
})
watch(
    () => props.fieldConfig?.field,
    (val) => {
        form.value.field = val || ''
    },
)
watch(
    () => props.visible,
    (val) => {
        currentVisible.value = val
    },
)

watch(
    () => currentVisible.value,
    (val) => {
        if (!val) {
            emits('cancel')
        }
        emits('update:visible', val)
    },
)
</script>
<template>
    <n-popconfirm v-model:show="currentVisible" :class="props.isDelete ? 'w-[352px]' : ''">
        <!-- <template #icon>
            <slot name="icon">
                <n-icon v-if="props.isDelete" :size="18">
                    <span class="i-tabler:alert-triangle" />
                </n-icon>
            </slot>
        </template> -->
        <template #trigger>
            <n-button text class="!mr-0 p-[2px]">
                <template #icon>
                    <n-icon :size="18">
                        <span class="i-tabler:square-rounded-plus-filled" />
                    </n-icon>
                </template>
            </n-button>
        </template>
        <div class="flex flex-row flex-nowrap items-center">
            <div :class="[titleClass]">
                {{ props.title || '' }}
            </div>
            <!-- 描述展示 -->
            <div v-if="props.subTitleTip" class="ml-8 mt-2 text-sm text-[var(--color-text-2)]">
                {{ props.subTitleTip }}
            </div>
            <!-- 表单展示 -->
            <n-form v-else ref="formRef" :model="form">
                <n-form-item>
                    <n-input
                        v-if="props.fieldConfig?.isTextArea"
                        v-model:value="form.field"
                        :maxlength="props.fieldConfig?.maxLength || 1000"
                        :placeholder="props.fieldConfig?.placeholder"
                        class="w-[245px]"
                    />
                    <n-input
                        v-else
                        v-model:value="form.field"
                        :maxlength="255"
                        :placeholder="props.fieldConfig?.placeholder"
                        class="w-[245px]"
                    />
                </n-form-item>
            </n-form>
        </div>
        <template #action>
            <n-button size="tiny" @click="handleCancel">
                {{ t(props.cancelText) || t('common.cancel') }}
            </n-button>
            <n-button type="primary" size="tiny">
                {{ t(props.okText) || t('common.confirm') }}
            </n-button>
        </template>
    </n-popconfirm>
</template>
<style scoped></style>
