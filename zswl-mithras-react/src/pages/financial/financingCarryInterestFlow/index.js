import DirectDetail from '@/pages/financial/direct/detail/[id$]'
import FundDetail from '@/pages/financial/fund/detail/[id$]'
import { observer } from '@zswl/admin'

const Index = ({ params: { id }, query: { canEditFlags, processType } }) => {
  if (processType === 'IndirectFinancingCarryInterestFlow') {
    return <FundDetail params={{ id }} query={{ canEditFlags, processType }} />
  }

  return <DirectDetail params={{ id }} query={{ canEditFlags, processType }} />
}
export default observer(Index)
