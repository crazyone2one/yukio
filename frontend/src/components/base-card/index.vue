<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import useFullScreen from '/@/hooks/use-full-screen.ts'
import { NScrollbar } from 'naive-ui'
import { useRouter } from 'vue-router'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { useAppStore } from '/@/store'

const props = withDefaults(
    defineProps<
        Partial<{
            simple: boolean // 简单模式，没有标题和底部栏
            title: string // 卡片标题
            subTitle: string // 卡片副标题
            hideContinue: boolean // 隐藏保存并继续创建按钮
            hideFooter: boolean // 隐藏底部栏
            loading: boolean // 卡片 loading 状态
            isEdit: boolean // 是否编辑状态
            specialHeight: number // 特殊高度，例如某些页面有面包屑，autoHeight 时无效
            hideBack: boolean // 隐藏返回按钮
            autoHeight: boolean // 内容区域高度是否自适应
            otherWidth: number // 该宽度为卡片外部同级容器的宽度
            headerMinWidth: number // 卡片头部最小宽度
            minWidth: number // 卡片内容最小宽度
            hasBreadcrumb: boolean // 是否有面包屑，如果有面包屑，高度需要减去面包屑的高度
            noContentPadding: boolean // 内容区域是否有padding
            noBottomRadius?: boolean // 底部是否有圆角
            isFullscreen?: boolean // 是否全屏
            hideDivider?: boolean // 是否隐藏分割线
            handleBack: () => void // 自定义返回按钮触发事件
            dividerHasPX: boolean // 分割线是否有左右padding;
            showFullScreen: boolean // 是否显示全屏按钮
        }>
    >(),
    {
        simple: false,
        hideContinue: false,
        hideFooter: false,
        isEdit: false,
        specialHeight: 0,
        hideBack: false,
        autoHeight: false,
        hasBreadcrumb: false,
        noContentPadding: false,
        noBottomRadius: false,
        dividerHasPX: false,
    },
)
const emit = defineEmits(['saveAndContinue', 'save', 'toggleFullScreen'])
const fullRef = ref<HTMLElement | null>()
const { isFullScreen, toggleFullScreen } = useFullScreen(fullRef)
const _specialHeight = props.hasBreadcrumb ? 32 + props.specialHeight : props.specialHeight // 有面包屑的话，默认面包屑高度32
const router = useRouter()
const { t } = useI18n()

const appStore = useAppStore()
const collapsedWidth = 86
const cardOverHeight = computed(() => {
    if (isFullScreen.value) {
        return 106
    }
    if (props.simple) {
        // 简单模式没有标题、没有底部
        return props.noContentPadding ? 76 + _specialHeight : 124 + _specialHeight
    }
    return 190 + _specialHeight
})
const menuWidth = computed(() => {
    return appStore.menuCollapse ? collapsedWidth : appStore.menuWidth
})
const getComputedContentStyle = computed(() => {
    if (props.isFullscreen || isFullScreen.value || props.noContentPadding) {
        return {
            overflow: 'auto',
            width: 'auto',
            height: props.autoHeight ? 'auto' : `calc(100vh - ${cardOverHeight.value}px)`,
        }
    }
    return {
        overflow: 'auto',
        width: props.otherWidth
            ? `calc(100vw - ${menuWidth.value}px - ${props.otherWidth}px)`
            : `calc(100vw - ${menuWidth.value}px - 58px)`,
        height: props.autoHeight ? 'auto' : `calc(100vh - ${cardOverHeight.value}px)`,
    }
})
const back = () => {
    if (typeof props.handleBack === 'function') {
        props.handleBack()
    } else {
        router.back()
    }
}
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
            <n-scrollbar v-if="!props.simple" :style="{ overflow: 'auto' }">
                <div
                    class="ms-card-header"
                    :style="props.headerMinWidth ? { minWidth: `${props.headerMinWidth}px` } : {}"
                >
                    <div v-if="!props.hideBack" class="back-btn" @click="back">
                        <n-icon>
                            <span class="i-tabler:arrow-left" />
                        </n-icon>
                    </div>
                    <slot name="headerLeft">
                        <div class="font-medium text-[var(--color-text-000)]">
                            {{ props.title }}
                        </div>
                        <div class="text-[var(--color-text-4)]">{{ props.subTitle }}</div>
                    </slot>
                    <div class="ml-auto flex items-center">
                        <slot name="headerRight"></slot>
                        <div
                            v-if="props.showFullScreen"
                            class="cursor-pointer text-right !text-[var(--color-text-4)]"
                            @click="toggleFullScreen"
                        >
                            <n-icon v-if="isFullScreen">
                                <span class="i-tabler:arrows-minimize" />
                            </n-icon>
                            <n-icon v-else>
                                <span class="i-tabler:arrows-maximize" />
                            </n-icon>

                            {{ t(isFullScreen ? 'common.offFullScreen' : 'common.fullScreen') }}
                        </div>
                    </div>
                    <div v-if="$slots.subHeader" class="basis-full">
                        <slot name="subHeader"></slot>
                    </div>
                </div>
            </n-scrollbar>
            <div :class="{ 'px-[24px]': props.dividerHasPX }">
                <n-divider v-if="!props.simple && !props.hideDivider" class="mb-[16px] mt-0" />
            </div>
            <div class="relativer">
                <n-scrollbar
                    :class="['h-full', props.noContentPadding ? '' : 'pr-[5px]']"
                    :style="getComputedContentStyle"
                >
                    <div
                        class="relative h-full w-full"
                        :style="{ minWidth: `${props.minWidth || 1000}px` }"
                    >
                        <slot></slot>
                    </div>
                </n-scrollbar>
            </div>
            <div
                v-if="!props.hideFooter && !props.simple"
                class="ms-card-footer fixed flex justify-between bg-white"
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
                        <n-button @click="back">{{ t('mscard.defaultCancelText') }}</n-button>
                        <n-button
                            v-if="!props.hideContinue && !props.isEdit"
                            @click="emit('saveAndContinue')"
                        >
                            {{ t('mscard.defaultSaveAndContinueText') }}
                        </n-button>
                        <n-button type="primary" @click="emit('save')">
                            {{ t(props.isEdit ? 'mscard.defaultUpdate' : 'mscard.defaultConfirm') }}
                        </n-button>
                    </div>
                </slot>
            </div>
        </div>
    </n-spin>
</template>

<style scoped></style>
