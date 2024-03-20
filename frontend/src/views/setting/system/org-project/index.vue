<script setup lang="ts">
import { ref } from 'vue'
import AddOrganization from './components/AddOrganization.vue'
import SystemOrganization from './components/SystemOrganization.vue'
import SystemProject from './components/SystemProject.vue'
import BaseCard from '/@/components/base-card/index.vue'
import { useI18n } from '/@/hooks/use-i18n'

const { t } = useI18n()
const currentTable = ref('organization')
const keyword = ref('')
const organizationCount = ref(0)
const projectCount = ref(0)
const orgTableRef = ref()
const projectTableRef = ref()
const organizationVisible = ref(false)
const projectVisible = ref(false)

const handleAddOrganization = () => {
  if (currentTable.value === 'organization') {
    organizationVisible.value = true
  } else {
    projectVisible.value = true
  }
}
const handleAddOrganizationCancel = (shouldSearch: boolean) => {
  organizationVisible.value = false
  if (shouldSearch) {
    // tableSearch();
  }
}
</script>
<template>
  <base-card :loading="false" hide-back hide-footer>
    <template #headerLeft>
      <n-button type="primary" @click="handleAddOrganization">
        {{
          currentTable === 'organization'
            ? t('system.organization.createOrganization')
            : t('system.organization.createProject')
        }}
      </n-button>
    </template>
    <template #headerRight>
      <n-input
        v-model:value="keyword"
        :placeholder="t('system.organization.searchIndexPlaceholder')"
        class="w-[240px]"
      />
      <n-radio-group
        v-model:value="currentTable"
        name="radiogroup"
        class="ml-[14px]"
      >
        <n-radio value="organization" name="organization">
          {{
            t('system.organization.organizationCount', {
              count: organizationCount,
            })
          }}
        </n-radio>
        <n-radio value="project" name="project">
          {{ t('system.organization.projectCount', { count: projectCount }) }}
        </n-radio>
      </n-radio-group>
    </template>
    <div>
      <system-organization
        v-if="currentTable === 'organization'"
        ref="orgTableRef"
      />
      <system-project v-if="currentTable === 'project'" ref="projectTableRef" />
    </div>
  </base-card>
  <add-organization
    :visible="organizationVisible"
    @cancel="handleAddOrganizationCancel"
  />
</template>

<style scoped></style>
