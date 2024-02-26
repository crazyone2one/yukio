import { WHITE_LIST_NAME } from '../router/constants'

const TOKEN = 'token'
const REFRESH_TOKEN = 'refreshToken'

// 获取token
const getToken = () => {
    return {
        [TOKEN]: localStorage.getItem(TOKEN),
        [REFRESH_TOKEN]: localStorage.getItem(REFRESH_TOKEN) || '',
    }
}
const setToken = (sessionId: string, csrfToken: string) => {
    localStorage.setItem(TOKEN, sessionId)
    localStorage.setItem(REFRESH_TOKEN, csrfToken)
}

const clearToken = () => {
    localStorage.removeItem(TOKEN)
    localStorage.removeItem(REFRESH_TOKEN)
}
const hasToken = (name: string) => {
    if (WHITE_LIST_NAME.includes(name)) {
        return true
    }
    return !!localStorage.getItem(TOKEN) && !!localStorage.getItem(REFRESH_TOKEN)
}
export { clearToken, getToken, hasToken, setToken }
