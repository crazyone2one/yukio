<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { ref, unref, watchEffect } from 'vue'
import { VueDraggable } from 'vue-draggable-plus'
import { FormItemModel, FormMode } from './types'
import { useI18n } from '/@/hooks/use-i18n'
import { FormInst, FormRules } from 'naive-ui'

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
        rules?: FormRules
    }>(),
    {
        maxHeight: '30vh',
        isShowDrag: false,
        hideAdd: false,
    },
)
const emit = defineEmits(['change'])
const formRef = ref<FormInst | null>(null)
const defaultForm = {
    list: [] as Record<string, any>[],
}
const form = ref<Record<string, any>>({ list: [...defaultForm.list] })
const formItem: Record<string, any> = {}
const getFormResult = () => {
    return unref<Record<string, any>[]>(form.value.list)
}
const formValidate = (cb: (res?: Record<string, any>[]) => void, isSubmit = true) => {
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
watchEffect(() => {
    props.models.forEach((item) => {
        // 默认填充表单项
        let value: string | number | boolean | string[] | number[] | undefined
        if (item.type === 'inputNumber') {
            value = undefined
        } else if (item.type === 'tagInput') {
            value = []
        } else {
            value = item.defaultValue
        }
        formItem[item.filed] = value
        if (props.showEnable) {
            // 如果有开启关闭状态，将默认禁用
            formItem.enable = false
        }
        // 默认填充表单项的子项
        item.children?.forEach((child) => {
            formItem[child.filed] = child.type === 'inputNumber' ? null : child.defaultValue
        })
    })
    form.value.list = [{ ...formItem }]
    if (props.defaultVals?.length) {
        // 取出defaultVals的表单 filed
        form.value.list = props.defaultVals.map((e) => e)
    }
})
defineExpose({
    formValidate,
    // getFormResult,
    // resetForm,
    // setFields,
})
</script>
<template>
    <n-form
        ref="formRef"
        :model="form"
        :rules="props.rules"
        label-placement="left"
        label-width="auto"
    >
        <div
            class="mb-[16px] overflow-y-auto rounded-[4px] bg-[var(--color-fill-1)] p-[12px]"
            :style="{ width: props.formWidth || '100%' }"
        >
            <VueDraggable
                v-model="form.list"
                ghost-class="ghost"
                drag-class="dragChosenClass"
                :disabled="!props.isShowDrag"
                :force-fallback="true"
                class="rounded"
            >
                <div
                    v-for="(element, index) in form.list"
                    :key="`${element.filed}${index}`"
                    class="flex w-full items-start justify-between rounded gap-[8px] py-[6px] pr-[8px]"
                    :class="[props.isShowDrag ? 'cursor-move' : '']"
                >
                    <div v-if="props.isShowDrag" class="ml-[8px] mr-[8px] pt-[8px]">xx</div>
                    <n-form-item
                        v-for="model of props.models"
                        :key="`${model.filed}${index}`"
                        :path="`${model.filed}`"
                    >
                        <template #label>
                            <div class="inline-flex flex-row">
                                <div>{{ index === 0 && model.label ? t(model.label) : '' }}</div>
                            </div>
                        </template>
                        <n-input
                            v-if="model.type === 'input'"
                            v-model:value="element[model.filed]"
                            :placeholder="t(model.placeholder || '')"
                            :max-length="model.maxLength || 255"
                            allow-clear
                            @change="emit('change')"
                        />
                    </n-form-item>
                </div>
            </VueDraggable>
        </div>
    </n-form>
</template>

<style scoped>
.ghost {
    border: 1px dashed rgba(var(--primary-5));
    background-color: rgba(var(--primary-1));
}
.dragChosenClass {
    background: white;
    opacity: 1 !important;

    .minus {
        margin: 0 !important;
    }
}
</style>
