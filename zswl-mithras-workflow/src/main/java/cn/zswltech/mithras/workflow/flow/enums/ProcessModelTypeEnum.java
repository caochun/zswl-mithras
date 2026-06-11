package cn.zswltech.mithras.workflow.flow.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流程模型 所有模型都需要在此登记
 *
 * @author wangchuanhao
 * @date 2022/8/3 1:27 PM
 */
@AllArgsConstructor
@Getter
public enum ProcessModelTypeEnum {

    /**
     * 测试模型
     */
    CUSTOMER("1001", "客户", "CUSTOMER", "0", true, false, 1),
    PROJESTABLISH("1002", "项目立项", "PROJESTABLISH", "0", true, false, 1),
    GROUPCREDITESTABLISH("1003", "项目授信", "GROUPCREDITESTABLISH", "0", true, false, 1),
    PROJPRICING("1015", "项目定价", "PROJPRICING", "0", true, false, 1),
    PROJREVIEW("1004", "项目评审", "PROJREVIEW", "0", true, false, 1),
    AFTERLEASE("1005", "项目调整", "AFTERLEASE", "0", true, false, 1),
    CONTRACT("1006", "项目合同", "CONTRACT", "0", true, false, 1),
    PAYMENT("1007", "项目付款", "PAYMENT", "0", true, false, 1),
    CREDITREPORT("1008", "租后", "CREDITREPORT", "0", true, false, 1),
    ASSETCLASSIFY("1009", "资产五级分类", "ASSETCLASSIFY", "0", true, false, 1),
    FTPQUARTERLY("1010", "FTP定价", "FTPQUARTERLY", "0", true, false, 1),
    POLICY("1011", "其他", "POLICY", "0", true, false, 1),
    FUNDRECEIPTREPAY("1012", "融资", "FUNDRECEIPTREPAY", "0", true, false, 1),
    KPIPROJECTDISTRIBUTION("1013", "管理", "KPIPROJECTDISTRIBUTION", "0", true, false, 1),
    TRACKEVENT("1014", "跟踪事项", "TRACKEVENT", "0", true, false, 1),
    RATING_CLIENT("1017", "客户评级", "RATING", "0", true, false, 1),
    RATING_AMOUNT("1016", "债项评级", "RATING", "0", true, false, 1),
    BLACK_GRAY("1101", "黑灰名单", "BLACK_GRAY", "0", true, false, 1),
    BUDGET_PLAN("1102", "预算管理", "BUDGET_PLAN", "0", true, false, 1),
    CREDITMANAGEMENT("1103","征信管理", "CREDITMANAGEMENT", "0", true, false, 1),
    ASSOCIATION_REPORT("1104", "金融局报送", "ASSOCIATION_REPORT", "0", true, false, 1),


    MithrasNewTestModel("001", "租赁新测试模型", "TEST", "0", false, true, 0),

    MithrasFlowComplexTestModel("002", "租赁审批流复合测试模型", "CLIENT", "0", false, false, 0),


    ClientModifyFlow("004", "客户变更", "CLIENT", "1001", true, false, 1),
    ClientTransferFlow("005", "客户移交", "CLIENT_TRANSFER", "1001", true, true, 1),


    ProjEstablishCreateFlow("006", "项目立项创建", "PROJ_ESTABLISH", "1002", true, true, 1),
    ProjEstablishModifyFlow("007", "项目立项变更", "PROJ_ESTABLISH", "1002", true, true, 1),

    ProjReviewCreateFlow("008", "项目评审创建", "PROJ_REVIEW", "1004", true, true, 1),
    ProjReviewModifyFlow("009", "项目评审变更", "PROJ_REVIEW", "1004", true, true, 1),
    ProjReviewPricingApprovalFlow("010", "项目定价创建", "PROJ_PRICING", "1015", true, true, 1),
    ProjReviewPricingModifyApprovalFlow("011", "项目定价变更", "PROJ_PRICING", "1015", true, true, 1),

