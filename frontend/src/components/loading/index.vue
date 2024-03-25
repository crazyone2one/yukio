<script setup lang="ts">
import { NSpin } from 'naive-ui'

withDefaults(defineProps<{ tip?: string; show: boolean }>(), { tip: 'hello' })
</script>
<template>
  <div v-show="show">
    <n-spin size="large" />
    <div>
      {{ tip }}
    </div>
  </div>
</template>

<style scoped></style>
