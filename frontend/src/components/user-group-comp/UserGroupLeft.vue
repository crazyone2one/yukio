<script setup lang="ts">
import { useRequest } from 'alova'
import { computed, inject, ref } from 'vue'
import { ActionsItem } from '../table-more-action/types'
import CreateUserGroupPopup from './CreateOrUpdateUserGroup.vue'
import { getUserGroupList } from '/@/api/modules/setting/user-group'
import MoreAction from '/@/components/table-more-action/index.vue'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import { CurrentUserGroupItem, PopVisible, UserGroupItem } from '/@/models/setting/usergroup'

const { t } = useI18n()
const systemType = inject<AuthScopeEnum>('systemType')
// 系统用户创建用户组visible
const systemUserGroupVisible = ref(false)
// 用户组列表
const userGroupList = ref<UserGroupItem[]>([])
// 系统用户组列表
const systemUserGroupList = computed(() => {
    return userGroupList.value.filter((ele) => ele.type === AuthScopeEnum.SYSTEM)
})
const emit = defineEmits<{
    (e: 'handleSelect', element: UserGroupItem): void
    (e: 'addUserSuccess', id: string): void
}>()
const systemToggle = ref(true)
const currentId = ref('')
const currentItem = ref<CurrentUserGroupItem>({
    id: '',
    name: '',
    internal: false,
    type: AuthScopeEnum.SYSTEM,
})
const addMemberActionItem: ActionsItem[] = [
    { label: 'system.userGroup.addMember', eventTag: 'addMember', permission: [] },
]
const systemMoreAction: ActionsItem[] = [
    {
        label: 'system.userGroup.rename',
        danger: false,
        eventTag: 'rename',
        permission: [],
    },
    {
        isDivider: true,
    },
    {
        label: 'system.userGroup.delete',
        danger: true,
        eventTag: 'delete',
        permission: ['SYSTEM_USER_ROLE:READ+DELETE'],
    },
]
const handleCreateUG = (scoped: AuthScopeEnum) => {
    if (scoped === AuthScopeEnum.SYSTEM) {
        systemUserGroupVisible.value = true
    }
}
const res = ref<UserGroupItem[]>([])
const popVisible = ref<PopVisible>({})
const isSystemShowAll = computed(() => {
    return true
})
const { send: initData, onSuccess } = useRequest(
    (id?: string, isSelect = true) => {
        if (systemType === AuthScopeEnum.SYSTEM) {
            return getUserGroupList()
        } else if (systemType === AuthScopeEnum.ORGANIZATION) {
            // ORGANIZATION
        } else if (systemType === AuthScopeEnum.PROJECT) {
            // PROJECT
        }
    },
    {
        // 当immediate为false时，默认不发出
        immediate: false,
    },
)
onSuccess((resp) => {
    res.value = resp.data
    if (res.value.length > 0) {
        userGroupList.value = res.value
    }
})
const handleListItemClick = (element: UserGroupItem) => {
    const { id, name, type, internal } = element
    currentItem.value = { id, name, type, internal }
    currentId.value = id
    emit('handleSelect', element)
}
const handleRenameCancel = (element: UserGroupItem, id?: string) => {
    if (id) {
        initData(id, true)
    }
    popVisible.value[element.id].visible = false
}
const handleAddMember = () => {
    // userModalVisible.value = true;
}
const handleMoreAction = (item: ActionsItem, id: string, authScope: AuthScopeEnum) => {
    if (item.eventTag === 'rename') {
        window.$message.info('rename')
    }
}
defineExpose({ initData })
</script>
<template>
    <div class="flex flex-col px-[24px] pb-[24px]">
        <div class="sticky top-0 z-[999] bg-white pt-[24px]">
            <n-input :placeholder="t('system.userGroup.searchHolder')" />
        </div>
        <div class="mt-2">
            <create-user-group-popup
                :visible="systemUserGroupVisible"
                :auth-scope="AuthScopeEnum.SYSTEM"
                :list="systemUserGroupList"
                @cancel="systemUserGroupVisible = false"
            >
                <div class="flex items-center justify-between px-[4px] py-[7px]">
                    <div class="flex flex-row items-center gap-1 text-[var(--color-text-4)]">
                        <n-icon v-if="systemToggle" :size="20" @click="systemToggle = false">
                            <span class="i-tabler:layout-navbar-expand cursor-pointer" />
                        </n-icon>
                        <n-icon v-else :size="20" @click="systemToggle = true">
                            <span class="i-tabler:layout-sidebar-left-expand" />
                        </n-icon>
                        <div class="text-[14px]">
                            {{ t('system.userGroup.systemUserGroup') }}
                        </div>
                    </div>
                    <n-icon
                        :size="20"
                        color="#0e7a0d"
                        @click="handleCreateUG(AuthScopeEnum.SYSTEM)"
                    >
                        <span class="i-tabler:plus cursor-pointer" />
                    </n-icon>
                </div>
            </create-user-group-popup>
            <Transition>
                <div v-if="systemToggle">
                    <div
                        v-for="element in systemUserGroupList"
                        :key="element.id"
                        class="flex h-[38px] cursor-pointer items-center py-[7px] pl-[20px] pr-[4px]"
                        :class="{ 'bg-[rgb(var(--primary-1))]': element.id === currentId }"
                        @click="handleListItemClick(element)"
                    >
                        <create-user-group-popup
                            :list="systemUserGroupList"
                            v-bind="popVisible[element.id]"
                            :auth-scope="AuthScopeEnum.SYSTEM"
                            :visible="systemUserGroupVisible"
                            @cancel="handleRenameCancel(element)"
                            @submit="handleRenameCancel(element, element.id)"
                        >
                            <div
                                class="flex max-w-[100%] grow flex-row items-center justify-between"
                            >
                                <n-tooltip trigger="hover">
                                    <template #trigger>
                                        <div
                                            class="one-line-text text-slate-950"
                                            :class="{
                                                'text-blue-700': element.id === currentId,
                                            }"
                                        >
                                            {{ element.name }}
                                        </div>
                                    </template>
                                    {{ element.name }}
                                </n-tooltip>
                                <div
                                    v-if="element.id === currentId"
                                    class="flex flex-row items-center gap-[8px]"
                                >
                                    <more-action
                                        v-if="element.type === systemType"
                                        :list="addMemberActionItem"
                                        @select="handleAddMember"
                                    >
                                        <n-icon :size="16" color="#0e7a0d">
                                            <span class="i-tabler:plus cursor-pointer" />
                                        </n-icon>
                                    </more-action>
                                    <more-action
                                        v-if="isSystemShowAll"
                                        :list="systemMoreAction"
                                        @select="
                                            (value) =>
                                                handleMoreAction(
                                                    value,
                                                    element.id,
                                                    AuthScopeEnum.SYSTEM,
                                                )
                                        "
                                    >
                                        <div class="icon-button">
                                            <n-icon :size="16">
                                                <span class="i-icon-more" />
                                            </n-icon>
                                        </div>
                                    </more-action>
                                </div>
                            </div>
                        </create-user-group-popup>
                    </div>
                </div>
            </Transition>
        </div>
    </div>
</template>

<style scoped>
.icon-button {
    display: flex;
    box-sizing: border-box;
    justify-content: center;
    align-items: center;
    width: 24px;
    height: 24px;
    color: rgb(var(--primary-7));
}
.icon-button:hover {
    background-color: rgb(var(--primary-9));
}
</style>
