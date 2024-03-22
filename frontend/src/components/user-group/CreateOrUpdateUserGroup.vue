<script setup lang="ts">
import { FormInst, FormItemRule } from 'naive-ui'
import { inject, reactive, ref, watchEffect } from 'vue'
import { UserGroupItem } from '/@/api/interface/setting/usergroup'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'

const { t } = useI18n()
const systemType = inject<AuthScopeEnum>('systemType')
const props = defineProps<{
  id?: string
  list: UserGroupItem[]
  visible: boolean
  defaultName?: string
  // 权限范围
  authScope: AuthScopeEnum
}>()
const emit = defineEmits<{
  (e: 'cancel', value: boolean): void
  (e: 'submit', currentId: string): void
}>()
const appStore = useAppStore()
const currentVisible = ref(props.visible)
const form = reactive({
  name: '',
})
const formRef = ref<FormInst | null>(null)
const rule = {
  name: {
    required: true,
    validator(_rule: FormItemRule, value: string) {
      if (value === '' || value === undefined) {
        return new Error(t('system.userGroup.userGroupNameIsNotNone'))
      } else {
        if (value.length > 255) {
          return new Error(t('common.nameIsTooLang'))
        }
        if (value === props.defaultName) {
          return true
        } else {
          const isExist = props.list.some((item) => item.name === value)
          if (isExist) {
            return new Error(
              t('system.userGroup.userGroupNameIsExist', { name: value }),
            )
          }
        }
      }
      return true
    },
  },
}
const handleOutsideClick = () => {
  if (currentVisible.value) {
    handleCancel()
  }
}
const handleCancel = () => {
  form.name = ''
  // loading.value = false;
  emit('cancel', false)
}
const handleBeforeOk = () => {
  formRef.value?.validate((errors) => {
    if (errors) {
      return false
    }
    if (systemType === AuthScopeEnum.SYSTEM) {
      console.log(
        `output->system`,
        `${{ id: props.id, name: form.name, type: props.authScope }}`,
      )
    } else if (systemType === AuthScopeEnum.ORGANIZATION) {
      // 组织用户组
      console.log(
        `output->org`,
        `${{ id: props.id, name: form.name, type: props.authScope, scopeId: appStore.currentOrgId }}`,
      )
    } else {
      // 项目用户组 项目用户组只有创建
      console.log(`output->org`, `${{ name: form.name }}`)
    }
  })
}
watchEffect(() => {
  currentVisible.value = props.visible
  form.name = props.defaultName || ''
})
</script>
<template>
  <n-popover
    trigger="click"
    :content-class="props.id ? 'move-left' : ''"
    class="w-[277px]"
    :show="currentVisible"
  >
    <template #trigger>
      <slot name="trigger"></slot>
    </template>
    <!-- <template #header>
      <n-text strong depth="1">
        下面就是分割线
      </n-text>
    </template> -->
    <div v-outer="handleOutsideClick">
      <div>
        <n-form ref="formRef" :model="form" label-placement="left">
          <div class="mb-[8px] text-[14px] font-medium">
            {{
              props.id
                ? t('system.userGroup.rename')
                : t('system.userGroup.createUserGroup')
            }}
          </div>
          <n-form-item path="name" :rule="rule">
            <n-input
              v-model:value="form.name"
              class="w-[243px]"
              :max="255"
              :placeholder="t('system.userGroup.pleaseInputUserGroupName')"
            />
          </n-form-item>
        </n-form>
      </div>
      <div class="flex flex-row flex-nowrap justify-end gap-2">
        <n-button size="tiny" @click="handleCancel">
          {{ t('common.cancel') }}
        </n-button>
        <n-button
          size="tiny"
          :disabled="form.name.length === 0"
          @click="handleBeforeOk"
        >
          {{ props.id ? t('common.confirm') : t('common.create') }}
        </n-button>
      </div>
    </div>
    <!-- <template #footer>
      上面就是分割线
    </template> -->
  </n-popover>
</template>

<style scoped>
.move-left {
  position: relative;
  right: 22px;
}
</style>
