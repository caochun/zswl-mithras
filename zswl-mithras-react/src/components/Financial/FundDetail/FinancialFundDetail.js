import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { useEffect, useMemo, useRef } from 'react'
import { Space } from 'antd'
import DetailLayout from '@/components/DetailLayout'
import BaseInfo from './BaseInfo'
import Pledge from './Pledge/FinancialFundDetailPledge'
import Scheme from './Scheme/FinancialFundDetailScheme'
import EstimateTable from './EstimateTable/FinancialFundDetailEstimateTable'
import ActualTable from './ActualTable/FinancialFundDetailActualTable'
import OtherAccount from './OtherAccount/FinancialFundDetailOtherAccount'
import RefundAccount from './RefundAccount/FinancialFundDetailRefundAccount'
import DataList from './DataList'
import { userIsProjSponsor } from '@/utils'
import Store from './store'
import { ApprovalAction as Approval } from '@/components/Actions'
import { ApprovalDetail } from '@/components/Table'
import CostDetail from './CostDetail'
import Property from './Property'

const Index = (props = {}) => {
  const { id } = props.params ?? {}
  const {
    businessVersion,
    newProject = 'false',
    canEditFlags = 'true',
    changeType = '',
    processType,
  } = props.query

  const SchemoRef = useRef()
  const store = useMemo(() => {
    return new Store({ id })
  }, [id])

  // 是否审批流页面 - test
  const isFormApproval = getQuery('typeId') == 'approval'
  const initEdit = newProject === 'true'

  const baseInfoDetail = store.page.getData()
  const baseInfoData = isFormApproval ? baseInfoDetail.newDetail : baseInfoDetail.detail
  const { approvalDetail } = baseInfoDetail

  // 状态 + 流程
  const isOtherChange = changeType === 'CHANGE_OTHER'
  const canEditApprovalStatus = ![
    'NEW_APPROVAL_PASS',
    'CHANGING_APPROVAL_PASS',
    'CHANGING_EFFECT',
  ].includes(baseInfoData?.approvalStatus)
  const canEditFlagAsStatus =
    (['CARRY_INTEREST', 'NEW', 'EFFECT'].includes(baseInfoData?.financingStatus) &&
      canEditApprovalStatus) ||
    isOtherChange
  const showSubmit = !['NEW_UNDER_APPROVAL', 'CHANGING_UNDER_APPROVAL'].includes(
    baseInfoData?.approvalStatus
  )
  // 创建人
  const canEditByCreate = userIsProjSponsor(baseInfoData?.fundManagerId)
  // 其它变更
  const canEdit = canEditFlagAsStatus && canEditFlags === 'true' && canEditByCreate
  const baseData = {
    financingId: id,
    isFormApproval,
    businessVersion,
    canEdit,
    initEdit,
    isOtherChange,
  }

  const anchorList = [
    { label: '变更说明', isHide: !approvalDetail?.remarkJsonList?.length },
    { label: '基本信息' },
    { label: '关联合同明细' },
    { label: '投放资产明细' },
    { label: '融资方案' },
    { label: '费用明细' },
    { label: '还款概算表' },
    { label: '实际还款表' },
    { label: '我司还款账户' },
    { label: '资料清单' },
  ].filter(Boolean)

  const approvalParams = {
    moduleType: 'FUND_FINANCING',
    mainId: id,
    remarkType: 'MODIFY',
  }
  const extra = useMemo(
    () => [
      !isFormApproval && (
        <Button key="log" style={{ marginRight: 8 }} onClick={() => store.changeLog(id)}>
          版本日志
        </Button>
      ),
      !isFormApproval && canEdit && isOtherChange && (
        <Space>
          <Button onClick={store.onCancel}>取消操作</Button>
          <Button type="primary" onClick={() => store.submitApproval(id, businessVersion)}>
            提交确认
          </Button>
        </Space>
      ),
      !isFormApproval && canEdit && !isOtherChange && showSubmit && (
        <Approval
          params={approvalParams}
          isEffect={baseInfoData?.financingStatus === 'EFFECT'}
          beforeClick={() => store.submitApproval(id, businessVersion, true)}
          onClick={(extParams) => store.submitApproval(id, businessVersion, false, extParams)}
        />
      ),
    ],
    [canEdit, isFormApproval, isOtherChange]
  )
  return (
    <Page
      store={store}
      params={{
        financingId: id,
        newProject: newProject === 'true',
        isFormApproval,
        businessVersion,
        SchemoRef,
        approvalParams,
      }}
      header={null}
    >
      <DetailLayout anchorList={anchorList} title="融资详情" extra={extra} moduleName="fund">
        <ApprovalDetail data={approvalDetail} params={approvalParams} />

        {!isFormApproval ? (
          <BaseInfo
            initEdit={initEdit}
            detail={baseInfoDetail.detail}
            saveData={store.postProjectBaseInfoModify}
            canEdit={canEdit}
            isOtherChange={isOtherChange}
          />
        ) : (
          <div>
            <BaseInfo
              initEdit={initEdit}
              detail={baseInfoDetail.newDetail}
              isLog={baseInfoDetail.isLog}
              canEdit={canEdit}
              isOtherChange={isOtherChange}
              saveData={store.postProjectBaseInfoModify}
            />
          </div>
        )}
        <Pledge {...baseData} />
        <Property {...baseData} baseInfoData={baseInfoData} baseStore={store} />
        <Scheme {...baseData} baseInfoData={baseInfoData} ref={SchemoRef} baseStore={store} />
        <CostDetail {...baseData} baseInfoData={baseInfoData} baseStore={store} />
        <EstimateTable {...baseData} baseInfoData={baseInfoData} baseStore={store} />
        <ActualTable {...baseData} baseInfoData={baseInfoData} baseStore={store} />
        <RefundAccount {...baseData} />
        <DataList {...baseData} baseInfoData={baseInfoData} processType={processType} />
      </DetailLayout>
    </Page>
  )
}

export default observer(Index)