    ContractCreateFlow("012", "合同创建", "CONTRACT", "1006", true, true, 1),
    ContractModifyFlow("013", "合同其他变更", "CONTRACT", "1006", true, true, 1),
    ContractStartRentFlow("0014", "合同起租", "CONTRACT", "1006", true, true, 1),
    @Deprecated
    ContractStartRentAutoFlow("015", "合同起租-系统自动发起", "CONTRACT", "1006", true, true, 1),
    ContractAddNewReceiptFlow("016", "合同新增借据（投放）", "CONTRACT", "1006", true, true, 1),
    @Deprecated
    ContractAddNewReceiptAutoFlow("017", "合同新增借据（投放）-系统自动发起", "CONTRACT", "1006", true, true, 1),
    ContractEarlySettleFlow("018", "合同提前结清", "CONTRACT", "1006", true, true, 1),
    ContractNormalSettleFlow("019", "合同正常结清", "CONTRACT", "1006", true, true, 1),
    ContractLPRChangeFlow("020", "合同LPR调整", "CONTRACT", "1006", true, true, 1),
    ContractExtensionFlow("021", "合同展期", "CONTRACT", "1006", true, true, 1),
    ContractEarlyRepayFlow("022", "合同提前还款", "CONTRACT", "1006", true, true, 1),
    ContractChangeRepayPlanFlow("023", "合同调整还款计划", "CONTRACT", "1006", true, true, 1),

    PaymentCreateFlow("024", "付款申请", "PAYMENT", "1007", true, true, 1),
    PaymentActualDetailFlow("025", "付款实际核销确认", "PAYMENT", "1007", true, false, 1),

    AfterLeaseExtendFlow("026", "项目展期", "ADJUST", "1005", true, true, 1),
    AfterLeaseRepaymentFlow("027", "项目调整还款计划", "ADJUST", "1005", true, true, 1),

    @Deprecated
    AfterLeaseCheckPlanPublishCreateFlow("028", "（废弃）租后检查计划发布", "AFTER_LEASE_CHECK_PLAN", "1008", true, false, 0),
    @Deprecated
    AfterLeaseCheckPlanPublishModifyFlow("029", "（废弃）租后检查计划变更", "AFTER_LEASE_CHECK_PLAN", "1008", true, false, 0),
    @Deprecated
    AfterLeaseCheckPlanFinishFlow("030", "（废弃）租后检查完结", "AFTER_LEASE_CHECK_PLAN", "1008", true, false, 0),
    @Deprecated
    AfterLeaseCheckProjectReportFlow("031", "（废弃）租后检查报告", "AFTER_LEASE_CHECK_PROJECT", "1008", true, false, 0),
    @Deprecated
    AfterLeaseCheckExternalQueryFlow("032", "（废弃）租后外部信息查询", "AFTER_LEASE_CHECK_EXTERNAL_QUERY", "1008", true, false, 0),

    NewAfterLeaseCheckPlanPublishCreateFlow("033", "租后检查计划发布", "NEW_AFTER_LEASE_CHECK_PLAN", "1008", true, false, 1),
    NewAfterLeaseCheckPlanPublishModifyFlow("034", "租后检查计划变更", "NEW_AFTER_LEASE_CHECK_PLAN", "1008", true, false, 1),
    NewAfterLeaseCheckPlanFinishFlow("035", "租后检查完结", "NEW_AFTER_LEASE_CHECK_PLAN", "1008", true, false, 1),
    NewAfterLeaseCheckReportFlow("036", "租后检查报告", "NEW_AFTER_LEASE_CHECK_REPORT", "1008", true, false, 1),
    NewAfterLeaseCheckExternalQueryFlow("037", "租后外部信息查询", "NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY", "1008", true, false, 1),

    GroupCreditEstablishCreateFlow("038", "授信立项创建", "GROUP_CREDIT_ESTABLISH", "1003", true, false, 1),
    GroupCreditEstablishModifyFlow("039", "授信立项变更", "GROUP_CREDIT_ESTABLISH", "1003", true, false, 1),
    GroupCreditReviewCreateFlow("040", "授信评审创建", "GROUP_CREDIT_REVIEW", "1003", true, false, 1),
    GroupCreditReviewModifyFlow("041", "授信评审变更", "GROUP_CREDIT_REVIEW", "1003", true, false, 1),

    RentCollectionExemptionFlow("042", "（废弃）罚息减免", "RENT_COLLECTION", "", true, false, 1),

