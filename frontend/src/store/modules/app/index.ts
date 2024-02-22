import { defineStore } from 'pinia'
import type { AppState } from './types'
const useAppStore = defineStore('app', {
    state: (): AppState => ({
        innerHeight: 0,
    }),
})
export default useAppStore
