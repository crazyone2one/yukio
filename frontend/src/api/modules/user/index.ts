import { LoginUrl, LogoutUrl, refreshTokenUrl } from '../../requrls/user'
import instance from '/@/api'
import { LoginData, LoginRes } from '/@/models/user'

export const login = (data: LoginData) => {
    const method = instance.Post<LoginRes>(LoginUrl, data)
    method.meta = {
        authRole: 'login',
    }
    return method
}

export const refreshToken = (refreshToken: string) => {
    const method = instance.Post<LoginRes>(`${refreshTokenUrl}?refreshToken=${refreshToken}`)
    method.meta = {
        authRole: 'refreshToken',
    }
    return method
}

export const logout = () => {
    const method = instance.Get(LogoutUrl)
    method.meta = {
        authRole: 'logout',
    }
    return method
}
