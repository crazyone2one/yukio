<script setup lang="ts">
import { OrganizationListItem } from '/@/api/interface/orgnization'
import { OrgProjectTableItem } from '/@/api/interface/setting/orgAndProject.ts'

defineProps<{
  record: OrganizationListItem | OrgProjectTableItem
}>()
const emits = defineEmits([
  'handleEdit',
  'handleAddMember',
  'handleEnd',
  'handleEnable',
  'handleDelete',
])
</script>
<template>
  <div v-if="record.deleted">
    <n-button text type="primary">{{ $t('common.revokeDelete') }} </n-button>
  </div>
  <div v-else-if="!record.enable">
    <n-button text type="info" @click="emits('handleEnable', record)">
      {{ $t('common.enable') }}
    </n-button>
    <n-button text type="error" @click="emits('handleDelete', record)">
      {{ $t('common.delete') }}
    </n-button>
  </div>
  <div v-else>
    <n-button text type="warning" @click="emits('handleEdit', record)">
      {{ $t('common.edit') }}
    </n-button>
    <n-button text type="primary" @click="emits('handleAddMember', record)">
      {{ $t('system.organization.addMember') }}
    </n-button>
    <n-button text type="warning" @click="emits('handleEnd', record)">
      {{ $t('common.end') }}
    </n-button>
    <slot name="more" />
  </div>
</template>
<style scoped>
.n-button {
  &:not(:last-child) {
    @apply mr-2;
  }

  padding: 0 2px;
  font-size: 14px;
  border-radius: 2px;
}
</style>
