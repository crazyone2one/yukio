<script setup lang="ts">
import { MsFileItem } from '/@/components/base-upload/types.ts'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { computed, ref, watch } from 'vue'
import { UploadStatus } from '/@/enums/upload-enum.ts'

const props = withDefaults(
    defineProps<{
        mode?: 'static' | 'remote' // 静态|远程
        fileList: MsFileItem[]
        showMode?: 'fileList' | 'imageList' // 展示模式, 文件列表|图片列表
        uploadFunc?: (params: any) => Promise<any> // 上传文件时，自定义上传方法
        requestParams?: Record<string, any> // 上传文件时，额外的请求参数
        route?: string // 用于后台上传文件时，查看详情跳转的路由
        routeQuery?: Record<string, string> // 用于后台上传文件时，查看详情跳转的路由参数
        showTab?: boolean // 是否显示tab
        handleDelete?: (item: MsFileItem) => void
        handleReUpload?: (item: MsFileItem) => void
        showDelete?: boolean // 是否展示删除按钮
        handleView?: (item: MsFileItem) => void // 是否自定义预览
        showUploadTypeDesc?: boolean // 自定义上传类型关联于&上传于
    }>(),
    {
        mode: 'remote',
        showTab: true,
        showDelete: true,
        showMode: 'fileList',
        boolean: false,
        showUploadTypeDesc: false,
    },
)
const { t } = useI18n()
const fileListTab = ref('all')
const innerFileList = ref<MsFileItem[]>(props.fileList)
const emit = defineEmits<{
    (e: 'update:fileList', fileList: MsFileItem[]): void
    (e: 'delete', item: MsFileItem): void
    (e: 'finish'): void
    (e: 'start'): void
}>()
const totalWaitingFileList = computed(() => {
    return innerFileList.value.filter(
        (e) => e.status && (e.status === UploadStatus.init || e.status === UploadStatus.uploading),
    )
})
const totalSuccessFileList = computed(() => {
    return innerFileList.value.filter((e) => e.status && e.status === UploadStatus.done)
})
const totalFailFileList = computed(() => {
    return innerFileList.value.filter((e) => e.status && e.status === UploadStatus.error)
})
const filterFileList = computed(() => {
    switch (fileListTab.value) {
        case 'waiting':
            return totalWaitingFileList.value
        case 'success':
            return totalSuccessFileList.value
        case 'error':
            return totalFailFileList.value
        default:
            return innerFileList.value
    }
})
watch(
    () => props.fileList,
    (val) => {
        innerFileList.value = val.sort((a, b) => {
            if (a.status === UploadStatus.init && b.status !== UploadStatus.init) {
                return -1 // "init" 排在前面
            }
            if (a.status !== UploadStatus.init && b.status === UploadStatus.init) {
                return 1 // "init" 排在前面
            }
            return 0 // 保持原始顺序
        })
    },
)

watch(
    () => innerFileList.value,
    (val) => {
        emit('update:fileList', val)
    },
)
</script>

<template>
    <div>
        <div
            v-if="props.mode === 'remote' && props.showTab"
            class="sticky top-[0] z-[9999] mb-[8px] flex justify-between bg-white"
        >
            <n-radio-group v-model:value="fileListTab" name="radiogroup">
                <n-space>
                    <n-radio value="all">
                        {{ `${t('ms.upload.all')} (${innerFileList.length})` }}
                    </n-radio>
                    <n-radio value="waiting">
                        {{ `${t('ms.upload.uploading')} (${totalWaitingFileList.length})` }}
                    </n-radio>
                    <n-radio value="success">
                        {{ `${t('ms.upload.success')} (${totalSuccessFileList.length})` }}
                    </n-radio>
                    <n-radio value="error">
                        {{ `${t('ms.upload.fail')} (${totalFailFileList.length})` }}
                    </n-radio>
                </n-space>
            </n-radio-group>
            <slot name="tabExtra"></slot>
        </div>
    </div>
</template>

<style scoped></style>
