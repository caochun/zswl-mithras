import { useMemo } from 'react'
import { observer } from '@zswl/admin'
import FundFinancingFundDetail from '@/pages/financial/fund/detail/[id$]'
import FundFinancingFundChange from '@/pages/financial/fund/change/[id$]'
import FinancialDirect from '@/pages/financial/direct/detail/[id$]'
import FinancingRepayPlanConfirmFlow from '../../application/detail/PrepareDetail/Component/FinancingRepayPlanConfirmFlow'
import FundFilingMaterialsApply from '@/components/FilingMaterials/FundFilingMaterialsApply'
import FinancingCarryInterestFlow from '@/pages/financial/financingCarryInterestFlow/index'
const Index = (props) => {
  const { canEditFlag, subModule, id, businessVersion, modelKey, curAssigneeIds } = props
  const renderContractType = useMemo(() => {
    if (
      ['FundFinancingModifyFlow', 'FundFinancingCreateFlow', 'FinancingRecordFlow'].includes(
        modelKey
      )
    ) {
      return (
        <div>
          <FundFinancingFundDetail
            params={{ id }}
            query={{
              canEditFlags: canEditFlag ? 'true' : 'false',
              businessVersion,
              curAssigneeIds,
              changeType: modelKey === 'FundFinancingModifyFlow' ? 'other' : '',
            }}
          />
        </div>
      )
    }
    if (modelKey === 'FundFinancingEarlySettleFlow') {
      return (
        <div>
          <FundFinancingFundChange
            params={{ id }}
            query={{
              canEditFlags: canEditFlag ? 'true' : 'false',
              businessVersion,
              changeType: 'CHANGE_EARLY_SETTLE',
            }}
          />
        </div>
      )
    }

    if (modelKey === 'DirectFinancingRecordFlow') {
      return (
        <FinancialDirect params={{ id }} query={{ canEditFlags: canEditFlag ? 'true' : 'false' }} />
      )
    }

    if (['FinancingRepayWriteOffConfirmFlow', 'FinancingRepayPlanConfirmFlow'].includes(modelKey)) {
      return (
        <FinancingRepayPlanConfirmFlow
          params={{ id }}
          query={{ canEditFlag: canEditFlag ? 'true' : 'false', processType: modelKey }}
        />
      )
    }
    if (['DirectFinancingCarryInterestFlow', 'IndirectFinancingCarryInterestFlow'].includes(modelKey)) {
      return (
        <FinancingCarryInterestFlow
          params={{ id }}
          query={{ canEditFlags: canEditFlag ? 'true' : 'false', processType: modelKey }}
        />
      )
    }
    if (modelKey === 'FundFilingMaterialsApplyFlow') {
      return (
        <FundFilingMaterialsApply
          params={{ id }}
          query={{
            canEditFlags: canEditFlag ? 'true' : 'false',
          }}
        />
      )
    }
    return null
  }, [subModule, businessVersion, id, canEditFlag, modelKey])

  return renderContractType
}
export default observer(Index)
