<script setup lang="ts">
import { DropdownOption, NAvatar, NIcon, NText } from 'naive-ui'
import { computed, h } from 'vue'
import { useI18n } from '/@/hooks/use-i18n'
import router from '/@/router'
import { useUserStore } from '/@/store'
import { getCurrentWeek } from '/@/utils'

const { t } = useI18n()
// const personalDrawerVisible = ref(false)
function renderCustomHeader() {
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
                    h(NText, { depth: 3 }, { default: () => '毫无疑问，你是办公室里最亮的星' }),
                ]),
            ]),
        ],
    )
}
const options: DropdownOption[] = [
    {
        key: 'header',
        type: 'render',
        render: renderCustomHeader,
    },
    {
        label: t('personal.info'),
        key: 'stmt1',
        icon: () => {
            return h(NIcon, { class: 'i-tabler:user-up' })
        },
    },
    {
        label: t('personal.switchOrg'),
        key: 'stmt2',
        icon: () => {
            return h(NIcon, { class: 'i-tabler:switch-horizontal text-blue-600' })
        },
    },
    {
        key: 'header-divider',
        type: 'divider',
    },
    {
        label: t('personal.exit'),
        key: 'stmt3',
        icon: () => {
            return h(NIcon, { class: 'i-tabler:user-x' })
        },
    },
]
// const router = useRouter()
const userStore = useUserStore()
const handleSelect = async (key: string) => {
    if (key === 'stmt3') {
        await userStore.logout()
        userStore.resetInfo()
        window.$message.success(t('message.logoutSuccess'))
        const route = router.currentRoute
        router.push(
            `/login?redirect=${route.value.path}&params=${JSON.stringify(
                route.value.query ? route.value.query : route.value.params,
            )}`,
        )
    }
}

const dat = computed(() => {
    const year = new Date().getFullYear()
    const currentWeek = getCurrentWeek()
    return `${year}年 第${currentWeek}周`
})
</script>
<template>
    <div class="nav-end">
        <n-dropdown trigger="hover" :options="options" @select="handleSelect">
            <n-button>{{ dat }}</n-button>
        </n-dropdown>
    </div>
</template>

<style scoped>
.nav-end {
    align-items: center;
    display: flex;
}
</style>
