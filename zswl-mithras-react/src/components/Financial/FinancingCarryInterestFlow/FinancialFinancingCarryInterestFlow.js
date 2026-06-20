import DirectDetail from '../DirectDetail/FinancialDirectDetail'
import FundDetail from '../FundDetail/FinancialFundDetail'
import { observer } from '@zswl/admin'

const Index = ({ params: { id }, query: { canEditFlags, processType } }) => {
  if (processType === 'IndirectFinancingCarryInterestFlow') {
    return <FundDetail params={{ id }} query={{ canEditFlags, processType }} />
  }

  return <DirectDetail params={{ id }} query={{ canEditFlags, processType }} />
}
export default observer(Index)
