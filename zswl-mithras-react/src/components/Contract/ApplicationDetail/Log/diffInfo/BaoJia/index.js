import { ContractDetailBaoJia as BaoJia } from '@/components/Contract/DetailEntries'

const Index = ({ detail, isLog, contractId }) => {
  return <BaoJia detail={detail} contractId={contractId} canEditFlag={false} isLog={isLog}></BaoJia>
}
export default Index
