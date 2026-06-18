import { Button, Dropdown, Menu } from 'antd'
import { useEffect, useMemo, useRef } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Form, Modal, Page } from '@zswl/components'
import BaseInfo from './BaseInfo'
import Data from './Data'
import QuotationScheme from './QuotationScheme'
import Report from './Report'
import Store from './store'
import CashFlowStatement from './CashFlowStatement'
import FormIrr from '@/components/FormIrr'
import { ApprovalDetail } from '@/components/Table'
import { ApprovalAction as Approval } from '@/components/Actions'
import DetailLayout from '@/components/DetailLayout'
import FinancialReportStatistics from '../FinancialReportStatistics'
import { TrackingModal as TrackModal } from '@/components/TrackEvent/ModalEntries'
import DebtDrawer from './DebtDrawer'
import { CreditSearchModal as CreditModal } from '@/components/Credit/CreditSearchEntries'
import MeetingModal from '../ReviewMeetingModal'

const Index = ({
  params: { id },
  query: { bizType, newProject, canEditFlag = 'true', businessVersion },
  processInstanceId,
  modelKey,
  compareData = {},
  isNewLayout,
  isRiskManagerProj,
  setMaterialObj
}) => {
  const financialRef = useRef()
  const approvalRef = useRef()
  const store = useMemo(() => new Store(), [])
  const { init } = store
  const isFormApproval = getQuery('typeId') == 'approval'
  const openAmount = getQuery('modal') == 'amount'
  const detail = store.page.getData()
  const approvalParams = {
    moduleType: 'PROJ_REVIEW',
    mainId: id,
    remarkType: 'MODIFY',
  }
  const {
    isProjSponsor,
    projReviewStatus,
    approvalDetail,
    reconsiderDetail,
    processModel,
    projName,
    curAssigneeIds,
  } = detail
  const canEdit = isFormApproval ? canEditFlag === 'true' : !['CLOSED'].includes(projReviewStatus)
  const canEditFlagsFormAuth = canEdit && isProjSponsor
  const isEffect = projReviewStatus === 'TAKE_EFFECT'

  useEffect(() => {
    init(newProject === 'true', id)
  }, [newProject])
  useEffect(() => {
    if (openAmount) {
      store.debtDrawer.open()
    }
  }, [openAmount])

  const reconsiderParams = {
    moduleType: 'PROJ_REVIEW',
    mainId: id,
    remarkType: 'RECONSIDER',
  }

  const anchorList = [
    {
      label: '变更说明',
      isHide:
        approvalDetail?.remarkJsonList?.length === 0 &&
        reconsiderDetail?.remarkJsonList?.length === 0,
    },
    { label: '基本信息' },
    { label: '报价方案' },
    { label: '现金流计划表' },
    { label: '项目评审资料', isHide: isFormApproval && isNewLayout },
    { label: '资料清单', isHide: isFormApproval && isNewLayout },
  ]
  const baseExtra = [
    !isFormApproval && <MeetingModal id={id} processInstanceId={processInstanceId} />,
    <TrackModal params={{ projName, curAssigneeIds, bizSource: 'PROJ_REVIEW', bizId: id }} />,
    <CreditModal params={{ projectId: id, bizSource: 'PROJ_REVIEW' }} />,
    <Button type="link" onClick={() => store.goRat()}>
      客户评级
    </Button>,
    <Button type="link" onClick={() => store.debtDrawer.open()}>
      债项评级
    </Button>,
  ]
  const extra = [
    <Button
      type="link"
      onClick={() =>
        financialRef.current?.modal.open({
          canSubmit: false,
        })
      }
    >
      财务报表情况
    </Button>,
    <Button onClick={() => store.changeLog(bizType, id)}>版本日志</Button>,
    canEditFlagsFormAuth && (
      <>
        <Button
          onClick={() => {
            store.notice()
          }}
        >
          通知法务/风控
        </Button>
        <Approval
          beforeClick={(e) => {
            return store.beforeSubmit({
              id,
              onlyCheck: true,
            })
          }}
          onClick={(extParams) => {
            return store.submitApproval({
              id,
              onlyCheck: false,
              extParams,
              isInitCheckFinancial: true,
            })
          }}
          params={approvalParams}
          isEffect={isEffect}
          text="提交审批"
        />
      </>
    ),
  ]
  const changeCanEdit = isFormApproval && canEditFlag === 'true'
  const { ratingClientIsDone, ratingAmountIsDone, undoRatingClientList } =
    store.ratTipsModal.getInitialValues() ?? {}
  return (
    <Page
      header={null}
      store={store}
      params={{
        id,
        bizType,
        newProject: newProject === 'true',
        processInstanceId,
        businessVersion,
        isFormApproval,
        reconsiderParams,
        approvalParams,
        financialRef,
        approvalRef,
        modelKey,
      }}
    >
      <Form store={store.baseForm}>
        <DetailLayout
          anchorList={anchorList}
          title={'项目评审'}
          extra={!isFormApproval ? [...baseExtra, ...extra] : baseExtra}
          moduleName="review"
        >
          <div>
            <ApprovalDetail data={approvalDetail} params={approvalParams} canEdit={changeCanEdit} />
            <ApprovalDetail
              data={reconsiderDetail}
              params={reconsiderParams}
              canEdit={changeCanEdit}
            />
          </div>

          <BaseInfo
            id={id}
            canEdit={canEdit}
            detail={detail}
            isProjSponsor={isProjSponsor}
            rootStore={store}
          />
          <QuotationScheme
            id={id}
            compareData={compareData}
            isProjSponsor={isProjSponsor}
            rootStore={store}
            canEdit={canEdit}
            processInstanceId={processInstanceId}
          />
          <div>
            <CashFlowStatement
              id={id}
              canEdit={canEdit}
              isProjSponsor={isProjSponsor}
              processInstanceId={processInstanceId}
              rootStore={store}
            />
            <div className="z-sub-title">
              <FormIrr.Item
                isChange={compareData.qs?.[bizType]?.includes('irrPercent')}
                name="irr"
                label={'IRR'}
                rules={[{ required: true }]}
                onBlur={store.irrChange}
                handleOpen={store.handleOpen}
                canEdit={canEdit}
              />
            </div>
          </div>
          <Report
            isProjSponsor={isProjSponsor}
            processInstanceId={processInstanceId}
            id={id}
            canEdit={canEdit}
            isFormApproval={isFormApproval}
            businessVersion={businessVersion}
            processModel={processModel}
            title={'项目评审资料'}
          />
          <Data
            id={id}
            title={isRiskManagerProj ? '' : '资料清单'}
            canEdit={canEdit}
            isProjSponsor={isProjSponsor}
            businessVersion={businessVersion}
            store={store}
            isRiskManagerProj={isRiskManagerProj}
            setMaterialObj={setMaterialObj}
          />
        </DetailLayout>
      </Form>
      <FinancialReportStatistics
        ref={financialRef}
        id={id}
        onClick={(extParams) => {
          // 提交审批
          store.submitApproval({
            id,
            onlyCheck: false,
            extParams,
            isInitCheckFinancial: false,
          })
        }}
      ></FinancialReportStatistics>
      <DebtDrawer store={store.debtDrawer} params={{ projReviewId: id }} detail={detail} />
      <Modal
        title="评级校验未通过"
        store={store.ratTipsModal}
        footer={[
          !ratingClientIsDone && (
            <Button
              type="primary"
              key="submit"
              onClick={() => {
                store.ratTipsModal.close()
                store.goRat()
              }}
            >
              客户评级
            </Button>
          ),
          !ratingAmountIsDone && (
            <Button
              type="primary"
              key="debt"
              onClick={() => {
                store.ratTipsModal.close()
                store.debtDrawer.open()
              }}
            >
              债项评级
            </Button>
          ),

          <Button key="cancel" onClick={() => store.ratTipsModal.close()}>
            确定
          </Button>,
        ]}
      >
        <div>
          <div>评级信息检查结果如下，请完成或更新以下评级后再提交流程！</div>
          <div style={{ padding: 20 }}>
            {!ratingClientIsDone && (
              <div style={{ color: 'red', fontWeight: 500 }}>
                · 以下承租人/评估主体无90天内有效的客户评级信息
              </div>
            )}
            {undoRatingClientList?.map((item) => (
              <div>客户名称：{item.clientName}</div>
            ))}
            {!ratingAmountIsDone && (
              <div style={{ color: 'red', marginTop: 20, fontWeight: 500 }}>
                · 该项目无有效的债项评级信息
              </div>
            )}
          </div>
        </div>
      </Modal>
    </Page>
  )
}

export default observer(Index)
