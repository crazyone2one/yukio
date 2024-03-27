<script setup lang="ts">
import { FormInst, FormItemRule, FormRules } from 'naive-ui'
import { computed, ref, useAttrs, watch } from 'vue'
import { useI18n } from '/@/hooks/use-i18n'
export type types = 'error' | 'info' | 'success' | 'warning'
interface FieldConfig {
  field?: string
  rules?: FormRules[]
  placeholder?: string
  maxLength?: number
  isTextArea?: boolean
  nameExistTipText?: string // 添加重复提示文本
}
const props = withDefaults(
  defineProps<{
    title: string // 文本提示标题
    subTitleTip?: string // 子内容提示
    type?: types
    isDelete?: boolean // 当前使用是否是移除
    loading?: boolean
    okText?: string // 确定按钮文本
    cancelText?: string
    visible?: boolean // 是否打开
    popupContainer?: string
    fieldConfig?: FieldConfig // 表单配置项
    allNames?: string[] // 添加或者重命名名称重复
  }>(),
  {
    type: 'warning',
    isDelete: true, // 默认移除pop
    okText: 'common.remove',
  },
)
const emits = defineEmits<{
  (e: 'confirm', formValue?: { field: string }, cancel?: () => void): void
  (e: 'cancel'): void
  (e: 'update:visible', visible: boolean): void
}>()
const { t } = useI18n()
const currentVisible = ref(props.visible || false)
const attrs = useAttrs()
const formRef = ref<FormInst>()
const isPass = ref(false)
const setValidateResult = (isValidatePass: boolean) => {
  isPass.value = isValidatePass
}

// 表单
const form = ref({
  field: props.fieldConfig?.field || '',
})
const rules = props.fieldConfig?.rules || {
  field: [
    {
      required: true,
      message: t('popConfirm.nameNotNull'),
      trigger: ['input', 'blur'],
    },
    {
      required: true,
      validator(rule: FormItemRule, value: string) {
        if ((props.allNames || []).includes(value)) {
          if (props.fieldConfig && props.fieldConfig.nameExistTipText) {
            return new Error(t(props.fieldConfig.nameExistTipText))
          } else {
            return new Error(t('popConfirm.nameExist'))
          }
        }
        return true
      },
      trigger: ['input', 'blur'],
    },
  ],
}
// 校验表单
const validateForm = async () => {
  await formRef.value?.validate((errors) => {
    if (!errors) {
      setValidateResult(true)
    } else {
      setValidateResult(false)
    }
  })
}
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
const handleConfirm = async () => {
  await validateForm()
  if (props.isDelete) {
    emits('confirm', undefined, handleCancel)
  } else {
    emits('confirm', form.value, handleCancel)
  }
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
defineExpose({
  form,
  isPass,
})
</script>
<template>
  <n-popover
    v-model:show="currentVisible"
    trigger="click"
    v-bind="attrs"
    :placement="props.isDelete ? 'right-end' : 'bottom'"
  >
    <template #trigger>
      <slot name="trigger" />
    </template>
    <div class="flex flex-row flex-nowrap items-center">
      <slot name="icon"> </slot>
      <div :class="[titleClass]">
        {{ props.title || '' }}
      </div>
    </div>
    <div
      v-if="props.subTitleTip"
      class="ml-8 mt-2 text-sm text-[var(--color-text-2)]"
    >
      {{ props.subTitleTip }}
    </div>
    <n-form ref="formRef" :model="form" :rules="rules">
      <n-form-item path="field">
        <n-input
          v-if="props.fieldConfig?.isTextArea"
          v-model:value="form.field"
          type="textarea"
          :placeholder="props.fieldConfig?.placeholder"
        />
        <n-input
          v-else
          v-model:value="form.field"
          :placeholder="props.fieldConfig?.placeholder"
          :maxlength="255"
          class="w-[245px]"
          @press-enter="handleConfirm"
        />
      </n-form-item>
    </n-form>
    <div class="mb-1 mt-4 flex flex-row flex-nowrap justify-end gap-2">
      <n-button
        secondary
        size="tiny"
        :disabled="props.loading"
        @click="handleCancel"
      >
        {{ props.cancelText || $t('common.cancel') }}
      </n-button>
      <n-button
        type="primary"
        size="tiny"
        :loading="props.loading"
        @click="handleConfirm"
      >
        {{ $t(props.okText) || $t('common.confirm') }}
      </n-button>
    </div>
    <slot></slot>
  </n-popover>
</template>

<style scoped></style>
