import ZiLiao from './ZiLiao'
import LeaseZiLiao from '@/components/Contract/LeaseMaterials'

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
