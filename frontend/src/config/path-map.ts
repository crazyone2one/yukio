import { RouteEnum } from '/@/enums/route-enum'
export type PathMapKey = keyof typeof RouteEnum

export type PathMapRoute = (typeof RouteEnum)[PathMapKey]
