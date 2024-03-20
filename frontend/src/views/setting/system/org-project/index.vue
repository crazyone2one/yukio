<script setup lang="ts">
import { ref } from 'vue'
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
</script>
<template>
  <base-card :loading="false" hide-back hide-footer>
    <template #headerLeft>
      <n-button type="primary">
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
</template>

<style scoped></style>