    AssetClassifyBoardMeetingFlow("043", "资产五级分类董事会审批", "ASSET_CLASSIFY", "1009", true, false, 1),
    AssetClassifyRiskMeetingFlow("044", "资产五级分类风委会审批", "ASSET_CLASSIFY", "1009", true, true, 1),
    AssetClassifyReviewMeetingFlow("045", "资产五级分类评审会审批", "ASSET_CLASSIFY", "1009", true, true, 1),

    AssetClassifyReview("046", "资产五级分类复核", "ASSET_CLASSIFY_REVIEW", "1009", true, false, 1),
    AssetClassifyReviewFlow("047", "资产五级分类复核审批", "ASSET_CLASSIFY", "1009", true, true, 1),

    FtpQuarterlyGuidanceCreateFlow("048", "季度收益率指导定价创建", "FTP_QUARTERLY_GUIDANCE", "1010", true, false, 1),
    FtpQuarterlyGuidanceModifyFlow("049", "季度收益率指导定价变更", "FTP_QUARTERLY_GUIDANCE", "1010", true, false, 1),
    FtpMonthlyGuidanceCreateFlow("050", "FTP定价指导创建", "NEW_FTP_GUIDANCE", "1010", true, false, 1),
    FtpMonthlyGuidanceModifyFlow("051", "FTP定价指导变更", "NEW_FTP_GUIDANCE", "1010", true, false, 1),

    CreditReportFlow("052", "征信报送", "CREDIT_REPORT", "1103", true, true, 1),

    @Deprecated
    KpiProjectAllocationFlow("053", "（废弃）绩效考核项目分配表", "KPI_PROJECT_ALLOCATION", "", true, false, 0),
    @Deprecated
    KpiProjectManagerAssessmentFlow("054", "（废弃）绩效考核项目经理考评表", "KPI_PROJECT_MANAGER_ASSESSMENT", "", true, false, 0),

    KpiProjectDistributionCreateFlow("055", "绩效考核项目分配创建", "KPI_PROJECT_DISTRIBUTION", "1013", true, false, 1),
    KpiProjectDistributionModifyFlow("056", "绩效考核项目分配变更", "KPI_PROJECT_DISTRIBUTION", "1013", true, true, 1),

    FundReceiptRepayFlow("057", "资金部付款审批", "FUND_RECEIPT_REPAY", "1012", true, true, 1),
    BatchFundReceiptRepayFlow("058", "资金部付款批量审批", "BATCH_FUND_RECEIPT_REPAY", "1012", true, true, 1),

    FundFinancingCreateFlow("059", "融资创建审批", "FUND_FINANCING", "1012", true, true, 1),
    FundFinancingModifyFlow("060", "融资生效前变更审批", "FUND_FINANCING", "1012", true, true, 1),
    FundFinancingEarlySettleFlow("061", "融资提前还款审批", "FUND_FINANCING", "1012", true, true, 1),
    ArchivesDownloadFlow("062", "归档文件借阅审批", "ARCHIVES_DOWNLOAD", "1013", true, false, 1),
    ArchivesFlow("063", "档案审批", "ARCHIVES", "1013", true, false, 1),
    RiskControlOpinionHandleFlow("064", "舆情处置(未放款)", "RISK_OPINION", "1011", true, true, 1),
    RiskControlOpinionHandleAfterLaunchFlow("065", "舆情处置(已放款)", "RISK_OPINION", "1011", true, true, 1),
    // 舆情处置(未放款)-删除流程
    RiskControlOpinionHandleCloseFlow("078", "舆情处置(未放款)-关闭", "RISK_OPINION", "1011", true, false, 1),
    // 舆情处置(已放款)-删除流程
    RiskControlOpinionHandleAfterLaunchCloseFlow("079", "舆情处置(已放款)-关闭", "RISK_OPINION", "1011", true, false, 1),

    PolicyCreateFlow("066", "保单创建", "POLICY", "1011", true, false, 1),
    PolicyModifyFlow("067", "保单变更", "POLICY", "1011", true, false, 1),
    LeaseCreateFlow("068", "租赁物创建", "LEASE", "1011", true, false, 1),
    LeaseModifyFlow("069", "租赁物变更", "LEASE", "1011", true, false, 1),

