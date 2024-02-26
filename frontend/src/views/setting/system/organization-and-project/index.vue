<script setup lang="ts">
import { useRequest } from 'alova'
import { nextTick, onBeforeMount, ref, watch } from 'vue'
import AddOrganizationModal from './components/AddOrganizationModal.vue'
import SysOrganization from './components/SystemOrganization.vue'
import SystemProject from './components/SystemProject.vue'
import { getOrgAndProjectCount } from '/@/api/modules/setting/OrganizationAndProject'
import { useI18n } from '/@/hooks/use-i18n'

const currentTable = ref('organization')
const { t } = useI18n()
const organizationCount = ref(0)
const projectCount = ref(0)
const currentKeyword = ref('')
const keyword = ref('')
const organizationVisible = ref(false)
const orgTableRef = ref<InstanceType<typeof SysOrganization> | null>(null)
const projectTableRef = ref<InstanceType<typeof SystemProject> | null>(null)
const addOrganizationModal = ref<InstanceType<typeof AddOrganizationModal> | null>(null)
const { send: initOrgAndProjectCount } = useRequest(() => getOrgAndProjectCount(), {
    // 当immediate为false时，默认不发出
    immediate: false,
})
const tableSearch = () => {
    if (currentTable.value === 'organization') {
        if (orgTableRef.value) {
            orgTableRef.value.fetchData()
        } else {
            nextTick(() => {
                orgTableRef.value?.fetchData()
            })
        }
    }
    initOrgAndProjectCount().then((res) => {
        organizationCount.value = res.organizationTotal
        projectCount.value = res.projectTotal
    })
}
const handleSearch = () => {
    currentKeyword.value = keyword.value || ''
    tableSearch()
}
const handleAddOrganization = () => {
    if (currentTable.value === 'organization') {
        organizationVisible.value = true
    } else {
        //   projectVisible.value = true;
    }
}
const handleAddOrganizationCancel = (shouldSearch: boolean) => {
    organizationVisible.value = false
    if (shouldSearch) {
        tableSearch()
    }
}
watch(
    () => currentTable.value,
    (newVal) => {
        if (newVal) {
            currentKeyword.value = ''
            keyword.value = ''
            tableSearch()
        }
    },
)
onBeforeMount(() => {
    tableSearch()
})
</script>
<template>
    <n-card>
        <div class="mb-4 flex items-center justify-between">
            <div>
                <n-button type="primary" @click="handleAddOrganization">
                    {{
                        currentTable === 'organization'
                            ? t('system.organization.createOrganization')
                            : t('system.organization.createProject')
                    }}
                </n-button>
            </div>
            <div class="flex items-center">
                <n-input
                    v-model:value="keyword"
                    :placeholder="t('system.organization.searchIndexPlaceholder')"
                    class="w-[240px]"
                    allow-clear
                    @clear="handleSearch"
                    @keyup="handleSearch"
                />
                <n-radio-group v-model:value="currentTable" class="ml-[14px]">
                    <n-radio-button value="organization">{{
                        t('system.organization.organizationCount', {
                            count: organizationCount,
                        })
                    }}</n-radio-button>
                    <n-radio-button value="project">{{
                        t('system.organization.projectCount', {
                            count: projectCount,
                        })
                    }}</n-radio-button>
                </n-radio-group>
            </div>
        </div>
        <div>
            <sys-organization
                v-if="currentTable === 'organization'"
                ref="orgTableRef"
                :keyword="currentKeyword"
            />
            <system-project
                v-if="currentTable === 'project'"
                ref="projectTableRef"
                :keyword="currentKeyword"
            />
        </div>
    </n-card>
    <add-organization-modal
        ref="addOrganizationModal"
        :visible="organizationVisible"
        @cancel="handleAddOrganizationCancel"
    />
</template>

<style scoped></style>
