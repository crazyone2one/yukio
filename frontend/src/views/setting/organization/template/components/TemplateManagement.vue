<script setup lang="ts">
import { onMounted, ref } from 'vue'
import router from '/@/router'
import { useRoute } from 'vue-router'
import { SettingRouteEnum } from '/@/enums/route-enum.ts'
import BaseCard from '/@/components/base-card/index.vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { getTemplateName } from './field-setting.ts'

const { t } = useI18n()
const routeName = ref<string>('')
const route = useRoute()
// 创建模板
const createTemplate = () => {
    router.push({
        name: routeName.value,
        query: {
            type: route.query.type,
        },
        params: {
            mode: 'create',
        },
    })
}
onMounted(() => {
    // fetchData();
    // updateColumns();
    if (route.query.type === 'FUNCTIONAL') {
        routeName.value = SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_CASE_DETAIL
    } else if (route.query.type === 'API') {
        routeName.value = SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_API_DETAIL
    } else {
        routeName.value = SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_BUG_DETAIL
    }
})
</script>

<template>
    <base-card simple>
        <div class="mb-4 flex items-center justify-between">
            <n-button @click="createTemplate">
                {{
                    t('system.orgTemplate.createTemplateType', {
                        type: getTemplateName('organization', route.query.type as string),
                    })
                }}
            </n-button>
        </div>
    </base-card>
</template>

<style scoped></style>
