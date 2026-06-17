import { useEffect, useMemo } from 'react'
import { observer, getQuery } from '@zswl/admin'
import Store from './store'
import Container from './Container'

const QuotationScheme = ({
  id,
  compareData,
  canEdit = true,
  isProjSponsor,
  businessVersion,
  rootStore,
}) => {
  const { bizType, page, QSShowValue, setQSShowValue, QSZLShowValue, setQSZLShowValue } = rootStore
  const store = useMemo(() => new Store(), [])
  store.businessVersion = businessVersion
  store.bizType = bizType

  const { getProjectQSDetail, newLeaseCredit, setProjectQSDetail } = store
  const isNormal = page?.getData().approvalType === 'NORMAL'

  useEffect(() => {
    getProjectQSDetail(id)
    return () => {
      setProjectQSDetail(undefined)
    }
  }, [id, bizType])

  useEffect(() => {
    return () => {
      setQSShowValue?.(true)
      setQSZLShowValue(true)
    }
  }, [])

  return (
    <Container
      projectId={id}
      isProjSponsor={isProjSponsor}
      compareData={compareData}
      canEdit={canEdit}
      showValue={QSShowValue}
      setShowValue={setQSShowValue}
      type={bizType}
      isNormal={isNormal}
      store={store}
      rootStore={rootStore}
    />
  )
}

export default observer(QuotationScheme)
