<template>
  <n-dropdown trigger="hover" :options="options" @select="handleSelect">
    <n-button secondary size="small">2021年 第36周</n-button>
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
import { useRequest } from 'alova'
import { NAvatar, NText } from 'naive-ui'
import { h, ref } from 'vue'
import { logoutApi } from '/@/api/modules/login'
import { useI18n } from '/@/hooks/use-i18n'
import router from '/@/router'
import useUserStore from '/@/store/modules/user'
import { renderIcon } from '/@/utils'
import { clearToken } from '/@/utils/auth'
import { removeRouteListener } from '/@/utils/route-listener'

const { t } = useI18n()
const show = ref(false)
const orgListLoading = ref(false)
const userStore = useUserStore()
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
    icon: renderIcon('i-mdi:card-account-details-outline'),
  },
  {
    label: t('personal.switchOrg'),
    key: 'switchOrg',
    icon: renderIcon('i-custom-switch'),
  },
  {
    label: t('personal.exit'),
    key: 'logout',
    icon: renderIcon('i-mdi:location-exit'),
  },
]
const { send: doLogout } = useRequest(logoutApi(), { immediate: false })
const logout = async (logoutTo?: string) => {
  doLogout()
    .then(() => {
      userStore.resetInfo()
      const currentRoute = router.currentRoute.value
      window.$message.success(t('message.logoutSuccess'))
      router.push({
        name: logoutTo && typeof logoutTo === 'string' ? logoutTo : 'login',
        query: {
          ...router.currentRoute.value.query,
          redirect: currentRoute.name as string,
        },
      })
    })
    .finally(() => {
      clearToken()
      removeRouteListener()
    })
}
const handleSelect = async (key: string) => {
  if ('switchOrg' === key) {
    show.value = true
  }
  window.$message.info(key)
  if ('logout' === key) {
    window.$dialog.warning({
      title: '警告',
      content: '你确定？',
      positiveText: '确定',
      negativeText: '不确定',
      onPositiveClick() {
        logout()
      },
    })
  }
}
</script>

<style scoped></style>