    RentPaymentNotifyFlow("070", "租金支付通知", "COLLECTION", "1008", true, true, 1),
    KpiProjectDistributionTransferFlow("071", "绩效考核项目分配移交", "KPI_PROJECT_DISTRIBUTION_NEW", "1013", true, true, 1),
    ContractEarlySettleConfirmFlow("072","结清确认", "CONTRACT","1006", true, false,1),
    NewAfterLeaseCheckReportCommonlyFlow("073", "租后检查报告(一般检查)", "NEW_AFTER_LEASE_CHECK_REPORT", "1008", true, false, 1),
    TrackEventCreateFlow("073","跟踪事项创建", "TRACK_EVENT","1014", true, false,1),
    NewRentCollectionExemptionFlow("074", "罚息减免", "RENT_COLLECTION", "1008", true, false, 1),

    RatingClientCreateFlow("074","客户评级创建", "RATING_CLIENT","1017", true, false,1),
    RatingAmountCreateFlow("075","债项评级创建", "RATING_AMOUNT","1016", true, false,1),
    RatingClientUpdateFlow("076","客户评级更新", "RATING_CLIENT","1017", true, false,1),
    RatingAmountUpdateFlow("077","债项评级更新", "RATING_AMOUNT","1016", true, false,1),

    ClientAuthorityCreateFlow("078", "客户权限创建申请", "CLIENT_AUTHORITY", "1001", true, false, 1),
    ClientAuthorityModifyFlow("079", "客户权限变更申请", "CLIENT_AUTHORITY", "1001", true, false, 1),
    ClientApplyAuthorityFlow("080", "客户申办权限申请", "CLIENT_APPLY", "1001", true, false, 1),
    OverdueCollectionLetterAuditFlow("081", "发函催收-催收函及相关函件", "OVERDUE_COLLECTION_ACTION", "1008", true, false, 1),
    OverdueLawerLetterAuditFlow("082", "发函催收-律师函", "OVERDUE_COLLECTION_ACTION", "1008", true, false, 1),
    DocPrintingAuditFlow("083", "诉讼审批", "DOC_PRINTING", "1008", true, true, 1),
    FinancingRecordFlow("084","资金档案归档", "FUNDRECEIPTREPAY","1012", true, false,1),
    DirectFinancingRecordFlow("085","资金档案归档", "FUNDRECEIPTREPAY","1012", true, false,1),
    FinancingFloatRateAdjustFlow("086","融资合同浮动利率调整", "FUNDRECEIPTREPAY","1012", true, false,1),
    BLACK_GRAY_WAREHOUSE("087","黑灰名单入库", "BLACK_GRAY_WAREHOUSE","1101", true, false,1),
    BLACK_GRAY_BREAK("088","黑灰名单突破", "BLACK_GRAY_BREAK","1101", true, false,1),
    BLACK_GRAY_OUTBOUND("089","黑灰名单出库", "BLACK_GRAY_OUTBOUND","1101", true, false,1),
    BLACK_GRAY_WAREHOUSE_TASK("090","黑灰名单入库任务", "BLACK_GRAY_WAREHOUSE_TASK","1101", true, false,1),

    RiskControlNotPaymentFlow("091", "舆情统一处置(未放款)", "RISK_OPINION", "1011", true, false, 1),
    RiskControlPaymentFlow("092", "舆情统一监测(已放款)", "RISK_OPINION", "1011", true, false, 1),

    RiskControlWarnNotPaymentFlow("093", "预警统一处置(未放款)", "RISK_OPINION", "1011", true, false, 1),
    RiskControlWarnPaymentFlow("094", "预警统一监测(已放款)", "RISK_OPINION", "1011", true, false, 1),

    PolicyReminderFlow("087", "保单到期提示", "POLICY", "1011", true, false, 1),
    PolicyOverdueReminderFlow("088", "续保逾期提示", "POLICY", "1011", true, false, 1),
    FinancingRepayPlanConfirmFlow("095","融资还款计划确认", "FUNDRECEIPTREPAY","1012", true, false,1),
    FinancingRepayWriteOffConfirmFlow("096","融资还款核销确认", "FUNDRECEIPTREPAY","1012", true, false,1),

