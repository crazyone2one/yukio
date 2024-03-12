<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css' // import css
import { onBeforeUnmount, ref, shallowRef } from 'vue'
import { useDebounceFn } from '@vueuse/core'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { IDomEditor } from '@wangeditor/editor'

const props = withDefaults(
    defineProps<{
        raw?: string
        uploadImage?: (file: File) => Promise<any>
        maxHeight?: string
        filedIds?: string[]
        commentIds?: string[]
        wrapperClass?: string
        placeholder?: string
    }>(),
    {
        raw: '',
        uploadImage: undefined,
        placeholder: 'editor.placeholder',
    },
)
const { t } = useI18n()
const editorRef = shallowRef<IDomEditor | undefined>(undefined)
const valueHtml = ref(props.raw)
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
const emit = defineEmits<{
    (event: 'update:raw', value: string): void
    (event: 'update:filedIds', value: string[]): void
    (event: 'update', value: string): void
    (event: 'update:commentIds', value: string): void
}>()
const handleCreated = (editor: IDomEditor) => {
    editorRef.value = editor // 记录 editor 实例，重要！
}
const handleChange = () => {
    debounceOnUpdate()
}
const debounceOnUpdate = useDebounceFn(() => {
    const html = `${editorRef.value?.getHtml()}`
    emit('update:raw', html)
    emit('update', html)
}, 250)
// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
})
</script>

<template>
    <div style="border: 1px solid #ccc">
        <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="editorRef"
            :default-config="toolbarConfig"
            mode="default"
        />
        <Editor
            v-model="valueHtml"
            :default-config="editorConfig"
            mode="default"
            @on-created="handleCreated"
            @onChange="handleChange"
        />
    </div>
</template>

<style>
.w-e-text-container {
    height: 150px !important;
}
</style>
