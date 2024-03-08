<script setup lang="ts">
import CardList from '/@/components/card-list/index.vue'
import { onBeforeMount, ref } from 'vue'
import TemplateItem from '/@/views/setting/organization/template/components/TemplateItem.vue'
import { getCardList } from '/@/views/setting/organization/template/components/field-setting.ts'
import useTemplateStore from '/@/store/modules/setting/template.ts'
import { useRouter } from 'vue-router'
import { SettingRouteEnum } from '/@/enums/route-enum.ts'

const templateStore = useTemplateStore()
const router = useRouter()
const cardList = ref<Record<string, any>[]>([])
// 更新状态列表
const updateState = () => {
    cardList.value = [...getCardList('organization')]
}
// 字段设置
const fieldSetting = (key: string) => {
    router.push({
        name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
        query: {
            type: key,
        },
    })
}
// 模板管理
const templateManagement = (key: string) => {
    router.push({
        name: SettingRouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
        query: {
            type: key,
        },
    })
}
onBeforeMount(() => {
    // doCheckIsTip();
    updateState()
    templateStore.getStatus()
})
</script>
<template>
    <n-card>
        <div style="display: flex !important" class="flex h-[100%] flex-col overflow-hidden">
            <card-list
                mode="static"
                :card-min-width="360"
                class="flex-1"
                :shadow-limit="50"
                :list="cardList"
                :is-proportional="false"
                :gap="16"
                padding-bottom-space="16px"
            >
                <template #item="{ item, index }">
                    <template-item
                        mode="organization"
                        :card-item="item"
                        :index="index"
                        @field-setting="fieldSetting"
                        @template-management="templateManagement"
                    />
                </template>
            </card-list>
        </div>
    </n-card>
</template>

<style scoped></style>
