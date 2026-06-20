import { useEffect, useMemo, useRef } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Form, Page, Button } from '@zswl/components'
import BaseInfo from './BaseInfo/ProjectPriceBaseInfo'
import Data from './Data'
import QuotationScheme from './QuotationScheme/ProjectPriceQuotationScheme'
import Report from './Report'
import Store from './store'
import CashFlowStatement from './CashFlowStatement'
import FormIrr from '@/components/LeasePricing/FormIrrEntries'
import { ApprovalDetail } from '@/components/Table'
import { DetailLayout } from '@/components/Layout'
import FinancialReportStatistics from '../FinancialReportStatistics/ProjectFinancialReportStatistics'
import { ApprovalAction as Approval } from '@/components/Actions'

const Index = ({
  params: { id },
  query: { newProject, canEditFlag = 'true', businessVersion },
  processInstanceId,
  compareData = {},
  isNewLayout,
  modelKey,
}) => {
  const financialRef = useRef()
  const approvalRef = useRef()
  const store = useMemo(() => new Store(), [])
  const { init } = store
  const isFormApproval = getQuery('typeId') == 'approval'
  const detail = store.page.getData()
  const { bizType } = detail
  const approvalParams = {
    moduleType: 'PROJ_PRICING',
    mainId: id,
    remarkType: 'MODIFY',
  }
  const { isProjSponsor, projPricingStatus, approvalDetail, reconsiderDetail, processModel } =
    detail
  const canEdit = isFormApproval ? canEditFlag === 'true' : !['CLOSED'].includes(projPricingStatus)
  const canEditFlagsFormAuth = canEdit && isProjSponsor

  useEffect(() => {
    init(newProject === 'true', id)
  }, [newProject])

  const reconsiderParams = {
    moduleType: 'PROJ_PRICING',
    mainId: id,
    remarkType: 'RECONSIDER',
  }

  const anchorList = [
    {
      label: '变更说明',
      isHide: !(
        approvalDetail?.remarkJsonList?.length === 0 ||
        reconsiderDetail?.remarkJsonList?.length === 0
      ),
    },
    { label: '基本信息' },
    { label: '报价方案' },
    { label: '现金流计划表' },
    { label: '项目定价资料', isHide: isFormApproval && isNewLayout },
    { label: '资料清单', isHide: isFormApproval && isNewLayout },
  ]
  const isEffect = projPricingStatus === 'TAKE_EFFECT'

  const extra = [
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
            return store.beforeSubmit({ id, onlyCheck: true })
          }}
          onClick={(extParams) => {
            return store.submitApproval({ id, extParams })
          }}
          params={approvalParams}
          isEffect={isEffect}
          text="提交审批"
        />
      </>
    ),
  ]
  const changeCanEdit = isFormApproval && canEditFlag === 'true'
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
          title={'项目定价'}
          extra={!isFormApproval ? extra : []}
          moduleName="pricing"
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
            title={'项目定价资料'}
          />
          <Data
            id={id}
            title={'资料清单'}
            canEdit={canEdit}
            isProjSponsor={isProjSponsor}
            businessVersion={businessVersion}
            store={store}
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
            isPrise: false,
            onlyCheck: false,
            extParams,
            isInitCheckFinancial: false,
          })
        }}
      ></FinancialReportStatistics>
    </Page>
  )
}

export default observer(Index)
