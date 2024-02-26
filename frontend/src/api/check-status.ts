import { useI18n } from '/@/hooks/use-i18n'

export default function checkStatus(status: number, msg: string) {
    const { t } = useI18n()
    let errMessage = ''
    switch (status) {
        case 400:
            errMessage = `${msg}`
            break
        case 401:
            errMessage = msg || t('api.errMsg401')
            break
        case 403:
            errMessage = msg || t('api.errMsg403')
            break
        // 404请求不存在
        case 404:
            errMessage = msg || t('api.errMsg404')
            break
        case 405:
            errMessage = msg || t('api.errMsg405')
            break
        case 408:
            errMessage = msg || t('api.errMsg408')
            break
        case 500:
            errMessage = msg || t('api.errMsg500')
            break
        case 501:
            errMessage = msg || t('api.errMsg501')
            break
        case 502:
            errMessage = msg || t('api.errMsg502')
            break
        case 503:
            errMessage = msg || t('api.errMsg503')
            break
        case 504:
            errMessage = msg || t('api.errMsg504')
            break
        case 505:
            errMessage = msg || t('api.errMsg505')
            break
        default:
    }
    if (errMessage) {
        window.$message.error(errMessage)
    }
}
