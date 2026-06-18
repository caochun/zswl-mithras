import { ContractQuotationDetail as BaoJia } from '../../../DetailParts'

const Index = ({ detail, isLog, contractId }) => {
  return <BaoJia detail={detail} contractId={contractId} canEditFlag={false} isLog={isLog}></BaoJia>
}
export default Index
