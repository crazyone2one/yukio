<script setup lang="ts">
import { useRequest } from 'alova'
import { computed, inject, ref } from 'vue'
import CreateOrUpdateUserGroup from './CreateOrUpdateUserGroup.vue'
import {
  CurrentUserGroupItem,
  PopVisible,
  UserGroupItem,
} from '/@/api/interface/setting/usergroup'
import { getUserGroupList } from '/@/api/modules/setting/user-group'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'

const systemType = inject<AuthScopeEnum>('systemType')
const { t } = useI18n()
const emit = defineEmits<{
  (e: 'handleSelect', element: UserGroupItem): void
  (e: 'addUserSuccess', id: string): void
}>()
// 用户组列表
const userGroupList = ref<UserGroupItem[]>([])
const currentItem = ref<CurrentUserGroupItem>({
  id: '',
  name: '',
  internal: false,
  type: AuthScopeEnum.SYSTEM,
})
const currentId = ref('')
// const currentName = computed(() => currentItem.value.name)
const popVisible = ref<PopVisible>({})
// 系统用户创建用户组visible
const systemUserGroupVisible = ref(false)
// 组织用户创建用户组visible
const orgUserGroupVisible = ref(false)
// 项目用户创建用户组visible
const projectUserGroupVisible = ref(false)
// 系统用户组Toggle
const systemToggle = ref(true)
// 组织用户组Toggle
const orgToggle = ref(true)
// 项目用户组Toggle
const projectToggle = ref(true)
const showSystem = computed(() => systemType === AuthScopeEnum.SYSTEM)
const showOrg = computed(
  () =>
    systemType === AuthScopeEnum.SYSTEM ||
    systemType === AuthScopeEnum.ORGANIZATION,
)
const showProject = computed(
  () =>
    systemType === AuthScopeEnum.SYSTEM || systemType === AuthScopeEnum.PROJECT,
)
// 系统用户组列表
const systemUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM)
})
// 组织用户组列表
const orgUserGroupList = computed(() => {
  return userGroupList.value.filter(
    (ele) => ele.type === AuthScopeEnum.ORGANIZATION,
  )
})
// 项目用户组列表
const projectUserGroupList = computed(() => {
  return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.PROJECT)
})
const handleCreateUG = (scoped: AuthScopeEnum) => {
  if (scoped === AuthScopeEnum.SYSTEM) {
    systemUserGroupVisible.value = true
  }
}
const handleListItemClick = (element: UserGroupItem) => {
  const { id, name, type, internal } = element
  currentItem.value = { id, name, type, internal }
  currentId.value = id
  emit('handleSelect', element)
}
const { send: ugList } = useRequest(getUserGroupList(), { immediate: false })
const initData = async (id?: string, isSelect = true) => {
  let res: UserGroupItem[] = []
  if (systemType === AuthScopeEnum.SYSTEM) {
    res = await ugList()
  }
  if (res.length > 0) {
    userGroupList.value = res
    if (isSelect) {
      if (id) {
        handleListItemClick(res.find((i) => i.id === id) || res[0])
      } else {
        handleListItemClick(res[0])
      }
    }
    const tmpObj: PopVisible = {}
    res.forEach((element) => {
      tmpObj[element.id] = {
        visible: false,
        authScope: element.type,
        defaultName: '',
        id: element.id,
      }
    })
    popVisible.value = tmpObj
  }
}
const handleCreateUserGroup = (id: string) => {
  initData(id)
}
const handleRenameCancel = (element: UserGroupItem, id?: string) => {
  if (id) {
    initData(id, true)
  }
  popVisible.value[element.id].visible = false
}
defineExpose({
  initData,
})
</script>
<template>
  <div class="flex flex-col px-[24px] pb-[24px]">
    <div class="sticky top-0 z-[999] bg-white pt-[24px]">
      <n-input :placeholder="t('system.userGroup.searchHolder')" />
    </div>
    <div v-if="showSystem" class="mt-2">
      <create-or-update-user-group
        :list="systemUserGroupList"
        :visible="systemUserGroupVisible"
        :auth-scope="AuthScopeEnum.SYSTEM"
        @cancel="systemUserGroupVisible = false"
        @submit="handleCreateUserGroup"
      >
        <template #trigger>
          <div class="flex items-center justify-between px-[4px] py-[7px]">
            <div class="flex flex-row items-center gap-1">
              <n-icon v-if="systemToggle" size="12">
                <div
                  class="i-mdi:arrow-expand-down cursor-pointer"
                  @click="systemToggle = false"
                />
              </n-icon>
              <n-icon v-else size="12">
                <div
                  class="i-mdi:arrow-expand-right cursor-pointer"
                  @click="systemToggle = true"
                />
              </n-icon>
              <div class="text-[14px]">
                {{ t('system.userGroup.systemUserGroup') }}
              </div>
            </div>
            <n-icon
              size="20"
              color="#0e7a0d"
              @click="handleCreateUG(AuthScopeEnum.SYSTEM)"
            >
              <div class="i-mdi:plus-circle cursor-pointer" />
            </n-icon>
          </div>
        </template>
      </create-or-update-user-group>
      <Transition>
        <div v-if="systemToggle">
          <div
            v-for="element in systemUserGroupList"
            :key="element.id"
            class="flex h-[25px] cursor-pointer items-center py-[1px] pl-[10px] pr-[4px]"
            :class="{ 'bg-green-400': element.id === currentId }"
            @click="handleListItemClick(element)"
          >
            <create-or-update-user-group
              :list="systemUserGroupList"
              v-bind="popVisible[element.id]"
              @cancel="handleRenameCancel(element)"
              @submit="handleRenameCancel(element, element.id)"
            >
              <template #trigger>
                <div
                  class="one-line-text"
                  :class="{
                    'text-[rgb(var(--primary-7))]': element.id === currentId,
                  }"
                >
                  {{ element.name }}
                </div>
                <div
                  v-if="
                    element.id === currentId && element.scopeId !== 'global'
                  "
                  class="flex flex-row items-center gap-[8px]"
                >
                  <n-button v-if="element.type === systemType">
                    {{ $t('system.userGroup.addMember') }}
                  </n-button>
                </div>
              </template>
            </create-or-update-user-group>
          </div>
          <n-divider class="my-[0px] mt-[2px]" />
        </div>
      </Transition>
    </div>
    <div v-if="showOrg" class="mt-2">
      <create-or-update-user-group
        :list="orgUserGroupList"
        :visible="orgUserGroupVisible"
        :auth-scope="AuthScopeEnum.ORGANIZATION"
        @cancel="orgUserGroupVisible = false"
      >
        <template #trigger>
          <div class="flex items-center justify-between px-[4px] py-[7px]">
            <div class="flex flex-row items-center gap-1">
              <n-icon v-if="orgToggle" size="12">
                <div
                  class="i-mdi:arrow-expand-down cursor-pointer"
                  @click="orgToggle = false"
                />
              </n-icon>
              <n-icon v-else size="12">
                <div
                  class="i-mdi:arrow-expand-right cursor-pointer"
                  @click="orgToggle = true"
                />
              </n-icon>
              <div class="text-[14px]">
                {{ t('system.userGroup.orgUserGroup') }}
              </div>
            </div>
            <n-icon
              size="20"
              color="#0e7a0d"
              @click="orgUserGroupVisible = true"
            >
              <div class="i-mdi:plus-circle cursor-pointer" />
            </n-icon>
          </div>
        </template>
      </create-or-update-user-group>
      <Transition>
        <div v-if="orgToggle">
          <div
            v-for="element in orgUserGroupList"
            :key="element.id"
            class="flex h-[25px] cursor-pointer items-center py-[1px] pl-[10px] pr-[4px]"
            :class="{ 'bg-green-400': element.id === currentId }"
            @click="handleListItemClick(element)"
          >
            <create-or-update-user-group
              :list="orgUserGroupList"
              v-bind="popVisible[element.id]"
              @cancel="handleRenameCancel(element)"
              @submit="handleRenameCancel(element, element.id)"
            >
              <template #trigger>
                <div
                  class="one-line-text"
                  :class="{
                    'text-[rgb(var(--primary-7))]': element.id === currentId,
                  }"
                >
                  {{ element.name }}
                </div>
                <div
                  v-if="
                    element.id === currentId && element.scopeId !== 'global'
                  "
                  class="flex flex-row items-center gap-[8px]"
                >
                  <n-button v-if="element.type === systemType">
                    {{ $t('system.userGroup.addMember') }}
                  </n-button>
                </div>
              </template>
            </create-or-update-user-group>
          </div>
          <n-divider class="my-[0px] mt-[2px]" />
        </div>
      </Transition>
    </div>
    <div v-if="showProject" class="mt-2">
      <create-or-update-user-group
        :list="projectUserGroupList"
        :visible="projectUserGroupVisible"
        :auth-scope="AuthScopeEnum.PROJECT"
        @cancel="projectUserGroupVisible = false"
        @submit="handleCreateUserGroup"
      >
        <template #trigger>
          <div class="flex items-center justify-between px-[4px] py-[7px]">
            <div class="flex flex-row items-center gap-1">
              <n-icon v-if="projectToggle" size="12">
                <div
                  class="i-mdi:arrow-expand-down cursor-pointer"
                  @click="projectToggle = false"
                />
              </n-icon>
              <n-icon v-else size="12">
                <div
                  class="i-mdi:arrow-expand-right cursor-pointer"
                  @click="projectToggle = true"
                />
              </n-icon>
              <div class="text-[14px]">
                {{ t('system.userGroup.projectUserGroup') }}
              </div>
            </div>
            <n-icon
              size="20"
              color="#0e7a0d"
              @click="projectUserGroupVisible = true"
            >
              <div class="i-mdi:plus-circle cursor-pointer" />
            </n-icon>
          </div>
        </template>
      </create-or-update-user-group>
      <Transition>
        <div v-if="projectToggle">
          <div
            v-for="element in projectUserGroupList"
            :key="element.id"
            class="flex h-[25px] cursor-pointer items-center py-[1px] pl-[10px] pr-[4px]"
            :class="{ 'bg-green-400': element.id === currentId }"
            @click="handleListItemClick(element)"
          >
            <create-or-update-user-group
              :list="projectUserGroupList"
              v-bind="popVisible[element.id]"
              @cancel="handleRenameCancel(element)"
              @submit="handleRenameCancel(element, element.id)"
            >
              <template #trigger>
                <div
                  class="one-line-text"
                  :class="{
                    'text-[rgb(var(--primary-7))]': element.id === currentId,
                  }"
                >
                  {{ element.name }}
                </div>
                <div
                  v-if="
                    element.id === currentId && element.scopeId !== 'global'
                  "
                  class="flex flex-row items-center gap-[8px]"
                >
                  <n-button v-if="element.type === systemType">
                    {{ $t('system.userGroup.addMember') }}
                  </n-button>
                </div>
              </template>
            </create-or-update-user-group>
          </div>
          <n-divider class="my-[0px] mt-[2px]" />
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped></style>
