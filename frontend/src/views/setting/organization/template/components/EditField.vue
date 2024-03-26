<script setup lang="ts">
import { FormInst, SelectOption } from 'naive-ui'
import { computed, onMounted, ref, watch } from 'vue'
import { fieldIconAndName } from './field-setting'
import {
  AddOrUpdateField,
  DefinedFieldItem,
  FormItemType,
} from '/@/api/interface/setting/template'
import BaseDrawer from '/@/components/base-drawer/index.vue'
import useAppStore from '/@/store/modules/app'
const props = defineProps<{
  visible: boolean
  mode: 'organization' | 'project'
  data: DefinedFieldItem[]
}>()
const appStore = useAppStore()
const showDrawer = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const emit = defineEmits(['success', 'update:visible'])
const formRef = ref<FormInst | null>(null)
const currentOrgId = computed(() => appStore.currentOrgId)
const currentProjectId = computed(() => appStore.currentProjectId)
const scopeId = computed(() => {
  return props.mode === 'organization'
    ? currentOrgId.value
    : currentProjectId.value
})
const initFieldForm: AddOrUpdateField = {
  name: '',
  used: false,
  type: undefined,
  remark: '',
  scopeId: scopeId.value,
  scene: 'FUNCTIONAL',
  options: [],
  enableOptionKey: false,
}
const model = ref<AddOrUpdateField>({ ...initFieldForm })
const rules = ref({})
// 字段类型列表选项
const fieldOptions = ref<SelectOption[]>([])
const isMultipleSelectMember = ref<boolean | undefined>(false) // 成员多选
// 是否展示选项添加面板
const showOptionsSelect = computed(() => {
  const showOptionsType: FormItemType[] = [
    'RADIO',
    'CHECKBOX',
    'SELECT',
    'MULTIPLE_SELECT',
  ]
  return showOptionsType.includes(model.value.type as FormItemType)
})
watch(
  () => showDrawer.value,
  (val) => {
    emit('update:visible', val)
  },
)

watch(
  () => props.visible,
  (val) => {
    showDrawer.value = val
  },
)
onMounted(() => {
  const excludeOptions = [
    'MULTIPLE_MEMBER',
    'DATETIME',
    'SYSTEM',
    'INT',
    'FLOAT',
  ]
  fieldOptions.value = fieldIconAndName.filter(
    (item: SelectOption) => excludeOptions.indexOf(item.value as string) < 0,
  )
})
</script>
<template>
  <base-drawer
    v-model:visible="showDrawer"
    :title="
      isEdit
        ? $t('system.orgTemplate.update')
        : $t('system.orgTemplate.addField')
    "
    :ok-text="
      isEdit ? 'system.orgTemplate.update' : 'system.orgTemplate.addField'
    "
    :width="800"
  >
    <n-form
      ref="formRef"
      :model="model"
      :rules="rules"
      label-placement="left"
      label-width="auto"
      require-mark-placement="right-hanging"
    >
      <n-form-item :label="$t('system.orgTemplate.fieldName')" path="name">
        <n-input
          v-model:value="model.name"
          :placeholder="$t('system.orgTemplate.fieldNamePlaceholder')"
        />
      </n-form-item>
      <n-form-item :label="$t('system.orgTemplate.description')" path="remark">
        <n-input
          v-model:value="model.remark"
          :placeholder="$t('system.orgTemplate.resDescription')"
          type="textarea"
        />
      </n-form-item>
      <n-form-item :label="$t('system.orgTemplate.fieldType')" path="type">
        <n-select
          v-model:value="model.type"
          :placeholder="$t('system.orgTemplate.fieldTypePlaceholder')"
          :options="fieldOptions"
        />
      </n-form-item>
      <n-form-item
        v-if="model.type === 'MEMBER'"
        :label="$t('system.orgTemplate.allowMultiMember')"
        path="type"
      >
        <n-switch v-model:value="isMultipleSelectMember" />
      </n-form-item>
      <n-form-item
        v-if="showOptionsSelect"
        :label="$t('system.orgTemplate.optionContent')"
        path="optionsModels"
      >
        <n-switch v-model:value="isMultipleSelectMember" />
      </n-form-item>
    </n-form>
  </base-drawer>
</template>

<style scoped></style>
