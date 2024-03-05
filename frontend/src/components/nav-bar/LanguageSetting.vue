<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n'
import { LOCALE_OPTIONS } from '/@/locale'
import useLocale from '/@/locale/use-locale'

const { t } = useI18n()
const { changeLocale, currentLocale } = useLocale()
const locales = [...LOCALE_OPTIONS]
</script>
<template>
    <n-popselect
        v-model:value="currentLocale"
        :options="locales"
        trigger="click"
        :disabled="true"
        @update:value="changeLocale"
    >
        <n-tooltip trigger="hover">
            <template #trigger>
                <n-icon :size="20" class="mt-[6px]">
                    <span class="i-tabler:language" />
                </n-icon>
            </template>
            {{ t('settings.language') }}
        </n-tooltip>
    </n-popselect>
</template>
<style scoped></style>
