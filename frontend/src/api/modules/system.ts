import { OrganizationListItem } from '../interface/setting/user'
import { OrgOptionsUrl, SwitchOrgUrl } from '../requrls/system'
import { alovaInst } from '/@/api/index.ts'

export const getOrgOptions = () => {
  const methodInstance =
    alovaInst.Get<Array<OrganizationListItem>>(OrgOptionsUrl)
  //   methodInstance.meta = {
  //     ignoreToken: true,
  //   }
  return methodInstance
}

export const switchUserOrg = (organizationId: string, userId: string) =>
  alovaInst.Post(SwitchOrgUrl, { organizationId, userId })
