<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { computed, useAttrs } from 'vue'
import YTag from './YTag.vue'

const props = withDefaults(
    defineProps<{
        tagList: Array<any>
        showNum?: number
        nameKey?: string
        isStringTag?: boolean // 是否是字符串数组的标签
    }>(),
    {
        showNum: 2,
        nameKey: 'name',
    },
)

const attrs = useAttrs()

const filterTagList = computed(() => {
    return (props.tagList || []).filter((item: any) => item) || []
})
const tagsTooltip = computed(() => {
    return filterTagList.value
        .map((e: any) => (props.isStringTag ? e : e[props.nameKey]))
        .join('，')
})
const showTagList = computed(() => {
    return filterTagList.value.slice(0, props.showNum)
})
</script>
<template>
    <n-tooltip trigger="hover">
        <template #trigger>
            <div class="flex max-w-[440px] flex-row">
                <y-tag v-for="tag of showTagList" :key="tag.id" v-bind="attrs">
                    {{ props.isStringTag ? tag : tag[props.nameKey] }}
                </y-tag>
                <y-tag v-if="props.tagList.length > props.showNum" v-bind="attrs">
                    +{{ props.tagList.length - props.showNum }}
                </y-tag>
            </div>
        </template>
        {{ tagsTooltip }}
    </n-tooltip>
</template>

<style scoped></style>
