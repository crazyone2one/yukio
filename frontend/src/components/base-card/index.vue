<script setup lang="ts">
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
</script>
<template>
  <n-spin :show="props.loading">
    <n-card
      :segmented="{
        content: true,
        footer: 'soft',
      }"
      hoverable
    >
      <template #header>
        <div class="ms-card-header">
          <div v-if="!props.hideBack" class="back-btn">
            <n-icon>
              <div class="i-carbon:arrow-left" />
            </n-icon>
          </div>
          <slot name="headerLeft">
            <div class="font-medium text-[var(--color-text-000)]">
              {{ props.title }}
            </div>
            <div class="text-[var(--color-text-4)]">{{ props.subTitle }}</div>
          </slot>
        </div>
      </template>
      <template #header-extra>
        <div class="ml-auto flex items-center">
          <slot name="headerRight"></slot>
        </div>
      </template>
      <div
        class="relative h-full w-full"
        :style="{ minWidth: `${props.minWidth || 1000}px` }"
      >
        <slot></slot>
      </div>

      <template #footer> <div v-if="!props.hideFooter">#footer</div> </template>
    </n-card>
  </n-spin>
</template>

<style scoped>
.ms-card-header {
  @apply flex flex-wrap items-center;

  /* padding-bottom: 16px; */
  .back-btn {
    @apply flex cursor-pointer items-center rounded-full;

    margin-right: 8px;
    width: 20px;
    height: 20px;
    border: 1px solid #ffffff;
    background: linear-gradient(
      90deg,
      rgb(var(--primary-9)) 3.36%,
      #ffffff 100%
    );
    box-shadow: 0 0 7px rgb(15 0 78 / 9%);
  }
}
</style>
