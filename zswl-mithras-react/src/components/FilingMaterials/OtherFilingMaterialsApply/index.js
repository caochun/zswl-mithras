import { getQuery, observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import Data from './Data'
import Store from './store'

const Index = ({ id, curTaskActivityIds, taskActivityId }) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  const finished = getQuery('tab') == 'finish'
  const processQueryPage = getQuery('nav') == 'myquery'
  const showDataList =
    curTaskActivityIds === 'userTask_otherInitReview' ||
    curTaskActivityIds === 'userTask_otherReview02' ||
    taskActivityId === 'userTask_otherReview02' ||
    taskActivityId === 'userTask_otherInitReview' ||
    taskActivityId === 'userTask_otherReview' ||
    curTaskActivityIds === 'userTask_otherReview' ||
    taskActivityId === 'userTask_otheryyglbfzr' ||
    curTaskActivityIds === 'userTask_otheryyglbfzr' ||
    finished ||
    processQueryPage
  const prepare = getQuery('tab') == 'prepare'
  const revocation = getQuery('tab') == 'revocation'
  const sendBack = getQuery('tab') == 'sendback'
  const isPending = getQuery('curTab') == 'pending'
  const canEditFlag = prepare || revocation || sendBack || isPending
  const { enumType } = store
  const { OTHER_FILING } = enumType
  useEffect(() => {
    if (!id) {
      return
    }
    store.getEnumType(id)
  }, [id])

  return (
    <Data
      id={id}
      canEdit={canEditFlag}
      showDataList={showDataList}
      enumType={OTHER_FILING}
      store={store}
    />
  )
}

export default observer(Index)
