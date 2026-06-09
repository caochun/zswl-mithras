package cn.zswltech.mithras.service.enums;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.rating.mapper.RatingAmountMapper;
import cn.zswltech.mithras.rating.mapper.RatingClientMapper;
import cn.zswltech.mithras.kpi.mapper.KpiParameterConfigMapper;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.*;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.archives.infrastructure.persistence.mapper.ArchivesManagementMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyClientMapper;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.AssetClassifyMapper;
import cn.zswltech.mithras.associationreport.mapper.AssociationReportApplyMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientTransferMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRetreatInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractTextManageMapper;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.financeprojectdistribution.mapper.FinanceProjectDistributionMapper;
import cn.zswltech.mithras.creditreport.mapper.CreditReportBaseInfoMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpMonthlyGuidanceMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpQuarterlyGuidanceMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundCreditMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundGuaranteeAgencyMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundOrganizationMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBatchMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.AppraisalCompanyWhitelistMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.LeaseItemInfoMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.TycAppraisalCompanyBaseInfoMapper;
import cn.zswltech.mithras.liquiditymanage.mapper.AccountBalanceBaseInfoMapper;
import cn.zswltech.mithras.liquiditymanage.mapper.FundFinancingAccountSettingMapper;
import cn.zswltech.mithras.margin.mapper.MarginBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.pubinfo.PublicInfoRecordMapper;
import cn.zswltech.mithras.policy.infrastructure.persistence.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.finance.mapper.stampduty.StampDutyMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.trackevent.TrackEventMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.DocPrintingMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.LitigationRegistrationMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.OverdueCollectionActionMapper;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.mapper.OverdueCollectionMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.NewFtpBaseInfoMapper;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum.*;


/**
 * 业务模块枚举
 *
 * @author wangchuanhao
 * @date 2022/7/20 10:32 AM
 */
@AllArgsConstructor
@Getter
public enum BusinessModuleEnum implements PullDown, DataAuthBusinessModule {

    /**
     * 客户管理
     */
    CLIENT(Arrays.asList(ClientModifyFlow.name()), ClientMapper.class, "clientId", null, null),


    CLIENT_TRANSFER(toList(ClientTransferFlow.name()), ClientTransferMapper.class, "", null, null),


