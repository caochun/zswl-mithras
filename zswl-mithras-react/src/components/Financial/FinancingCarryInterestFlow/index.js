import DirectDetail from '@/components/Financial/DirectDetail'
import FundDetail from '../FundDetail'
import { observer } from '@zswl/admin'

const Index = ({ params: { id }, query: { canEditFlags, processType } }) => {
  if (processType === 'IndirectFinancingCarryInterestFlow') {
    return <FundDetail params={{ id }} query={{ canEditFlags, processType }} />
  }

  return <DirectDetail params={{ id }} query={{ canEditFlags, processType }} />
}
export default observer(Index)
