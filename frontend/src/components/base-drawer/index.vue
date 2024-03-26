<script setup lang="ts">
import { ref, watch } from 'vue'

interface DrawerProps {
  visible: boolean
  title?: string | undefined
  titleTag?: string
  titleTagColor?: string
  footer?: boolean
  mask?: boolean // 是否显示遮罩
  showSkeleton?: boolean // 是否显示骨架屏
  okLoading?: boolean
  okDisabled?: boolean
  okPermission?: string[] // 确认按钮权限
  okText?: string
  cancelText?: string
  saveContinueText?: string
  showContinue?: boolean
  width: string | number // 抽屉宽度，为数值时才可拖拽改变宽度
  noContentPadding?: boolean // 是否没有内容内边距
  popupContainer?: string
  disabledWidthDrag?: boolean // 是否禁止拖拽宽度
  closable?: boolean // 是否显示右上角的关闭按钮
  noTitle?: boolean // 是否不显示标题栏
  showFullScreen?: boolean // 是否显示全屏按钮
}
const props = withDefaults(defineProps<DrawerProps>(), {
  footer: true,
  mask: true,
  closable: true,
  showSkeleton: false,
  showContinue: false,
  popupContainer: 'body',
  disabledWidthDrag: false,
  showFullScreen: false,
  okPermission: () => [], // 确认按钮权限
})
const emit = defineEmits([
  'update:visible',
  'confirm',
  'cancel',
  'continue',
  'close',
])
const visible = ref(props.visible)
const drawerWidth = ref(props.width) // 抽屉初始宽度
const handleContinue = () => {
  emit('continue')
}

const handleOk = () => {
  emit('confirm')
}

const handleCancel = () => {
  visible.value = false
  emit('update:visible', false)
  emit('cancel')
}

const handleClose = () => {
  visible.value = false
  emit('update:visible', false)
  emit('close')
}
watch(
  () => props.visible,
  (val) => {
    visible.value = val
  },
)
</script>
<template>
  <n-drawer
    v-model:show="visible"
    :width="drawerWidth"
    :mask-closable="props.mask"
    @close="handleClose"
  >
    <n-drawer-content>
      <template #header>
        <div class="flex items-center justify-between gap-[4px]">
          <slot name="title">
            <div class="flex flex-1 items-center justify-between">
              <div class="flex items-center">
                {{ props.title }}
                <slot name="headerLeft"></slot>
              </div>
              <slot name="tbutton"></slot>
            </div>
          </slot>
          <div class="right-operation-button-icon"></div>
        </div>
      </template>
      <slot></slot>
      <template #footer>
        <slot name="footer">
          <div class="flex items-center justify-between">
            <slot name="footerLeft"></slot>
            <div class="ml-auto flex gap-[12px]">
              <n-button :disabled="props.okLoading" @click="handleCancel">
                {{ $t(props.cancelText || 'ms.drawer.cancel') }}
              </n-button>
              <n-button
                v-if="showContinue"
                :disabled="props.okLoading"
                @click="handleContinue"
              >
                {{ $t(props.saveContinueText || 'ms.drawer.saveContinue') }}
              </n-button>
              <n-button :disabled="props.okLoading" @click="handleOk">
                {{ $t(props.okText || 'ms.drawer.ok') }}
              </n-button>
            </div>
          </div>
        </slot>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped></style>
