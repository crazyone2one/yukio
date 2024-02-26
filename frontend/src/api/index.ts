import { createServerTokenAuthentication } from '@alova/scene-vue'
import { createAlova } from 'alova'
import GlobalFetch from 'alova/GlobalFetch'
import VueHook from 'alova/vue'
import useLocale from '../locale/use-locale'
import { useAppStore } from '../store'
import checkStatus from './check-status'
import { refreshToken } from '/@/api/modules/user'

const { onAuthRequired, onResponseRefreshToken } = createServerTokenAuthentication({
    // ...
    assignToken(method) {
        if (method.meta?.authRole === 'refreshToken') {
            method.config.headers.Authorization = `Bearer ${localStorage.getItem('refreshToken')}`
        } else {
            method.config.headers.Authorization = `Bearer ${localStorage.getItem('token')}`
        }
    },
    // login(response, method) {
    //     localStorage.setItem('token', response.token)
    //     localStorage.setItem('refresh_token', response.refresh_token)
    // },
    logout(response, method) {
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
    },
    refreshTokenOnSuccess: {
        metaMatches: {
            refreshToken: true,
        },
        // 响应时触发，可获取到response和method，并返回boolean表示token是否过期
        // 当服务端返回401时，表示token过期
        isExpired: (response, method) => {
            return response.status === 401
        },

        // 当token过期时触发，在此函数中触发刷新token
        handler: async (response, method) => {
            console.log(`output->response`, response)
            const { token, refresh_token } = await refreshToken(
                localStorage.getItem('refreshToken') || '',
            )
            localStorage.setItem('token', token)
            localStorage.setItem('refreshToken', refresh_token)
        },
    },
    refreshTokenOnError: {
        metaMatches: {
            refreshToken: true,
        },
        // 响应时触发，可获取到error和method，并返回boolean表示token是否过期
        // 当服务端返回401时，表示token过期
        isExpired: (error, method) => {
            return error.response.status === 401
        },

        // 当token过期时触发，在此函数中触发刷新token
        handler: async (error, method) => {
            const { token, refresh_token } = await refreshToken(
                localStorage.getItem('refreshToken') || '',
            )
            localStorage.setItem('token', token)
            localStorage.setItem('refreshToken', refresh_token)
        },
    },
})
const instance = createAlova({
    baseURL: import.meta.env.VITE_APP_BASE_API,
    // 请求超时时间，单位为毫秒，默认为0，表示永不超时
    timeout: 120 * 1000,
    statesHook: VueHook,
    requestAdapter: GlobalFetch(),
    beforeRequest: onAuthRequired((method) => {
        const appStore = useAppStore()
        const { currentLocale } = useLocale()
        // method.config.headers.token = 'token'
        method.config.headers['Accept-Language'] = currentLocale.value
        method.config.headers['ORGANIZATION'] = appStore.currentOrgId
        method.config.headers['PROJECT'] = appStore.currentProjectId
    }),
    responded: onResponseRefreshToken({
        onSuccess: async (response, method) => {
            if (response.status >= 400) {
                throw new Error(response.statusText)
            }
            const json = await response.json()
            if (json.code !== 100200) {
                // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
                checkStatus(json.code, json.message ?? '')
                // throw new Error(json.message)
                throw Promise.reject(json.message)
            }

            // 解析的响应数据将传给method实例的transformData钩子函数，这些函数将在后续讲解
            return method.meta?.isDownload ? response.blob() : json.data
        },
        onError: (error, method) => {
            const { response, code, message, config } = error || {}
            console.log(`output->response`, response)
            console.log(`output->code`, code)
            console.log(`output->code`, code)
            console.log(`output->message`, message)
            console.log(`output->config`, JSON.stringify(config))
            return Promise.reject(error?.response?.data?.message || error)
        },
        // onComplete: method => {
        //   //...原响应完成拦截器
        // }
    }),
})

export default instance
