import { observer } from '@zswl/admin'
import Bifrost from './Bifrost'

function PermissionDictionaryPage() {
  return <Bifrost path={'/dictionary'} />
}

function PermissionOrganizationPage() {
  return <Bifrost path={'/permission/organization'} />
}

function PermissionRolePage() {
  return <Bifrost path={'/permission/role'} />
}

export const PermissionDictionary = observer(PermissionDictionaryPage)
export const PermissionOrganization = observer(PermissionOrganizationPage)
export const PermissionRole = observer(PermissionRolePage)
