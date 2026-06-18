import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { FinancialChangeLogLayout as ChangeLogLayout } from '@/components/Financial/ChangeLogEntries'
import {
  FinancialPaymentDetailBaseInfo as BaseInfo,
  FinancialPaymentDetailInterest as Interest,
  FinancialPaymentDetailLoanIn as LoanIn,
  FinancialPaymentDetailMarginDetail as MarginDetail,
  FinancialPaymentDetailPledgeDetail as PledgeDetail,
  FinancialPaymentDetailReceiptAccount as ReceiptAccount,
  FinancialPaymentDetailRefundAccount as RefundAccount,
  FinancialPaymentDetailRepaymentPlan as RepaymentPlan,
} from '@/components/Financial/PaymentDetailEntries'
import paymentApprovalApi from '@/api/financial/paymentApprovalApi'
import { ChangeLogDiff } from '@/components/ChangeLogDiff/ChangeLogDiffEntries'

function Index({ params: { id }, query: { bizType } }) {
  const compareData = store.page.getData()
  const DETAIL_MAP = [
    { label: '基础信息', key: 'BASE_INFO', Component: BaseInfo, componentType: 'desc' },
    { label: '抵质押物', key: 'PLEDGE', Component: PledgeDetail },
    { label: '借款流入', key: 'BORROWING', Component: LoanIn },
    { label: '本金利息一览表', key: 'CASH_FLOW', Component: Interest },
    { label: '费用一览表', key: 'EXPENSE', Component: RepaymentPlan },
    { label: '保证金明细', key: 'CASH_DEPOSIT', Component: MarginDetail },
    { label: '付款方收款账户', key: 'RECEIPT_ACCOUNT', Component: ReceiptAccount },
    { label: '还款账户', key: 'REPAY_ACCOUNT', Component: RefundAccount },
    // { label: '付款资料', key: 'MATERIALS_LIST', Component: Report },
    {
      label: '文件变更日志',
      key: 'file',
      forceRender: true,
      render() {
        return (
          <ChangeLogDiff
            version={id}
            moduleType="FUND_RECEIPT_REPAY"
            options="fundFinancingMaterialsEnum"
            functionCode="sfkfilelistversioncompare"
          />
        )
      },
    },
  ]
  return (
    <Page params={{ id }} store={store} header={null}>
      <ChangeLogLayout
        changeList={DETAIL_MAP}
        compareData={compareData}
        compareApi={() => paymentApprovalApi.postComparePreVersion({ id })}
      />
    </Page>
  )
}

export default observer(Index)
