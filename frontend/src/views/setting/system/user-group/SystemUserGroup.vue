<script setup lang="ts">
import { NResult } from 'naive-ui'
</script>
<template>
    <n-result
        status="info"
        title="信息"
        description="在这个年代，信息就是金钱，金钱就是信息。"
    >
        <template #footer>
            <n-button> system-user-group is waiting for development </n-button>
        </template>
    </n-result>
</template>

<style scoped></style>