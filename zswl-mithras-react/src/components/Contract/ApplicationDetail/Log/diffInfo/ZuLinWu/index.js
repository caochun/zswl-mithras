import { LeaseItemListContent } from '@/components/Contract/Detail/LeaseItemList'

const Index = ({ businessVersion, contractId, isChange }) => {
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

export default Index
