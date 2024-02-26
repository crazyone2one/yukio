import { defineStore } from 'pinia'
import { useAppStore } from '../..'
import { UserState } from './types'
import { LoginRes } from '/@/models/user'
import { setToken } from '/@/utils/auth'
const useUserStore = defineStore('user', {
    persist: true,
    state: (): UserState => ({
        name: undefined,
        avatar: undefined,
        job: undefined,
        organization: undefined,
        location: undefined,
        email: undefined,
        introduction: undefined,
        personalWebsite: undefined,
        jobName: undefined,
        organizationName: undefined,
        locationName: undefined,
        phone: undefined,
        registrationDate: undefined,
        id: undefined,
        certification: undefined,
        role: '',
        userRolePermissions: [],
        loginType: [],
    }),
    actions: {
        // 设置用户信息
        setInfo(partial: Partial<UserState>) {
            this.$patch(partial)
        },
        loginFN(res: LoginRes) {
            const appStore = useAppStore()
            setToken(res.token, res.refresh_token)
            appStore.setCurrentOrgId(res.lastOrganizationId || '')
            appStore.setCurrentProjectId(res.lastProjectId || '')
            this.setInfo(res.user)
        },
    },
})
export default useUserStore
