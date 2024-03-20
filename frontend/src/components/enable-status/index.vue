<script setup lang="ts">
import { OrganizationListItem } from '/@/api/interface/orgnization'

defineProps<{
  record: OrganizationListItem
}>()
</script>
<template>
  <div
    v-if="record.enable"
    class="flex flex-row flex-nowrap items-center gap-[2px]"
  >
    <n-icon color="#0e7a0d">
      <div class="i-carbon:checkmark-filled" />
    </n-icon>
    <div>
      {{ $t('msTable.enable') }}
    </div>
  </div>
  <div v-else class="flex flex-row flex-nowrap items-center gap-[2px]">
    <n-icon>
      <div class="i-local-disable" />
    </n-icon>
    <div>
      {{ $t('msTable.disable') }}
    </div>
  </div>
</template>

<style scoped></style>
