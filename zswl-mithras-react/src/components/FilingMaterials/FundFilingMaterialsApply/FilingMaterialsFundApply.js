import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import Data from './Data'
import Store from './store'
const FilingMaterialsFundApply = ({ params: { id }, query: { canEditFlags } }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const canEdit = canEditFlags === 'true'
  const { enumType } = store
  const { FUND_FILING } = enumType

  useEffect(() => {
    if (!id) {
      return
    }
    store.getEnumType(id)
  }, [id])

  return <Data id={id} canEdit={canEdit} enumType={FUND_FILING} />
}
export default observer(FilingMaterialsFundApply)
