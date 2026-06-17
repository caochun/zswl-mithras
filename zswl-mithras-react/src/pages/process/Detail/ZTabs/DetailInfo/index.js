import ProfitDistribution from '@/components/Budget/ProfitDistribution'
import PolicyRemind from '@/pages/afterLease/policyManage/remind/[id$]'
import CreditReportSelectFlow from '@/pages/creditManage/search/detail/[id$].js'
import ApplyPermission from '@/pages/customer/maintain/applyPermission/[id$].js'
import CustomerDetail from '@/pages/customer/maintain/detail/[id$]'
import FilingMaterialsApply from '@/pages/fillingMaterialsDetail/filingMaterialsApply'
import Adjust from '@/pages/process/Detail/Adjust'
import AfterLeaseCheck from '@/pages/process/Detail/AfterLeaseCheck'
import AfterLeaseCheckExternal from '@/pages/process/Detail/AfterLeaseCheckExternal'
import AfterLeaseCheckReport from '@/pages/process/Detail/AfterLeaseCheckReport'
import AfterLeaseRentCollection from '@/pages/process/Detail/AfterLeaseRentCollection'
import ArchivesInfo from '@/pages/process/Detail/ArchivesInfo'
import ArchivesTemplate from '@/pages/process/Detail/ArchivesTemplate'
import BatchFundReceiptRepay from '@/pages/process/Detail/BatchFundReceiptRepay'
import ClientTransfer from '@/pages/process/Detail/ClientTransfer'
import { useFlowData } from '@/utils/processFlow'
import ContractApplication from '@/pages/process/Detail/ContractApplication'
import CreateReportTable from '@/pages/process/Detail/CreateReportTable'
import FinancialFund from '@/pages/process/Detail/FinancialFund'
import FtpBusiness from '@/pages/process/Detail/FtpBusiness'
import FundReceiptRepay from '@/pages/process/Detail/FundReceiptRepay'
import GroupCreditEstablish from '@/pages/process/Detail/GroupCreditEstablish'
import GroupCreditReview from '@/pages/process/Detail/GroupCreditReview'
import KpiPmAssess from '@/pages/process/Detail/KpiPmAssess'
import KpiPorjectAllocation from '@/pages/process/Detail/KpiPorjectAllocation'
import LeaseMaintain from '@/pages/process/Detail/LeaseMaintain'
import Leave5Type from '@/pages/process/Detail/Leave5Type'
import Leave5TypeDetail from '@/pages/process/Detail/Leave5TypeDetail'
import MarginRefund from '@/pages/process/Detail/MarginRefund'
import PaymentApplication from '@/pages/process/Detail/PaymentApplication'
import ProjPricing from '@/pages/process/Detail/ProjPricing'
import ProjectEstablishment from '@/pages/process/Detail/ProjectEstablishment'
import ProjectReview from '@/pages/process/Detail/ProjectReview'
import RentPaymentNotice from '@/pages/process/Detail/RentPaymentNotice'
import TrackEvent from '@/pages/process/Detail/TrackEvent'
import { ErrorBoundary } from '@zswl/admin'
import { Empty, Tag } from 'antd'
import { cloneElement, useMemo } from 'react'
import AfterFilingMaterialsApply from '../../AfterFilingMaterialsApply'
import AppraisalCompanyWhitelistCreateFlow from '../../AppraisalCompanyWhitelistCreateFlow'
import BudgetExamineFlow from '../../BudgetExamineFlow'
import BudgetPlanPayment from '../../BudgetPlanPayment'
import FinanceOverdue from '../../FinanceOverdue'
import FinancialReport from '../../FinancialReport'
import FtpInterestChange from '../../FtpInterestChange'
import LitigationDocDetail from '../../LitigationDocDetail'
import MonthPlanEventFlow from '../../MonthPlanEventFlow'
import OverdueDetail from '../../OverdueDetail'
import RatingClient from '../../RatingClient'
import RatingDebt from '../../RatingDebt'
import RiskOption from '../../RiskOption'
import OtherFilingMaterialsApply from '../../OtherFilingMaterialsApply'
const Index = () => {
  const { detailData, canEditFlag, id, curTab, isNewLayout, setEditing, registerCallback, isRiskManagerProj, setMaterialObj } = useFlowData()
  const {
    subModule,
    mainModule,
    businessKey,
    modelKey,
    canEditFlag: nodeCanEditFlag,
    processInstanceId,
    taskActivityId,
    curTaskActivityIds,
    businessVersion,
    curAssigneeIds,
    taskStatus,
    dynamicFormKeyList,
    processStatus,
    startUserId,
    taskId,
    processName,
    startUserName,
    startUserDeptName,
    modelCode,
    clientId,
  } = detailData
  const CurrentModule = useMemo(() => {
    const commonProps = {
      processStatus,
      businessVersion,
      canEditFlag,
      processInstanceId,
      id: businessKey,
      modelKey,
      taskId,
      startUserId,
      taskActivityId,
      curTaskActivityIds,
      startUserName,
      modelCode,
      clientId,
      isRiskManagerProj,
      setMaterialObj,
    }
    if (mainModule && businessKey) {
      const moduleMap = {
        MarginFlowManually: <MarginRefund {...commonProps} setEditing={setEditing} registerCallback={registerCallback} />,
        MarginFlowAuto: <MarginRefund {...commonProps} setEditing={setEditing} registerCallback={registerCallback} />,
        PROJ_ESTABLISH: <ProjectEstablishment {...commonProps} />,
        PROJ_REVIEW: <ProjectReview {...commonProps} />,
        ARCHIVES_DOWNLOAD: <ArchivesInfo {...commonProps} />,
        ARCHIVES: <ArchivesTemplate {...commonProps} />,
        PAYMENT: (
          <PaymentApplication
            id={id}
            params={{ id: businessKey }}
            taskActivityId={taskActivityId}
            modelKey={modelKey}
            taskStatus={taskStatus}
            dynamicFormKeyList={dynamicFormKeyList}
            {...commonProps}
          />
        ),
        CLIENT: (
          <CustomerDetail
            params={{
              id: businessKey,
            }}
            query={{
              clientType: subModule,
              ...commonProps,
            }}
          />
        ),
        CLIENT_APPLY: (
          <ApplyPermission
            params={{
              id: businessKey,
            }}
            query={{ ...commonProps }}
          />
        ),
        CLIENT_AUTHORITY: (
          <CustomerDetail
            params={{
              id: businessKey,
            }}
            query={{
              clientType: subModule,
              startUserId,
              ...commonProps,
            }}
          />
        ),
        CONTRACT: <ContractApplication subModule={subModule} taskActivityId={taskActivityId} modelKey={modelKey} taskStatus={taskStatus} {...commonProps} />,
        CLIENT_TRANSFER: <ClientTransfer {...commonProps} />,
        ADJUST: <Adjust subModule={subModule} {...commonProps} />,
        NEW_AFTER_LEASE_CHECK_PLAN: <AfterLeaseCheck modelKey={modelKey} taskStatus={taskStatus} {...commonProps} />,
        NEW_AFTER_LEASE_CHECK_REPORT: <AfterLeaseCheckReport modelKey={modelKey} taskActivityId={taskActivityId} {...commonProps} />,

        NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY: <AfterLeaseCheckExternal subModule={subModule} {...commonProps} />,
        RENT_COLLECTION: <AfterLeaseRentCollection {...commonProps} modelKey={modelKey} />,
        GROUP_CREDIT_REVIEW: <GroupCreditReview {...commonProps} />,
        GROUP_CREDIT_ESTABLISH: <GroupCreditEstablish {...commonProps} />,
        ASSET_CLASSIFY: <Leave5Type modelKey={modelKey} curTaskActivityIds={taskActivityId} taskStatus={taskStatus} {...commonProps}></Leave5Type>,
        ASSET_CLASSIFY_REVIEW: <Leave5TypeDetail modelKey={modelKey} curTaskActivityIds={taskActivityId} {...commonProps}></Leave5TypeDetail>,
        CREDIT_REPORT: <CreateReportTable businessKey={businessKey} {...commonProps} />,
        KPI_PROJECT_DISTRIBUTION: <KpiPorjectAllocation modelKey={modelKey} curTaskActivityIds={taskActivityId} taskStatus={taskStatus} {...commonProps} />,
        KPI_PROJECT_DISTRIBUTION_NEW: <KpiPorjectAllocation modelKey={modelKey} curTaskActivityIds={taskActivityId} taskStatus={taskStatus} {...commonProps} />,
        KPI_PROJECT_MANAGER_ASSESSMENT: <KpiPmAssess businessKey={businessKey} {...commonProps} />,
        FUND_RECEIPT_REPAY: <FundReceiptRepay {...commonProps} />,
        BATCH_FUND_RECEIPT_REPAY: <BatchFundReceiptRepay {...commonProps} />,
        FUND_FINANCING: <FinancialFund modelKey={modelKey} curAssigneeIds={curAssigneeIds} {...commonProps} />,
        RISK_OPINION: <RiskOption modelKey={modelKey} {...commonProps} />,
        NEW_FTP_GUIDANCE: <FtpBusiness modelKey={modelKey} {...commonProps} />,
        LEASE: (
          <LeaseMaintain
            taskActivityId={taskActivityId}
            modelKey={modelKey}
            taskStatus={taskStatus}
            curTab={curTab}
            {...commonProps}
            // 覆盖，特殊处理
          />
        ),
        COLLECTION: <RentPaymentNotice {...commonProps} taskActivityId={taskActivityId} taskStatus={taskStatus} />,
        POLICY: <PolicyRemind {...commonProps}></PolicyRemind>,
        TRACK_EVENT: <TrackEvent {...commonProps} />,
        RATING_CLIENT: <RatingClient {...commonProps} taskActivityId={taskActivityId} />,
        RATING_AMOUNT: <RatingDebt {...commonProps} taskActivityId={taskActivityId} />,
        PROJ_PRICING: <ProjPricing {...commonProps} />,
        FUNDRECEIPTREPAY: <FinancialFund modelKey={modelKey} curAssigneeIds={curAssigneeIds} {...commonProps} />,
        OVERDUE_COLLECTION_ACTION: <OverdueDetail {...commonProps} />,
        DOC_PRINTING: <LitigationDocDetail {...commonProps} />,
        FTP_INTEREST_CHANGE: <FtpInterestChange {...commonProps} />,
        MonthPlanEventFlow: <MonthPlanEventFlow {...commonProps} processName={processName} />,
        YearHalfOtherPlanEventFlow: <MonthPlanEventFlow {...commonProps} processName={processName} />,
        BudgetPlanPayWeeklyFlow: <BudgetPlanPayment {...commonProps} />,
        FinalPlanEventFlow: <MonthPlanEventFlow {...commonProps} mainModule={mainModule} />,
        BudgetExamineFlow: <BudgetExamineFlow {...commonProps} />,
        CreditReportSelectFlow: <CreditReportSelectFlow {...commonProps} params={{ id: businessKey }} />,
        AppraisalCompanyWhitelistCreateFlow: <AppraisalCompanyWhitelistCreateFlow {...commonProps} type="create" />,
        AppraisalCompanyWhitelistModifyFlow: <AppraisalCompanyWhitelistCreateFlow {...commonProps} type="modify" />,
        AppraisalCompanyWhitelistOutFlow: <AppraisalCompanyWhitelistCreateFlow {...commonProps} type="out" />,
        ASSOCIATION_REPORT_APPLY: <FinancialReport {...commonProps} />,
        AssociationReportRealtimeFlow: <FinancialReport {...commonProps} />,
        AssociationReportCaseTypeRelatedFlow: <FinancialReport {...commonProps} />,
        AssociationReportQuarterMonthFlow: <FinancialReport {...commonProps} />,
        AssociationReportMainBusinessFlow: <FinancialReport {...commonProps} />,
        AssociationReportPushFlow: <FinancialReport {...commonProps} />,
        FilingMaterialsApplyFlow: <FilingMaterialsApply params={commonProps} />,
        ProjectProfitSharingFlow: <ProfitDistribution params={commonProps} />,
        FinanceOverdue: <FinanceOverdue {...commonProps} />,
        FundFilingMaterialsApplyFlow: <FinancialFund modelKey={modelKey} {...commonProps} />,
        IndirectFinancingCarryInterestFlow: <FinancialFund modelKey={modelKey} {...commonProps} />,
        DirectFinancingCarryInterestFlow: <FinancialFund modelKey={modelKey} {...commonProps} />,
        AfterFilingMaterialsApplyFlow: <AfterFilingMaterialsApply {...commonProps} />,
        OtherFilingMaterialsApplyFlow: <OtherFilingMaterialsApply {...commonProps} />,
      }
      return moduleMap[mainModule] ?? <Empty description="未匹配到该模型"></Empty>
    }
    return <div></div>
  }, [subModule, mainModule, businessKey, canEditFlag, modelKey, taskActivityId, taskStatus])
  return <ErrorBoundary fallback={<Tag>渲染出错了</Tag>}>{cloneElement(CurrentModule, { isNewLayout })}</ErrorBoundary>
}

export default Index
