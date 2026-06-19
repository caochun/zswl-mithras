import { useEffect, useMemo } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { Space, Anchor } from 'antd'
import BaseInfo from './BaseInfo'
import Data from './Data'
import Report from './Report'
import DataStore from './store'
import { ApprovalAction as Approval } from '@/components/Actions'
import { ApprovalDetail } from '@/components/Table'
import DetailLayout from '@/components/DetailLayout'
import CreditModal from '../CreditSearchModal'
import { ProjectReviewMeetingModal as MeetingModal } from '@/components/Project/ProjectReviewMeetingModalEntries'

const { Link } = Anchor

const Index = ({
  params: { id },
  query: { canEditFlags = 'true', newProject, businessVersion },
  processInstanceId,
}) => {
  const newStore = useMemo(() => {
    return new DataStore()
  }, [])
  // 是否审批流页面
  const isFormApproval = getQuery('typeId') == 'approval'

  // 按钮权限控制，审批流后端控制 + 是否主办人
  const baseInfoDetail = newStore.page.getData()
  const canEditFlagsFormAuth = canEditFlags === 'true' && baseInfoDetail.newDetail?.isBizDept
  const approvalParams = {
    moduleType: 'GROUP_CREDIT_REVIEW',
    mainId: id,
    remarkType: 'MODIFY',
  }
  const reconsiderParams = {
    moduleType: 'GROUP_CREDIT_REVIEW',
    mainId: id,
    remarkType: 'RECONSIDER',
  }
  const baseExtra = [
    <CreditModal params={{ projectId: id, bizSource: 'GROUP_CREDIT_REVIEW' }} />,
    <MeetingModal
      id={id}
      processInstanceId={processInstanceId}
      projReviewType="GROUP_CREDIT_REVIEW"
      functionCode="groupCreditReviewMeetMinuteBaseInfoDetail"
    />,
  ]
  const anchorList = [
    {
      label: '变更说明',
      isHide:
        baseInfoDetail.approvalDetail?.remarkJsonList?.length === 0 &&
        baseInfoDetail.reconsiderDetail?.remarkJsonList?.length === 0,
    },
    {
      label: '基本信息',
    },
    {
      label: '授信评审资料',
    },
    {
      label: '资料清单',
    },
  ]
  const extra = [
    <Button type="link" onClick={() => newStore.goRat()}>
      客户评级
    </Button>,
    <Button style={{ marginRight: 8 }} onClick={() => newStore.changeLog(id)}>
      版本日志
    </Button>,
    canEditFlagsFormAuth && (
      <Approval
        params={approvalParams}
        loading={newStore.approvalLoading}
        isEffect={baseInfoDetail.newDetail?.groupCreditReviewStatus === 'TAKE_EFFECT'}
        beforeClick={() => newStore.submitApproval(true)}
        onClick={(extParams) => {
          newStore.submitApproval(false, extParams)
        }}
      />
    ),
  ]

  return (
    <Page
      header={null}
      store={newStore}
      params={{ id, isFormApproval, businessVersion, approvalParams, reconsiderParams }}
    >
      <DetailLayout
        anchorList={anchorList}
        title={'授信评审'}
        extra={!isFormApproval ? [...baseExtra, ...extra] : null}
        moduleName="creditReview"
      >
        <div>
          <ApprovalDetail
            data={baseInfoDetail.approvalDetail}
            params={approvalParams}
            canEdit={canEditFlagsFormAuth}
          />
          <ApprovalDetail
            data={baseInfoDetail.reconsiderDetail}
            params={reconsiderParams}
            canEdit={canEditFlagsFormAuth}
          />
        </div>
        <div>
          {!isFormApproval ? (
            <BaseInfo
              detail={{ ...baseInfoDetail.newDetail, ...newStore.newDetail }}
              saveData={newStore.postProjectBaseInfoModify}
              canEdit={canEditFlagsFormAuth}
              initEdit={newProject === 'true'}
              updateInfo={newStore.updateInfo}
            />
          ) : (
            <div>
              <BaseInfo
                detail={{ ...baseInfoDetail.newDetail, ...newStore.newDetail }}
                saveData={newStore.postProjectBaseInfoModify}
                isLog={baseInfoDetail.isLog}
                canEdit={canEditFlagsFormAuth}
                updateInfo={newStore.updateInfo}
              />
            </div>
          )}
        </div>
        <Report
          id={id}
          canEdit={canEditFlagsFormAuth}
          rootStore={newStore}
          businessVersion={businessVersion}
        />

        <Data id={id} canEdit={canEditFlagsFormAuth} businessVersion={businessVersion} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
