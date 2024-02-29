<script setup lang="ts">
import { NSplit } from 'naive-ui'
import { computed, onMounted, provide, ref } from 'vue'
import AuthTable from '/@/components/user-group-comp/AuthTable.vue'
import UserGroupLeft from '/@/components/user-group-comp/UserGroupLeft.vue'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import { CurrentUserGroupItem } from '/@/models/setting/usergroup'

provide('systemType', AuthScopeEnum.SYSTEM)
const currentTable = ref('auth')
const { t } = useI18n()
const ugLeftRef = ref()
const currentUserGroupItem = ref<CurrentUserGroupItem>({
    id: '',
    name: '',
    type: AuthScopeEnum.SYSTEM,
    internal: true,
})
const handleSelect = (item: CurrentUserGroupItem) => {
    currentUserGroupItem.value = item
}
const couldShowUser = computed(() => currentUserGroupItem.value.type === AuthScopeEnum.SYSTEM)
const couldShowAuth = computed(() => currentUserGroupItem.value.id !== 'admin')
onMounted(() => {
    ugLeftRef.value?.initData()
})
</script>
<template>
    <div>
        <n-split direction="horizontal" style="height: 200px" :max="0.75" :min="0.25">
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
                                class="w-[240px]"
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
                        <div v-if="currentTable === 'user' && couldShowUser">user</div>
                        <auth-table
                            v-if="currentTable === 'auth' && couldShowAuth"
                            :current="currentUserGroupItem"
                        />
                    </div>
                </div>
            </template>
        </n-split>
    </div>
</template>

<style scoped></style>
