import Lease from '@/pages/contract/list/detail/ZuLinWu/Lease'

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
