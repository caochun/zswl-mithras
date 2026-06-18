import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import { FinancialChangeLogLayout as ChangeLogLayout } from '@/components/Financial/ChangeLogEntries'
import {
  FinancialFundDetailActualTable as ActualTable,
  FinancialFundDetailBaseInfo as BaseInfo,
  FinancialFundDetailEstimateTable as EstimateTable,
  FinancialFundDetailOtherAccount as OtherAccount,
  FinancialFundDetailPledge as Pledge,
  FinancialFundDetailRefundAccount as RefundAccount,
  FinancialFundDetailScheme as Scheme,
} from '@/components/Financial/FundDetailEntries'
import Api from '@/api/financial/fundApi'
import { FileDiff } from '@/components/FileDiff/FileDiffEntries'

function Index(props) {
  const { id } = props.params ?? {}
  const DETAIL_MAP = [
    { label: '基础信息', key: 'BASE_INFO', Component: BaseInfo, componentType: 'desc' },
    { label: '抵质押物', key: 'PLEDGE', Component: Pledge },
    { label: '融资方案', key: 'PLAN', Component: Scheme, componentType: 'desc' },
    { label: '还款概算表', key: 'REPAY_ESTIMATE', Component: EstimateTable },
    { label: '实际还款表', key: 'REPAY_ACTUAL', Component: ActualTable },
    // { label: '对方收款账户', key: 'COLLECTION_ACCOUNT', Component: OtherAccount },
    { label: '我司还款账户', key: 'PAY_ACCOUNT', Component: RefundAccount },
    {
      label: '文件变更日志',
      key: 'file',
      forceRender: true,
      render() {
        return (
          <FileDiff
            version={id}
            moduleType="FUND_FINANCING"
            options="fundFinancingMaterialsEnum"
            functionCode="financialfilelistversioncompare"
          />
        )
      },
    },
  ]

  return (
    <Page params={{ id }} header={null}>
      <ChangeLogLayout changeList={DETAIL_MAP} compareApi={() => Api.getPreVersion({ id })} />
    </Page>
  )
}

export default observer(Index)
