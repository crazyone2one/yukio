import { alovaInst } from '/@/api/index.ts'
import { LoginRes } from '/@/api/interface/user.ts'

/**
 * 登录接口
 * @param param
 */
export const loginAPI = (param: { username: string; password: string }) => {
  const methodInstance = alovaInst.Post<LoginRes>('/api/auth/login', param)
  methodInstance.meta = {
    ignoreToken: true,
  }
  return methodInstance
}
/**
 * 刷新token接口
 * @param refreshToken
 */
export const refreshTokenAPI = (refreshToken: string) => {
  const methodInstance = alovaInst.Post<LoginRes>(
    `/api/auth/refresh?refreshToken=${refreshToken}`,
  )
  methodInstance.meta = {
    authRole: 'refreshToken',
  }
  return methodInstance
}
export const logoutApi = () => {
  const method = alovaInst.Get('/api/auth/signout')
  method.meta = {
    authRole: 'logout',
  }
  return method
}