    PaymentReviewInAdvancedFlow("097", "付款实际核销运营提前审核", "PAYMENT", "1007", true, false, 1),
    FtpInterestChangeApplyFlow("098", "FTP计息变更", "FTP_INTEREST_CHANGE", "1010", true, false, 1),
    BudgetPlanPayWeeklyFlow("099", "投放计划周报", "BudgetPlanPayWeeklyFlow", "1102", true, false, 1),

    YearHalfOtherPlanEventFlow("100","投放计划收集-年度/半年度/其它", "YearHalfOtherPlanEventFlow","1102", true, false,1),
    MonthPlanEventFlow("101","投放计划收集-月度", "MonthPlanEventFlow","1102", true, false,1),
    BudgetExamineFlow("102","预算考核待办", "BudgetExamineFlow","1102", true, false,1),
    FinalPlanEventFlow("103","投放计划收集-财务", "FinalPlanEventFlow","1102", true, false,1),
    FinanceOverdue("104","应收逾期集成/结算单", "FinanceOverdue","1011", true, true,1),

    AssociationReportRealtimeFlow("107","实时-金融局报送审批", "AssociationReportRealtimeFlow","1103", true, false,1),
    AssociationReportCaseTypeRelatedFlow("108","法务-金融局报送审批", "AssociationReportCaseTypeRelatedFlow","1103", true, false,1),
    AssociationReportQuarterMonthFlow("109","财务-金融局报送审批", "AssociationReportQuarterMonthFlow","1103", true, false,1),
    AssociationReportMainBusinessFlow("110","风控-金融局报送审批", "AssociationReportMainBusinessFlow","1103", true, false,1),
    AssociationReportPushFlow("111","金融局报送上报审批", "AssociationReportPushFlow","1103", true, false,1),

    AppraisalCompanyWhitelistCreateFlow("104","评估机构白名单准入申请", "AppraisalCompanyWhitelistCreateFlow","1011", true, false,1),
    AppraisalCompanyWhitelistModifyFlow("105","评估机构信息变更", "AppraisalCompanyWhitelistModifyFlow","1011", true, false,1),
    AppraisalCompanyWhitelistOutFlow("106","评估机构白名单出库", "AppraisalCompanyWhitelistOutFlow","1011", true, false,1),
    FilingMaterialsApplyFlow("107","项目资料归档", "FilingMaterialsApplyFlow","1011", true, false,1),
    ProjectProfitSharingFlow("108","项目利润分配", "ProjectProfitSharingFlow","1102", true, false,1),
    //征信报告查询
    CreditReportSelectFlow("107","征信报告查询", "CreditReportSelectFlow","1103", true, false,1),
    //融资起息审批流程
    IndirectFinancingCarryInterestFlow("112","融资起息审批（间融）", "IndirectFinancingCarryInterestFlow","1012", true, false,1),
    DirectFinancingCarryInterestFlow("113","融资起息审批（直融）", "DirectFinancingCarryInterestFlow","1012", true, false,1),

    FundFilingMaterialsApplyFlow("112","直融/间融资料归档", "FundFilingMaterialsApplyFlow","1011", true, false,1),
    AfterFilingMaterialsApplyFlow("113","租后资料归档", "AfterFilingMaterialsApplyFlow","1011", true, false,1),
    OtherFilingMaterialsApplyFlow("114","其他资料归档", "OtherFilingMaterialsApplyFlow","1011", true, false,1),


    MarginFlowAuto("110","保证金抵退自动流程", "MarginFlowAuto","1006", true, false,1),
    MarginFlowManually("112","保证金抵退手动", "MarginFlowManually","1006", true, false,1),
    MarginBackNotice("113","保证金退款通知", "MarginBackNotice","1006", true, false,1),
    ;

    private static Map<String, ProcessModelTypeEnum> map;

    static {
        map = Stream.of(ProcessModelTypeEnum.values()).collect(Collectors.toMap(ProcessModelTypeEnum::name, e -> e));
    }

    private String id;
    private String display;
    private String businessModuleName;
    private String parentCode;
    private Boolean formal;
    // 产生消息是否发送到oa系统
    private Boolean sendOa;
    /*
     * 0废弃 1使用
     */
    private Integer status;

    public static ProcessModelTypeEnum getByName(String name) {
        return map.get(name);
    }

}
