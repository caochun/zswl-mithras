import ZiLiao from './ZiLiao'
import { ContractLeaseMaterials as LeaseZiLiao } from '@/components/Contract/InfoEntries'

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
