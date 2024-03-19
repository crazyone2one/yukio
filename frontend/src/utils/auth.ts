const TOKEN = 'token'
const REFRESH_TOKEN = 'refresh_token'

// 获取token
const getToken = () => {
  return {
    [TOKEN]: localStorage.getItem(TOKEN),
    [REFRESH_TOKEN]: localStorage.getItem(REFRESH_TOKEN) || '',
  }
}
const setToken = (token: string, refresh_token: string) => {
  localStorage.setItem(TOKEN, token)
  localStorage.setItem(REFRESH_TOKEN, refresh_token)
}

const clearToken = () => {
  localStorage.removeItem(TOKEN)
  localStorage.removeItem(REFRESH_TOKEN)
}

const hasToken = () => {
  return !!localStorage.getItem(TOKEN) && !!localStorage.getItem(REFRESH_TOKEN)
}
const setLoginExpires = () => {
  localStorage.setItem('loginExpires', Date.now().toString())
}

const isLoginExpires = () => {
  const lastLoginTime = Number(localStorage.getItem('loginExpires'))
  const now = Date.now()
  const diff = now - lastLoginTime
  const thirtyDay = 24 * 60 * 60 * 1000 * 30
  return diff > thirtyDay
}
export {
  clearToken,
  getToken,
  hasToken,
  isLoginExpires,
  setLoginExpires,
  setToken,
}
