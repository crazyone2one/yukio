<script setup lang="ts">
import { useRequest } from 'alova'
import { DataTableColumns } from 'naive-ui'
import { inject, watchEffect } from 'vue'
import { getGlobalUSetting, getOrgUSetting } from '/@/api/modules/setting/user-group'
import { AuthScopeEnum } from '/@/enums/commonEnum'
import { useI18n } from '/@/hooks/use-i18n'
import {
    AuthTableItem,
    CurrentUserGroupItem,
    UserGroupAuthSetting,
} from '/@/models/setting/usergroup'

const systemType = inject<AuthScopeEnum>('systemType')
const props = withDefaults(
    defineProps<{
        current: CurrentUserGroupItem
    }>(),
    {},
)
const { t } = useI18n()
const columns: DataTableColumns<AuthTableItem> = [
    {
        title: t('system.userGroup.function'),
        key: 'ability',
        width: 100,
    },
    {
        title: t('system.userGroup.operationObject'),
        key: 'operationObject',
        width: 150,
    },
    {
        title: t('system.userGroup.auth'),
        key: 'auth',
    },
]
const { send: globalUSetting } = useRequest((id) => getGlobalUSetting(id), {
    immediate: false,
})
const { send: orgUSetting } = useRequest((id) => getOrgUSetting(id), {
    immediate: false,
})
const initData = (id: string) => {
    let res: UserGroupAuthSetting[] = []
    if (systemType === AuthScopeEnum.SYSTEM) {
        globalUSetting(id).then((res) => {
            console.log(`output->res`, res)
        })
    } else if (systemType === AuthScopeEnum.ORGANIZATION) {
        orgUSetting(id).then((res) => {
            console.log(`output->res`, res)
        })
    }
}
watchEffect(() => {
    if (props.current.id) {
        initData(props.current.id)
    }
})
</script>
<template>
    <n-data-table :columns="columns" :data="[]" />
</template>

<style scoped></style>
