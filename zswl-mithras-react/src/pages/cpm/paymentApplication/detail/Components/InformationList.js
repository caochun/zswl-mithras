import ZiLiao from './ZiLiao'
import LeaseZiLiao from '@/pages/contract/list/detail/LeaseZiLiao'

const InformationList = ({ baseDetailData, businessVersion, mainId, title }) => {
  const { contractId, leaseType } = baseDetailData ?? {}

  return (
    <>
      <ZiLiao canEdit={false} mainId={mainId} businessVersion={businessVersion} title={title} />
      {leaseType === '租赁-回租' && (
        <LeaseZiLiao id={contractId} businessVersion={businessVersion} />
      )}
    </>
  )
}

export default InformationList
