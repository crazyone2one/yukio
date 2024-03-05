<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n'
import { useAppStore } from '/@/store'

const appStore = useAppStore()
const { t } = useI18n()
</script>
<template>
    <n-card class="rounded-2xl bg-white" hoverable>
        <div class="p-[24px] pb-[16px]">
            <n-button size="small" type="primary">{{
                t('caseManagement.featureCase.creatingCase')
            }}</n-button>
            <n-button size="small" class="mx-3">
                {{ t('caseManagement.featureCase.importExcel') }}
            </n-button>
        </div>
        <n-divider class="!my-0" />
        <div class="page-wrap">
            <n-split :v-model:size="0.1" class="h-full" :max="0.75" :min="0.1">
                <template #1> Pane 1 </template>
                <template #2> Pane 2 </template>
            </n-split>
        </div>
    </n-card>
</template>

<style scoped></style>
