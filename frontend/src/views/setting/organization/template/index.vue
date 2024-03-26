<script setup lang="ts">
import { onBeforeMount, ref } from 'vue'
import { useRouter } from 'vue-router'
import TemplateItem from './components/TemplateItem.vue'
import { getCardList } from './components/field-setting'
import CardList from '/@/components/card-list/index.vue'
import { SettingRouteEnum } from '/@/enums/route-enum'

const router = useRouter()
const cardListData = ref<Record<string, number | string | boolean>[]>([])
const updateState = () => {
  cardListData.value = [...getCardList('organization')]
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
onBeforeMount(() => {
  // doCheckIsTip();
  updateState()
})
</script>
<template>
  <card-list
    mode="static"
    :card-min-width="360"
    class="flex-1"
    :shadow-limit="50"
    :list="cardListData"
    :is-proportional="false"
    :gap="16"
    padding-bottom-space="16px"
  >
    <template #item="{ item, index }">
      <template-item
        :card-item="item"
        :index="index"
        mode="organization"
        @field-setting="fieldSetting"
      />
    </template>
  </card-list>
</template>

<style scoped></style>
