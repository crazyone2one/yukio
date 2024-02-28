<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import useFullScreen from '/@/hooks/use-full-screen'
import { useI18n } from '/@/hooks/use-i18n'
import { useAppStore } from '/@/store'

const props = withDefaults(
    defineProps<
        Partial<{
            loading: boolean // 卡片 loading 状态
            isFullscreen?: boolean // 是否全屏
            autoHeight: boolean // 内容区域高度是否自适应
            noContentPadding: boolean // 内容区域是否有padding
            noBottomRadius?: boolean // 底部是否有圆角
            dividerHasPX: boolean // 分割线是否有左右padding;
            simple: boolean // 简单模式，没有标题和底部栏
            hideDivider?: boolean // 是否隐藏分割线
            minWidth: number // 卡片内容最小宽度
            hideFooter: boolean // 隐藏底部栏
            isEdit: boolean // 是否编辑状态
            hideContinue: boolean // 隐藏保存并继续创建按钮
        }>
    >(),
    {
        autoHeight: false,
        noContentPadding: false,
        noBottomRadius: false,
        dividerHasPX: false,
        hideFooter: false,
        isEdit: false,
        hideContinue: false,
    },
)
const { t } = useI18n()
const appStore = useAppStore()
const collapsedWidth = 86
const fullRef = ref<HTMLElement | null>()
const { isFullScreen } = useFullScreen(fullRef)
const emit = defineEmits(['saveAndContinue', 'save', 'toggleFullScreen'])
const menuWidth = computed(() => {
    return appStore.menuCollapse ? collapsedWidth : appStore.menuWidth
})
watch(
    () => isFullScreen.value,
    (val) => {
        emit('toggleFullScreen', val)
    },
)
</script>
<template>
    <n-spin :show="props.loading" class="!block h-full">
        <div
            ref="fullRef"
            :class="[
                'ms-card',
                'relative',
                'h-full',
                props.isFullscreen || isFullScreen ? 'ms-card--fullScreen' : '',
                props.autoHeight ? '' : 'min-h-[500px]',
                props.noContentPadding ? 'ms-card--noContentPadding' : 'p-[24px]',
                props.noBottomRadius ? 'ms-card--noBottomRadius' : '',
            ]"
        >
            <div :class="{ 'px-[24px]': props.dividerHasPX }">
                <n-divider v-if="!props.simple && !props.hideDivider" class="mb-[16px] mt-0" />
            </div>
            <div class="ms-card-container">
                <div class="relative h-full w-full">
                    <slot></slot>
                </div>
            </div>
            <div
                v-if="!props.hideFooter && !props.simple"
                class="ms-card-footer"
                :style="{
                    width:
                        props.isFullscreen || isFullScreen
                            ? '100%'
                            : `calc(100% - ${menuWidth + 16}px)`,
                }"
            >
                <div class="ml-0 mr-auto">
                    <slot name="footerLeft"></slot>
                </div>
                <slot name="footerRight">
                    <div class="flex justify-end gap-[16px]">
                        <n-button>{{ t('mscard.defaultCancelText') }}</n-button>
                        <n-button v-if="!props.hideContinue && !props.isEdit">
                            {{ t('mscard.defaultSaveAndContinueText') }}
                        </n-button>
                        <n-button>
                            {{ t(props.isEdit ? 'mscard.defaultUpdate' : 'mscard.defaultConfirm') }}
                        </n-button>
                    </div>
                </slot>
            </div>
        </div>
    </n-spin>
</template>

<style scoped></style>
