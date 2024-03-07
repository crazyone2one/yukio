<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n'
import { characterLimit } from '/@/utils'

const { t } = useI18n()
const props = withDefaults(defineProps<{ name?: string; isAdmin?: boolean }>(), { isAdmin: false })
</script>
<template>
    <div class="flex flex-row flex-nowrap">
        <n-tooltip trigger="hover">
            <template #trigger>
                <div class="flex flex-row flex-nowrap">{{ characterLimit(props.name) }}</div>
            </template>
        </n-tooltip>
        <div v-if="props.isAdmin" class="ml-1 flex flex-row flex-nowrap text-[var(--color-text-4)]">
            {{ `(${t('common.admin')})` }}
        </div>
    </div>
</template>

<style scoped></style>
