import { history, observer, getQuery } from '@zswl/admin'
import { Button, Page } from '@zswl/components'
import { useEffect, useMemo } from 'react'
import TipsModal from './Components/TipsModal'
import { useRef } from 'react'
import DetailLayout from '@/components/DetailLayout'
import { BusinessInfoCheck } from '@/components/BusinessInfoCheck/BusinessInfoCheckEntries'
import BaseInfo from './Components/BaseInfo'
import LendingMaterials from '../PaymentApplication/LendingMaterials'
import InformationList from '../PaymentApplication/InformationList'
import { InsurancePolicy } from '@/components/InsurancePolicy/InsurancePolicyEntries'
import Application from './Components/Applicat'
import LeaseCheck from './Components/LeaseCheck'
import Store from './store'
import LoanReview from './Components/LoanReview'
import PublicInformation from '../PaymentApplication/PublicInformation'
import { jumpZhongDeng } from '@/utils'
import PublicCheckModal from '../PaymentApplication/PublicCheckModal'
import { EvaluationAgency } from '@/components/EvaluationAgency/EvaluationAgencyEntries'
import ZhongDengregistration from './ZhongDengregistration'
import { CreditReportSearchModal } from '@/components/Credit/CreditReportSearchEntries'
import { ProjectReviewMeetingModal as MeetingModal } from '@/components/Project/ReviewMeetingEntries'
import { TrackEventModal as TrackModal } from '@/components/TrackEvent/TrackEventModalEntries'
import { Space } from 'antd'

const PaymentApplicationDetail = ({
  params: { id },
  query: {
    isNew,
    canEditFlag = 'true',
    businessVersion,
    taskActivityId,
    modelKey,
    processInstanceId,
    taskStatus,
  },
  isNewLayout,
}) => {
  const store = useMemo(() => {
    return new Store({ businessVersion })
  }, [businessVersion])

  const isFormApproval = getQuery('typeId') == 'approval'
  const isRevocation = ['sendback', 'revocation'].includes(getQuery('tab'))

  const canEdit = canEditFlag === 'true' || isRevocation

  const detail = store.page.getData()
  const userNames = store.getLessees()
  const {
    contractCode,
    curAssigneeIds,
    processStatus,
    leaseTypeCode,
    bizTypeCode,
    createBy,
    paymentCode,
    projName,
    contractId,
    projReviewId,
  } = detail

  useEffect(() => {
    if (isNew === 'true') {
      store.setApplicationEditStatus(true)
    }
  }, [isNew])

  const businessRef = useRef()
  const anchorList = [
    { label: '合同信息' },
    { label: '本次申请' },
    { label: '保单信息' },
    { label: '租赁物查重' },
    { label: '评估机构' },
    { label: '放款审核表' },
    { label: '放款材料', isHide: isFormApproval && isNewLayout },
    { label: '资料清单', isHide: isFormApproval && isNewLayout },
  ]

  const goProcess = () => {
    const search = JSON.stringify({
      projName,
    })
    if (isFormApproval) {
      window.open(`/process/query?search=${search}`)
    } else {
      history.push(`/process/query?search=${search}`)
    }
  }
  const unApprovalCanEdit = ['UN_SUBMIT', 'APPROVAL_REJECT'].includes(processStatus)
  const publicCanEdit = isFormApproval ? canEdit : unApprovalCanEdit
  const publicShow = ['ZZ', 'ZL'].includes(bizTypeCode)
  return (
    <Page store={store.page} params={{ id, businessRef }} header={null}>
      <DetailLayout
        moduleName="paymentApplication"
        anchorList={anchorList}
        title={!isFormApproval ? `付款材料明细 - ${paymentCode}` : null}
        extra={[
          <Space wrap={false}>
            <Space wrap={true}>
              <CreditReportSearchModal params={{ projectId: id, bizSource: 'PAYMENT' }} />
              <MeetingModal id={projReviewId} processInstanceId={processInstanceId} />
              {publicShow && (
                <PublicInformation
                  paymentId={id}
                  taskActivityId={taskActivityId}
                  canEditFlag={publicCanEdit}
                  taskStatus={taskStatus}
                />
              )}
              <TrackModal
                params={{
                  contractCode,
                  createBy: unApprovalCanEdit ? createBy : '',
                  curAssigneeIds,
                  bizSource: 'PAYMENT',
                  bizId: id,
                }}
              />
              <BusinessInfoCheck
                ref={businessRef}
                paymentId={id}
                flowId={processInstanceId}
                modelKey={modelKey}
                contractId={contractId}
                taskActivityId={taskActivityId}
                taskStatus={taskStatus}
              />
              <Button
                onClick={() =>
                  jumpZhongDeng({
                    userNames,
                  })
                }
                type="link"
              >
                中登网查询
              </Button>
              <ZhongDengregistration detail={detail} userNames={userNames} />
              <Button onClick={goProcess} type="link">
                查询历史流程
              </Button>
            </Space>
            {!isFormApproval && canEdit && (
              <Button type="primary" onClick={() => store.checkPublic(store.beforeSubmit)}>
                提交审批
              </Button>
            )}
          </Space>,
        ]}
      >
        <BaseInfo id={id} store={store} goProcess={goProcess} />
        <Application store={store} canEditFlag={canEdit} />
        <InsurancePolicy
          canEditFlag={canEdit}
          mainId={id}
          businessVersion={businessVersion}
          baseDetailData={store.page.getData()}
        />
        <LeaseCheck id={id} canEditFlag={canEdit}></LeaseCheck>
        <EvaluationAgency
          id={contractId}
          canEdit={false}
          notLease
          functionCodeList={{
            detail: 'paymentLedgerAppraisalDetail',
            list: 'paymentEvaluationAgencyList',
            fileList: 'paymentEvaluationAgencyFileList',
            download: 'paymentEvaluationAgencyFileDownload',
            batchDown: 'paymentEvaluationAgencyFileBatchDownload',
          }}
        />
        <LoanReview
          id={id}
          canEdit={
            canEdit ||
            ['userTask_loanReviewPost', 'userTask_startUser', 'curTaskActivityIds'].includes(
              taskActivityId
            )
          }
        />
        <LendingMaterials
          store={store}
          isNewLayout={isNewLayout}
          canEditFlag={canEdit}
          mainId={id}
          businessVersion={businessVersion}
          title={<h4>放款材料</h4>}
        />
        <InformationList
          isNewLayout={isNewLayout}
          canEditFlag={canEdit}
          mainId={id}
          businessVersion={businessVersion}
          baseDetailData={store.page.getData()}
          title={<h4>资料清单</h4>}
        />
      </DetailLayout>
      <PublicCheckModal
        modal={store.publicModal}
        submit={store.beforeSubmit}
        id={id}
        canEdit={publicCanEdit}
      />
      <TipsModal store={store.tipsModal} />
    </Page>
  )
}
export default observer(PaymentApplicationDetail)
