import Lease from '@/components/Contract/Detail/LeaseItemList/Lease'

const Index = ({ businessVersion, contractId, isChange }) => {
  return (
    <Lease
      businessVersion={businessVersion}
      contractId={contractId}
      canEditFlag={false}
      isChange={isChange}
      isLog
    ></Lease>
  )
}

export default Index
