<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { ref } from 'vue'
import { VueDraggable } from 'vue-draggable-plus'
import { FormItemModel, FormMode } from './types'
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
    },
)
const defaultForm = {
    list: [] as Record<string, any>[],
}
const form = ref<Record<string, any>>({ list: [...defaultForm.list] })
</script>
<template>
    <n-form ref="formRef" :model="form" label-placement="left" label-width="auto">
        <div class="mb-[16px] overflow-y-auto rounded-[4px] bg-[var(--color-fill-1)] p-[12px]">
            <VueDraggable
                v-model="form.list"
                ghost-class="ghost"
                drag-class="dragChosenClass"
                :disabled="!props.isShowDrag"
                :force-fallback="true"
            >
                <div
                    v-for="(element, index) in form.list"
                    :key="`${element.filed}${index}`"
                    class="draggableElement gap-[8px] py-[6px] pr-[8px]"
                    :class="[props.isShowDrag ? 'cursor-move' : '']"
                >
                    <div v-if="props.isShowDrag" class="ml-[8px] mr-[8px] pt-[8px]">xx</div>
                    <n-form-item v-for="model of props.models" :key="`${model.filed}${index}`">
                        <template #label>
                            <div class="inline-flex flex-row">
                                <div
                                    v-if="model.hasRedStar"
                                    class="ml-[2px] flex items-center"
                                ></div>
                            </div>
                        </template>
                        <n-input
                            v-if="model.type === 'input'"
                            v-model:value="element[model.filed]"
                            :placeholder="t(model.placeholder || '')"
                            :max-length="model.maxLength || 255"
                            allow-clear
                        />
                    </n-form-item>
                </div>
            </VueDraggable>
        </div>
    </n-form>
</template>

<style scoped></style>
