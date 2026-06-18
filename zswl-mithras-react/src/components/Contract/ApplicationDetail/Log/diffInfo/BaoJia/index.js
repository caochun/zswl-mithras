import BaoJia from '../../../../Detail/BaoJia'

const Index = ({ detail, isLog, contractId }) => {
  return <BaoJia detail={detail} contractId={contractId} canEditFlag={false} isLog={isLog}></BaoJia>
}
export default Index
