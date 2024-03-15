<template>
  <n-dropdown trigger="hover" :options="options" @select="handleSelect">
    <n-button secondary>2021年 第36周</n-button>
  </n-dropdown>
  <n-drawer v-model:show="show" :width="502">
    <n-drawer-content title="斯通纳" closable>
      <div>
        <n-input
          v-model:value="orgKeyword"
          type="text"
          :placeholder="t('personal.searchOrgPlaceholder')"
        />
        <n-divider />
        <n-spin :show="orgListLoading">
          <div class="w-full">
            <n-list hoverable clickable>
              <n-list-item v-for="item in orgList" :key="item.id">
                <div class="flex w-full">
                  <n-tooltip trigger="hover">
                    <template #trigger>
                      <div class="one-line-text max-w-[220px]">
                        {{ item.name }}
                      </div>
                    </template>
                    {{ item.name }}
                  </n-tooltip>
                  <n-tag
                    v-if="item.id === 1"
                    size="small"
                    type="success"
                    class="ml-[4px] px-[4px]"
                  >
                    {{ t('personal.currentOrg') }}
                  </n-tag>
                </div>
              </n-list-item>
            </n-list>
          </div>
        </n-spin>
      </div>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup lang="ts">
import { NAvatar, NText } from 'naive-ui'
import { h, ref } from 'vue'
import { useI18n } from '/@/hooks/use-i18n'
import { renderIcon } from '/@/utils'

const { t } = useI18n()
const show = ref(false)
const orgListLoading = ref(false)
const orgKeyword = ref('')
const orgList = ref([
  { id: 1, name: '测试1' },
  { id: 2, name: '测试2' },
])
const renderCustomHeader = () => {
  return h(
    'div',
    {
      style: 'display: flex; align-items: center; padding: 8px 12px;',
    },
    [
      h(NAvatar, {
        round: true,
        style: 'margin-right: 12px;',
        src: 'https://07akioni.oss-cn-beijing.aliyuncs.com/demo1.JPG',
      }),
      h('div', null, [
        h('div', null, [h(NText, { depth: 2 }, { default: () => '打工仔' })]),
        h('div', { style: 'font-size: 12px;' }, [
          h(
            NText,
            { depth: 3 },
            { default: () => '毫无疑问，你是办公室里最亮的星' },
          ),
        ]),
      ]),
    ],
  )
}

const options = [
  {
    key: 'header',
    type: 'render',
    render: renderCustomHeader,
  },
  {
    key: 'header-divider',
    type: 'divider',
  },
  {
    label: t('personal.info'),
    key: 'profile',
    icon: renderIcon('i-solar:user-id-linear'),
  },
  {
    label: t('personal.switchOrg'),
    key: 'switchOrg',
    icon: renderIcon('i-custom-switch'),
  },
  {
    label: t('personal.exit'),
    key: 'logout',
    icon: renderIcon('i-solar:exit-broken'),
  },
]
const handleSelect = (key: string) => {
  if ('switchOrg' === key) {
    show.value = true
  }
  window.$message.info(key)
}
</script>

<style scoped></style>
