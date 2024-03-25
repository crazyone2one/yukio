<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import {
  FormInst,
  FormRules,
  NCheckbox,
  NCheckboxGroup,
  SelectOption,
} from 'naive-ui'
import { computed, ref, watchEffect } from 'vue'
import {
  CreateOrUpdateSystemProjectParams,
  SystemOrgOption,
} from '/@/api/interface/setting/orgAndProject.ts'
import {
  createOrUpdateProject,
  getAdminByOrganizationOrProject,
  getSystemOrgOption,
} from '/@/api/modules/setting/org-and-project'
import { MsUserSelectorOption } from '/@/components/user-selector/types'
import { UserRequestTypeEnum } from '/@/components/user-selector/utils.ts'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import useUserStore from '/@/store/modules/user'

const props = defineProps<{
  currentProject?: CreateOrUpdateSystemProjectParams
}>()
const { t } = useI18n()
const userStore = useUserStore()
const formRef = ref<FormInst | null>(null)
const affiliatedOrgOption = ref<SystemOrgOption[]>([])
const userOptions = ref<SelectOption[]>([])
const showModal = defineModel<boolean>('visible', { default: false })
const emit = defineEmits<{
  (e: 'cancel', shouldSearch: boolean): void
}>()
const appStore = useAppStore()
const isEdit = computed(() => !!props.currentProject?.id)

const rules: FormRules = {
  name: [
    {
      required: true,
      trigger: ['blur', 'input'],
      message: t('system.project.projectNameRequired'),
    },
    { max: 255, message: t('common.nameIsTooLang') },
  ],
  organizationId: [
    {
      required: true,
      trigger: ['blur', 'select'],
      message: t('system.project.affiliatedOrgRequired'),
    },
  ],
  userIds: [
    {
      type: 'array',
      required: true,
      trigger: ['blur', 'select'],
      message: t('system.project.projectAdminIsNotNull'),
    },
  ],
}
const moduleOption = [
  // { label: 'menu.workbench', value: 'workstation' },
  { label: 'menu.testPlan', value: 'testPlan' },
  { label: 'menu.bugManagement', value: 'bugManagement' },
  { label: 'menu.caseManagement', value: 'caseManagement' },
  // { label: 'menu.apiTest', value: 'apiTest' },
  // { label: 'menu.uiTest', value: 'uiTest' },
  // { label: 'menu.performanceTest', value: 'loadTest' },
]
const allModuleIds = ['bugManagement', 'caseManagement', 'apiTest']
const {
  loading,
  form,
  send: submit,
  reset,
} = useForm(
  (formData) => {
    // 可以在此转换表单数据并提交
    return createOrUpdateProject(formData)
  },
  {
    // 初始化表单数据
    initialForm: {
      id: '',
      name: '',
      userIds: userStore.id ? [userStore.id] : [],
      organizationId: '',
      description: '',
      enable: true,
      moduleIds: allModuleIds,
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
    submit({ id: isEdit.value ? props.currentProject?.id : '', ...form }).then(
      () => {
        window.$message.success(
          isEdit.value
            ? t('system.project.updateProjectSuccess')
            : t('system.project.createProjectSuccess'),
        )
        appStore.initProjectList()
        handleClose(true)
      },
    )
  })
}
const { send: initAffiliatedOrgOption } = useRequest(getSystemOrgOption(), {
  immediate: false,
})
const { send: initUserOption } = useRequest(getAdminByOrganizationOrProject(), {
  immediate: false,
})
watchEffect(() => {
  initAffiliatedOrgOption().then((res) => {
    affiliatedOrgOption.value = res
  })
  initUserOption().then((res) => {
    const list: Array<MsUserSelectorOption> = res as Array<MsUserSelectorOption>
    userOptions.value = []
    list.map((item) => {
      userOptions.value.push({
        value: item.id,
        label: `${item.name}(${item.email})`,
      })
    })
  })
  if (props.currentProject?.id) {
    if (props.currentProject) {
      form.value.id = props.currentProject.id
      form.value.name = props.currentProject.name
      form.value.description = props.currentProject.description
      form.value.enable = props.currentProject.enable
      form.value.userIds = props.currentProject.userIds
      form.value.organizationId = props.currentProject.organizationId
      form.value.moduleIds = props.currentProject.moduleIds
    }
  } else {
    reset()
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
        {{ t('system.project.updateProject') }}
      </span>
      <span v-else>
        {{ t('system.project.createProject') }}
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
        <n-form-item :label="t('system.project.name')" path="name">
          <n-input
            v-model:value="form.name"
            :placeholder="t('system.project.projectNamePlaceholder')"
          />
        </n-form-item>
        <n-form-item
          :label="t('system.project.affiliatedOrg')"
          path="organizationId"
        >
          <n-select
            v-model:value="form.organizationId"
            :options="affiliatedOrgOption"
            filterable
            :placeholder="t('system.project.affiliatedOrgPlaceholder')"
          />
        </n-form-item>
        <n-form-item :label="t('system.project.projectAdmin')" path="userIds">
          <n-select
            v-model:value="form.userIds"
            :type="UserRequestTypeEnum.SYSTEM_PROJECT_ADMIN"
            :options="userOptions"
            multiple
            filterable
            :placeholder="t('system.project.pleaseSelectAdmin')"
          />
        </n-form-item>
        <n-form-item :label="t('system.project.moduleSetting')" path="module">
          <n-checkbox-group v-model:value="form.moduleIds">
            <n-space item-style="display: flex;">
              <n-checkbox
                v-for="item in moduleOption"
                :key="item.value"
                :value="item.value"
                :label="$t(item.label)"
              />
            </n-space>
          </n-checkbox-group>
        </n-form-item>
        <n-form-item
          :label="t('system.organization.description')"
          path="description"
        >
          <n-input
            v-model:value="form.description"
            :placeholder="t('system.project.descriptionPlaceholder')"
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
      <div class="flex gap-[10px]">
        <n-button :disabled="loading" @click="handleClose(false)">
          {{ t('common.cancel') }}
        </n-button>
        <n-button type="primary" :disabled="loading" @click="handleBeforeOk">
          {{ isEdit ? t('common.update') : t('common.create') }}
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<style scoped></style>
