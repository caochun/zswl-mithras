import { getQuery, observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import Data from './Data'
import Store from './store'

const Index = ({ id, taskActivityId }) => {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const prepare = getQuery('tab') == 'prepare'
  const revocation = getQuery('tab') == 'revocation'
  const sendBack = getQuery('tab') == 'sendback'
  const isPending = getQuery('curTab') == 'pending'
  const canEditFlag =
    prepare ||
    revocation ||
    sendBack ||
    (['userTask_assetManager', 'userTask_afterReview'].includes(taskActivityId) &&
      isPending)
  const { enumType } = store
  const { AFTER_LEASING_FILING } = enumType

  useEffect(() => {
    if (!id) {
      return
    }
    store.getEnumType(id)
  }, [id])

  return <Data id={id} canEdit={canEditFlag} enumType={AFTER_LEASING_FILING} />
}

export default observer(Index)
