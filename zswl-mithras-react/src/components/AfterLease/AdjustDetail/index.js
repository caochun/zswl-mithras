import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import ReviewStore from '@/components/Project/ReviewDetail/store'
import BaseInfo from '@/components/Project/ReviewDetail/BaseInfo'
import QuotationScheme from '@/components/Project/ReviewDetail/QuotationScheme'
import CashFlowStatement from '@/components/Project/ReviewDetail/CashFlowStatement'
import DataList from '@/components/Project/ReviewDetail/Data'
import ReviewData from '@/components/Project/ReviewDetail/Report'
import Exhibi from './Exhibi'
import Replay from './Replay'
import ZiLiao from './ZiLiao'
import Store from './store'
import { useEffect, useMemo } from 'react'
import DetailLayout from '@/components/DetailLayout'

const Index = ({ params: { id }, query: { canEditFlags = 'true', isCreate, businessVersion } }) => {
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  const store = useMemo(() => {
    return new Store({})
  }, [])

  const reviewStore = useMemo(() => {
    return new ReviewStore({})
  }, [])

  const { page, setShowValue } = store
  const { afterLeaseAdjustType, projId, typeText, adjustProcessStatus } = page.getData() ?? {}

  useEffect(() => {
    setShowValue(!isCreate)
  }, [isCreate])

  // 按钮权限控制，审批状态 通过、拒绝 不让编辑，审批流后端控制 + 是否主办人
  const canEditFlagsFormAuth =
    !['APPROVAL_PASS', 'APPROVAL_REJECT'].includes(adjustProcessStatus) &&
    canEditFlags === 'true' &&
    store.isProjSponsor

  const anchorList = [
    { label: '基本信息', isHide: !projId },
    { label: '报价方案', isHide: !projId },
    { label: `现金流计划表`, isHide: !projId },
    { label: `项目评审资料`, isHide: !projId },
    { label: '资料清单', isHide: !projId },
    { label: `${typeText}`, isHide: afterLeaseAdjustType !== 'EXTEND' },
    { label: `${typeText}`, isHide: afterLeaseAdjustType !== 'REPAYMENT' },
    { label: '项目调整资料' },
  ].filter(Boolean)

  const extra = [
    <Button onClick={store.cancelFlow}>取消操作</Button>,
    <Button type="primary" onClick={store.onSubmit}>
      提交审批
    </Button>,
  ]

  return (
    <Page
      store={store}
      current={`${typeText || ''}`}
      header={null}
      params={{ adjustId: id, isFormApproval, businessVersion }}
    >
      <DetailLayout
        moduleName="adjust"
        anchorList={anchorList}
        title={typeText}
        extra={!isFormApproval && canEditFlagsFormAuth ? extra : null}
      >
        <BaseInfo id={projId} canEdit={false} rootStore={reviewStore} isFormAdjust />
        <QuotationScheme id={projId} canEdit={false} isFormAdjust rootStore={reviewStore} />
        <CashFlowStatement id={projId} canEdit={false} rootStore={reviewStore} />
        <ReviewData id={projId} canEdit={false} rootStore={reviewStore} title={'项目评审资料'} />
        <DataList id={projId} canEdit={false} rootStore={reviewStore} />
        <Exhibi canEditFlag={canEditFlagsFormAuth} store={store} />
        <Replay canEditFlag={canEditFlagsFormAuth} store={store} />
        <ZiLiao
          adjustId={id}
          canEditFlag={canEditFlagsFormAuth}
          baseStore={store}
          businessVersion={businessVersion}
        />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
