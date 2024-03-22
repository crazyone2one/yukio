<template>
  <div v-if="!record.enable">
    <n-button text @click="emits('handleEnableUser', record)">
      {{ $t('system.user.enable') }}
    </n-button>
    <n-button text @click="emits('handleDelete', record)">
      {{ $t('system.user.delete') }}
    </n-button>
  </div>

  <div v-else>
    <n-button text type="warning" @click="emits('handleEdit', record)">
      {{ $t('system.user.editUser') }}
    </n-button>
    <slot name="more" />
  </div>
</template>

<script setup lang="ts">
import { UserListItem } from '/@/api/interface/setting/user'

defineProps<{
  record: UserListItem
}>()
const emits = defineEmits(['handleEnableUser', 'handleDelete', 'handleEdit'])
</script>

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
