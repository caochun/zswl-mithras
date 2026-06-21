import { LeaseItemListContent } from '../../../../Detail/LeaseItemList'

const ContractApplicationLogLeaseItemDiff = ({ businessVersion, contractId, isChange }) => {
  return (
    <LeaseItemListContent
      businessVersion={businessVersion}
      contractId={contractId}
      canEditFlag={false}
      isChange={isChange}
      isLog
    ></LeaseItemListContent>
  )
}

export default ContractApplicationLogLeaseItemDiff
