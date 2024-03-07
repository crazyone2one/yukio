<script setup lang="ts">
import { NDrawer, NDrawerContent, NTag } from 'naive-ui'
import { ref, watch } from 'vue'
import { characterLimit } from '../../utils'
import { useI18n } from '/@/hooks/use-i18n.ts'
interface DrawerProps {
    visible: boolean
    width: string | number // 抽屉宽度，为数值时才可拖拽改变宽度
    title?: string | undefined
    titleTag?: string
    titleTagColor?: string
    showFullScreen?: boolean // 是否显示全屏按钮
    okText?: string
    cancelText?: string
    saveContinueText?: string
    showContinue?: boolean
    mask?: boolean // 是否显示遮罩
}
const props = withDefaults(defineProps<DrawerProps>(), {
    showFullScreen: false,
    mask: true,
})
const { t } = useI18n()
const visible = ref(props.visible)
const fullScreen = ref()
const drawerWidth = ref(props.width) // 抽屉初始宽度
const emit = defineEmits(['update:visible', 'confirm', 'cancel', 'continue'])
watch(
    () => props.visible,
    (val) => {
        visible.value = val
    },
)
const handleCancel = () => {
    visible.value = false
    emit('update:visible', false)
    emit('cancel')
}
</script>

<template>
    <n-drawer
        v-bind="props"
        v-model:show="visible"
        :width="fullScreen?.isFullScreen ? '100%' : drawerWidth"
        :on-update-show="handleCancel"
        :mask-closable="mask"
    >
        <n-drawer-content>
            <template #header>
                <div class="flex items-center justify-between gap-[4px]">
                    <slot name="title">
                        <div class="flex flex-1 items-center justify-between">
                            <div class="flex items-center">
                                <n-tooltip>
                                    <template #trigger>
                                        <span> {{ characterLimit(props.title) }}</span>
                                    </template>
                                    {{ props.title }}
                                </n-tooltip>
                                <slot name="headerLeft"></slot>
                                <n-tag
                                    v-if="titleTag"
                                    :color="props.titleTagColor"
                                    class="ml-[8px] mr-auto"
                                >
                                    {{ props.titleTag }}
                                </n-tag>
                            </div>
                            <slot name="tbutton"></slot>
                        </div>
                    </slot>
                    <div>
                        <n-button
                            v-if="props.showFullScreen"
                            size="tiny"
                            @click="fullScreen?.toggleFullScreen"
                        >
                            <template #icon>
                                <n-icon>
                                    <span
                                        :class="
                                            fullScreen?.isFullScreen
                                                ? 'i-tabler:capture-off'
                                                : 'i-tabler:capture'
                                        "
                                    />
                                </n-icon>
                            </template>
                            {{
                                fullScreen?.isFullScreen
                                    ? t('common.offFullScreen')
                                    : t('common.fullScreen')
                            }}
                        </n-button>
                    </div>
                </div>
            </template>
            <slot>xx</slot>
            <template #footer>
                <slot name="footer">
                    <div class="flex items-center justify-between">
                        <slot name="footerLeft"></slot>
                        <div class="ml-auto flex gap-[12px]">
                            <n-button @click="handleCancel">
                                {{ t(props.cancelText || 'ms.drawer.cancel') }}
                            </n-button>
                            <n-button v-if="showContinue">
                                {{ t(props.saveContinueText || 'ms.drawer.saveContinue') }}
                            </n-button>
                            <n-button type="primary">
                                {{ t(props.okText || 'ms.drawer.ok') }}
                            </n-button>
                        </div>
                    </div>
                </slot>
            </template>
        </n-drawer-content>
    </n-drawer>
</template>

<style scoped></style>
