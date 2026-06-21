import BaoJia from '../../../../Detail/BaoJia'

const ContractApplicationDiffPricing = ({ detail, isLog, contractId }) => {
  return <BaoJia detail={detail} contractId={contractId} canEditFlag={false} isLog={isLog}></BaoJia>
}
export default ContractApplicationDiffPricing
