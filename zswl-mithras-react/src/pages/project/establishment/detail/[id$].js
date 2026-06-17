import { Button } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import { Modal, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import BaseInfo from './BaseInfo'
import Data from './Data'
import QuotationScheme from './QuotationScheme'
import Report from './Report'
import Store from './store'
import TipsModal from './TipsModal'
import DetailLayout from '@/components/DetailLayout'
import CreditModal from '@/components/Credit/CreditSearchModal'

const ProjectEstablishmentDetail = ({
  params: { id },
  query: { bizType, newProject, canEditFlag, businessVersion },
  compareData = {},
}) => {
  const isFormApproval = getQuery('typeId') == 'approval'
  const store = useMemo(() => new Store(), [])
  const { setBaseInfoShowValue, setQSZLShowValue, setQSShowValue } = store
  const detail = store.page.getData()
  const { isProjSponsor, projEstablishStatus, processModel } = detail
  const canEdit = isFormApproval
    ? canEditFlag === 'true'
    : !['CLOSED'].includes(projEstablishStatus)

  const canEditFlagsFormAuth = canEdit && isProjSponsor

  useEffect(() => {
    setBaseInfoShowValue?.(!newProject)
    setQSZLShowValue?.(!newProject)
    setQSShowValue?.(!newProject)
  }, [newProject])

  const anchorList = [
    { label: '基本信息' },
    { label: '报价方案' },
    { label: '立项报告' },
    { label: '资料清单' },
  ]

  const baseExtra = [
    <CreditModal params={{ projectId: id, bizSource: 'PROJ_ESTABLISH' }} />,
    <Button type="link" onClick={() => store.goRat()}>
      客户评级
    </Button>,
  ]

  const extra = [
    <Button onClick={() => store.changeLog(bizType, id)}>版本日志</Button>,
    canEditFlagsFormAuth && (
      <Button
        type="primary"
        loading={store.approvalLoading}
        onClick={() => {
          store.validateBeforeSubmit(id)
        }}
      >
        提交审批
      </Button>
    ),
  ]

  return (
    <Page
      store={store}
      params={{ id, newProject: newProject === 'true', businessVersion, isFormApproval }}
      header={null}
    >
      <DetailLayout
        anchorList={anchorList}
        title={'立项明细'}
        extra={!isFormApproval ? [...baseExtra, ...extra] : baseExtra}
        moduleName="establishment"
      >
        <BaseInfo
          id={id}
          compareData={compareData}
          canEdit={canEdit}
          isProjSponsor={isProjSponsor}
          parentStore={store}
        />
        <QuotationScheme
          id={id}
          bizType={bizType}
          businessVersion={businessVersion}
          compareData={compareData}
          canEdit={canEdit}
          isProjSponsor={isProjSponsor}
          rootStore={store}
        />
        <Report
          id={id}
          canEdit={canEdit && isProjSponsor}
          processModel={processModel}
          businessVersion={businessVersion}
          rootStore={store}
        />
        <Data
          id={id}
          canEdit={canEdit}
          compareData={compareData}
          isProjSponsor={isProjSponsor}
          rootStore={store}
          businessVersion={businessVersion}
        />
      </DetailLayout>

      <TipsModal
        store={store.tipsModal}
        footer={[
          <Button key="cancel" onClick={() => store.tipsModal.close()}>
            取消
          </Button>,
          <Button
            type="primary"
            key="submit"
            loading={store.approvalLoading}
            onClick={() => {
              store.submitApproval(id)
            }}
          >
            仍要立项
          </Button>,
        ]}
      />
      <Modal
        store={store.ratTipsModal}
        title="提示"
        footer={[
          <Button
            type="primary"
            key="submit"
            onClick={() => {
              store.ratTipsModal.close()
              store.goRat()
            }}
          >
            客户评级
          </Button>,
          <Button key="cancel" onClick={() => store.ratTipsModal.close()}>
            确定
          </Button>,
        ]}
      >
        无90天内有效的主承租人/评估主体客户评级信息，请完成评级后再提交流程！
      </Modal>
    </Page>
  )
}

export default observer(ProjectEstablishmentDetail)
