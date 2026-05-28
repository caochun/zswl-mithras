import { Page } from '@zswl/components'
import { observer } from '@zswl/admin'
import ChangeLogLayout from '@/components/ChangeLogLayout'
import BaseInfo from '@/pages/financial/fund/detail/BaseInfo'
import Pledge from '@/pages/financial/fund/detail/Pledge'
import Scheme from '@/pages/financial/fund/detail/Scheme'
import EstimateTable from '@/pages/financial/fund/detail/EstimateTable'
import ActualTable from '@/pages/financial/fund/detail/ActualTable'
import OtherAccount from '@/pages/financial/fund/detail/OtherAccount'
import RefundAccount from '@/pages/financial/fund/detail/RefundAccount'
import Api from '@/pages/financial/fund/api'
import FileDiff from '@/components/FileDiff'

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
