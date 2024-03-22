<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormItemRule, NModal } from 'naive-ui'
import { reactive, ref, watchEffect } from 'vue'
import { CreateUserParams, UserListItem } from '/@/api/interface/setting/user'
import { batchCreateUser } from '/@/api/modules/setting/user'
import { useI18n } from '/@/hooks/use-i18n'
import { validateEmail, validatePhone } from '/@/utils/validate'

type UserModalMode = 'create' | 'edit'
interface IUserForm {
  id: string
  name: string
  email: string
  phone: string
  userGroup: string[]
}
const props = defineProps<{
  userFormMode: UserModalMode
  currentUser: UserListItem | undefined
}>()
const { t } = useI18n()
const formRef = ref<FormInst | null>(null)

const userFormMode = ref<UserModalMode>(props.userFormMode)
const showModal = defineModel<boolean>('visible', { default: false })

// const userForm = ref<UserForm>(defaultUserForm)
const emit = defineEmits<{
  (e: 'cancel'): void
  (e: 'loadList'): void
}>()

const rules = {
  name: [
    {
      required: true,
      validator(_rule: FormItemRule, value: string) {
        if (value === '' || value === undefined) {
          return new Error(t('system.user.createUserNameNotNull'))
        } else if (value.length > 255) {
          return new Error(t('system.user.createUserNameOverLength'))
        }
        return true
      },
    },
  ],
  email: [
    {
      required: true,
      validator(_rule: FormItemRule, value: string) {
        if (value === '' || value === undefined) {
          return new Error(t('system.user.createUserEmailNotNull'))
        } else if (!validateEmail(value)) {
          return new Error(t('system.user.createUserEmailErr'))
        }
        return true
      },
      trigger: ['input', 'blur'],
    },
  ],
  phone: [
    {
      validator(_rule: FormItemRule, value: string) {
        if (value !== '' && value !== undefined && !validatePhone(value)) {
          return new Error(t('system.user.createUserPhoneErr'))
        }
        return true
      },
      trigger: ['input', 'blur'],
    },
  ],
  userGroup: {
    type: 'array',
    required: true,
    trigger: ['blur', 'change'],
    message: t('system.user.createUserUserGroupNotNull'),
  },
}
const userForm: IUserForm = reactive({
  id: '',
  name: '',
  email: '',
  phone: '',
  userGroup: [],
})
const { loading, send: submit } = useForm(
  (formData:CreateUserParams) => {
    // 可以在此转换表单数据并提交
    formData.userInfoList = [userForm]
    formData.userRoleIdList = [...userForm.userGroup]
    return batchCreateUser(formData)
  },
  {
    // 初始化表单数据
    initialForm: {
      userInfoList: [
        {
          name: '',
          email: '',
          phone: '',
        },
      ],
      userRoleIdList: [],
    },
  },
)
const handleClose = () => {
  emit('cancel')
  formRef.value?.restoreValidation()
  resetForm()
}
const resetForm = () => {
  userForm.id = ''
  userForm.email = ''
  userForm.name = ''
  userForm.phone = ''
  userForm.userGroup = []
}
const createUser = (isContinue?: boolean) => {
  formRef.value?.validate((errors) => {
    if (!errors) {
      submit().then((res) => {
        if (res.errorEmails !== null) {
          window.$message.error(
            t('system.user.createUserEmailExist', {
              errorEmails: res.errorEmails,
            }),
          )
        } else {
          window.$message.success(t('system.user.addUserSuccess'))
          if (!isContinue) {
            handleClose()
          }
          emit('loadList')
        }
      })
    }
  })
}
const handleSave = () => {
  if (userFormMode.value === 'create') {
    createUser()
  }
}
watchEffect(() => {
  if (props.currentUser && userFormMode.value === 'edit') {
    userForm.id = props.currentUser.id
    userForm.name = props.currentUser.name
    userForm.email = props.currentUser.email
    userForm.phone = props.currentUser.phone
      ? props.currentUser.phone.replace(/\s/g, '')
      : props.currentUser.phone
    userForm.userGroup = props.currentUser.userRoleList.map((item) => item.id)
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
      <span>
        {{
          userFormMode === 'create'
            ? t('system.user.createUserModalTitle')
            : t('system.user.editUserModalTitle')
        }}
      </span>
    </template>
    <div>
      <n-form
        ref="formRef"
        :model="userForm"
        :rules="rules"
        label-placement="left"
        label-width="auto"
        require-mark-placement="right-hanging"
      >
        <n-form-item :label="$t('system.user.createUserName')" path="name">
          <n-input
            v-model:value="userForm.name"
            :placeholder="$t('system.user.createUserNamePlaceholder')"
          />
        </n-form-item>
        <n-form-item :label="$t('system.user.createUserEmail')" path="email">
          <n-input
            v-model:value="userForm.email"
            :placeholder="$t('system.user.createUserEmailPlaceholder')"
          />
        </n-form-item>
        <n-form-item :label="$t('system.user.createUserPhone')" path="phone">
          <n-input
            v-model:value="userForm.phone"
            :placeholder="$t('system.user.createUserPhonePlaceholder')"
          />
        </n-form-item>
        <n-form-item
          :label="t('system.user.createUserUserGroup')"
          path="userGroup"
        >
          <n-select
            v-model:value="userForm.userGroup"
            :placeholder="t('system.user.createUserUserGroupPlaceholder')"
            :options="[{ label: 'admin', value: 'admin' }]"
            multiple
          />
        </n-form-item>
      </n-form>
    </div>
    <template #action>
      <div>
        <n-button :disabled="loading" @click="handleClose">
          {{ t('system.user.editUserModalCancelCreate') }}
        </n-button>
        <n-button v-if="userFormMode === 'create'" :disabled="loading">
          {{ t('system.user.editUserModalSaveAndContinue') }}
        </n-button>
        <n-button :disabled="loading" @click="handleSave">
          {{
            t(
              userFormMode === 'create'
                ? 'system.user.editUserModalCreateUser'
                : 'system.user.editUserModalEditUser',
            )
          }}
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<style scoped></style>
