<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { onBeforeMount, ref } from 'vue'
import { UserRequestTypeEnum } from './utils'
import { getAdminByOrganizationOrProject } from '/@/api/modules/setting/OrganizationAndProject'

export interface UserSelectorOption {
    id: string
    name: string
    email: string
    disabled?: boolean
    [key: string]: string | number | boolean | undefined
}
const props = withDefaults(
    defineProps<{
        disabled?: boolean // 是否禁用
        disabledKey?: string // 禁用的key
        valueKey?: string // value的key
        placeholder?: string
        firstLabelKey?: string // 首要的的字段key
        secondLabelKey?: string // 次要的字段key
        loadOptionParams?: Record<string, any> // 加载选项的参数
        type?: UserRequestTypeEnum // 加载选项的类型
        atLeastOne?: boolean // 是否至少选择一个
    }>(),
    {
        disabled: false,
        disabledKey: 'disabled',
        valueKey: 'id',
        firstLabelKey: 'name',
        secondLabelKey: 'email',
        type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
        atLeastOne: false,
        placeholder: 'common.pleaseSelectMember',
        loadOptionParams: () => ({}),
    },
)
const currentValue = defineModel<string[] | string>({ default: [] })
const remoteOriginOptions = ref<Array<SelectOption>>([])
const {
    send: loadList,
    loading,
    onSuccess,
} = useRequest(getAdminByOrganizationOrProject(), {
    immediate: false,
})
onBeforeMount(() => {
    loadList({})
})
onSuccess((res) => {
    console.log(`output->res`, res)
    const { firstLabelKey, secondLabelKey, disabledKey, valueKey } = props
    res.data.forEach((item: UserSelectorOption) => {
        const option: SelectOption = {}
        option.label = `${item[firstLabelKey]}(${item[secondLabelKey]})`
        option.value = item[valueKey] as string
        option.disabled = item[disabledKey] as boolean
        remoteOriginOptions.value.push(option)
    })
    return res
})
</script>
<template>
    <n-select
        v-model:value="currentValue"
        :options="remoteOriginOptions"
        multiple
        clearable
        :loading="loading"
        :placeholder="props.placeholder"
    />
</template>

<style scoped></style>
