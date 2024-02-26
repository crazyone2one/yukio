import { useRouter } from 'vue-router'
import { useI18n } from '/@/hooks/use-i18n'
import { useUserStore } from '/@/store'

export default function useUser() {
    const router = useRouter()
    const { t } = useI18n()
    const logout = async (logoutTo?: string) => {
        // const appStore = useAppStore();
        const userStore = useUserStore()
        await userStore.logout()
        userStore.resetInfo()
        console.log(`output->router`, router)
        const currentRoute = router.currentRoute.value
        window.$message.success(t('message.logoutSuccess'))
        router.push({
            name: logoutTo && typeof logoutTo === 'string' ? logoutTo : 'login',
            query: {
                ...router.currentRoute.value.query,
                redirect: currentRoute.name as string,
            },
        })
    }
    return {
        logout,
    }
}
