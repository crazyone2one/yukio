<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { onMounted, ref } from 'vue'
import initOptionsFunc, {
  UserRequestTypeEnum,
} from '/@/components/user-selector/utils.ts'

export interface MsUserSelectorOption {
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
    loadOptionParams?: Record<
      string,
      string | number | boolean | UserRequestTypeEnum
    > // 加载选项的参数
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
    placeholder: 'system.project.pleaseSelectAdmin',
    loadOptionParams: () => ({}),
  },
)
const options = ref<Array<SelectOption>>([])
const currentValue = defineModel<string[] | string>({ default: [] })

const { send: loadList } = useRequest(
  (type, params) => {
    return initOptionsFunc(type, params)
  },
  {
    immediate: false,
  },
)

onMounted(() => {
  loadList(props.type, props.loadOptionParams).then((res) => {
    const list: Array<MsUserSelectorOption> = res as Array<MsUserSelectorOption>
    options.value = []
    list.map((item) => {
      options.value.push({
        value: item.id,
        label: item.name,
      })
    })
  })
})
</script>

<template>
  <n-select v-model:value="currentValue" multiple :options="options" />
</template>

<style scoped></style>
