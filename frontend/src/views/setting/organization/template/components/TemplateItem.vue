<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n'
const props = defineProps<{
  cardItem: Record<string, any>
  mode: 'organization' | 'project'
}>()
const { t } = useI18n()
const emit = defineEmits<{
  (e: 'fieldSetting', key: string): void
  (e: 'templateManagement', key: string): void
  (e: 'workflowSetup', key: string): void
  (e: 'updateState'): void
}>()
// 字段设置
const fieldSetting = () => {
  emit('fieldSetting', props.cardItem.key)
}
const templateManagement = () => {
  emit('templateManagement', props.cardItem.key)
}
</script>
<template>
  <div class="outerWrapper p-[3px]">
    <div class="innerWrapper">
      <div class="content">
        <div></div>
        <div class="template-operation">
          <div class="flex items-center">
            <span class="font-medium">{{ props.cardItem.name }}</span>
            <span class="enable">
              {{ t('system.orgTemplate.enabledTemplates') }}
            </span>
          </div>
          <div class="flex min-w-[300px] flex-nowrap items-center">
            <span class="operation hover:text-[#0052cc]">
              <span @click="fieldSetting">
                {{ t('system.orgTemplate.fieldSetting') }}
              </span>
              <n-divider vertical />
            </span>
            <span class="operation hover:text-[#0052cc]">
              <span @click="templateManagement">
                {{ t('system.orgTemplate.TemplateManagement') }}
              </span>
              <n-divider v-if="props.cardItem.key === 'BUG'" vertical />
            </span>
            <span
              v-if="props.cardItem.key === 'BUG'"
              class="operation hover:text-[#0052cc]"
            >
              <span>
                {{ t('system.orgTemplate.workflowSetup') }}
              </span>
              <n-divider v-if="props.mode === 'organization'" vertical />
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.n-divider--vertical) {
  margin: 0 8px;
}
.outerWrapper {
  box-shadow: 0 6px 15px rgba(120 56 135/ 5%);
  @apply rounded bg-white;
  .innerWrapper {
    @apply rounded p-6;
    .content {
      @apply flex;
      .logo-img {
        @apply mr-3 flex items-center bg-white;
      }
      .template-operation {
        .operation {
          cursor: pointer;
        }
        .enable {
          color: rgb(201, 205, 212);
          background: var(--color-text-n8);
          @apply ml-4 rounded p-1 text-xs;
        }
        @apply flex flex-col justify-between;
      }
    }
  }
}
</style>
