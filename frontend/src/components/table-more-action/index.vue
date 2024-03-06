<script setup lang="ts">
import { DropdownOption } from 'naive-ui'
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const emit = defineEmits(['select'])
const options = ref([
    {
        label: t('system.user.resetPassword'),
        key: 'resetPassword',
        eventTag: 'resetPassword',
    },
    {
        label: t('system.user.disable'),
        eventTag: 'disabled',
        key: 'disabled',
    },
    {
        type: 'divider',
        key: 'd1',
    },
    {
        label: t('system.user.delete'),
        eventTag: 'delete',
        key: 'delete',
    },
])

const handleSelect = (value: string) => {
    const item = options.value.find((e: DropdownOption) => e?.eventTag === value)
    emit('select', item)
}
</script>
<template>
    <div>
        <n-dropdown trigger="click" :options="options" size="small" @select="handleSelect">
            <slot>
                <n-button text>
                    <template #icon>
                        <n-icon>
                            <span class="i-tabler:adjustments" />
                        </n-icon>
                    </template>
                </n-button>
            </slot>
        </n-dropdown>
    </div>
</template>

<style scoped></style>
