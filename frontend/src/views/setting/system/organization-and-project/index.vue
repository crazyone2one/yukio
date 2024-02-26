<script setup lang="ts">
import { useRequest } from 'alova'
import { nextTick, onBeforeMount, ref, watch } from 'vue'
import SysOrganization from './components/SystemOrganization.vue'
import { getOrgAndProjectCount } from '/@/api/modules/setting/OrganizationAndProject'
import { useI18n } from '/@/hooks/use-i18n'

const currentTable = ref('organization')
const { t } = useI18n()
const organizationCount = ref(0)
const projectCount = ref(0)
const currentKeyword = ref('')
const keyword = ref('')
const orgTableRef = ref<InstanceType<typeof SysOrganization> | null>(null)
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
watch(
    () => currentTable.value,
    (newVal) => {
        if (newVal) {
            currentKeyword.value = ''
            keyword.value = ''
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
                <n-button>
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
                <n-radio-group v-model="currentTable" class="ml-[14px]">
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
        </div>
    </n-card>
</template>

<style scoped></style>
