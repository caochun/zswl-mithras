import ProfitDistribution from '@/pages/ProfitDistribution'
import AfterLeaseGeneralCheck from '@/pages/afterLease/checkPlan/Tab/CheckList/PrepareProcess' // 租后检查-一般检查
import NewAfterLeaseCheckReportCommonlyFlow from '@/pages/afterLease/checkPlan/template/[id$]' // 租后检查模版
import DepositRefundNotification from '@/components/Contract/DepositRefundNotification'
import JQsettlement from '@/pages/contract/list/settlement/[id$]' //提前结清
import KSQZstartRent from '@/pages/contract/list/startRent/[id$]' // 合同自动起租
import CustomerRat from '@/pages/customer/customerRat/detail/[id$]' // 客户评级'
import FilingMaterialsApply from '@/pages/fillingMaterialsDetail/filingMaterialsApply'
import FundFilingMaterialsApply from '@/pages/fillingMaterialsDetail/fundFilingMaterialsApply/index'
import FinancialDirect from '@/pages/financial/direct/detail/[id$]'
import FinancingCarryInterestFlow from '@/pages/financial/financingCarryInterestFlow/index'
import FinancialFund from '@/pages/financial/fund/detail/[id$]'
import FinancialReportApprovalFlow from '@/pages/financialReport/approval/index.js'
import KpiPorjectAllocation from '@/pages/kpi/projectAllot/detail/[id$]' // 绩效
import OverdueListSearch from '@/pages/overdueListSearch/index.js'
import RentPaymentNotice from '@/pages/process/Detail/RentPaymentNotice' // 租金催收，投放项⽬还款账⼾优化
import { observer } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useMemo } from 'react'
import ExchangeRateFlow from './Component/ExchangeRateFlow'
import FinancingRepayPlanConfirmFlow from './Component/FinancingRepayPlanConfirmFlow'
import Store from './Store'

const Index = ({ id }) => {
  const store = useMemo(() => new Store(), [id])
  const detail = store.page.getData()

  const Comp = useMemo(() => {
    const { businessId, processType, businessData } = detail
    const processComp = {
      // MarginFlowAuto: <DepositRefundNotification id={businessId}/>,
      MarginBackNotice: <DepositRefundNotification id={businessId} />,
      RentPaymentNotifyFlow: <RentPaymentNotice id={businessId}></RentPaymentNotice>,
      ContractStartRentAutoFlow: <KSQZstartRent params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      ContractEarlySettleConfirmFlow: <JQsettlement params={{ id: businessId }} query={{ canEditFlags: 'true', planType: 'SETTLE_IN_ADVANCE' }}></JQsettlement>,
      KpiProjectDistributionTransferFlow: (
        <KpiPorjectAllocation params={{ id: businessId }} query={{ canEditFlags: true, source: 'adjust' }}></KpiPorjectAllocation>
      ),
      NewAfterLeaseCheckPlanPublishCreateFlow: (
        <AfterLeaseGeneralCheck params={{ id: businessId, commonId: id, businessData }} refresh={store.page.init}></AfterLeaseGeneralCheck>
      ),
      NewAfterLeaseCheckReportCommonlyFlow: (
        <NewAfterLeaseCheckReportCommonlyFlow
          params={{ id: businessId }}
          query={{
            canEditFlags: 'true',
          }}
        ></NewAfterLeaseCheckReportCommonlyFlow>
      ),
      RatingClientUpdateFlow: <CustomerRat params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      RatingClientCreateFlow: <CustomerRat params={{ id: businessId }} query={{ canEditFlags: 'false' }} />,
      FinancingRecordFlow: <FinancialFund params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      DirectFinancingRecordFlow: <FinancialDirect params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      FinancingFloatRateAdjustFlow: <FinancialFund params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      FinancingRepayPlanConfirmFlow: <FinancingRepayPlanConfirmFlow params={{ id: businessId }} query={{ processType: 'FinancingRepayPlanConfirmFlow' }} />,
      FinancingRepayWriteOffConfirmFlow: (
        <FinancingRepayPlanConfirmFlow params={{ id: businessId }} query={{ processType: 'FinancingRepayWriteOffConfirmFlow' }} />
      ),
      baseDataExchangeRateTodo: <ExchangeRateFlow params={{ id: businessId, detail }} query={{ canEditFlags: 'true', processType }} />,
      AssociationReportQuarterMonthFlow: <FinancialReportApprovalFlow params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      AssociationReportMainBusinessFlow: <FinancialReportApprovalFlow params={{ id: businessId }} query={{ canEditFlags: 'true', processType }} />,
      FilingMaterialsApplyFlow: <FilingMaterialsApply params={{ id: businessId, detail }} />,
      ProjectProfitSharingFlow: <ProfitDistribution params={{ id: businessId, detail }} />,
      overdueListTodo: <OverdueListSearch params={{ id: businessId, type: 'todoList' }} query={{ canEditFlags: 'true' }} />,
      FundFilingMaterialsApplyFlow: <FundFilingMaterialsApply params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      IndirectFinancingCarryInterestFlow: <FinancingCarryInterestFlow params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
      DirectFinancingCarryInterestFlow: <FinancingCarryInterestFlow params={{ id: businessId }} query={{ canEditFlags: 'true' }} />,
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
