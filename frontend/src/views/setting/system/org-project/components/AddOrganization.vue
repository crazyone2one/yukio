<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules, NModal } from 'naive-ui'
import { computed, ref, watchEffect } from 'vue'
import { CreateOrUpdateSystemOrgParams } from '/@/api/interface/orgnization'
import { createOrUpdateOrg } from '/@/api/modules/setting/org-and-project'
import { useI18n } from '/@/hooks/use-i18n'
import useUserStore from '/@/store/modules/user'

const props = defineProps<{
  currentOrganization?: CreateOrUpdateSystemOrgParams
}>()
const { t } = useI18n()
const formRef = ref<FormInst | null>(null)
const showModal = defineModel<boolean>('visible', { default: false })
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void
}>()
const userStore = useUserStore()
const isEdit = computed(() => !!props.currentOrganization?.id)

const rules: FormRules = {
  name: [
    {
      required: true,
      trigger: ['blur', 'input'],
      message: t('system.organization.organizationNameRequired'),
    },
    { max: 255, message: t('common.nameIsTooLang') },
  ],
}

const {
  loading,
  form,
  send: submit,
} = useForm(
  (formData) => {
    // 可以在此转换表单数据并提交
    return createOrUpdateOrg(formData)
  },
  {
    // 初始化表单数据
    initialForm: {
      id: '',
      name: '',
      userIds: userStore.id ? [userStore.id] : [],
      description: '',
    },
  },
)
const handleClose = (shouldSearch = false) => {
  emit('cancel', shouldSearch)
  formRef.value?.restoreValidation()
}
const handleBeforeOk = () => {
  formRef.value?.validate(async (errors) => {
    if (errors) {
      return false
    }
    submit({ id: props.currentOrganization?.id, ...form }).then(() => {
      window.$message.success(
        isEdit.value
          ? t('system.organization.updateOrganizationSuccess')
          : t('system.organization.createOrganizationSuccess'),
      )
      handleClose(true)
    })
  })
}
watchEffect(() => {
  if (props.currentOrganization) {
    form.value.id = props.currentOrganization.id as string
    form.value.name = props.currentOrganization.name
    form.value.userIds = props.currentOrganization.userIds
    form.value.description = props.currentOrganization.description
  }
})
</script>
<template>
  <n-modal
    v-model:show="showModal"
    preset="dialog"
    :mask-closable="false"
    @close="handleClose"
  >
    <template #header>
      <span v-if="isEdit">
        {{ t('system.organization.updateOrganization') }}
      </span>
      <span v-else>
        {{ t('system.organization.createOrganization') }}
      </span>
    </template>
    <div>
      <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
        class="py-2"
      >
        <n-form-item
          :label="t('system.organization.organizationName')"
          path="name"
        >
          <n-input
            v-model:value="form.name"
            :placeholder="t('system.organization.organizationNamePlaceholder')"
          />
        </n-form-item>
        <n-form-item
          :label="t('system.organization.description')"
          path="description"
        >
          <n-input
            v-model:value="form.description"
            :placeholder="t('system.organization.descriptionPlaceholder')"
            type="textarea"
            :autosize="{
              minRows: 1,
            }"
            maxlength="1000"
            clearable
          />
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div>
        <n-button :disabled="loading" @click="handleClose(false)">
          {{ t('common.cancel') }}
        </n-button>
        <n-button type="primary" :disabled="loading" @click="handleBeforeOk">
          {{ isEdit ? t('common.confirm') : t('common.create') }}
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<style scoped></style>
