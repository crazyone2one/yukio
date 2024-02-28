<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { computed, onMounted, ref, watch } from 'vue'
import { postOrgList } from '/@/api/modules/setting/OrganizationAndProject'
import { updateUserOrg } from '/@/api/modules/setting/user'
import { useI18n } from '/@/hooks/use-i18n'
import { OrgProjectTableItem } from '/@/models/setting/system/orgAndProject'
import { useAppStore } from '/@/store'

const { t } = useI18n()
const appStore = useAppStore()
const wsOptions = ref<Array<SelectOption>>([])
const { send: loadWsList, onSuccess } = useRequest(() => postOrgList(), {
    immediate: false,
})
const { send: switchWorkspace } = useRequest((orgId) => updateUserOrg(orgId), {
    immediate: false,
})
const currentWorkspaceName = ref<string>('')

const workspaceId = computed({
    get: () => appStore.currentOrgId,
    set: (val) => {
        appStore.currentOrgId = val
    },
})
onSuccess((res) => {
    let _tmp = res.data
    let workspace = res.data.filter((r) => r.id === workspaceId.value)
    if (workspace.length > 0) {
        currentWorkspaceName.value = workspace[0].name
        _tmp = res.data.filter((r) => r.id !== workspaceId.value)
        _tmp.unshift(workspace[0])
    } else {
        currentWorkspaceName.value = res.data[0].name
        switchWorkspace(res.data[0].id)
    }
    _tmp.forEach((item: OrgProjectTableItem) => {
        const option: SelectOption = {
            label: item.name,
            value: item.id,
            disabled: !item.enable,
        }
        wsOptions.value.push(option)
    })
})
const handleUpdateValue = (value: string) => {
    switchWorkspace(value).then((res) => {
        appStore.currentOrgId = res.lastOrganizationId
        appStore.currentProjectId = res.lastProjectId
        window.location.reload()
    })
}
onMounted(() => {
    loadWsList()
})
watch(
    () => currentWorkspaceName.value,
    (newValue) => {
        sessionStorage.setItem('workspace_name', newValue)
    },
)
const workspaceName = computed({
    get: () => sessionStorage.getItem('workspace_name'),
    set: (val) => {
        currentWorkspaceName.value = val || ''
    },
})
</script>
<template>
    <n-popselect
        v-model:value="workspaceId"
        :options="wsOptions"
        trigger="click"
        scrollable
        @update:value="handleUpdateValue"
    >
        <n-button quaternary :type="workspaceId ? 'success' : 'warning'">
            {{ workspaceName || t('personal.switchOrg') }}
        </n-button>
    </n-popselect>
</template>

<style scoped></style>
