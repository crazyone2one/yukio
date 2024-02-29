<script setup lang="ts">
import { DropdownOption } from 'naive-ui'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { ActionsItem, SelectedValue } from './types'

const props = defineProps<{
    list: ActionsItem[]
    trigger?: 'click' | 'hover' | 'focus' | 'manual'
}>()
const { t } = useI18n()
const emit = defineEmits(['select', 'close'])
const options = ref<Array<DropdownOption>>([])
const visible = ref(false)
watch(
    () => props.list,
    (newValue) => {
        if (newValue.length > 0) {
            newValue.forEach((item) => {
                const option: DropdownOption = {
                    label: t(item.label || ''),
                    key: item.label,
                    disabled: item.disabled || false,
                }
                options.value.push(option)
                // if (item.isDivider) {
                //     options.value.push({
                //         type: 'divider',
                //         key: `${item.label}-divider`,
                //     })
                // } else {
                //     const option: DropdownOption = {}
                //     option.label = t(item.label || '')
                //     option.key = item.label
                //     option.disabled = item.disabled || false
                //     options.value.push(option)
                // }
            })
        }

        console.log(`output->newValue`, options.value)
    },
)
const handleSelect = (value: SelectedValue) => {
    const item = props.list.find((e: ActionsItem) => e.eventTag === value)
    emit('select', item)
}
const visibleChange = (val: boolean) => {
    if (!val) {
        emit('close')
    }
}
</script>
<template>
    <div>
        <n-dropdown
            v-model:show="visible"
            trigger="hover"
            :options="options"
            @select="handleSelect"
            @update:show="visibleChange"
        >
            <slot>
                <n-button text size="tiny">
                    <template #icon>
                        <n-icon :size="30">
                            <span class="i-icon-more" />
                        </n-icon>
                    </template>
                </n-button>
            </slot>
        </n-dropdown>
    </div>
</template>

<style scoped></style>
