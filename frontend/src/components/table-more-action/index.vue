<script setup lang="ts">
import { DropdownOption } from 'naive-ui'
import { ref, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
import { ActionsItem } from './types'

const props = defineProps<{
    list: ActionsItem[]
}>()
const { t } = useI18n()
const emit = defineEmits(['select'])
const options = ref<Array<DropdownOption>>([])

const handleSelect = (value: string) => {
    const item = options.value.find((e) => e.key === value)
    emit('select', item)
}
watchEffect(() => {
    props.list.forEach((e: ActionsItem) => {
        const option: DropdownOption = {}
        if (e.isDivider) {
            option.type = 'divider'
        } else {
            option.label = t(e.label || '')
        }
        option.key = e.eventTag
        options.value.push(option)
    })
})
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
