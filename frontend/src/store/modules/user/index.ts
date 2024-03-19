import { defineStore } from 'pinia'
import { UserState } from './types'

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
    userRoles: [],
    userRoleRelations: [],
    loginType: [],
  }),
  getters: {
    isAdmin(state: UserState): boolean {
      if (!state.userRolePermissions) return false
      return (
        state.userRolePermissions.findIndex(
          (ur) => ur.userRole.id === 'admin',
        ) > -1
      )
    },
  },
  actions: {
    // 设置用户信息
    setInfo(partial: Partial<UserState>) {
      this.$patch(partial)
    },
    // 重置用户信息
    resetInfo() {
      this.$reset()
    },
  },
})
export default useUserStore
