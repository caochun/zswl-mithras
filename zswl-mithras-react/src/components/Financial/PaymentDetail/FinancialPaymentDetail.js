import { observer, getQuery } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useMemo } from 'react'
import Store from './store'
import { DetailLayout } from '@/components/Layout'
import Report from './Report'
import DataList from './DataList'
import BaseInfo from './BaseInfo'
import PledgeDetail from './PledgeDetail'
import LoanIn from './LoanIn'
import Interest from './Interest'
import RepaymentPlan from './RepaymentPlan'
import MarginDetail from './MarginDetail'
import ReceiptAccount from './ReceiptAccount'
import RefundAccount from './RefundAccount'
import DirectBaseInfo from './DirectBaseInfo'

const Detail = ({
  params: { id },
  query: { newProject, canEditFlags = 'true', businessVersion },
}) => {
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])
  // 是否审批流页面
  const isFormApproval =
    getQuery('typeId') == 'approval' &&
    getQuery('tab') !== 'revocation' &&
    getQuery('tab') !== 'sendback'

  const baseInfoDetail = store.page.getData()
  const isDirect = baseInfoDetail?.baseInfo?.financingType === 'DIRECT'
  const { cashDeposit, factoringFee, licenseFee, otherFee } = baseInfoDetail?.newDetail || {}
  const isHideMarginDetail = !cashDeposit
  // 按钮权限控制，审批流后端控制 + 是否主办人
  const canEdit = canEditFlags === 'true'

  // 基本信息、质押明细、借款流入、本金和利息一览表、费用一览表、保证金明细、对方收款账户、还款账户、付款资料、资料清单
  const anchorList = [
    { label: '基本信息' },
    { label: '质押明细', isHide: isDirect },
    { label: '借款流入' },
    { label: '本金和利息一览表' },
    { label: '费用一览表' },
    { label: '保证金明细', isHide: isHideMarginDetail },
    { label: '对方收款账户', isHide: true },
    { label: '还款账户' },
    { label: '付款资料' },
    { label: '资料清单' },
  ]

  const extra = useMemo(
    () => [
      !isFormApproval && (
        <Button key="log" style={{ marginRight: 8 }} onClick={() => store.changeLog(id)}>
          版本日志
        </Button>
      ),
      !isFormApproval && canEdit && (
        <Button
          type="primary"
          onClick={() => store.submitApproval(id, businessVersion)}
          key="submit"
        >
          提交审批
        </Button>
      ),
    ],
    [canEdit]
  )
  const comParams = {
    id,
    canEdit,
    businessVersion,
    isFormApproval,
    isDirect,
  }
  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', isFormApproval }}
      header={null}
    >
      <DetailLayout
        width={140}
        anchorList={anchorList}
        title="收付款核销"
        extra={extra}
        moduleName="financialPayment"
      >
        {isDirect ? (
          <DirectBaseInfo {...comParams} isDirect={isDirect} />
        ) : (
          <BaseInfo
            detail={baseInfoDetail.newDetail}
            isLog={baseInfoDetail.isLog}
            canEdit={canEdit}
            businessVersion={businessVersion}
            saveData={store.saveData}
          />
        )}
        <PledgeDetail {...comParams} />
        <LoanIn {...comParams} />
        <Interest {...comParams} />
        <RepaymentPlan {...comParams} />
        <MarginDetail {...comParams} />
        <ReceiptAccount {...comParams} />
        <RefundAccount {...comParams} />
        <Report {...comParams} />
        <DataList {...comParams} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Detail)
