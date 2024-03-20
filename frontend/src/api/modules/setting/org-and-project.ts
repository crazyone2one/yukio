import {
  CreateOrUpdateSystemOrgParams,
  CreateOrUpdateSystemProjectParams,
} from '../../interface/orgnization'
import { alovaInst } from '/@/api/index.ts'

export const createOrUpdateOrg = (
  data: CreateOrUpdateSystemOrgParams | CreateOrUpdateSystemProjectParams,
) => alovaInst.Post('/api/v1/orgs', data)
