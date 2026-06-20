import { getQuery, observer } from '@zswl/admin'
import { Page, Modal } from '@zswl/components'
import { useMemo, useRef } from 'react'
import Store from './store'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from './BaseInfo'
import DataList from './DataList'
import AgentMaterials from './AgentMaterials'
import CreditReport from './CreditReport'
import ReportModal from './ReportModal'
import { Button } from 'antd'
import { BusinessInfoCheck } from '@/components/BusinessInfoCheck/BusinessInfoCheckEntries'

const Detail = ({ params: { id }, taskActivityId }) => {
  const businessInfoCheckRef = useRef(null)
  const store = useMemo(() => new Store({ id, businessInfoCheckRef }), [id, businessInfoCheckRef])

  const detail = store.page.getData()
  const isFormApproval = getQuery('typeId') == 'approval'
  const isRevocation = getQuery('tab') == 'revocation'
  const canUpload = ['userTask_CreditCheckOfficer'].includes(taskActivityId)
  const canEdit = isFormApproval
    ? isRevocation || canUpload
    : ['UN_SUBMIT'].includes(detail.auditStatus)

  const showIDCard = ['userTask_headofyyglb', 'userTask_CreditCheckOfficer'].includes(
    taskActivityId
  )

  // const showIDCard = true
  const anchorList = [
    { label: '本次查询信息', href: 'baseInfo' },
    { label: '企业资料', href: 'dataList' },
    { label: '经办人资料', href: 'agentMaterials', isHide: !showIDCard },
    { label: '客户征信报告', href: 'creditReport' },
  ]

  const handleScrollToReport = () => {
    store.openCreditReportModal()
  }

  const canSearch = ['SUCCESS'].includes(detail.searchStatus)
  return (
    <Page
      store={store}
      params={{ id }}
      header={null}
      extra={[
        <Button type="primary" onClick={handleScrollToReport} disabled={!canSearch}>
          客户征信报告
        </Button>,
        <BusinessInfoCheck creditSearchId={detail?.id} needOption={false} ref={businessInfoCheckRef} />,
        !isFormApproval && (
          <Button
            type="primary"
            onClick={async () => {
              businessInfoCheckRef.current.setSubmitFn(() => store.submit())
              await businessInfoCheckRef.current.store.checkCompare()
            }}
            disabled={!canEdit}
          >
            提交申请
          </Button>
        ),
      ]}
    >
      <DetailLayout anchorList={anchorList} title="征信信息">
        <BaseInfo dataSource={detail} store={store} canEdit={canEdit} />
        <DataList mainId={detail?.id} canEdit={canEdit} dataSource={detail?.clientInfos} />
        <AgentMaterials mainId={detail?.id} canEdit={true} dataSource={detail?.clientInfos} />
        <CreditReport mainId={detail?.id} canEdit={false} dataSource={detail?.clientInfos} />
      </DetailLayout>

      <ReportModal store={store} />
    </Page>
  )
}

export default observer(Detail)
