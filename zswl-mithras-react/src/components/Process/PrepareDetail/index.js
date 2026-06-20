import {
  AfterLeaseCheckPlanPrepareProcess as PrepareAfterLeaseGeneralCheck,
  AfterLeaseCheckPlanTemplate as PrepareAfterLeaseCheckReportTemplate,
} from '@/components/AfterLease/CheckPlanPrepareEntries'
import {
  ContractDepositRefundNotification as PrepareContractDepositRefundNotification,
  ContractSettlementDetail as PrepareContractEarlySettlement,
  ContractStartRentDetail as PrepareContractStartRent,
} from '@/components/Contract/ProcessPrepareDetailEntries'
import { BudgetProfitDistribution as PrepareProfitDistribution } from '@/components/Budget/ProfitDistributionEntries'
import { CustomerRatDetail as PrepareCustomerRating } from '@/components/Customer/CustomerRatingDetailEntries'
import { FilingMaterialsApply as PrepareFilingMaterialsApply } from '@/components/FilingMaterials/ApplyEntries'
import { FilingMaterialsFundApply as PrepareFundFilingMaterialsApply } from '@/components/FilingMaterials/FundApplyEntries'
import { FinancialDirectDetail as PrepareFinancialDirect } from '@/components/Financial/DirectDetailEntries'
import { FinancialFinancingCarryInterestFlow as PrepareFinancialCarryInterest } from '@/components/Financial/FinancingCarryInterestEntries'
import { FinancialFundDetail as PrepareFinancialFund } from '@/components/Financial/FundProcessEntries'
import { FinancialReportApproval as PrepareFinancialReportApproval } from '@/components/Report/FinancialReportApprovalEntries'
import PrepareFinancingRepayPlanConfirmFlow from '../FinancingRepayPlanConfirmFlow'
import { KpiProjectAllotDetail as PrepareKpiProjectAllocation } from '@/components/Kpi/ProjectAllotDetailEntries'
import { RiskOverdueListSearch as PrepareOverdueListSearch } from '@/components/Risk/OverdueEntries'
import PrepareRentPaymentNotice from '../RentPaymentNotice'
import { observer } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useMemo } from 'react'
import ExchangeRateFlow from './Component/ExchangeRateFlow'
import Store from './Store'

const Index = ({ id }) => {
  const store = useMemo(() => new Store(), [id])
  const detail = store.page.getData()

  const Comp = useMemo(() => {
    const { businessId, processType, businessData } = detail
    const processComp = {
      // MarginFlowAuto: <DepositRefundNotification id={businessId}/>,
      MarginBackNotice: <PrepareContractDepositRefundNotification id={businessId} />,
      RentPaymentNotifyFlow: <PrepareRentPaymentNotice id={businessId}></PrepareRentPaymentNotice>,
      ContractStartRentAutoFlow: <PrepareContractStartRent params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      ContractEarlySettleConfirmFlow: <PrepareContractEarlySettlement params={{ id: businessId }} query={{ canEditFlags: 'true', planType: 'SETTLE_IN_ADVANCE' }}></PrepareContractEarlySettlement>,
      KpiProjectDistributionTransferFlow: (
        <PrepareKpiProjectAllocation params={{ id: businessId }} query={{ canEditFlags: true, source: 'adjust' }}></PrepareKpiProjectAllocation>
      ),
      NewAfterLeaseCheckPlanPublishCreateFlow: (
        <PrepareAfterLeaseGeneralCheck params={{ id: businessId, commonId: id, businessData }} refresh={store.page.init}></PrepareAfterLeaseGeneralCheck>
      ),
      NewAfterLeaseCheckReportCommonlyFlow: (
        <PrepareAfterLeaseCheckReportTemplate
          params={{ id: businessId }}
          query={{
            canEditFlags: 'true',
          }}
        ></PrepareAfterLeaseCheckReportTemplate>
      ),
      RatingClientUpdateFlow: <PrepareCustomerRating params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      RatingClientCreateFlow: <PrepareCustomerRating params={{ id: businessId }} query={{ canEditFlags: 'false' }} />,
      FinancingRecordFlow: <PrepareFinancialFund params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      DirectFinancingRecordFlow: <PrepareFinancialDirect params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      FinancingFloatRateAdjustFlow: <PrepareFinancialFund params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      FinancingRepayPlanConfirmFlow: <PrepareFinancingRepayPlanConfirmFlow params={{ id: businessId }} query={{ processType: 'FinancingRepayPlanConfirmFlow' }} />,
      FinancingRepayWriteOffConfirmFlow: (
        <PrepareFinancingRepayPlanConfirmFlow params={{ id: businessId }} query={{ processType: 'FinancingRepayWriteOffConfirmFlow' }} />
      ),
      baseDataExchangeRateTodo: <ExchangeRateFlow params={{ id: businessId, detail }} query={{ canEditFlags: 'true', processType }} />,
      AssociationReportQuarterMonthFlow: <PrepareFinancialReportApproval params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      AssociationReportMainBusinessFlow: <PrepareFinancialReportApproval params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      FilingMaterialsApplyFlow: <PrepareFilingMaterialsApply params={{ id: businessId, detail }} />,
      ProjectProfitSharingFlow: <PrepareProfitDistribution params={{ id: businessId, detail }} />,
      overdueListTodo: <PrepareOverdueListSearch params={{ id: businessId, type: 'todoList' }} query={{ canEditFlags: 'true' }} />,
      FundFilingMaterialsApplyFlow: <PrepareFundFilingMaterialsApply params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      IndirectFinancingCarryInterestFlow: <PrepareFinancialCarryInterest params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      DirectFinancingCarryInterestFlow: <PrepareFinancialCarryInterest params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
    }
    return processComp[processType] ?? null
  }, [JSON.stringify(detail), id])

  const ActionBtn = useMemo(() => {
    const { processType } = detail
    let btnText = '提交审批'
    // ['FinancingRepayWriteOffConfirmFlow', 'FinancingRepayPlanConfirmFlow'].includes(processType)
    if (['NewAfterLeaseCheckPlanPublishCreateFlow', 'overdueListTodo'].includes(processType)) {
      btnText = '确认计划'
    }
    if (processType === 'MarginBackNotice') {
      btnText = '确认'
    }
    return (
      <Button type="primary" onClick={store.submitProcess} access={'process_prepare_commit'}>
        {btnText}
      </Button>
    )
  }, [JSON.stringify(detail), store])

  return (
    <Page
      params={{ id }}
      store={store.page}
      header={{
        arrow: false,
        onBack: () => void 0,
        title: detail.formName,
      }}
      extra={ActionBtn}
    >
      {Comp}
    </Page>
  )
}

export default observer(Index)
