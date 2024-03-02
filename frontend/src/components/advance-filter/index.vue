<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n'
const { t } = useI18n()
const innerKeyword = defineModel<string>('keyword', { default: '' })
</script>
<template>
    <div class="flex flex-row justify-between">
        <slot name="left"></slot>
        <div class="flex flex-row gap-[8px]">
            <n-input v-model:value="innerKeyword" :placeholder="t('common.search')" size="small" class="w-[240px]"/>
            <n-button>
                <template #icon>
                    <n-icon>
                        <span class="i-tabler:zoom-reset" />
                    </n-icon>
                </template>
            </n-button>
            <slot name="right"></slot>
        </div>
    </div>
</template>

<style scoped></style>
