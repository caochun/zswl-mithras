import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import Data from './Data'
import Store from './store'
const ArchivesManagementDetail = ({ params: { id } }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const { enumType } = store
  const { FUND_FILING, OTHER_FILING } = enumType

  useEffect(() => {
    if (!id) {
      return
    }
    store.getEnumType(id)
  }, [id])

  return (
    <Page>
      <Data id={id} canEdit={false} enumType={FUND_FILING || OTHER_FILING} />
    </Page>
  )
}
export default observer(ArchivesManagementDetail)
