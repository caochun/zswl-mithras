import { useMemo, useRef } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Page } from '@zswl/components'
import Store from './store'
import BaseInfo from './BaseInfo'
import AllocateInfo from './AllocateInfo'
const Index = ({ params }) => {
  const { id, detail, taskActivityId } = params
  const prepare = getQuery('tab') == 'prepare'
  const revocation = getQuery('tab') == 'revocation'
  const sendBack = getQuery('tab') == 'sendback'
  const isPending = getQuery('curTab') == 'pending'

  const store = useMemo(() => new Store(), [id])
  const baseInfoRef = useRef()
  const allocateInfoRef = useRef()
  const canEdit =
    prepare ||
    revocation ||
    sendBack ||
    (['userTask_startUser'].includes(taskActivityId) && isPending)
  const commonProps = {
    projectDistributionId: prepare ? detail.businessId : id,
    //   source,
    //   modelKey,
    //   businessVersion,
    //   curTaskActivityIds,
    //   taskStatus,
    canEdit: canEdit,
    //   canEdit: canEditFlags && canEditFormSource && isBusinesshead(baseData.contractBelongDeptId),
  }

  return (
    <Page store={store.page} params={commonProps}>
      <BaseInfo {...commonProps} editRef={baseInfoRef} store={store}></BaseInfo>
      <AllocateInfo {...commonProps} editRef={allocateInfoRef} store={store}></AllocateInfo>
    </Page>
  )
}

export default observer(Index)
