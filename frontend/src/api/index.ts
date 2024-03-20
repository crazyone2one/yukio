import { createAlova } from 'alova'
import GlobalFetch from 'alova/GlobalFetch'
import VueHook from 'alova/vue'
import { useI18n } from '../hooks/use-i18n'
import useAppStore from '../store/modules/app'
import { getToken } from '../utils/auth'

const logOnDev = (message: string) => {
  if (import.meta.env.MODE === 'development') {
    console.debug(message)
  }
}
/**
 * alova 实例
 */
export const alovaInst = createAlova({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // 请求超时时间，单位为毫秒，默认为0，表示永不超时
  timeout: 120 * 1000,
  statesHook: VueHook,
  requestAdapter: GlobalFetch(),

  beforeRequest(method) {
    const appStore = useAppStore()
    const token = getToken()
    method.config.headers = {
      ...method.config.headers,
      'Content-Type': 'application/json;charset=UTF-8',
      ORGANIZATION: appStore.currentOrgId,
      PROJECT: appStore.currentProjectId,
    }
    if (!method.meta?.ignoreToken) {
      method.config.headers.Authorization = token.token
    }
  },
  responded: {
    // 请求成功的拦截器
    // 当使用GlobalFetch请求适配器时，第一个参数接收Response对象
    // 第二个参数为当前请求的method实例，你可以用它同步请求前后的配置信息
    onSuccess: async (response, method) => {
      const { t } = useI18n()
      let errMessage = ''
      if (response.status >= 400) {
        switch (response.status) {
          case 400:
            break
          case 401:
            errMessage = t('api.errMsg401')
            break
          case 403:
            errMessage = t('api.errMsg403')
            break
          // 404请求不存在
          case 404:
            errMessage = t('api.errMsg404')
            break
          case 405:
            errMessage = t('api.errMsg405')
            break
          case 408:
            errMessage = t('api.errMsg408')
            break
          case 500:
            errMessage = t('api.errMsg500')
            break
          case 501:
            errMessage = t('api.errMsg501')
            break
          case 502:
            errMessage = t('api.errMsg502')
            break
          case 503:
            errMessage = t('api.errMsg503')
            break
          case 504:
            errMessage = t('api.errMsg504')
            break
          case 505:
            errMessage = t('api.errMsg505')
            break
          default:
        }
        if (errMessage) {
          window.$message.error(errMessage)
          throw new Error(errMessage)
        }
      }
      const json = await response.json()
      if (json.code !== 100200) {
        // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
        window.$message.error(json.message)
        throw new Error(json.message)
      }
      // 解析的响应数据将传给method实例的transformData钩子函数，这些函数将在后续讲解
      return method.meta?.isDownload ? response.blob() : json.data
    },
    // 请求失败的拦截器
    // 第二个参数为当前请求的method实例，你可以用它同步请求前后的配置信息
    onError: (err, method) => {
      logOnDev(method.baseURL)
      const { response, code, message, config } = err || {}
      console.log(`output->response`, response)
      console.log(`output->code`, code)
      console.log(`output->message`, message)
      console.log(`output->config`, config)
    },
  },
})
