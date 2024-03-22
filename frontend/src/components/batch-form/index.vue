<script setup lang="ts">
import { FormInst, NScrollbar } from 'naive-ui'
import { ref, unref, watchEffect } from 'vue'
import { VueDraggable } from 'vue-draggable-plus'
import type { FormItemModel, FormMode } from './types'
import { useI18n } from '/@/hooks/use-i18n'

const { t } = useI18n()

const props = withDefaults(
  defineProps<{
    models: FormItemModel[]
    formMode: FormMode
    addText: string
    maxHeight?: string
    defaultVals?: any[] // 当外层是编辑状态时，可传入已填充的数据
    isShowDrag?: boolean // 是否可以拖拽
    formWidth?: string // 自定义表单区域宽度
    showEnable?: boolean // 是否显示启用禁用switch状态
    hideAdd?: boolean // 是否隐藏添加按钮
  }>(),
  {
    maxHeight: '30vh',
    isShowDrag: false,
    hideAdd: false,
    showEnable: false,
  },
)
const emit = defineEmits(['change'])
const formRef = ref<FormInst | null>(null)
const formItem: Record<string, any> = {}
const defaultForm = {
  list: [] as Record<string, string>[],
}
const form = ref<Record<string, any>>({ list: [...defaultForm.list] })
function getFormResult() {
  return unref<Record<string, any>[]>(form.value.list)
}
const formValidate = (
  cb: (res?: Record<string, any>[]) => void,
  isSubmit = true,
) => {
  formRef.value?.validate((errors) => {
    if (errors) {
      return
    }
    if (typeof cb === 'function') {
      if (isSubmit) {
        cb(getFormResult())
        return
      }
      cb()
    }
  })
}
const addField = () => {
  const item = [{ ...formItem }]
  item[0].type = []
  formValidate(() => {
    form.value.list.push(item[0]) // 序号自增，不会因为删除而重复
  }, false)
}
function removeField(i: number) {
  form.value.list.splice(i, 1)
}
watchEffect(() => {
  props.models.forEach((item) => {
    let value: string | number | boolean | string[] | number[] | undefined
    formItem[item.filed] = value
    if (props.showEnable) {
      // 如果有开启关闭状态，将默认禁用
      formItem.enable = false
    }
    // 默认填充表单项的子项
    item.children?.forEach((child) => {
      formItem[child.filed] =
        child.type === 'inputNumber' ? null : child.defaultValue
    })
    form.value.list = [{ ...formItem }]
    if (props.defaultVals?.length) {
      // 取出defaultVals的表单 filed
      form.value.list = props.defaultVals.map((e) => e)
    }
  })
})
defineExpose({
  formValidate,
  // getFormResult,
  // resetForm,
  // setFields,
})
</script>
<template>
  <n-form ref="formRef" :model="form">
    <div
      class="mb-[16px] overflow-y-auto rounded-[4px] p-[12px]"
      :style="{ width: props.formWidth || '100%' }"
    >
      <n-scrollbar
        class="overflow-y-auto"
        :style="{ 'max-height': props.maxHeight }"
      >
        <VueDraggable
          v-model="form.list"
          ghost-class="ghost"
          drag-class="dragChosenClass"
          :disabled="!props.isShowDrag"
          :force-fallback="true"
          :animation="150"
          handle=".dragIcon"
        >
          <div
            v-for="(element, index) in form.list"
            :key="`${element.filed}${index}`"
            class="draggableElement gap-[8px] py-[6px] pr-[8px]"
            :class="[props.isShowDrag ? 'cursor-move' : '']"
          >
            <div
              v-if="props.isShowDrag"
              class="dragIcon ml-[8px] mr-[8px] pt-[8px]"
            >
              <n-icon class="block text-[16px]">
                <div class="i-mdi:drag-variant" />
              </n-icon>
            </div>
            <n-form-item
              v-for="model of props.models"
              :key="`${model.filed}${index}`"
              :label="index === 0 && model.label ? t(model.label) : ''"
              :path="model.filed"
            >
              <n-input
                v-if="model.type === 'input'"
                v-model:value="element[model.filed]"
                :placeholder="t(model.placeholder || '')"
                class="flex-1"
                clearable
              />
            </n-form-item>
            <div v-if="showEnable">
              <n-switch
                class="mt-[8px]"
                :style="{
                  'margin-top': index === 0 && !props.isShowDrag ? '36px' : '',
                }"
              />
            </div>
            <div
              v-if="!props.hideAdd"
              v-show="form.list.length > 1"
              class="minus"
              :class="[
                'flex',
                'h-full',
                'w-[32px]',
                'cursor-pointer',
                'items-center',
                'justify-center',
                'mt-[8px]',
              ]"
              :style="{
                'margin-top': index === 0 && !props.isShowDrag ? '36px' : '',
              }"
              @click="removeField(index)"
            >
              <n-icon :size="20">
                <div class="i-mdi:minus-circle-outline" />
              </n-icon>
            </div>
          </div>
        </VueDraggable>
      </n-scrollbar>

      <div v-if="props.formMode === 'create'" class="w-full">
        <n-button class="px-0" text @click="addField">
          <template #icon>
            <n-icon>
              <div class="i-mdi:plus-circle-outline" />
            </n-icon>
          </template>
          {{ t(props.addText) }}
        </n-button>
      </div>
    </div>
  </n-form>
</template>

<style scoped>
.draggableElement {
  @apply flex w-full items-start justify-between rounded;
}
.dragChosenClass {
  background: white;
  opacity: 1 !important;
  @apply rounded;
  .minus {
    margin: 0 !important;
  }
}
.ghost {
  /* border: 1px dashed rgba(var(--primary-5)); */
  /* background-color: rgba(var(--primary-1)); */
  @apply rounded;
}
</style>
