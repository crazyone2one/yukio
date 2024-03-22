<script setup lang="ts">
import { computed, onMounted, provide, ref, watchEffect } from 'vue'
import { CurrentUserGroupItem } from '/@/api/interface/setting/usergroup'
import AuthTable from '/@/components/user-group/AuthTable.vue'
import UserGroupLeft from '/@/components/user-group/UserGroupLeft.vue'
import UserTable from '/@/components/user-group/UserTable.vue'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'

const currentTable = ref('auth')
provide('systemType', AuthScopeEnum.SYSTEM)
const { t } = useI18n()
const currentKeyword = ref('')
const ugLeftRef = ref<InstanceType<typeof UserGroupLeft> | null>(null)
const userTablRef = ref<InstanceType<typeof UserTable> | null>(null)
const authTableRef = ref<InstanceType<typeof AuthTable> | null>(null)
const currentUserGroupItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  type: AuthScopeEnum.SYSTEM,
  internal: true,
})
const couldShowUser = computed(
  () => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM,
)
const couldShowAuth = computed(() => currentUserGroupItem.value.id !== 'admin')
const handleSelect = (item: CurrentUserGroupItem) => {
  currentUserGroupItem.value = item
}
watchEffect(() => {
  if (!couldShowAuth.value) {
    currentTable.value = 'user'
  } else if (!couldShowUser.value) {
    currentTable.value = 'auth'
  } else {
    currentTable.value = 'auth'
  }
})
onMounted(() => {
  ugLeftRef.value?.initData()
})
</script>
<template>
  <n-split direction="horizontal" :default-size="0.2" :max="0.8" :min="0.2">
    <template #1>
      <user-group-left ref="ugLeftRef" @handle-select="handleSelect" />
    </template>
    <template #2>
      <div class="p-[24px]">
        <div class="flex flex-row items-center justify-between">
          <n-tooltip trigger="hover">
            <template #trigger>
              <div class="one-line-text max-w-[300px]">
                {{ currentUserGroupItem.name }}
              </div>
            </template>
            {{ currentUserGroupItem.name }}
          </n-tooltip>
          <div class="flex items-center">
            <n-input
              v-if="currentTable === 'user'"
              :placeholder="t('system.user.searchUser')"
            />
            <n-radio-group
              v-if="couldShowUser && couldShowAuth"
              v-model:value="currentTable"
              name="radiogroup"
              class="ml-[14px]"
            >
              <n-radio v-if="couldShowAuth" value="auth">
                {{ t('system.userGroup.auth') }}
              </n-radio>
              <n-radio v-if="couldShowUser" value="user">
                {{ t('system.userGroup.user') }}
              </n-radio>
            </n-radio-group>
          </div>
        </div>
        <div class="mt-[16px]">
          <user-table
            v-if="currentTable === 'user' && couldShowUser"
            ref="userTableRef"
            :keyword="currentKeyword"
            :current="currentUserGroupItem"
          />
          <auth-table
            v-if="currentTable === 'auth' && couldShowAuth"
            ref="authTableRef"
          />
        </div>
      </div>
    </template>
  </n-split>
</template>

<style scoped></style>