    /**
     * 立项
     */
    PROJ_ESTABLISH(Arrays.asList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name()), ProjEstablishBaseInfoMapper.class,
            "projEstablishId", null, null),
    PROJ_ESTABLISH_CLIENT(Arrays.asList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name()), ProjEstablishBaseInfoMapper.class, null, null, null),

    /**
     * 评审
     */
    PROJ_REVIEW(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
            ProcessModelTypeEnum.ProjReviewModifyFlow.name()),
            ProjReviewBaseInfoMapper.class,
            "projectId", null, null),
    PROJ_REVIEW_CLIENT(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()), ProjReviewBaseInfoMapper.class, null, null, null),

    PROJ_REVIEW_MEET_MINUTE(null, null, null, null, null),

    /**
     * 定价
     */
    PROJ_PRICING(Arrays.asList(ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(),
            ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name()),
            ProjPricingBaseInfoMapper.class,
            "projectId", null, null),


    /**
     * 租赁物文本
     */
    LEASE_TEXT(Arrays.asList(ProcessModelTypeEnum.LeaseCreateFlow.name(),
            ProcessModelTypeEnum.LeaseModifyFlow.name()), LeaseItemInfoMapper.class,
            "leaseItemInfoId", null, null),

    /**
     * 租赁物管理-租赁物文本
     */
    LEASE_DATA_LIST(null, LeaseItemInfoMapper.class,
            "leaseItemInfoId", null, null),


    /**
     * 租赁物管理-评估机构租赁资料清单
     */
    LEASE_APPRAISAL_DATA_LIST(null, TycAppraisalCompanyBaseInfoMapper.class,
            "leaseItemInfoId", null, null),

    /**
     * 合同
     */
    CONTRACT(Arrays.asList(ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(), ProcessModelTypeEnum.ContractEarlySettleFlow.name(),
            ProcessModelTypeEnum.ContractNormalSettleFlow.name(), ProcessModelTypeEnum.ContractLPRChangeFlow.name(), ProcessModelTypeEnum.ContractExtensionFlow.name(),
            ProcessModelTypeEnum.ContractEarlyRepayFlow.name(), ProcessModelTypeEnum.ContractStartRentFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name(),
            ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name(), ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name(),
            ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name()),
            ContractBaseInfoMapper.class, "contractId", null, null),

    /**
     * 合同文本管理
     */
    CONTRACT_TEXT_MANAGE(null, ContractTextManageMapper.class, null, null, null),

    /**
     * 租后调整
     **/
    ADJUST(Arrays.asList(ProcessModelTypeEnum.AfterLeaseExtendFlow.name(), ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name()), AfterLeaseAdjustInfoMapper.class, "adjustId", null, null),

    /**
     * 租后催收-罚息减免 罚息依附于合同，主表使用合同
     **/
    OVERDUE_COLLECTION_REDUCTION(Collections.singletonList(ProcessModelTypeEnum.RentCollectionExemptionFlow.name()), PenaltyReduceBaseInfoMapper.class, "overdueCollectionId", null, null),


    /**
     * 租后检查计划
     */
    @Deprecated
    AFTER_LEASE_CHECK_PLAN(Arrays.asList(
            ProcessModelTypeEnum.AfterLeaseCheckPlanPublishCreateFlow.name(),
            ProcessModelTypeEnum.AfterLeaseCheckPlanPublishModifyFlow.name(),
            ProcessModelTypeEnum.AfterLeaseCheckPlanFinishFlow.name()
    ), NewAfterLeaseCheckPlanBaseMapper.class, "planId", "PROJ_REVIEW", "projectId"),

    @Deprecated
    AFTER_LEASE_CHECK_PROJECT(Collections.singletonList(ProcessModelTypeEnum.AfterLeaseCheckProjectReportFlow.name()), NewAfterLeaseCheckPlanClientMapper.class, "checkPlanProjectId", "PROJ_REVIEW", "projectId"),

    @Deprecated
    AFTER_LEASE_CHECK_EXTERNAL_QUERY(
            Collections.singletonList(ProcessModelTypeEnum.AfterLeaseCheckExternalQueryFlow.name()),
            NewAfterLeaseCheckExternalQueryMapper.class, "queryId", "PROJ_REVIEW", "projReviewId"),

    NEW_AFTER_LEASE_CHECK_PLAN(Arrays.asList(
            ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishCreateFlow.name(),
            ProcessModelTypeEnum.NewAfterLeaseCheckPlanPublishModifyFlow.name(),
            ProcessModelTypeEnum.NewAfterLeaseCheckPlanFinishFlow.name()
    ), NewAfterLeaseCheckPlanBaseMapper.class, "planId", "CLIENT", "clientId"),

    NEW_AFTER_LEASE_CHECK_REPORT(ListUtil.toList(ProcessModelTypeEnum.NewAfterLeaseCheckReportFlow.name(), ProcessModelTypeEnum.NewAfterLeaseCheckReportCommonlyFlow.name()), NewAfterLeaseCheckPlanClientMapper.class, "checkPlanClientId", null, null),

    NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY(
            Collections.singletonList(ProcessModelTypeEnum.NewAfterLeaseCheckExternalQueryFlow.name()),
            NewAfterLeaseCheckExternalQueryMapper.class, "queryId", "CLIENT", "clientId"),

    /**
     * 租后检查台账
     */
    NEW_AFTER_LEASE_CHECK_PLAN_LEDGER(null, NewAfterLeaseCheckPlanBaseMapper.class, "planId", "PROJ_REVIEW", "projectId"),

    /**
     * 付款
     */
    PAYMENT(Arrays.asList(ProcessModelTypeEnum.PaymentCreateFlow.name(), ProcessModelTypeEnum.PaymentActualDetailFlow.name()), PaymentBaseInfoMapper.class, "paymentId", "CONTRACT", "contractId"),

    /**
     * 公开信息
     */
    PUBLIC_INFO(Collections.singletonList(ProcessModelTypeEnum.PaymentCreateFlow.name()), PublicInfoRecordMapper.class, "id", "PAYMENT", "paymentId"),

    /**
     * 集团授信立项
     */
    GROUP_CREDIT_ESTABLISH(Arrays.asList(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name()), GroupCreditEstablishBaseInfoMapper.class, "groupCreditEstablishId", null, null),
    GROUP_CREDIT_ESTABLISH_CLIENT(Arrays.asList(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name()), GroupCreditEstablishBaseInfoMapper.class, null, null, null),

    /**
     * 集团授信评审
     */
    GROUP_CREDIT_REVIEW(Arrays.asList(ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()), GroupCreditReviewBaseInfoMapper.class, "groupCreditReviewId", null, null),
    GROUP_CREDIT_REVIEW_CLIENT(Arrays.asList(ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name()), GroupCreditReviewBaseInfoMapper.class, null, null, null),

    /**
     * 收款
     */
    COLLECTION(Collections.emptyList(), CollectionBaseInfoMapper.class, "collectionId", "CONTRACT", "contractId"),

    /**
     * 资金管理
     **/
    FUND_ORGANIZATION(Collections.emptyList(), FundOrganizationMapper.class, null, null, null),
    FUND_GUARANTEE_AGENCY(Collections.emptyList(), FundGuaranteeAgencyMapper.class, null, null, null),
    FUND_CREDIT(Collections.emptyList(), FundCreditMapper.class, null, null, null),
    FUND_CREDIT_LIMIT(Collections.emptyList(), FundCreditMapper.class, null, null, null),
    FUND_GUARANTEE_AGENCY_LIMIT(Collections.emptyList(), FundGuaranteeAgencyMapper.class, null, null, null),

    /**
     * 资金管理-流动性管理
     */
    ACCOUNT_BALANCE(Collections.emptyList(), AccountBalanceBaseInfoMapper.class, null, null, null),
    ACCOUNT_SETTING(Collections.emptyList(), FundFinancingAccountSettingMapper.class, null, null, null),
    LIQUIDITY_BOARD(Collections.emptyList(), null, null, null, null),
    LIQUIDITY_MISMATCH(Collections.emptyList(), null, null, null, null),
    FUND_TRANSFER(Collections.emptyList(), null, null, null, null),
    LIQUIDITY_RENT_INCOME(Collections.emptyList(), null, null, null, null),
    LIQUIDITY_REPAY(Collections.emptyList(), null, null, null, null),


    /**
     * 收款
     */
    MARGIN(Collections.emptyList(), MarginBaseInfoMapper.class, "marginId", "CONTRACT", "contractId"),

    /**
     * 资产分类-复核审批
     */
    ASSET_CLASSIFY_REVIEW(Arrays.asList(ProcessModelTypeEnum.AssetClassifyReview.name()), AssetClassifyClientMapper.class, "assetClassifyClientId", "CLIENT", "clientId"),

    /**
     * 资产分类
     */
    ASSET_CLASSIFY(Arrays.asList(ProcessModelTypeEnum.AssetClassifyReviewFlow.name(), ProcessModelTypeEnum.AssetClassifyReviewMeetingFlow.name(), ProcessModelTypeEnum.AssetClassifyRiskMeetingFlow.name(), ProcessModelTypeEnum.AssetClassifyBoardMeetingFlow.name()), AssetClassifyMapper.class, "assetClassifyId", "CLIENT", "clientId"),

    FTP_QUARTERLY_GUIDANCE(Arrays.asList(ProcessModelTypeEnum.FtpQuarterlyGuidanceCreateFlow.name(),
            ProcessModelTypeEnum.FtpQuarterlyGuidanceModifyFlow.name()), FtpQuarterlyGuidanceMapper.class,
            "guidanceId", null, null),
    FTP_MONTHLY_GUIDANCE(Arrays.asList(ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name(),
            ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name()), FtpMonthlyGuidanceMapper.class,
            "guidanceId", null, null),
    NEW_FTP_GUIDANCE(Arrays.asList(ProcessModelTypeEnum.FtpMonthlyGuidanceCreateFlow.name(),
            ProcessModelTypeEnum.FtpMonthlyGuidanceModifyFlow.name()),
            NewFtpBaseInfoMapper.class, "ftpId", null, null),

    ARCHIVES(Arrays.asList(ProcessModelTypeEnum.ArchivesFlow.name()), ArchivesManagementMapper.class, null, null, null),

    /**
     * 绩效考核-参数设置
     */
    KPI_PARAMETER_CONFIG(null, KpiParameterConfigMapper.class, null, null, null),
    /**
     * 绩效考核-项目分配表
     */
    KPI_PROJECT_DISTRIBUTION(Arrays.asList(ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name()), KpiProjectDistributionMapper.class, "projectDistributionId", null, null),

    /**
     * 公告
     */
    ANNOUNCEMENT(Collections.emptyList(), null, "default", null, null),

    /**
     * 风控管理-风险预警监控
     */
    RISK_MONITOR(Collections.emptyList(), null, "default", null, null),
    /**
     * 风控管理-舆情监测
     */
    RISK_OPINION(Arrays.asList(RiskControlOpinionHandleFlow.name(), RiskControlOpinionHandleAfterLaunchFlow.name(), RiskControlOpinionHandleCloseFlow.name(), RiskControlOpinionHandleAfterLaunchCloseFlow.name(), RiskControlNotPaymentFlow.name(), RiskControlPaymentFlow.name()), null, "default", null, null),
    /**
     * 风控管理-关联交易报送
     */
    RELATED_TRANSACTION_REPORT(Collections.emptyList(), null, "default", null, null),

    RISK_CONTROL_SCORE_CARD(Collections.emptyList(), null, "default", null, null),
    /**
     * 业务模块不需指定时使用，仅用于标识
     **/
    DEFAULT(Collections.emptyList(), null, "default", null, null),


    /**
     * 资金收付款模块
     */
    FUND_RECEIPT_REPAY(Arrays.asList(ProcessModelTypeEnum.FundReceiptRepayFlow.name()), FundReceiptRepayBaseInfoMapper.class, "receiptRepayId", null, null),
    /**
     * 资金收付款 批量操作模块 （虚拟模块，非业务模块）用于审批流、文件管理
     */
    BATCH_FUND_RECEIPT_REPAY(Arrays.asList(ProcessModelTypeEnum.BatchFundReceiptRepayFlow.name()), FundReceiptRepayBatchMapper.class, null, null, null),

    /**
     * 财务资金管理-流动性风险（虚拟模块，非业务模块）用于权限管理
     */
    VIRTUAL_LIQUIDITY_RISK(Collections.emptyList(), null, "default", null, null),

    /**
     * 融资管理
     */
    FUND_FINANCING(Arrays.asList(
            ProcessModelTypeEnum.FundFinancingCreateFlow.name(),
            ProcessModelTypeEnum.FundFinancingModifyFlow.name(),
            ProcessModelTypeEnum.FundFinancingEarlySettleFlow.name()
    ), FundFinancingBaseInfoMapper.class, "financingId", null, null),

    /**
     * 文件模板
     */
    FILE_TEMPLATE(Collections.emptyList(), null, "default", null, null),

    /**
     * 直融管理
     */
    FUND_DIRECT_FINANCING(Collections.singletonList("NONE"), FundDirectFinancingBaseInfoMapper.class, "financingId", null, null),


    /**
     * 保单管理
     */
    POLICY(Arrays.asList(ProcessModelTypeEnum.PolicyCreateFlow.name(), ProcessModelTypeEnum.PolicyModifyFlow.name(), ProcessModelTypeEnum.PolicyReminderFlow.name(), ProcessModelTypeEnum.PolicyOverdueReminderFlow.name()), PolicyInfoMapper.class,
            null, null, null),

    /**
     * FTP计息
     */
    FTP_INTEREST(Collections.emptyList(), null, "default", null, null),

    /**
     * 保单缓存表
     **/
    POLICY_TMP(Collections.emptyList(), null, null, null, null),

    /**
     * 我的流程-我收到的-已审批
     */
    MY_PROCESS_RECEIVED_AUDITED(Collections.emptyList(), null, null, null, null),

    /**
     * 我的流程-流程查询
     */
    MY_PROCESS_PROCESS_QUERY(Collections.emptyList(), null, null, null, null),

    /**
     * 跟踪事项
     */
    TRACK_EVENT(Collections.singletonList(ProcessModelTypeEnum.TRACKEVENT.name()), TrackEventMapper.class, null, null, null),

    /**
     * 客户评级创建
     */
    RATING_CLIENT(Arrays.asList(ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name()), RatingClientMapper.class, null, null, null),

    /**
     * 债项评级创建
     */
    RATING_AMOUNT(Arrays.asList(ProcessModelTypeEnum.RatingAmountCreateFlow.name(), ProcessModelTypeEnum.RatingAmountUpdateFlow.name()), RatingAmountMapper.class, null, null, null),

    /**
     * 逾期催收
     */
    OVERDUE_COLLECTION(Collections.emptyList(), OverdueCollectionMapper.class, null, null, null),

    /**
     * 逾期催收记录
     */
    OVERDUE_COLLECTION_ACTION(Arrays.asList(ProcessModelTypeEnum.OverdueCollectionLetterAuditFlow.name(), ProcessModelTypeEnum.OverdueLawerLetterAuditFlow.name()), OverdueCollectionActionMapper.class, null, null, null),

    /**
     * 诉讼登记
     */
    LITIGATION_REGISTRATION(Collections.emptyList(), LitigationRegistrationMapper.class, null, null, null),
    /**
     * 用印申请
     */
    DOC_PRINTING(Collections.singletonList(ProcessModelTypeEnum.DocPrintingAuditFlow.name()), DocPrintingMapper.class, null, null, null),

    /**
     * 流水中心导出接口注册(非流程)
     */
    BUSINESS_FLOW_PROJ_PAY(Collections.emptyList(), null, null, null, null),
    BUSINESS_FLOW_PROJ_COLLECT(Collections.emptyList(), null, null, null, null),
    BUSINESS_FLOW_FINANCIAL_PAY(Collections.emptyList(), null, null, null, null),
    BUSINESS_FLOW_FINANCIAL_COLLECT(Collections.emptyList(), null, null, null, null),

    /**
     * 拜访记录
     */
    VISIT_RECORD(null, VisitRecordMapper.class, null, null, null),

    /**
     * 征信报送
     */
    CREDIT_REPORT(Collections.singletonList(ProcessModelTypeEnum.CreditReportFlow.name()), null, null, null, null),
    BLACK_GRAY(Collections.emptyList(), null, null, null, null),
    BLACK_GRAY_MANUAL_OUTBOUND(Collections.emptyList(), null, null, null, null),
    BLACK_GRAY_BREAK(Collections.emptyList(), null, null, null, null),
    BLACK_GRAY_WAREHOUSE_TASK(Collections.emptyList(), null, null, null, null),

    /**
     * 征信报送查询
     */
    CREDIT_REPORT_SELECT(Collections.singletonList(ProcessModelTypeEnum.CreditReportSelectFlow.name()), CreditReportBaseInfoMapper.class, null, null, null),

    /**
     * 风控管理-预警监测
     */
    RISK_WARN(ListUtil.toList(RiskControlWarnNotPaymentFlow.name(), RiskControlWarnPaymentFlow.name()), null, "default", null, null),

    /**
     * 预算管理
     **/
    BUDGET_PLAN(ListUtil.toList(BudgetPlanPayWeeklyFlow.name()), null, null, null, null),

    /**
     * 金融局上报
     */
    ASSOCIATION_REPORT_APPLY(Arrays.asList(ProcessModelTypeEnum.AssociationReportRealtimeFlow.name(),ProcessModelTypeEnum.AssociationReportCaseTypeRelatedFlow.name(),ProcessModelTypeEnum.AssociationReportQuarterMonthFlow.name(),ProcessModelTypeEnum.AssociationReportMainBusinessFlow.name()),
            AssociationReportApplyMapper.class,
            "reportInstanceId", null, null),
    /**
     * 逾期报送管理
     **/
    FINANCE_OVERDUE(ListUtil.toList(FinanceOverdue.name()), null, null, null, null),

    /**
     * 评估机构白名单
     */
    APPRAISAL_COMPANY_WHITELIST(ListUtil.toList(AppraisalCompanyWhitelistCreateFlow.name(), AppraisalCompanyWhitelistModifyFlow.name(), AppraisalCompanyWhitelistOutFlow.name()), AppraisalCompanyWhitelistMapper.class, null, null, null),

    FILING_MATERIALS_PROJECT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),



    /*内部资料-资料参考-基本信息*/
    BUSINESS_REVIEW_CLIENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部资料-运营终审-承租人*/
    FILING_BUSINESS_LESSEE_CLIENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部资料-运营终审-法人保证人*/
    FILING_BUSINESS_ENT_CLIENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部资料-运营终审-自然保证人*/
    FILING_BUSINESS_IND_CLIENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /*内部操作资料-资料参考-立项上传资料*/
    BUSINESS_PROJ_ESTABLISH(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-项目定价*/
    BUSINESS_PROJ_PRICING(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-项目评审*/
    BUSINESS_PROJ_REVIEW(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-付款流程中-放款审核表*/
    BUSINESS_PAYMENT_LOAN_APPROVAL(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-租赁物*/
    BUSINESS_LEASE_DATA_LIST(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-公开信息查询资料*/
    BUSINESS_PUBLIC_INFORMATION_QUERY(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-资料参考-流程审批快照*/
    BUSINESS_FLOW_SNAPSHOT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*内部操作资料-运营终审*/
    FILING_BUSINESS_INNER_OPERATION(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /*合同信息-资料参考-合同资料*/
    BUSINESS_PAYMENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*合同信息-运营终审*/
    FILING_BUSINESS_PAYMENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /*租赁物资料-资料参考-租赁物相关信息*/
    BUSINESS_LEASEHOLD(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*租赁物资料-资料参考-保单信息*/
    BUSINESS_LEASEHOLD_POLICY(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    /*租赁物资料-运营资料*/
    FILING_BUSINESS_LEASEHOLD(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /*抵质押资料-运营资料*/
    FILING_BUSINESS_COLLATERALIZATION(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /*归档流程-基础资料清单*/
    FILING_BUSINESS_CLIENT(Arrays.asList(FilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),

    /**
     * 财务-项目利润分配
     */
    FINANCE_PROJECT_DISTRIBUTION(Arrays.asList(ProcessModelTypeEnum.ProjectProfitSharingFlow.name()), FinanceProjectDistributionMapper.class, "projectDistributionId", null, null),

    /**
     * 融资起息流程（间融）
     */
    INDIRECT_CARRY_INTEREST(Arrays.asList(ProcessModelTypeEnum.IndirectFinancingCarryInterestFlow.name()), FundFinancingBaseInfoMapper.class, "financingId", null, null),


    /**
     * 融资起息流程（直融）
     */
    DIRECT_CARRY_INTEREST(Arrays.asList(ProcessModelTypeEnum.DirectFinancingCarryInterestFlow.name()), FundDirectFinancingBaseInfoMapper.class, "financingId", null, null),

    /**
     * 保证金退抵*/
    CONTARCT_DEPOSIT(Arrays.asList(ProcessModelTypeEnum.MarginFlowAuto.name(), ProcessModelTypeEnum.MarginFlowManually.name()), ContractRetreatInfoMapper.class, null, null, null),
    FUND_FILING(Arrays.asList(ProcessModelTypeEnum.FundFilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    AFTER_LEASING_FILING(Arrays.asList(ProcessModelTypeEnum.AfterFilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),
    OTHER_FILING(Arrays.asList(ProcessModelTypeEnum.OtherFilingMaterialsApplyFlow.name()), FilingMaterialsMapper.class, null, null, null),


    /**
     * 印花税管理台账
     */
    STAMP_DUTY(null, StampDutyMapper.class, null, null, null),
    ;


    private static Map<String, BusinessModuleEnum> map;

    static {
        map = Stream.of(BusinessModuleEnum.values()).collect(Collectors.toMap(BusinessModuleEnum::name, e -> e));
    }

    /**
     * 模型key
     */
    private List<String> modelKeyList;
    /**
     * 业务主表mapper
     */
    private Class<? extends BaseMapper> mainMapperClass;
    /**
     * 子表中 主表id字段名
     * 后面模块依赖前面模块 默认也要用这个fieldName
     * 比如付款依赖合同 要存 contractId
     */
    private String subTableMainIdFieldName;
    /**
     * 该模块自己没有主办字段 但是又有主办权限 需要从相关模块取该条数据的关联数据的主办
     * 如果有值说明需要递归取 没值说明取当前模块即可
     */
    private String sponsorModule;
    /**
     * 该模块存的sponsorModule模块的主表id名称
     */
    private String sponsorModuleIdFieldName;

    public static BusinessModuleEnum of(String name) {
        return map.get(name);
    }


    @Override
    public String display() {
        return subTableMainIdFieldName;
    }
}
